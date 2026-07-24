package br.com.bellato.gerenciador_fifa.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;

/**
 * Migração de desenvolvimento para {@code estatisticas_clube}.
 * Usa nomes snake_case (padrão Hibernate/Spring Boot deste projeto).
 * Deve rodar antes de {@link ClubeRankInitializer}.
 */
@Component
@Order(1)
public class EstatisticaClubeDataMigrator implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(EstatisticaClubeDataMigrator.class);

    private final JdbcTemplate jdbcTemplate;

    public EstatisticaClubeDataMigrator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            garantirTabela();
            migrarDeClube();
            migrarDeClubeEstatisticaIntermediaria();
            garantirLinhasZeradas();
            copiarRankPendenteDeClube();
            normalizarRanksEmTodasTabelas();
            removerColunasAntigasDeClube();
            droparTabelaIntermediaria();
            log.info("EstatisticaClube: migração concluída");
        } catch (Exception ex) {
            log.warn("EstatisticaClube: migração automática ignorada ({})", ex.getMessage(), ex);
        }
    }

    private void garantirTabela() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS estatisticas_clube (
                    id BIGSERIAL PRIMARY KEY,
                    clube_id BIGINT NOT NULL,
                    clube_rank VARCHAR(8),
                    clube_gols_pro INTEGER DEFAULT 0,
                    clube_gols_contra INTEGER DEFAULT 0,
                    clube_vitorias INTEGER DEFAULT 0,
                    clube_empates INTEGER DEFAULT 0,
                    clube_derrotas INTEGER DEFAULT 0,
                    clube_titulos INTEGER DEFAULT 0,
                    CONSTRAINT uk_estatisticas_clube_clube UNIQUE (clube_id)
                )
                """);
        garantirColuna("estatisticas_clube", "clube_rank", "VARCHAR(8)");
        garantirColuna("estatisticas_clube", "clube_gols_pro", "INTEGER DEFAULT 0");
        garantirColuna("estatisticas_clube", "clube_gols_contra", "INTEGER DEFAULT 0");
        garantirColuna("estatisticas_clube", "clube_vitorias", "INTEGER DEFAULT 0");
        garantirColuna("estatisticas_clube", "clube_empates", "INTEGER DEFAULT 0");
        garantirColuna("estatisticas_clube", "clube_derrotas", "INTEGER DEFAULT 0");
        garantirColuna("estatisticas_clube", "clube_titulos", "INTEGER DEFAULT 0");
        // Não usar try/catch em ADD CONSTRAINT: no PostgreSQL o erro aborta a transação
        // mesmo quando a exception é engolida em Java (SQLState 25P02).
        if (!restricaoExiste("estatisticas_clube", "fk_estatisticas_clube_clube")) {
            jdbcTemplate.execute("""
                    ALTER TABLE estatisticas_clube
                    ADD CONSTRAINT fk_estatisticas_clube_clube
                    FOREIGN KEY (clube_id) REFERENCES clube (clube_id)
                    """);
        }
    }

    private void garantirColuna(String tabela, String coluna, String tipo) {
        if (!colunaExiste(tabela, coluna)) {
            jdbcTemplate.execute("ALTER TABLE " + tabela + " ADD COLUMN " + coluna + " " + tipo);
        }
    }

    private boolean restricaoExiste(String tabela, String restricao) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.table_constraints
                WHERE table_schema = current_schema()
                  AND LOWER(table_name) = LOWER(?)
                  AND LOWER(constraint_name) = LOWER(?)
                """, Integer.class, tabela, restricao);
        return count != null && count > 0;
    }

    private void migrarDeClube() {
        boolean temStats = colunaExiste("clube", "clube_gols_pro");
        boolean temRank = colunaExiste("clube", "clube_rank");
        if (!temStats && !temRank) {
            return;
        }

        String sql;
        if (temStats && temRank) {
            sql = """
                    INSERT INTO estatisticas_clube (
                        clube_id, clube_rank, clube_gols_pro, clube_gols_contra,
                        clube_vitorias, clube_empates, clube_derrotas, clube_titulos
                    )
                    SELECT
                        c.clube_id,
                        c.clube_rank,
                        COALESCE(c.clube_gols_pro, 0),
                        COALESCE(c.clube_gols_contra, 0),
                        COALESCE(c.clube_vitorias, 0),
                        COALESCE(c.clube_empates, 0),
                        COALESCE(c.clube_derrotas, 0),
                        COALESCE(c.clube_titulos, 0)
                    FROM clube c
                    WHERE NOT EXISTS (
                        SELECT 1 FROM estatisticas_clube e WHERE e.clube_id = c.clube_id
                    )
                    """;
        } else if (temRank) {
            sql = """
                    INSERT INTO estatisticas_clube (
                        clube_id, clube_rank, clube_gols_pro, clube_gols_contra,
                        clube_vitorias, clube_empates, clube_derrotas, clube_titulos
                    )
                    SELECT
                        c.clube_id,
                        c.clube_rank,
                        0, 0, 0, 0, 0, 0
                    FROM clube c
                    WHERE NOT EXISTS (
                        SELECT 1 FROM estatisticas_clube e WHERE e.clube_id = c.clube_id
                    )
                    """;
        } else {
            sql = """
                    INSERT INTO estatisticas_clube (
                        clube_id, clube_gols_pro, clube_gols_contra,
                        clube_vitorias, clube_empates, clube_derrotas, clube_titulos
                    )
                    SELECT
                        c.clube_id,
                        COALESCE(c.clube_gols_pro, 0),
                        COALESCE(c.clube_gols_contra, 0),
                        COALESCE(c.clube_vitorias, 0),
                        COALESCE(c.clube_empates, 0),
                        COALESCE(c.clube_derrotas, 0),
                        COALESCE(c.clube_titulos, 0)
                    FROM clube c
                    WHERE NOT EXISTS (
                        SELECT 1 FROM estatisticas_clube e WHERE e.clube_id = c.clube_id
                    )
                    """;
        }
        int copiados = jdbcTemplate.update(sql);
        log.info("EstatisticaClube: {} registro(s) migrados de clube", copiados);
    }

    private void migrarDeClubeEstatisticaIntermediaria() {
        if (!tabelaExiste("clube_estatistica")) {
            return;
        }
        int copiados = jdbcTemplate.update("""
                INSERT INTO estatisticas_clube (
                    clube_id, clube_gols_pro, clube_gols_contra,
                    clube_vitorias, clube_empates, clube_derrotas, clube_titulos
                )
                SELECT
                    ce.clube_id,
                    COALESCE(ce.clube_gols_pro, 0),
                    COALESCE(ce.clube_gols_contra, 0),
                    COALESCE(ce.clube_vitorias, 0),
                    COALESCE(ce.clube_empates, 0),
                    COALESCE(ce.clube_derrotas, 0),
                    COALESCE(ce.clube_titulos, 0)
                FROM clube_estatistica ce
                WHERE NOT EXISTS (
                    SELECT 1 FROM estatisticas_clube e WHERE e.clube_id = ce.clube_id
                )
                """);
        if (copiados > 0) {
            log.info("EstatisticaClube: {} registro(s) migrados de clube_estatistica", copiados);
        }
    }

    private void garantirLinhasZeradas() {
        int criados = jdbcTemplate.update("""
                INSERT INTO estatisticas_clube (
                    clube_id, clube_gols_pro, clube_gols_contra,
                    clube_vitorias, clube_empates, clube_derrotas, clube_titulos
                )
                SELECT c.clube_id, 0, 0, 0, 0, 0, 0
                FROM clube c
                WHERE NOT EXISTS (
                    SELECT 1 FROM estatisticas_clube e WHERE e.clube_id = c.clube_id
                )
                """);
        if (criados > 0) {
            log.info("EstatisticaClube: {} registro(s) zerados criados", criados);
        }
    }

    private void copiarRankPendenteDeClube() {
        if (!colunaExiste("clube", "clube_rank")) {
            return;
        }
        int atualizados = jdbcTemplate.update("""
                UPDATE estatisticas_clube e
                SET clube_rank = c.clube_rank
                FROM clube c
                WHERE e.clube_id = c.clube_id
                  AND (e.clube_rank IS NULL OR TRIM(e.clube_rank) = '')
                  AND c.clube_rank IS NOT NULL
                """);
        if (atualizados > 0) {
            log.info("EstatisticaClube: rank copiado para {} registro(s)", atualizados);
        }
    }

    private void normalizarRanksEmTodasTabelas() {
        normalizarColuna("estatisticas_clube", "clube_rank");
        if (colunaExiste("campeonato_clube", "campeonato_clube_rank")) {
            normalizarColuna("campeonato_clube", "campeonato_clube_rank");
        }
        if (colunaExiste("historico_clube_campeonato", "historico_clube_rank_anterior")) {
            normalizarColuna("historico_clube_campeonato", "historico_clube_rank_anterior");
            normalizarColuna("historico_clube_campeonato", "historico_clube_rank_novo");
        }
        if (colunaExiste("campeonato_distribuicao_rank", "campeonato_distribuicao_rank_rank")) {
            normalizarColuna("campeonato_distribuicao_rank", "campeonato_distribuicao_rank_rank");
        }
        if (colunaExiste("clube", "clube_rank")) {
            normalizarColuna("clube", "clube_rank");
        }
    }

    private void normalizarColuna(String tabela, String coluna) {
        List<String> valores = jdbcTemplate.queryForList(
                "SELECT DISTINCT " + coluna + " FROM " + tabela + " WHERE " + coluna + " IS NOT NULL",
                String.class);
        for (String valor : valores) {
            ClubRank rank = ClubRank.parse(valor);
            String canonico = rank != null ? rank.getDatabaseValue() : null;
            if (canonico == null || canonico.equals(valor)) {
                continue;
            }
            jdbcTemplate.update(
                    "UPDATE " + tabela + " SET " + coluna + " = ? WHERE " + coluna + " = ?",
                    canonico, valor);
        }
    }

    private void removerColunasAntigasDeClube() {
        String[] colunas = {
                "clube_gols_pro", "clube_gols_contra", "clube_vitorias",
                "clube_empates", "clube_derrotas", "clube_titulos", "clube_rank"
        };
        boolean removeu = false;
        for (String coluna : colunas) {
            if (colunaExiste("clube", coluna)) {
                jdbcTemplate.execute("ALTER TABLE clube DROP COLUMN IF EXISTS " + coluna);
                removeu = true;
            }
        }
        if (removeu) {
            log.info("EstatisticaClube: colunas esportivas removidas da tabela clube");
        }
    }

    private void droparTabelaIntermediaria() {
        if (tabelaExiste("clube_estatistica")) {
            jdbcTemplate.execute("DROP TABLE IF EXISTS clube_estatistica");
            log.info("EstatisticaClube: tabela intermediária clube_estatistica removida");
        }
    }

    private boolean colunaExiste(String tabela, String coluna) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = current_schema()
                  AND LOWER(table_name) = LOWER(?)
                  AND LOWER(column_name) = LOWER(?)
                """, Integer.class, tabela, coluna);
        return count != null && count > 0;
    }

    private boolean tabelaExiste(String tabela) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = current_schema()
                  AND LOWER(table_name) = LOWER(?)
                """, Integer.class, tabela);
        return count != null && count > 0;
    }
}
