# Consolidação de complexidade, cobertura e mutação

Responsável pela consolidação: Pedro Piaes. Revalidação das cinco classes em 23/11/2026, sobre a main `3934419`, com OpenJDK 21.0.11. Cada classe foi medida separadamente pelo perfil correspondente; todos os comandos de cobertura e mutação concluíram com sucesso. Os hashes das fontes estão em [metricas.json](metricas.json), e os comandos e códigos de saída estão em [execucoes.json](execucoes.json).

## Resultados das cinco classes

| Responsável | Classe | Complexidade agregada JaCoCo | Branches | Mutantes mortos / total | Situação nas metas de 80% |
| --- | --- | ---: | ---: | ---: | --- |
| Sofia | GameLoader | 60 | 63/68 = 92,65% | 76/77 = 98,70% | Atende |
| Weslley | Game | 177 | 166/179 = 92,74% | 203/213 = 95,31% | Atende |
| Lucio | MoveList | 153 | 159/196 = 81,12% | 169/211 = 80,09% | Atende |
| Pedro | TimeControl | 41 | 40/42 = 95,24% | 60/69 = 86,96% | Atende |
| Lucas | MoveGenerator | 55 | 67/68 = 98,53% | 64/78 = 82,05% | Atende numericamente; ocorrências na execução |

## Estados do PIT

| Classe | KILLED | SURVIVED | NO_COVERAGE | MEMORY_ERROR | Total |
| --- | ---: | ---: | ---: | ---: | ---: |
| GameLoader | 76 | 1 | 0 | 0 | 77 |
| Game | 203 | 7 | 3 | 0 | 213 |
| MoveList | 169 | 16 | 26 | 0 | 211 |
| TimeControl | 60 | 7 | 2 | 0 | 69 |
| MoveGenerator | 64 | 3 | 5 | 6 | 78 |

## Critérios e decisões

- Cobertura: contador BRANCH do JaCoCo, obtido pela regressão completa de 361 testes em cada execução de cobertura desta revalidação. Não é cobertura de linhas. A associação acadêmica com todas-arestas segue a convenção do grupo e não inclui todas as arestas excepcionais de um grafo de fluxo.
- Mutação estrita da tabela: somente KILLED dividido por todos os mutantes gerados. Sobreviventes, ausência de cobertura e erros de execução ficam no denominador.
- MoveGenerator registrou 64 KILLED, 3 SURVIVED, 5 NO_COVERAGE e 6 MEMORY_ERROR nesta revalidação. O PIT apresenta 90% porque considera os erros como detecções; a fração estrita permanece 64/78 = 82,05%. Erros de execução são informados separadamente, sem serem apresentados como mortes por asserção. A rodada anterior classificou essas seis ocorrências como quatro erros de memória e dois timeouts; não se afirma que o problema de execução foi resolvido.
- MoveList passou de 168/211 = 79,62% para 169/211 = 80,09% após o novo cenário `testConversaoFanSemNumeracao` de Lucio, integrado no PR #24. A meta exata de 80% está atendida, com 16 sobreviventes e 26 mutantes sem cobertura. O arredondamento da medição anterior não é usado como aprovação.
- GameLoader foi revalidada após as correções e os testes isolados de Sofia: 63/68 branches (92,65%) e 76/77 mutantes eliminados (98,70%). Os 36/63 (57,14%) da medição inicial e 59/63 (93,65%) da etapa intermediária são históricos e não representam o resultado atual. Consulte o [relatório de isolamento](../sofia/gameloader/isolamento.md).
- A complexidade é a soma dos contadores COMPLEXITY da classe no JaCoCo, incluindo contribuição dos métodos. Não é complexidade cognitiva do Sonar nem complexidade de um único método. O maior método, pela mesma ferramenta, está no JSON para evitar confundir as medidas.

## Execução e configuração

Ambiente desta medição: OpenJDK 21.0.11, Maven 3.9.15, JaCoCo 0.8.12 e PIT 1.17.4. Selecionar JDK 21 e confirmar a JVM em `mvn -version` antes dos comandos.

| Classe | Perfil Maven | Testes selecionados pelo PIT |
| --- | --- | --- |
| GameLoader | `gameloader-quality` | `GameLoaderTest`, `GameLoaderIsolatedTest` |
| Game | `game-quality` | `game.Game*Test` |
| MoveList | `movelist-quality` | `MoveListTest`, `MoveListQetTest` |
| TimeControl | `timecontrol-quality` | `PedroTimeControlTest` |
| MoveGenerator | padrão, sem perfil | `MoveGeneratorTest`, `LucasMoveGeneratorTest` |

Cada perfil possui filtro de classe, meta de 80% de branches, meta de 80% no PIT e diretório próprio. A configuração padrão e os perfis existentes foram preservados. Os perfis novos são `timecontrol-quality` e `gameloader-quality`.

Exemplo para TimeControl, a partir da raiz:

```sh
mvn -B -f chesslib/pom.xml -Ptimecontrol-quality -Dmaven.javadoc.skip=true verify
mvn -B -f chesslib/pom.xml -Ptimecontrol-quality -Djacoco.skip=true test-compile org.pitest:pitest-maven:1.17.4:mutationCoverage
```

Para repetir as cinco classes:

```sh
bash scripts/medir-qualidade.sh
```

Antes de cada etapa, o script remove somente os XML e os dados instrumentados gerados anteriormente para aquela medição, evitando reaproveitar relatórios antigos se o comando falhar. Os logs e os códigos de saída são preservados em `logs/` e `execucoes.json`.

O script prossegue após uma reprovação para medir as demais classes, mas termina com código de falha se qualquer comando falhar ou se uma classe não alcançar 80% exatos de branches ou mutantes mortos. A mensagem de conclusão de um comando não substitui essa verificação. Não usar vários perfis de classe simultaneamente nem executar esses comandos em paralelo no mesmo diretório `target`.

Para consolidar relatórios já gerados:

```sh
python3 scripts/consolidar-metricas.py --java-version 21.0.11 --check-thresholds
```

## Evidências e limites

Nos logs publicados, os caminhos absolutos locais foram normalizados: `<REPOSITORIO>` representa a raiz do projeto e `<HOME>` representa o diretório pessoal. Espaços em branco no fim das linhas foram removidos. As mensagens, resultados e horários das execuções foram preservados.

[Estados dos comandos desta revalidação](execucoes.json). Os logs e XMLs das cinco classes foram atualizados sobre a mesma base. As rodadas anteriores permanecem no histórico do repositório. As saídas XML completas foram copiadas para `evidencias/<classe>/jacoco.xml` e `evidencias/<classe>/mutations.xml` e permitem verificar números e localizações sem depender de arquivos temporários em target. A execução individual de TimeControl está descrita no [relatório de Pedro](../pedro/timecontrol-testes.md).

As tabelas correspondem ao código local identificado pelos hashes. Após novas contribuições ou alterações nas classes, repetir as medições antes de apresentar os números como resultados finais da main. O relatório não declara concluídas as metas que os dados ainda não comprovam.
