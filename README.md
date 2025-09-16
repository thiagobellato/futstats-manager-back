# 🏟️ Gerenciador de Atletas - Backend (Spring Boot)

Este é o backend da aplicação **futstats**, desenvolvido com **Java + Spring Boot**, que permite o controle de jogadores, seus clubes, estatísticas e transferências entre equipes.

## 🚀 Tecnologias

- Java 21+
- Spring Boot
- Spring Data JPA
- Hibernate
- H2 Database (ou substituível por outro)
- Swagger (documentação da API)

## 📁 Estrutura Principal

- **Atleta**: representa o jogador.
- **Clube**: representa um time de futebol.
- **EstatisticaAtleta**: entidade relacional entre atleta e clube, com data de início, fim, gols e assistências.

## 🔄 Funcionalidades do Backend

- **CRUD completo de Atletas e Clubes**
- **Registro e atualização de estatísticas (gols, assistências)**
- **Transferência de atleta entre clubes**, com:
  - Finalização automática da estatística atual (com `dataFim`)
  - Criação de uma nova estatística para o novo clube
  - Atualização do clube vinculado ao atleta
- **Busca de estatísticas ativas ou por histórico completo**
- **Swagger UI** com todos os endpoints documentados

## ✅ Endpoints principais

- `GET /atletas`, `POST /atletas`, `PUT /atletas/{id}`, `DELETE /atletas/{id}`
- `GET /clubes`, `POST /clubes`
- `POST /atletas/{id}/transferir`
- `GET /estatisticas/atleta/{atletaId}`
