# Gerenciador de Boards Kanban

Gerenciador de boards estilo Kanban desenvolvido como parte do desafio Decola Tech 2025 da DIO. Esta aplicação permite criar, gerenciar e organizar boards com colunas e cartões, oferecendo funcionalidades como criação de boards, movimentação de cartões, bloqueio/desbloqueio e cancelamento, com persistência de dados em MySQL.

## Tecnologias Utilizadas
- **Java 17**: Linguagem principal do projeto.
- **MySQL**: Banco de dados relacional para persistência de dados.
- **Liquibase**: Ferramenta para gerenciamento de migrações de banco de dados.
- **Lombok**: Biblioteca para redução de código boilerplate.
- **SLF4J + Logback**: Framework de logging para monitoramento e depuração.
- **Gradle**: Sistema de build e gerenciamento de dependências.

## Pré-requisitos
Antes de executar o projeto, certifique-se de ter os seguintes itens instalados e configurados:
- **JDK 17**: Instalado e configurado no `PATH` do sistema.
- **MySQL**: Servidor rodando localmente na porta padrão `3306`.
- **Gradle**: O projeto utiliza o Gradle Wrapper (`gradlew`), eliminando a necessidade de instalação manual do Gradle.

## Configuração
Siga os passos abaixo para configurar o ambiente e preparar o projeto para execução:

1. **Clone o Repositório**:
   ```bash
   git clone https://github.com/seu-usuario/gerenciador-boards-kanban.git
   cd gerenciador-boards-kanban
   ```

2. **Configurar o MySQL**:
   - Crie o banco de dados `board`:
     ```bash
     mysql -u root -p -e "CREATE DATABASE board;"
     ```
   - As credenciais padrão são:
     - Usuário: `root`
     - Senha: `818283`
   - Caso utilize credenciais diferentes, edite os seguintes arquivos:
     - `src/main/java/br/com/dio/persistence/config/ConnectionConfig.java`
     - `src/main/resources/liquibase.properties`

3. **Dependências**:
   - O Gradle Wrapper baixa automaticamente todas as dependências definidas em `build.gradle.kts` ao executar o projeto pela primeira vez.

## Executando o Projeto
Existem duas formas principais de compilar e executar a aplicação:

1. **Compilar e Rodar com Gradle**:
   ```bash
   ./gradlew clean build
   ./gradlew run
   ```
   - O Liquibase aplicará as migrações de banco de dados automaticamente ao iniciar a aplicação.

2. **Executar no IntelliJ IDEA**:
   - Abra o projeto no IntelliJ IDEA.
   - Configure o JDK 17 em `File > Project Structure > SDKs`.
   - Clique com o botão direito em `src/main/java/br/com/dio/Main.java` e selecione `Run` ou `Debug`.

## Estrutura do Projeto
A estrutura de diretórios do projeto é organizada da seguinte forma:
```
gerenciador-boards-kanban/
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
│   │   │   ├── service/          # Serviços com a lógica de negócio
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
A aplicação oferece as seguintes funcionalidades principais:
- **Criação de Boards**: Crie novos boards com nome e colunas personalizáveis.
- **Gerenciamento de Colunas**: Suporta colunas do tipo `INITIAL`, `FINAL`, `CANCEL` e `PENDING`.
- **Gestão de Cartões**: 
  - Crie cartões com título e descrição.
  - Mova cartões entre colunas.
  - Bloqueie ou desbloqueie cartões com motivo.
  - Cancele cartões movendo-os para a coluna de cancelamento.
- **Persistência de Dados**: Todas as operações são salvas em um banco MySQL com transações seguras.

## Exemplo de Uso
1. Inicie a aplicação:
   ```
   Bem-vindo ao gerenciador de boards Kanban!
   Escolha uma opção:
   1 - Criar um novo board
   2 - Selecionar um board existente
   3 - Excluir um board
   4 - Sair
   ```
2. Escolha a opção `1` para criar um novo board:
   - Digite o nome do board (ex.: "Projeto X").
   - Configure as colunas iniciais (ex.: "To Do", "In Progress", "Done").
3. Após criar o board, adicione cartões e gerencie-os utilizando as opções disponíveis.

## Contribuição
Contribuições são bem-vindas! Para contribuir:
- Abra issues ou envie pull requests no repositório.
- Antes de submeter alterações, execute:
  ```bash
  ./gradlew build
  ```
- Certifique-se de que o build passe sem erros.

## Licença
Este projeto foi desenvolvido para fins educacionais como parte do desafio Decola Tech 2025 e não possui uma licença formal.
