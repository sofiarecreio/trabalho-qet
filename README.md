# Trabalho Prático - Qualidade e Teste de Software

> **Disciplina:** Qualidade e Teste de Software  
> **Sistemas sob teste (SUTs):** [Chesslib](https://github.com/bhlangonijr/chesslib) e [Spring PetClinic](https://github.com/spring-projects/spring-petclinic)  
> **Repositório do grupo:** [sofiarecreio/trabalho-qet](https://github.com/sofiarecreio/trabalho-qet)  
> **Branch de entrega:** [`main`](https://github.com/sofiarecreio/trabalho-qet/tree/main)

Este repositório reúne os códigos-fonte dos dois projetos adotados, testes unitários e de integração do Chesslib, testes manuais e de sistema com Selenium no Spring PetClinic, medições de cobertura e mutação, inspeção de código e evidências. Os resultados históricos da Entrega 1 e os resultados disponíveis da Entrega 2 estão identificados separadamente.


---

## Equipe e responsabilidades da Entrega 1

| Integrante | Classe testada no Chesslib | Teste manual no Spring PetClinic |
|---|---|---|
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

O [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) é uma aplicação web para gerenciamento de uma clínica veterinária. O sistema permite buscar e cadastrar proprietários, cadastrar e alterar animais e registrar visitas. No trabalho, os testes funcionais manuais do TestLink são complementados por testes de integração e de sistema com Selenium na Entrega 2.

- [Código-fonte preservado no repositório do grupo](spring-petclinic/)
- [Código de produção](spring-petclinic/src/main/java/)
- [Configuração Maven](spring-petclinic/pom.xml)
- [Repositório original](https://github.com/spring-projects/spring-petclinic)

---

## Índice de artefatos

### Documentação geral

- **Plano de Teste editável:** [documento compartilhado](https://docs.google.com/document/d/1JDJVJR6WbpOXpzfRQsbcVWZm8XzQYc8xMSGoEAKj0xo/edit?tab=t.0); atualização final da Entrega 2 a consolidar pela equipe.
- **Escopo dos sistemas:** descrito no Plano de Teste e resumido neste README.
- **Registro de uso de IA:** [`docs/ai/AI-LOG.md`](docs/ai/AI-LOG.md).
- **Entrega 2 — Pedro:** [índice dos artefatos e situação de integração](docs/entrega2/pedro/README.md).
- **TimeControl — Pedro:** [testes, cobertura, mutação e comandos](docs/entrega2/pedro/timecontrol-testes.md), com evidências XML da medição local.
- **Consolidação das cinco classes:** [tabela, estados do PIT, comandos e pendências](docs/entrega2/consolidacao/README.md).
- **Inspeção PgnHolder — Pedro:** [correções e capturas antes/depois](docs/inspecao/pgnholder/README.md).
- **Selenium de alteração de animal — Pedro:** [nove cenários e capturas](docs/selenium/alterar-animal/README.md).
- **Selenium de cadastro de proprietário — Sofia:** [oito cenários, execução e capturas](docs/selenium/cadastrar-proprietario/README.md).
- **Organização dos testes manuais:** [`docs/teste-manual/README.md`](docs/teste-manual/README.md).
- **Inspeção compartilhada:** [relatório Sonar](https://docs.google.com/document/d/1LBd-p5gI73bchdHi-wWXTSUu7rfhmelV1IxTPfpdvk8/edit?tab=t.0).
- **Testes e integração de Game:** [relatório técnico de Weslley](docs/entrega2/weslley/game-testes-integracao.md).
- **Inspeção de Game:** [relatório, achados e resultados](docs/inspecao/game/relatorio.md), [PNG antes](docs/inspecao/game/sonar-antes.png) e [PNG depois](docs/inspecao/game/sonar-depois.png).
- **Selenium de visitas:** [nove cenários, execução e capturas](docs/selenium/visitas/README.md).
- **Artefatos de Lucas já disponíveis:** [cobertura](docs/entrega2/lucas/movegenerator-cobertura.md), [mutação](docs/entrega2/lucas/mutacao-movegenerator.md), [correções de MoveGenerator](docs/entrega2/lucas/qualidade-movegenerator.md), [integração PetClinic](docs/entrega2/lucas/integracao-petclinic.md) e [Selenium de busca](docs/entrega2/lucas/selenium-petclinic.md).
- **Avaliação ISO/IEC 25010:** consolidação pendente pela equipe; os dados medidos de Weslley estão no relatório técnico acima.
- **Slides da apresentação:** link pendente de inclusão pela coordenação da documentação.


---

## Entrega 1 - Testes unitários do Chesslib

Os testes desenvolvidos pelo grupo utilizam JUnit 4. Na execução inicial da Entrega 1, os cinco arquivos possuíam **43 casos de teste**: **41 aprovados** e **dois que reproduziam defeitos da classe `Game`**, registrados nas Issues [#3](https://github.com/sofiarecreio/trabalho-qet/issues/3) e [#4](https://github.com/sofiarecreio/trabalho-qet/issues/4). A tabela preserva esse registro histórico. Os dois defeitos foram corrigidos posteriormente e as issues estão fechadas; os resultados atuais estão na seção da Entrega 2.

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

Os erros reproduzidos pelo grupo foram cadastrados no [GitHub Issues](https://github.com/sofiarecreio/trabalho-qet/issues?q=is%3Aissue). As Issues #3, #4 e #6 estão fechadas, com correções integradas à `main` pelo [PR #13](https://github.com/sofiarecreio/trabalho-qet/pull/13). Os testes e evidências posteriores de Weslley entraram pelo [PR #16](https://github.com/sofiarecreio/trabalho-qet/pull/16).

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

- Java: alvo 11 no Chesslib e mínimo 17 no PetClinic; medições aqui registradas executadas com JDK 21;
- Maven;
- Eclipse IDE;
- JUnit 4 no Chesslib e JUnit Jupiter no PetClinic;
- JaCoCo e PIT para cobertura e mutação;
- Selenium e Chrome para testes de sistema;
- SonarQube para inspeção de código;
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

Para executar as cinco suítes de `Game` (89 casos na versão registrada):

```bash
mvn "-Dtest=GameTest,GameIsolatedTest,GameProcessingTest,GameIntegrationTest,GameQualityTest" test
```

Para executar a regressão completa e verificar o mínimo de 80% de branches de `Game`:

```bash
mvn -Pgame-quality "-Dmaven.javadoc.skip=true" clean verify
```

Para executar a mutação da mesma classe com mínimo configurado de 80%:

```bash
mvn -Pgame-quality "-Djacoco.skip=true" test-compile org.pitest:pitest-maven:1.17.4:mutationCoverage
```

O perfil `game-quality` é opcional e preserva o padrão de `MoveGenerator` disponibilizado por Lucas. Os relatórios de Game são gerados em `chesslib/target/site/jacoco-game/` e `chesslib/target/pit-reports-game/`. Os comandos acima partem do diretório `chesslib`.

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

Para executar os nove cenários de visitas a partir da raiz do repositório, com Chrome instalado e ChromeDriver localizado pelo Selenium Manager:

```bash
mvn -B -f spring-petclinic/pom.xml "-Dtest=WeslleyVisitSeleniumTests" test
```

Para conferir conjuntamente os testes de visitas, os testes Selenium/integração de Lucas e o controlador de visitas:

```bash
mvn -B -f spring-petclinic/pom.xml "-Dtest=WeslleyVisitSeleniumTests,LucasOwnerSeleniumTests,LucasOwnerIntegrationTests,VisitControllerTests" test
```

Esses testes iniciam a aplicação automaticamente com H2 e porta aleatória. As capturas e seus resultados estão no [relatório de Selenium de visitas](docs/selenium/visitas/README.md).

---

## Entrega 2 — resultados disponíveis e consolidação

Atualização: 22/11/2026. A contribuição de Weslley está integrada à `main` pelo PR #16, commit `f2f875e`. A conclusão da entrega do grupo depende dos resultados e confirmações dos demais responsáveis.

### Resultados de Weslley

| Item | Resultado verificado | Evidência |
| --- | --- | --- |
| Suítes de Game | 89 casos em cinco arquivos; a regressão completa do Chesslib teve 307 aprovados | [Testes e integração](docs/entrega2/weslley/game-testes-integracao.md) |
| Isolamento | 18 casos de navegação com stub de Board | [GameIsolatedTest](chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameIsolatedTest.java) |
| Integração Chesslib | 12 cenários de arquivo PGN com componentes reais | [Catálogo e comandos](docs/entrega2/weslley/game-testes-integracao.md#integração-do-chesslib) |
| Cobertura de Game | 166/179 branches, **92,74%** | [Medição e limites](docs/entrega2/weslley/game-testes-integracao.md#cobertura-e-mutação) |
| Mutação de Game | 203/213 mortos, **95,31%**; sete sobreviventes e três sem cobertura | [Resultado PIT](docs/inspecao/game/relatorio.md#validação-após-as-correções) |
| Inspeção Game | **16 → 0 apontamentos**, mesmo perfil de 608 regras | [Relatório e PNGs](docs/inspecao/game/relatorio.md) |
| Selenium de visitas | **9 casos aprovados**; seleção conjunta de 18 testes aprovada | [Cenários e capturas](docs/selenium/visitas/README.md) |

Os 307 testes do Chesslib incluem os testes originais e dos demais integrantes. Os 18 do PetClinic correspondem à seleção indicada no comando, e não à suíte inteira da aplicação. O critério todas-arestas é medido pelo contador de branches do JaCoCo, como apresentado na aula de cobertura com EclEmma/JaCoCo.

- [x] Ampliar e executar os testes de Game e a integração do Chesslib.
- [x] Executar PIT de Game acima de 80%, mantendo todos os mutantes no denominador.
- [x] Corrigir os apontamentos Sonar de Game e preservar capturas antes/depois.
- [x] Executar Selenium de visitas e versionar cenários, comandos e capturas.
- [x] Integrar o código e as evidências de Weslley à main.
- [x] Comprovar o critério todas-arestas em Game: 166/179 branches (92,74%), acima da meta de 80%.
- [x] Realizar a revisão individual dos testes, métricas e decisões.
- [ ] Preparar a explicação da própria contribuição para a apresentação.

### Resultados de Pedro

Os resultados abaixo correspondem à validação da contribuição de Pedro; a integração na main deve ser confirmada pelo responsável pelo merge:

| Item | Resultado | Evidência |
| --- | --- | --- |
| TimeControl | 23 testes; 40/42 branches (95,24%); 60/69 mutantes mortos (86,96%) | [Relatório](docs/entrega2/pedro/timecontrol-testes.md) |
| PgnHolder | 10 para 0 apontamentos no mesmo perfil Sonar; oito testes de regressão adicionais | [Inspeção](docs/inspecao/pgnholder/README.md) |
| Regressão Chesslib | 341 aprovados | [Escopo e validação](docs/inspecao/pgnholder/README.md#compatibilidade-e-validação) |
| Edição de animal | Nove cenários Selenium aprovados; seleção conjunta de 42 testes aprovada | [Selenium](docs/selenium/alterar-animal/README.md) |
| Consolidação | Cinco classes medidas; MoveList revalidada em 80,09% após o PR #24; GameLoader revalidada localmente em 93,65% na contribuição de Sofia abaixo | [Tabela e dados originais](docs/entrega2/consolidacao/README.md) |

A seleção do PetClinic não corresponde à suíte inteira. A seção de PgnHolder no relatório Sonar compartilhado está atualizada. A seção 6 do [Plano de Teste](https://docs.google.com/document/d/1JDJVJR6WbpOXpzfRQsbcVWZm8XzQYc8xMSGoEAKj0xo/edit?tab=t.0) registra o escopo, a estratégia, os critérios e os resultados da contribuição de Pedro na Entrega 2. Os dados para a avaliação ISO estão [disponíveis nesta contribuição](docs/entrega2/pedro/dados-documentacao.md). As correções de PgnHolder estão registradas nas Issues [#21](https://github.com/sofiarecreio/trabalho-qet/issues/21) e [#22](https://github.com/sofiarecreio/trabalho-qet/issues/22).

### Pendências de consolidação do grupo

Resultado local de Sofia em 22/11/2026: `GameLoaderTest` ampliado de 7 para 14 casos; PIT de 36/63 (57,14%) para **59/63 (93,65%)**; cobertura de **58/66 branches (87,88%)** na regressão completa, com **349 testes aprovados**. [Relatório, comandos, XMLs antes/depois e texto para a issue](docs/entrega2/sofia/gameloader/README.md). A publicação e a integração desta contribuição na main ainda devem ser confirmadas; este resultado não conclui as demais tarefas de Sofia.

Selenium de cadastro de proprietário de Sofia, execução local em 23/11/2026: **8 casos aprovados** (cadastro válido com busca posterior, nome no limite de 30 caracteres, quatro telefones inválidos, formulário vazio e campos só com espaços); seleção conjunta com os demais Selenium do grupo e `OwnerControllerTests` com **43 testes aprovados**. [Cenários, comandos e capturas](docs/selenium/cadastrar-proprietario/README.md). A integração na main ainda deve ser confirmada.

| Responsável | Próxima consolidação | Situação documentada |
| --- | --- | --- |
| Pedro | Consolidar métricas e publicar sua contribuição | [Tabela preparada](docs/entrega2/consolidacao/README.md); resultados e limites documentados; integração pelo responsável do grupo |
| Lucio | Relatório final de inspeção com os resultados das cinco classes | MoveList já descrita no documento compartilhado; Game preenchida; demais resultados finais a confirmar |
| Lucas | Consolidação das funcionalidades Selenium e confirmação dos resultados finais de sua classe | Artefatos de MoveGenerator, integração e busca de proprietário já disponíveis nos links acima |
| Sofia | ISO/IEC 25010, Plano de Teste, índice de evidências e slides | Consolidação e links finais pendentes |
| Todos | Entregar seus próprios resultados, evidências e revisão individual | Esta atualização não declara concluídas as partes ainda sem confirmação |

Os dados medidos que Weslley pode fornecer à consolidação estão reunidos no [relatório técnico](docs/entrega2/weslley/game-testes-integracao.md). A avaliação ISO 25010 ainda precisa de atributos, medidas, escala, resultados e justificativas consolidados pela equipe.

---

## Licenças e créditos

O código do Chesslib é distribuído sob a licença Apache 2.0 e pertence aos respectivos autores. O Spring PetClinic também mantém sua licença e seus créditos originais. Os testes, relatórios e documentos adicionados neste repositório foram produzidos para fins acadêmicos na disciplina de Qualidade e Teste de Software.
