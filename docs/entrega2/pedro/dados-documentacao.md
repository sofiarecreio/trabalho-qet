# Dados para documentação e apresentação — Pedro

Este material reúne os dados medidos e o escopo da contribuição. A seção individual da Entrega 2 já foi incorporada à seção 6 do Plano de Teste compartilhado. Os indicadores abaixo também servem de base para a avaliação coordenada pela equipe; este arquivo não substitui o histórico de participação nos Google Docs.

## Escopo para o Plano de Teste

Na Entrega 2, a atividade de Pedro compreende a ampliação de testes de TimeControl, a configuração e medição de cobertura e mutação, a automatização da edição de animais no PetClinic e a inspeção/correção de PgnHolder. TimeControl permanece como classe dos testes estruturais e de mutação; PgnHolder é a classe selecionada para inspeção.

O ambiente usa JDK 21 e Maven. Os testes de TimeControl utilizam JUnit 4, JaCoCo e PIT; a interface do PetClinic é exercitada por Selenium com Chrome e H2 reais. Os perfis individuais de qualidade preservam os filtros dos colegas. As metas são pelo menos 80% de branches e 80% de mutantes mortos, mantendo todos os mutantes no denominador da consolidação estrita.

O critério local de conclusão de Pedro é a execução aprovada dos testes, os relatórios reproduzíveis, a comparação Sonar com mesmo perfil, a documentação dos limites e a disponibilidade dos artefatos. A contribuição já foi integrada à main; o Plano de Teste e o relatório compartilhado de inspeção receberam a seção de Pedro. A entrega acadêmica ainda exige a revisão individual, a apresentação e a submissão dos links pelo grupo.

## Indicadores para avaliação ISO/IEC 25010

| Característica relacionada | Indicador observado | Resultado | Limite da conclusão |
| --- | --- | --- | --- |
| Adequação funcional | Cenários selecionados de edição de animal | 9/9 aprovados | Cobertura funcional restrita aos cenários descritos; não comprova toda a aplicação |
| Confiabilidade | Regressão Chesslib na revisão final da main `3934419` | 361/361 aprovados | Resultado em ambiente local; não é taxa de falha em produção |
| Confiabilidade | Seleção conjunta PetClinic da rodada original | 42/42 aprovados | Registro histórico; uma repetição teve erro transitório no Selenium de visitas. A revisão individual de Pedro aprovou os 9 cenários; não é a suíte inteira |
| Manutenibilidade — testabilidade | Branches de TimeControl | 40/42 = 95,24% | Branches não incluem todas as arestas excepcionais |
| Manutenibilidade — testabilidade | Mutantes mortos de TimeControl | 60/69 = 86,96% | Sete sobreviventes e dois sem cobertura continuam contabilizados |
| Manutenibilidade | Apontamentos de PgnHolder | 10 para 0 | Mesmo perfil comprehensive de 608 regras; escopo de um arquivo |
| Segurança | Apontamento de impressão de stack trace | S4507 resolvido | Inspeção estática; não equivale a teste de penetração |
| Portabilidade/adaptabilidade | Execução em macOS ARM64 com JDK 21 | Execução local aprovada | Não é evidência de execução em todos os sistemas operacionais |

As medidas não constituem uma avaliação completa de cada característica da norma. A avaliação final coordenada por Sofia adota a edição ISO/IEC 25010:2011 e escalas próprias do grupo; os indicadores de Pedro estão incorporados ao [documento Trabalho QeT](https://docs.google.com/document/d/16PhvQ19VGINYlqDE0NsTWH6VHgzwLYcO_FdjD46PtVU/edit). A escala do grupo não é apresentada como prescrição da ISO; indicadores sem evidência permanecem não avaliados. Pedro não mediu desempenho de sua funcionalidade, satisfação de uso, concorrência ou interoperabilidade; não atribuir notas máximas sem evidência.

## Pontos para explicar na apresentação

1. Por que TimeControl e PgnHolder são classes diferentes nesta entrega: a troca ocorreu apenas na inspeção do Sonar.
2. Como os testes passaram de cinco para 23: formatos, limites, representação, independência de instâncias e exceções; o stub de MovePerTime verifica delegação com saída controlada.
3. Por que 95,24% de branches e 86,96% de mutação medem coisas diferentes: o primeiro verifica execução de decisões; o segundo, detecção de alterações artificiais pelo teste.
4. O que resta: dois caminhos do parser não cobertos, sete mutantes sobreviventes e dois sem cobertura; não foram eliminados do denominador.
5. Como o Selenium verifica persistência: formulário real, consulta do registro exato, recarga da tela e verificação do segundo animal intacto.
6. Qual mudança de contrato foi feita em PgnHolder: falhas de gravação agora geram UncheckedIOException com causa; recursos fecham mesmo em falhas de inicialização do iterador.
7. Por que um BUILD SUCCESS pode não bastar na consolidação: arredondamento de 79,62% para 80% no PIT na medição anterior de MoveList (corrigida depois para 169/211 = 80,09% no PR #24) e tratamento de erros de memória como detecção pela ferramenta.

Antes da apresentação, executar os comandos e revisar os cenários e asserções para conseguir explicá-los e modificá-los quando solicitado.
