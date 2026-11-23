# Consolidação de complexidade, cobertura e mutação

Responsável pela consolidação: Pedro Piaes. Medição local em 22/11/2026, sobre a base `4982c6e` com as alterações locais de Pedro; a atualização desde `4750daa` é apenas documental. Cada fonte está identificada por SHA-256 em [metricas.json](metricas.json).

## Resultados das cinco classes

| Responsável | Classe | Complexidade agregada JaCoCo | Branches | Mutantes mortos / total | Situação nas metas de 80% |
| --- | --- | ---: | ---: | ---: | --- |
| Sofia | GameLoader | 50 | 57/66 = 86,36% | 36/63 = 57,14% | Mutação abaixo de 80% |
| Weslley | Game | 177 | 166/179 = 92,74% | 203/213 = 95,31% | Atende |
| Lucio | MoveList | 153 | 159/196 = 81,12% | 168/211 = 79,62% | Mutação abaixo de 80% |
| Pedro | TimeControl | 41 | 40/42 = 95,24% | 60/69 = 86,96% | Atende |
| Lucas | MoveGenerator | 55 | 67/68 = 98,53% | 64/78 = 82,05% | Atende numericamente; ocorrências na execução |

## Estados do PIT

| Classe | KILLED | SURVIVED | NO_COVERAGE | MEMORY_ERROR | TIMED_OUT | Total |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| GameLoader | 36 | 13 | 14 | 0 | 0 | 63 |
| Game | 203 | 7 | 3 | 0 | 0 | 213 |
| MoveList | 168 | 17 | 26 | 0 | 0 | 211 |
| TimeControl | 60 | 7 | 2 | 0 | 0 | 69 |
| MoveGenerator | 64 | 3 | 5 | 4 | 2 | 78 |

## Critérios e decisões

- Cobertura: contador BRANCH do JaCoCo, obtido pela regressão completa de 341 testes. Não é cobertura de linhas. A associação acadêmica com todas-arestas segue a convenção do grupo e não inclui todas as arestas excepcionais de um grafo de fluxo.
- Mutação estrita da tabela: somente KILLED dividido por todos os mutantes gerados. Sobreviventes, ausência de cobertura e erros de execução ficam no denominador.
- O PIT de MoveGenerator informa 90% porque considera quatro MEMORY_ERROR e dois TIMED_OUT como detecções. O XML distingue 64 KILLED e essas seis ocorrências de execução: a fração estrita é 64/78 = 82,05%, ainda acima de 80%. Esses estados são apresentados separadamente e não como mortes por asserção. Na medição anterior, as mesmas seis ocorrências foram classificadas como MEMORY_ERROR; a variação entre erro de memória e timeout mostra por que o total KILLED é apresentado separadamente.
- MoveList tem 168/211 = 79,62%. O PIT aceita o limiar inteiro configurado e imprime 80%, mas o valor exato permanece abaixo de 80%. Com o mesmo total, 169 mortos corresponderiam a 80,09%.
- GameLoader tem 36/63 = 57,14%. O comando PIT reprovou corretamente o limiar de 80%. A consolidação preserva esse resultado e não altera os testes individuais.
- A complexidade é a soma dos contadores COMPLEXITY da classe no JaCoCo, incluindo contribuição dos métodos. Não é complexidade cognitiva do Sonar nem complexidade de um único método. O maior método, pela mesma ferramenta, está no JSON para evitar confundir as medidas.

## Execução e configuração

Ambiente desta medição: OpenJDK 21.0.11, Maven 3.9.15, JaCoCo 0.8.12 e PIT 1.17.4. Selecionar JDK 21 e confirmar a JVM em `mvn -version` antes dos comandos.

| Classe | Perfil Maven | Testes selecionados pelo PIT |
| --- | --- | --- |
| GameLoader | `gameloader-quality` | `GameLoaderTest` |
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

Nos logs publicados, os caminhos absolutos locais foram normalizados: `<REPOSITORIO>` representa a raiz do projeto e `<HOME>` representa o diretório pessoal. Essa substituição preserva as demais mensagens, resultados e horários das execuções.

[Estados dos comandos desta medição](execucoes.json). As saídas XML completas foram copiadas para `evidencias/<classe>/jacoco.xml` e `evidencias/<classe>/mutations.xml` e permitem verificar números e localizações sem depender de arquivos temporários em target. A execução individual de TimeControl está descrita no [relatório de Pedro](../pedro/timecontrol-testes.md).

As tabelas correspondem ao código local identificado pelos hashes. Após novas contribuições ou alterações nas classes, repetir as medições antes de apresentar os números como resultados finais da main. O relatório não declara concluídas as metas que os dados ainda não comprovam.
