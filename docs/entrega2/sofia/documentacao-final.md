# Avaliação ISO/IEC 25010 e documentação final — Sofia

Data: 23/11/2026. Consolidação documental dos resultados existentes; não é uma nova execução dos testes.

Documento editável: https://docs.google.com/document/d/16PhvQ19VGINYlqDE0NsTWH6VHgzwLYcO_FdjD46PtVU/edit
Plano de Teste: https://docs.google.com/document/d/1JDJVJR6WbpOXpzfRQsbcVWZm8XzQYc8xMSGoEAKj0xo/edit
Pasta da apresentação: https://drive.google.com/drive/folders/1LVjwh2mAI-q-r4dDPsyD1pxVFcnPD1Ff

## Método e limites

Adota-se explicitamente o modelo de qualidade de produto ISO/IEC 25010:2011, com oito características, para organizar esta avaliação acadêmica. A edição 2023 possui nove características e não é misturada nesta matriz. A escolha é uma decisão metodológica desta consolidação, não uma afirmação sobre qual edição a professora exigiu. Referências: [ISO 2011](https://www.iso.org/standard/35733.html) e [ISO 2023](https://www.iso.org/standard/78176.html).

As notas pertencem aos indicadores e ao escopo descritos, não ao produto inteiro. Cada resultado deriva de uma execução identificada no relatório de origem; resultados de commits e ambientes distintos não formam uma nova execução conjunta. Não se calcula média global de características heterogêneas.

## Escalas e justificativa

- **P (percentual, maior é melhor):** nota 1 para p < 50%; 2 para 50% ≤ p < 70%; 3 para 70% ≤ p < 80%; 4 para 80% ≤ p < 95%; 5 para 95% ≤ p ≤ 100%. A nota 4 começa no mínimo de 80% exigido para branches e mutação. Nos testes funcionais, o critério de aprovação continua sendo todos os cenários selecionados passarem; a escala não autoriza ignorar falhas.
- **T (tempo, menor é melhor):** sendo L = 1.000 ms, nota 5 para p95 ≤ 0,5L; 4 para 0,5L < p95 ≤ L; 3 para L < p95 ≤ 1,5L; 2 para 1,5L < p95 ≤ 2L; 1 para p95 > 2L. L foi adotado no RNF-01; as faixas são uma convenção do grupo, não da ISO.
- **B (binária):** aprovado quando todas as asserções do cenário selecionado ou o reteste do achado foram satisfeitos; reprovado caso contrário. O resultado não se estende a cenários não avaliados.
- **NE:** não avaliado com evidência suficiente. Não é nota zero, não é aprovação e permanece como limitação/pendência.

## Matriz de atributos, medidas, resultados e justificativas

| Característica / escopo | Medida e escala | Resultado | Justificativa e evidência |\n| --- | --- | --- | --- |
| Adequação funcional
Chesslib e PetClinic | Casos aprovados / executados; escala P. Avaliação limitada aos cenários selecionados. | Chesslib: 349/349, 100%, nota 5. PetClinic: visitas 9/9, edição de animal 9/9 e cadastro de proprietário 8/8; seleção conjunta 43/43, nota 5. | Asserções verificam resultados e persistência. Não representa cobertura funcional total. Fontes E1, E3, E4, E9. |
| Eficiência de desempenho
PetClinic — visitas | p95 de resposta; escala T, limite L = 1.000 ms. Três execuções, 30 iterações após aquecimento. | Pior p95 individual: GET 9 ms; POST + redirecionamento 19 ms. Nota 5. | Limite adotado pelo grupo para o fluxo de atendimento. Um usuário, H2, máquina local, sem renderização do navegador; não é carga/concorrência. Fonte E5. |
| Compatibilidade
Chesslib — intercâmbio PGN | Preservação de dados no cenário salvar e reler PGN; escala B. | Aprovado no cenário shouldPreserveGameAfterSavingAndReloadingPgn, integrante da suíte de 12 cenários de integração. | Confere metadados, SAN, comentários, NAG, variante e FEN. Mesma biblioteca na ida e volta; compatibilidade com outro software e bancos do PetClinic não avaliada. Fonte E2. |
| Usabilidade
PetClinic — proteção contra erros | Rejeição de entradas inválidas e feedback nos cenários Selenium; escala B. | Aprovado nos cenários documentados de visita e edição de animal. Aprendizagem, acessibilidade e satisfação: NE. | Mensagens e proteção contra erros são um recorte de usabilidade. Não houve estudo com usuários; aprovação funcional não demonstra boa experiência geral. Fontes E3, E4. |
| Confiabilidade
Chesslib — falhas de entrada | Tratamento previsível de PGN ilegal e arquivo ausente; escala B. Regressão como evidência complementar. | Cenários de erro aprovados na suíte GameIntegrationTest; regressão de 349 testes aprovada. | Verifica exceção, contexto e causa dos erros. Não mede disponibilidade em produção, MTBF ou recuperação após interrupção. Fontes E1, E2. |
| Segurança
Chesslib — inspeção pontual | Tratamento do apontamento S4507 em PgnHolder; escala B. Demais controles: NE. | Apontamento de impressão de stack trace resolvido segundo a inspeção de Pedro. Segurança global: NE. | Correção estática de um achado não equivale a avaliação de autenticação, autorização ou teste de penetração. Segurança do PetClinic não foi medida por esta consolidação. Fonte E6. |
| Manutenibilidade
Chesslib — testabilidade | BRANCH cobertos / total e KILLED / total; escala P. Meta mínima 80% por classe e indicador. | Cinco classes atingem as duas metas numericamente. GameLoader: 87,88% branches e 93,65% mutação, nota 4 em ambos. | Mutantes sobreviventes, sem cobertura e erros ficam no denominador. MoveGenerator tem 4 MEMORY_ERROR e 2 TIMED_OUT; não contam como KILLED. Não se atribui nota única de manutenibilidade. Fontes E1, E7. |
| Portabilidade
Ambos os sistemas | Combinações SO/JDK aprovadas / planejadas, na mesma revisão; escala P. | NE: não há matriz comparável de ambientes para a mesma revisão. | Há execuções individuais em ambientes distintos, mas isso não comprova uma matriz controlada. Definir ambientes, repetir build/testes e registrar resultados antes de pontuar. Fonte E8. |

## Fontes

- **E1 — GameLoader — testes, PIT e JaCoCo:** [relatório](../../../docs/entrega2/sofia/gameloader/README.md).
- **E2 — Game — testes e integração PGN:** [relatório](../../../docs/entrega2/weslley/game-testes-integracao.md).
- **E3 — Selenium de visitas:** [relatório](../../../docs/selenium/visitas/README.md).
- **E4 — Selenium de edição de animal:** [relatório](../../../docs/selenium/alterar-animal/README.md).
- **E5 — Desempenho de visitas:** [relatório](../../../docs/desempenho/visitas/README.md).
- **E6 — Inspeção de PgnHolder:** [relatório](../../../docs/inspecao/pgnholder/README.md).
- **E7 — Consolidação de cobertura e mutação:** [relatório](../../../docs/entrega2/consolidacao/README.md).
- **E8 — Dados para documentação — Pedro:** [relatório](../../../docs/entrega2/pedro/dados-documentacao.md).
- **E9 — Selenium de cadastro de proprietário:** [relatório](../../selenium/cadastrar-proprietario/README.md).

## Pendências identificadas

1. Inspeção de GameLoader com prints antes/depois e comprovação de isolamento individual não foram localizadas no escopo consultado. A melhoria de mutação não substitui essas entregas.
2. Portabilidade controlada, avaliação ampla de segurança e estudo de usabilidade não têm medição suficiente. A matriz registra os recortes disponíveis e NE para o restante.
3. Consolidar o resultado final das cinco inspeções com Lucio. MoveList preserva a pendência ThreadLocal (issue #12); não apresentar todas as classes como “zero problemas”.
4. Integrar a branch de trabalho na main e conferir os links públicos. A consolidação não realiza merge nem confirma entrega no Classroom.
5. Todos os integrantes devem revisar suas seções logados, conferir as evidências e ensaiar sua apresentação. A autoria humana da revisão não pode ser inferida de edições realizadas pelo agente.

