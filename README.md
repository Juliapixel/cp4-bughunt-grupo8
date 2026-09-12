# Checkpoint 4 — Bug Hunt StreamFIAP

> Copie este arquivo para a raiz do seu repositório com o nome **README.md**
> e preencha todas as seções.

## Identificação

**Grupo:** ___

| Integrante | RM | Turma |
|---|---|---|
| Allan de Souza Cardoso | 561721 | 2CCPH |
| Eduardo Bacelar Rudner | 564925 | 2CCPH |
| Giovana Dias Valentini | 562390 | 2CCPH |
| Júlia Borges Paschoalinoto | 564725 | 2CCPH |
| Raquel Amaral de Oliveira | 566491 | 2CCPH |


| Campo | |
|---|---|
| **Total de bugs corrigidos** | ___ / 12 |
| **Total de ajustes de Clean Code** | ___ / 6 |

---

## Parte 1 — Bugs encontrados

> Uma linha por bug, na ordem em que você os encontrou. Use a numeração dos seus
> commits (`fix: bug01 ...`). Preencha TODAS as colunas — metade da nota está aqui.

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | | | | |
| bug02 | | | | |
| bug03 | | | | |
| bug04 | | | | |
| bug05 | | | | |
| bug06 | | | | |
| bug07 | | | | |
| bug08 | | | | |
| bug09 | | | | |
| bug10 | | | | |
| bug11 | | | | |
| bug12 | | | | |

## Parte 2 — Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que eu mudei |
|---|---|---|---|
| clean01 | | | |
| clean02 | | | |
| clean03 | | | |
| clean04 | | | |
| clean05 | | | |
| clean06 | | | |

---

## Parte 3 — Perguntas de reflexão

> Responda com suas palavras, 5 a 10 linhas cada, **usando o código real do projeto
> como exemplo**. Respostas genéricas de tutorial não pontuam.

### 1. Injeção de dependência (Aula 13)
Os controllers recebem os repositories via `@Autowired` (ex.: `ConteudoController`
usa `ConteudoRepository`). Explique por que o Spring precisa gerenciar esses objetos
em vez de criarmos com `new ConteudoRepository()`. O que exatamente o Spring faz ao
injetar um bean, e por que isso não funcionaria com um `new` comum?

R: No nosso projeto, o `ConteudoController` recebe o `ConteudoRepository` pelo `@Autowired`, em vez de criar um objeto com `new ConteudoRepository()`. Isso acontece porque o Spring precisa gerenciar o repository como um bean, sendo responsável por criar e disponibilizar essa instância para o controller. O `ConteudoRepository` estende `JpaRepository`, então o Spring Data JPA fornece recursos como `findAll()`, `findById()` e `save()`, além da integração com o banco de dados. Quando o Spring faz a injeção, ele coloca uma instância gerenciada do repository na variável `conteudoRepository`. Com um `new` comum, o objeto seria criado fora do controle do Spring e não teria essa configuração e integração automática com o contexto da aplicação. Por isso, no nosso código, o `@Autowired` permite que o controller apenas utilize o repository, sem precisar se preocupar em criá-lo ou configurá-lo.


### 2. JDBC vs Spring Data JPA (Aulas 12 e 13)
Na Aula 12 escrevemos um `ProdutoDAO` na mão com `Connection`, `PreparedStatement` e
`ResultSet`. Aqui o `ConteudoRepository` tem 2 linhas e faz CRUD completo. Compare as
duas abordagens: o que o Spring Data JPA automatiza, o que o JDBC/DAO ainda resolve
melhor, e como o `findByCategoria` consegue funcionar sem implementação.

R: No JDBC/DAO da Aula 12, nós precisávamos escrever manualmente o código para abrir a `Connection`, preparar o `PreparedStatement`, executar a consulta e ler os resultados pelo `ResultSet`. Já no nosso projeto, o `ConteudoRepository` estende `JpaRepository<Conteudo, Long>`, então o Spring Data JPA automatiza operações de CRUD como `findAll()`, `findById()` e `save()`, sem precisarmos escrever essas consultas manualmente. O JDBC/DAO ainda pode ser melhor quando precisamos ter um controle mais específico sobre a conexão, a consulta SQL ou operações muito personalizadas. O `findByCategoria` funciona sem implementação porque o Spring Data JPA interpreta o nome do método `findByCategoria` e cria automaticamente a consulta correspondente para buscar conteúdos pela propriedade `categoria`. Assim, conseguimos adicionar uma consulta específica escrevendo apenas a assinatura do método.


