# Saída da revisão Sonar de Game

A versão inicial (`71df887`) aplicou correções de nomes, listas vazias e parâmetro privado sem uso. A revisão separou geração de cabeçalhos, anotações e variações e decompôs o parser em normalização, comentários, delimitadores e fechamento de variações. Os objetos internos novos contêm somente dados; os métodos continuam em Game, no alvo da cobertura e mutação.

Resultado: **16 → 0 apontamentos**, com SonarQube Community Build 26.11.0.134175, analisador Java 8.46.0.59609 e o mesmo perfil oficial Sonar way comprehensive de 608 regras. Complexidade cognitiva agregada **218 → 109**, sem método acima do limite 15 na análise final.

Foi aceita uma mudança de contrato explícita: `toString()` retorna string vazia para conversão inválida ou metadados ausentes, e uma partida completa vazia aceita texto bruto ausente. O teste anterior de retorno nulo foi atualizado para esse contrato e foram acrescentados dois cenários de robustez, totalizando 15 casos em GameQualityTest.

Foi rejeitada a troca automática de `trim()` por `isBlank()`, pois eles não tratam todos os caracteres da mesma forma. Manteve-se a semântica de trim usando a operação correspondente de StringUtils, sem suprimir a regra. Também não foram implementados os parâmetros de filtragem de comentários/variações, que continuam ignorados conforme o contrato anterior.

Validação: **307 testes** aprovados; JaCoCo **166/179 branches (92,74%)**; PIT **203/213 mutantes mortos (95,31%)**, sete sobreviventes, três sem cobertura e nenhum erro. A alteração da produção muda os denominadores; não foi feita comparação por índices de instrução entre as duas versões. As metas de 80% foram preservadas sem desativar regras ou excluir sobreviventes.

**Decisão e justificativa da validação:** aceitar a extração por responsabilidade, pois os oráculos de saída exata preservam a ordem dos lances, os números e o fechamento das variantes, além do texto dos comentários e do processamento dos componentes reais. O caso de falha controlada verifica o novo retorno não nulo e os casos de partida vazia/incompleta reproduzem os estados que motivavam o TODO. As regras S3776 e S135 deixaram de apontar problemas após a mudança estrutural, enquanto PIT continuou acima da meta medindo os novos auxiliares de Game. Não aceitar a conclusão de ausência de defeitos pelo zero do Sonar: escopo e regras são limitados e os dez mutantes restantes continuam explicitados.

O [relatório de inspeção](../../inspecao/game/relatorio.md) contém as capturas reais antes/depois, o mapeamento dos 16 achados, ambiente, comandos, limitações e resultados. A contribuição de Lucas e o POM não foram modificados nesta etapa. Nenhum script ou log bruto foi acrescentado aos commits.
