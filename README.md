# Desafio Board Decola Tech 2025

Gerenciador de boards estilo Kanban desenvolvido como parte do desafio Decola Tech 2025 da DIO. Permite criar, gerenciar e organizar boards com colunas e cartões, incluindo funcionalidades de bloqueio, movimentação e cancelamento.

## Tecnologias Utilizadas
- **Java 17**: Linguagem principal do projeto.
- **MySQL**: Banco de dados relacional para persistência.
- **Liquibase**: Gerenciamento de migrações de banco de dados.
- **Lombok**: Redução de boilerplate no código.
- **SLF4J + Logback**: Logging da aplicação.
- **Gradle**: Gerenciamento de dependências e build.

## Pré-requisitos
- **JDK 17**: Instalado e configurado no PATH.
- **MySQL**: Server rodando localmente na porta 3306.
- **Gradle**: O projeto usa o Gradle Wrapper (`gradlew`), então não é necessária instalação manual.

## Configuração
1. **Clone o Repositório**:
   ```bash
   git clone https://github.com/seu-usuario/desafio-board-decola-tech-2025.git
   cd desafio-board-decola-tech-2025
   ```

2. **Configurar o MySQL**:
   - Crie o banco de dados:
     ```bash
     mysql -u root -p123456 -e "CREATE DATABASE board;"
     ```
   - O usuário padrão é `root` com senha `123456`. Caso use outras credenciais, ajuste em:
     - `src/main/java/br/com/dio/persistence/config/ConnectionConfig.java`
     - `src/main/resources/liquibase.properties`

3. **Dependências**:
   - O Gradle baixa automaticamente as dependências listadas em `build.gradle.kts` ao rodar o projeto.

## Executando o Projeto
1. **Compilar e Rodar com Gradle**:
   ```bash
   ./gradlew clean build
   ./gradlew run
   ```
   - O Liquibase aplicará as migrações automaticamente ao iniciar.

2. **Executar no IntelliJ**:
   - Abra o projeto no IntelliJ IDEA.
   - Configure o JDK 17 em `File > Project Structure > SDKs`.
   - Clique com o botão direito em `src/main/java/br/com/dio/Main.java` e selecione `Run` ou `Debug`.

## Estrutura do Projeto
```
desafio-board-decola-tech-2025/
├── src/
│   ├── main/
│   │   ├── java/br/com/dio/
│   │   │   ├── dto/              # Objetos de transferência de dados (DTOs)
│   │   │   ├── exception/        # Exceções personalizadas
│   │   │   ├── persistence/
│   │   │   │   ├── config/       # Configuração da conexão com o banco
│   │   │   │   ├── converter/    # Conversores de tipos (ex.: OffsetDateTime)
│   │   │   │   ├── dao/          # Camada de acesso a dados (DAOs)
│   │   │   │   ├── entity/       # Entidades do banco de dados
│   │   │   │   └── migration/    # Lógica de migração com Liquibase
│   │   │   ├── service/          # Lógica de negócio
│   │   │   ├── ui/               # Interface de usuário (CLI)
│   │   │   └── Main.java         # Ponto de entrada da aplicação
│   │   └── resources/
│   │       ├── db/changelog/     # Arquivos de migração do Liquibase
│   │       └── liquibase.properties  # Configuração do Liquibase
│   └── test/                     # Testes (ainda não implementados)
├── build.gradle.kts              # Configuração do Gradle
└── README.md                     # Este arquivo
```

## Funcionalidades
- **Criação de Boards**: Adicione novos boards com colunas personalizáveis.
- **Gerenciamento de Colunas**: Suporta colunas iniciais, finais, de cancelamento e pendentes.
- **Cartões**: Crie, mova, bloqueie/desbloqueie e cancele cartões entre colunas.
- **Persistência**: Dados salvos em MySQL com transações seguras.

## Exemplo de Uso
1. Inicie o programa:
   ```
   Bem vindo ao gerenciador de boards, escolha a opção desejada
   1 - Criar um novo board
   2 - Selecionar um board existente
   3 - Excluir um board
   4 - Sair
   ```
2. Escolha `1`, informe o nome do board e configure as colunas.

## Contribuição
- Sinta-se à vontade para abrir issues ou pull requests no repositório.
- Certifique-se de rodar `./gradlew build` antes de submeter alterações.

## Licença
Este projeto é de uso educacional e não possui licença formal.