### 3. Exceções checked vs unchecked (Aula 11)
A `ClassificacaoIndicativaException` estourava como um erro genérico do servidor,
sem mensagem útil para o cliente. Explique a diferença entre `extends Exception` e
`extends RuntimeException` no contexto desse bug, e como você fez a mensagem da
regra (classificação indicativa) chegar de forma clara ao cliente da API.

R: `extends Exception` cria uma checked exception, o compilador obriga a tratar (try/catch ou throws), ideal para erros previsíveis de regra de negócio. O `extends RuntimeException` cria uma unchecked, usada para erros de programação, e se não tratada por um handler específico, cai no tratamento genérico e vira erro 500 sem mensagem clara. O bug era a `ClassificacaoIndicativaException` não ter um `@ExceptionHandler` próprio no `GlobalExceptionHandler`. A nossa correção foi capturar essa exceção específica e retornar um status com a mensagem da regra no corpo da resposta.


### 4. Sobrescrita vs sobrecarga (Aula 7)
Um dos bugs compilava sem nenhum erro: o método da `Serie` parecia sobrescrever
`calcularPrecoAluguel`, mas na verdade sobrecarregava. Explique a diferença entre
override e overload nesse caso e por que a anotação `@Override` teria impedido o bug.

R: `Sobrescrita (override)` é quando a subclasse redefine um método da mãe com exatamente a mesma assinatura (nome, parâmetros e tipo de retorno), mudando o comportamento em tempo de execução (polimorfismo). `Sobrecarga (overload)` é quando existem métodos com o mesmo nome mas assinaturas diferentes, tratados como métodos distintos. No bug, `Serie` declarou `calcularPrecoAluguel` com uma assinatura diferente da classe mãe, criando um método novo em vez de substituir o antigo. A anotação `@Override` teria impedido isso porque o compilador dá erro caso o método não corresponda exatamente a um método existente na superclasse.


### 5. Onde blindar o objeto? (Aulas 3, 4 e 13)
Vimos bugs de dados inválidos aceitos (duração negativa, créditos negativos, campos
nulos). Em quais lugares (construtor, setter, método do model) cada tipo de validação
deve ficar? Justifique usando os bugs que você encontrou e explique por que validar só
em um lugar não foi suficiente.

R: As validações dos dados devem ficar no próprio objeto principalmente no construtor e nos setters. No `Conteudo`, a duração é atribuida diretamente, permitindo valores negativos: `this,duracaoMinutos = diracaoMinutos; `

Em `Usuario`, os créditos também são atribuídos sem validação: `this.creditos = creditos;`

As regras do aluguel ficam no método `alugar()`, que verifica idade e créditos. Validar só no `controller` não é o suficiente, pois o objeto poderia ser criado ou alterado por putro caminho.

### 6. Abstração e interface (Aulas 8 e 9)
`Conteudo` é abstrata e `Promocionavel` é uma interface. Explique a diferença de
propósito entre as duas nesse projeto e o que mudaria no código se o Documentário
passasse a ter promoções — quais classes/linhas seriam tocadas e quais ficariam
intactas? O que isso diz sobre o design do sistema?

R: `Conteudo` é uma classeabstrata que reúne caracteríscas comuns aos conteúdos do projeto. `Promocionavel` define uma capacidade específica através do: `double aplicarPromocao(double preco);`

`Filme e Serie` implementam: `public class filme extends Conteudo implements Promocionavel public class serie extends Conteudo implements Promocionavel`

Se `Documentario` também tivesse promoções, seria necessário implementar promocionavel e crar `aplicarPromocao()`. A classe `Conteudo `poderia continuar intacta, mostrando a separação entre característcas comuns e comportamentos específicos.


---
