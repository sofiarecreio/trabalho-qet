# Entrega 2 — Pedro

## Artefatos da contribuição

- [TimeControl: 23 testes, cobertura de 95,24% e mutação de 86,96%](timecontrol-testes.md).
- [PgnHolder: inspeção, correções, capturas e comparação de dez para zero apontamentos](../../inspecao/pgnholder/README.md).
- [Alteração de animal: nove cenários Selenium e capturas](../../selenium/alterar-animal/README.md).
- [Consolidação das cinco classes, comandos, contagens e pendências](../consolidacao/README.md).
- [Dados de Pedro para o plano, ISO e preparação da apresentação](dados-documentacao.md).
- [Defeitos reproduzidos e registrados nas Issues #21 e #22](defeitos-pgnholder.md).

## Situação de entrega

O código e as evidências foram integrados à main pelo [PR #23](https://github.com/sofiarecreio/trabalho-qet/pull/23). As atualizações de consolidação e o registro de TimeControl foram incorporados pelos PRs [#25](https://github.com/sofiarecreio/trabalho-qet/pull/25) e [#31](https://github.com/sofiarecreio/trabalho-qet/pull/31), respectivamente. A validação inicial partiu de `4750daa`; a revisão final incorpora a atualização documental `4982c6e`, sem alteração das classes dos colegas. Os hashes das fontes analisadas estão nos relatórios.

A seção 6 de PgnHolder e as linhas correspondentes das tabelas do [relatório Sonar compartilhado](https://docs.google.com/document/d/1LBd-p5gI73bchdHi-wWXTSUu7rfhmelV1IxTPfpdvk8/edit) foram atualizadas pela conta Google de Pedro, com as duas capturas. A seção 6 do [Plano de Teste](https://docs.google.com/document/d/1JDJVJR6WbpOXpzfRQsbcVWZm8XzQYc8xMSGoEAKj0xo/edit?tab=t.0) foi atualizada com o escopo individual da Entrega 2, a estratégia, os critérios de aceitação, os resultados e os links das evidências. Os dados para a avaliação ISO estão disponíveis no [material de documentação](dados-documentacao.md).

A consolidação contém as medições das cinco classes e seus limites. As alterações de código desta contribuição se restringem a TimeControl (testes), PgnHolder (inspeção e regressão), Selenium de edição de animal e à configuração das ferramentas. A integração da contribuição está concluída.

## Revalidação de MoveList após o PR #24

A consolidação foi atualizada após a correção de Lucio incorporada à main `da4a3f0`: MoveList atingiu 169/211 mutantes eliminados (80,09%), com 16 sobreviventes e 26 sem cobertura. A cobertura de branches permaneceu em 159/196 (81,12%), e a regressão completa executou 342 testes aprovados. Os XMLs, logs normalizados e metadados dessa classe estão no [relatório consolidado](../consolidacao/README.md). As medições das demais classes foram preservadas.

## Revisão final de 23 de novembro

Sobre a main `3934419`, as cinco classes foram revalidadas com JaCoCo e PIT, mantendo todos os mutantes no denominador. A regressão completa teve 361 testes aprovados. TimeControl permaneceu com 40/42 branches (95,24%) e 60/69 mutantes eliminados (86,96%). Os nove cenários Selenium de edição de animal foram executados novamente e aprovados. As Issues #21 e #22 estão fechadas após a integração das correções. A avaliação ISO compartilhada já contém os indicadores de TimeControl, PgnHolder e edição de animal.
