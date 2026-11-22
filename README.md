# Trabalho Prático - Qualidade e Teste de Software

> **Disciplina:** Qualidade e Teste de Software  
> **Sistemas sob teste (SUTs):** [Chesslib](https://github.com/bhlangonijr/chesslib) e [Spring PetClinic](https://github.com/spring-projects/spring-petclinic)  
> **Repositório do grupo:** [sofiarecreio/trabalho-qet](https://github.com/sofiarecreio/trabalho-qet)  
> **Branch de entrega:** [`main`](https://github.com/sofiarecreio/trabalho-qet/tree/main)

Este repositório reúne os códigos-fonte dos dois projetos adotados, os testes unitários desenvolvidos pelo grupo, os testes funcionais manuais executados no Spring PetClinic, os relatórios do TestLink, as evidências, os defeitos registrados e o histórico de uso de Inteligência Artificial.


---

## Equipe e responsabilidades

| Integrante | Classe testada no Chesslib | Teste manual no Spring PetClinic |
|---|---|---|---|
| **Sofia Recreio** | `GameLoader` | CTM-02 - Cadastrar proprietário |
| **Weslley Ribeiro** | `Game` | CTM-05 - Registro de visita veterinária | 
| **Lucio** | `MoveList` | CTM-03 - Cadastrar novo animal | 
| **Pedro Piaes** | `TimeControl` | CTM-04 - Alterar informações do animal | 
| **Lucas** | `MoveGenerator` | CTM-01 - Buscar proprietário | 

Além das responsabilidades individuais, todos os integrantes participam da revisão dos artefatos, das pull requests, do Plano de Teste e da apresentação.

---

## Visão geral dos sistemas

### Chesslib

O [Chesslib](https://github.com/bhlangonijr/chesslib) é uma biblioteca Java para representação de partidas de xadrez. O projeto possui lógica de geração e conversão de movimentos, manipulação do tabuleiro, leitura de PGN, representação FEN e validação de regras do jogo. No trabalho, o Chesslib é utilizado principalmente para os testes unitários de classes não CRUD com lógica de decisão relevante.

- [Código-fonte preservado no repositório do grupo](chesslib/)
- [Código de produção](chesslib/src/main/java/)
- [Diretório de testes](chesslib/src/test/java/)
- [Configuração Maven](chesslib/pom.xml)
- [Repositório original](https://github.com/bhlangonijr/chesslib)

### Spring PetClinic

O [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) é uma aplicação web para gerenciamento de uma clínica veterinária. O sistema permite buscar e cadastrar proprietários, cadastrar e alterar animais e registrar visitas. No trabalho, o PetClinic é utilizado principalmente para os testes funcionais manuais gerenciados no TestLink.

- [Código-fonte preservado no repositório do grupo](spring-petclinic/)
- [Código de produção](spring-petclinic/src/main/java/)
- [Configuração Maven](spring-petclinic/pom.xml)
- [Repositório original](https://github.com/spring-projects/spring-petclinic)

---

## Índice de artefatos

### Documentação geral

- **Plano de Teste editável:** **https://docs.google.com/document/d/1JDJVJR6WbpOXpzfRQsbcVWZm8XzQYc8xMSGoEAKj0xo/edit?tab=t.0**.
- **Escopo dos sistemas:** descrito no Plano de Teste e resumido neste README.
- **Registro de uso de IA:** [`docs/ai/AI-LOG.md`](docs/ai/AI-LOG.md).
- **Organização dos testes manuais:** [`docs/teste-manual/README.md`](docs/teste-manual/README.md).
- **Slides da apresentação:** .


---

## Entrega 1 - Testes unitários do Chesslib

Os testes desenvolvidos pelo grupo utilizam JUnit 4. Ao todo, os cinco arquivos possuem **43 casos de teste**: **41 aprovados** e **dois que reproduzem defeitos conhecidos da classe `Game`**, registrados nas Issues [#3](https://github.com/sofiarecreio/trabalho-qet/issues/3) e [#4](https://github.com/sofiarecreio/trabalho-qet/issues/4).

| Responsável | Classe | Arquivo de teste | Casos | Resultado registrado |
|---|---|---|---:|---|
| Sofia | `GameLoader` | [`GameLoaderTest.java`](chesslib/src/test/java/com/github/bhlangonijr/chesslib/GameLoaderTest.java) | 7 | 7 aprovados |
| Weslley | `Game` | [`GameTest.java`](chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameTest.java) | 18 | 16 aprovados e 2 falhas que reproduzem defeitos |
| Lucio | `MoveList` | [`MoveListQetTest.java`](chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/MoveListQetTest.java) | 8 | 8 aprovados |
| Pedro | `TimeControl` | [`PedroTimeControlTest.java`](chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/PedroTimeControlTest.java) | 5 | 5 aprovados |
| Lucas | `MoveGenerator` | [`LucasMoveGeneratorTest.java`](chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/LucasMoveGeneratorTest.java) | 5 | 5 aprovados |

---

## Entrega 1 - Testes manuais do Spring PetClinic

Os testes manuais foram organizados por caso em [`docs/teste-manual/`](docs/teste-manual/). Os documentos exportados do TestLink e as capturas de tela estão disponíveis nos links abaixo.

### CTM-01 - Buscar proprietário

**Responsável:** Lucas  
**Resultado:** passou - cinco passos aprovados na build `PETCLINIC-MAIN-01`.

- [Relatório de execução exportado do TestLink](docs/teste-manual/CTM-01-buscar-proprietario/relatorio-teste-manual.pdf)

### CTM-03 - Cadastrar novo animal

**Responsável:** Lucio  
**Cenário:** cadastrar o animal `mel`, do tipo `dog`, para o proprietário `lucio teste`.

- [Especificação exportada do TestLink](docs/teste-manual/CTM-03-cadastrar-animal/caso-teste-testlink.pdf)
- [Evidência - formulário de cadastro](docs/teste-manual/CTM-03-cadastrar-animal/evidencias/formulario-cadastro.png)
- [Evidência - animal cadastrado](docs/teste-manual/CTM-03-cadastrar-animal/evidencias/animal-cadastrado.png)

> O PDF atualmente armazenado é um relatório de **design** do plano de testes. Antes da entrega, recomenda-se adicionar também o relatório de **execução** do CTM-03, mostrando executor, build e estado final.

### CTM-04 - Alterar informações do animal

**Responsável:** Pedro  
**Resultado:** passou - cinco passos aprovados na build `PETCLINIC-MAIN-01`.

- [Relatório de execução exportado do TestLink](docs/teste-manual/CTM-04-alterar-informacoes-animal/relatorio-teste-manual.pdf)

### Registro de visita veterinária

**Responsável:** Weslley  
**Resultado:** seis cenários executados; cinco passaram e um falhou ao testar o limite do campo de descrição.

- [Relatório de execução exportado do TestLink](docs/teste-manual/CTM-05-registrar-visita/relatorio-teste-manual.pdf)
- [Evidência do erro com descrição de 256 caracteres](docs/teste-manual/CTM-05-registrar-visita/evidencias/erro-500-descricao-256-caracteres.png)
- [Issue #6 - erro HTTP 500 acima de 255 caracteres](https://github.com/sofiarecreio/trabalho-qet/issues/6)

---

## Defeitos registrados

Os erros reproduzidos pelo grupo foram cadastrados no [GitHub Issues](https://github.com/sofiarecreio/trabalho-qet/issues?q=is%3Aissue).

| Issue | Sistema | Defeito | Evidência relacionada |
|---|---|---|---|
| [#3](https://github.com/sofiarecreio/trabalho-qet/issues/3) | Chesslib | `Game.gotoLast(MoveList)` utiliza o tamanho da lista principal | [`GameTest.java`](chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameTest.java) |
| [#4](https://github.com/sofiarecreio/trabalho-qet/issues/4) | Chesslib | `Game.isStartOfMoveList()` retorna `false` na primeira jogada | [`GameTest.java`](chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameTest.java) |
| [#6](https://github.com/sofiarecreio/trabalho-qet/issues/6) | PetClinic | Descrição com 256 caracteres causa HTTP 500 em vez de validação | [Captura do erro](docs/teste-manual/CTM-05-registrar-visita/evidencias/erro-500-descricao-256-caracteres.png) |

---

## Registro do uso de Inteligência Artificial

O uso relevante de IA está documentado em [`docs/ai/AI-LOG.md`](docs/ai/AI-LOG.md), incluindo responsável, atividade, ferramenta, prompt, resultado, decisão e validação.

---

## Ferramentas utilizadas

- Java e JDK 17;
- Maven;
- Eclipse IDE;
- JUnit 4;
- Spring Boot;
- TestLink;
- Git e GitHub;
- GitHub Issues e Pull Requests;
- Google Docs para documentação colaborativa;
- ChatGPT como ferramenta de IA generativa, com uso registrado e validado.

---

## Como executar

### Chesslib

Pré-requisitos: JDK 17 e Maven.

```bash
cd chesslib
mvn clean test
```

Para executar somente as classes cujos casos estão aprovados:

```bash
mvn -Dtest=GameLoaderTest,MoveListQetTest,PedroTimeControlTest,LucasMoveGeneratorTest test
```

Para executar a classe `GameTest`:

```bash
mvn -Dtest=GameTest test
```

Na versão atual, dois casos de `GameTest` reproduzem os defeitos das Issues #3 e #4; portanto, essa execução registra duas falhas até que o código de produção seja corrigido.

### Spring PetClinic

Pré-requisito: JDK 17 ou superior.

Linux/macOS:

```bash
cd spring-petclinic
./mvnw spring-boot:run
```

Windows:

```bat
cd spring-petclinic
mvnw.cmd spring-boot:run
```

Após a inicialização, acessar <http://localhost:8080/>.

---

## Entrega 2 - planejamento

As atividades abaixo pertencem à segunda entrega e ainda serão desenvolvidas ou ampliadas:

- [ ] melhorar e ampliar os testes unitários;
- [ ] isolar dependências com mocks, stubs ou outros dublês adequados;
- [ ] implementar testes de integração;
- [ ] avaliar os atributos de qualidade da ISO/IEC 25010;
- [ ] implementar testes de sistema com Selenium;
- [ ] atingir ao menos 80% de cobertura no critério todas-arestas nas classes selecionadas;
- [ ] atingir ao menos 80% de escore de mutação nas mesmas classes;
- [ ] executar inspeção de código com SonarQube ou SonarCloud;
- [ ] registrar evidências anteriores e posteriores às correções.

---

---

## Licenças e créditos

O código do Chesslib é distribuído sob a licença Apache 2.0 e pertence aos respectivos autores. O Spring PetClinic também mantém sua licença e seus créditos originais. Os testes, relatórios e documentos adicionados neste repositório foram produzidos para fins acadêmicos na disciplina de Qualidade e Teste de Software.
