package br.com.bellato.gerenciador_fifa.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Renomeia tabelas do módulo de usuários autenticados do inglês para o padrão
 * do projeto (português + snake_case), preservando dados.
 * <p>
 * Necessário porque o schema efetivo usa {@code ddl-auto=update} (Flyway documental):
 * ao mudar {@code @Table}, o Hibernate pode criar a tabela nova vazia e deixar a antiga.
 * Este migrator resolve isso no startup.
 */
@Component
@Order(0)
public class TabelasUsuarioNomenclaturaMigrator implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TabelasUsuarioNomenclaturaMigrator.class);

    private static final Map<String, String> RENAMES = new LinkedHashMap<>();

    static {
        RENAMES.put("user_statistics", "estatisticas_usuario");
        RENAMES.put("user_club_usage", "uso_clube_usuario");
        RENAMES.put("user_rivalry", "rivalidade_usuario");
        RENAMES.put("head_to_head_match", "historico_confronto_usuario");
    }

    private final JdbcTemplate jdbcTemplate;

    public TabelasUsuarioNomenclaturaMigrator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            for (Map.Entry<String, String> entry : RENAMES.entrySet()) {
                renomearPreservandoDados(entry.getKey(), entry.getValue());
            }
            garantirColunasRivalidade();
            garantirColunasHistorico();
            log.info("Nomenclatura de tabelas de usuário: padronização concluída");
        } catch (Exception ex) {
            log.warn("Nomenclatura de tabelas de usuário: migração ignorada ({})", ex.getMessage(), ex);
        }
    }

    private void renomearPreservandoDados(String antiga, String nova) {
        boolean temAntiga = tabelaExiste(antiga);
        boolean temNova = tabelaExiste(nova);

        if (!temAntiga && temNova) {
            return;
        }
        if (!temAntiga && !temNova) {
            return;
        }

        if (temAntiga && !temNova) {
            jdbcTemplate.execute("ALTER TABLE " + antiga + " RENAME TO " + nova);
            log.info("Tabela renomeada: {} → {}", antiga, nova);
            return;
        }

        // Ambas existem: típico após ddl-auto criar a nova vazia.
        long linhasNova = contarLinhas(nova);
        long linhasAntiga = contarLinhas(antiga);

        if (linhasNova == 0 && linhasAntiga >= 0) {
            jdbcTemplate.execute("DROP TABLE " + nova + " CASCADE");
            jdbcTemplate.execute("ALTER TABLE " + antiga + " RENAME TO " + nova);
            log.info("Tabela nova vazia removida e antiga renomeada: {} → {}", antiga, nova);
            return;
        }

        if (linhasAntiga == 0) {
            jdbcTemplate.execute("DROP TABLE " + antiga + " CASCADE");
            log.info("Tabela antiga vazia removida (mantida {}): {}", nova, antiga);
            return;
        }

        // Ambas com dados — preserva a nova (destino JPA) e remove a antiga para evitar duplicata.
        log.warn(
                "Tabelas {} e {} existem com dados ({} e {} linhas). Mantendo {} e removendo {}.",
                antiga, nova, linhasAntiga, linhasNova, nova, antiga);
        jdbcTemplate.execute("DROP TABLE " + antiga + " CASCADE");
    }

    private void garantirColunasRivalidade() {
        if (!tabelaExiste("rivalidade_usuario")) {
            return;
        }
        garantirColuna("rivalidade_usuario", "campeonatosDisputados", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "campeonatos_disputados", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "titulosUsuarioA", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "titulos_usuarioa", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "titulosUsuarioB", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "titulos_usuariob", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "maior_goleada_a_gols_pro", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "maior_goleada_a_gols_contra", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "maior_goleada_a_margem", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "maior_goleada_a_clube_pro", "VARCHAR(120)");
        garantirColuna("rivalidade_usuario", "maior_goleada_a_clube_contra", "VARCHAR(120)");
        garantirColuna("rivalidade_usuario", "maior_goleada_a_campeonato_nome", "VARCHAR(120)");
        garantirColuna("rivalidade_usuario", "maior_goleada_b_gols_pro", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "maior_goleada_b_gols_contra", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "maior_goleada_b_margem", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "maior_goleada_b_clube_pro", "VARCHAR(120)");
        garantirColuna("rivalidade_usuario", "maior_goleada_b_clube_contra", "VARCHAR(120)");
        garantirColuna("rivalidade_usuario", "maior_goleada_b_campeonato_nome", "VARCHAR(120)");
        garantirColuna("rivalidade_usuario", "maiorSequenciaVitoriasA", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "maiorSequenciaVitoriasB", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "sequenciaAtual", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "sequencia_atual", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("rivalidade_usuario", "sequencia_atual_user_id", "BIGINT");
    }

    private void garantirColunasHistorico() {
        if (!tabelaExiste("historico_confronto_usuario")) {
            return;
        }
        garantirColuna("historico_confronto_usuario", "maiorGoleadaGolsA", "INTEGER DEFAULT 0 NOT NULL");
        garantirColuna("historico_confronto_usuario", "maiorGoleadaGolsB", "INTEGER DEFAULT 0 NOT NULL");
    }

    private void garantirColuna(String tabela, String coluna, String tipo) {
        if (colunaExiste(tabela, coluna)) {
            return;
        }
        // Hibernate/Spring pode ter criado em snake_case
        String snake = paraSnakeCase(coluna);
        if (!snake.equals(coluna) && colunaExiste(tabela, snake)) {
            return;
        }
        // Sem try/catch: no PostgreSQL, falha em DDL aborta a transação mesmo se engolida em Java.
        jdbcTemplate.execute("ALTER TABLE " + tabela + " ADD COLUMN " + coluna + " " + tipo);
    }

    private static String paraSnakeCase(String nome) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nome.length(); i++) {
            char c = nome.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private boolean tabelaExiste(String tabela) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM information_schema.tables
                        WHERE LOWER(table_name) = LOWER(?)
                          AND table_schema IN (CURRENT_SCHEMA(), 'PUBLIC', 'public')
                        """,
                Integer.class,
                tabela);
        return count != null && count > 0;
    }

    private boolean colunaExiste(String tabela, String coluna) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM information_schema.columns
                        WHERE LOWER(table_name) = LOWER(?)
                          AND LOWER(column_name) = LOWER(?)
                          AND table_schema IN (CURRENT_SCHEMA(), 'PUBLIC', 'public')
                        """,
                Integer.class,
                tabela,
                coluna);
        return count != null && count > 0;
    }

    private long contarLinhas(String tabela) {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tabela, Long.class);
        return count != null ? count : 0L;
    }
}
