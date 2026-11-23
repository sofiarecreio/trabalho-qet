# GameLoader — melhoria dos testes de mutação

Responsável: Sofia. Validação local em 22/11/2026 sobre a base `30a5ed59aa28af3711f8d393508c590024cbfe8d`, com as alterações de testes desta contribuição. Ferramentas: Java 21.0.12, PIT 1.17.4 e JaCoCo 0.8.12.

## Problema e resultado

O log inicial terminou com `BUILD FAILURE` porque o escore ficou abaixo do limite configurado de 80%. A ferramenta executou normalmente; a falha era no critério de qualidade.

| Medida | Antes | Depois |
| --- | ---: | ---: |
| Testes em GameLoaderTest | 7 | 14 |
| KILLED | 36 | 59 |
| SURVIVED | 13 | 2 |
| NO_COVERAGE | 14 | 2 |
| Total de mutantes | 63 | 63 |
| Escore estrito (KILLED / total) | 36/63 = 57,142857…% | 59/63 = 93,650793…% |
| Resultado do PIT | BUILD FAILURE | BUILD SUCCESS |

A melhoria eliminou mais 23 mutantes. A meta exigia pelo menos 51 dos 63. Todos os mutantes permanecem no denominador; não houve exclusões, mudança de operadores, alteração do perfil Maven nem alteração do código de produção. Não ocorreram timeouts ou erros de execução de mutantes nos dois XMLs.

## Alterações nos testes

- O cenário válido agora verifica ID do evento, vínculo da rodada, IDs e descrições dos jogadores, texto das jogadas e contagem de meios-lances.
- Um cenário sem jogadas verifica as tags opcionais Annotator, ECO, Opening, Variation e PlyCount. Sem jogadas, o valor do cabeçalho não é substituído pela contagem calculada.
- Uma posição FEN personalizada valida o cabeçalho e os movimentos gerados a partir dela.
- Dois cenários verificam a conversão de Elo e a preservação do último valor válido após uma entrada inválida.
- Um cenário verifica a rodada padrão quando os jogadores são informados sem a tag Round.
- A leitura sequencial verifica os quatro marcadores de resultado, a continuação em outra linha e a preservação da segunda partida no iterador.
- Um comentário multilinha contendo um resultado verifica que a partida não termina antes do fechamento do comentário e do resultado real.

Os testes exercitam a API pública com entradas PGN em memória e componentes reais. Esta contribuição melhora a mutação; não declara concluída a exigência separada de isolamento de dependências.

## Reprodução

Executar a partir da raiz do repositório, em sequência:

```powershell
mvn -B -f chesslib/pom.xml -Pgameloader-quality "-Dtest=GameLoaderTest" test
mvn -B -f chesslib/pom.xml -Pgameloader-quality test-compile org.pitest:pitest-maven:mutationCoverage
mvn -B -f chesslib/pom.xml -Pgameloader-quality "-Dmaven.javadoc.skip=true" verify
```

Os três comandos foram aprovados: 14 testes individuais sem falhas, PIT com 59/63 mutantes mortos e regressão completa com 349 testes sem falhas, erros ou ignorados. O `verify` confirmou o limite de cobertura: 58/66 branches (87,878787…%) de GameLoader, medidos com a suíte completa. A cobertura de linhas foi 114/117. O HTML navegável do PIT fica em `chesslib/target/pit-reports-gameloader/index.html`.

A execução apenas de GameLoaderTest mediu 55/66 branches (83,33%). O PIT seleciona somente essa suíte; o resultado de 58/66 pertence à regressão completa. Cobertura de branches e escore de mutação são medidas diferentes. A associação com todas-arestas segue a convenção acadêmica registrada pelo grupo, com as limitações do contador BRANCH do JaCoCo.

## Evidências

- [XML PIT inicial](evidencias/mutations-antes.xml).
- [XML PIT final](evidencias/mutations-depois.xml).
- [XML JaCoCo da regressão completa](evidencias/jacoco-depois.xml).
- [Resumo das suítes da regressão, extraído dos XMLs do Surefire](evidencias/testes-resumo.json).
- [Testes antes da melhoria](evidencias/GameLoaderTest-antes.md).
- [Primeira proposta de testes da IA](evidencias/GameLoaderTest-ia-inicial.md).
- [Testes finais](../../../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/GameLoaderTest.java).
- [Registro de uso de IA](../../../ai/AI-LOG.md#registro-006--ampliação-dos-testes-unitários-e-melhoria-do-escore-de-mutação-de-gameloader).

A primeira proposta passou nos testes e atingiu a meta sem uma segunda alteração no código de testes. O código Java preservado no Markdown da proposta inicial é igual ao código final nesta validação. A revisão individual da responsável e a inclusão no documento Google Docs compartilhado ainda devem ser realizadas; a execução pelo agente não representa essa revisão humana.

## Mutantes restantes

Permanecem dois sobreviventes e dois sem cobertura. Três estão na condição e na chamada de `setMoveText` do caso `event` em `addProperty` (linhas 95–96); outro altera o limite de comparação da rodada em `setPlayerDetails` (linha 278). Não foram excluídos nem classificados automaticamente como equivalentes. Atingir 80% não significa que todos os comportamentos estejam cobertos.

## Texto para a issue

Título: **[test] Aumentar o escore de mutação de GameLoader para pelo menos 80%**

O PIT identificou 36/63 mutantes mortos (57,14%), 13 sobreviventes e 14 sem cobertura em GameLoader. O build falha por ficar abaixo da meta de 80% da Entrega 2. Ampliar os cenários e as asserções de GameLoaderTest mantendo o mesmo escopo de mutação.

Critérios de aceite: testes aprovados, KILLED/total >= 0,80 sem arredondamento para aprovação, configuração preservada e evidências antes/depois disponíveis no README.

Comentário de resolução: a suíte foi ampliada de 7 para 14 testes e passou a eliminar 59/63 mutantes (93,65%), sem modificar código de produção ou excluir mutantes. Restam dois sobreviventes e dois sem cobertura. Vincular o PR e este relatório antes de fechar a issue. Este arquivo é um rascunho; nenhuma issue foi publicada por esta execução.
