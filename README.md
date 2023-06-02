# API REST - LABSky
- API REST de uma aplicação para gerenciar os passageiros de um voo da empresa LABSky Linhas Aéreas

## Tecnologias utilizadas
- Java 17
- Spring Framework 3.1.0
- Banco de Dados H2

## Dependências do Spring
- Lombok

## Collection Postman
- Os arquivos utilizados nas requisições HTTP estão na raiz do projeto, dentro do diretório postman

## Como executar
- Clonar o repositório para a sua máquina
- Baixar e instalar o IntelliJ IDEA ou outra IDE de sua preferência
- Baixar e instalar o Postman
- Baixar a collection do Postman
- Executar a API REST através da IDE
- Os dados iniciar serão carregados na memória do banco de dados H2
- Executar as requisições no Postman

## Banco de Dados H2
- O H2 é um Banco de Dados em memória e a cada re-deploy da aplicação, o Banco de Dados também é reiniciado e os dados armazenados são perdidos
- É possível acessar o seu cliente SQL nativo, em qualquer navegador, através da URL http://localhost:8080/h2-console, informando a JDBC URL exibida no console da IDE e utilizando o username sa

## Funcionalidades
- Carregamento de dados iniciais
    - Pacientes
      - CPF (String)
      - Nome (String)
      - Data de nascimento (LocalDate)
      - Classificação (Enum)
      - Milhas (Integer)

- Cadastros
    - Confirmação de voo (check-in)

- Listagens
    - Listagem de passageiro por CPF
    - Listagem de todos os passageiros
    - Listagem de todos os assentos da aeronave

- Validações
    - Impede o check-in de passageiros em um assento já ocupado por outro passageiro
    - Impede o check-in de passageiros menores de idade sentem nos assentos de emergência (5 e 6)
    - Impede o check-in de passageiros que não fizeram o despache das malas nos assentos de emergência (5 e 6)
    - Informa no body da requisição quando não há passageiro cadastrado com o CPF informado
    - Informa no body da requisição quando o assento informado não existe na aeronave
    - Incrementa as milhas do passageiro de acordo com a sua classificação no plano de fidelidade da empresa
  
- Testes Unitários
  - Implementação de testes unitários nas camadas controller e service

- Exceptions
    - Tratador de erros:
        - Erro 400 (Bad Request)
        - Erro 404 (Not Found)
        - Erro 409 (Conflict)

- Records
    - Substituição de classes DTO pelos records, introduzidos de maneira experimental na versão 14 do Java e liberadas de forma oficial na versão 16
