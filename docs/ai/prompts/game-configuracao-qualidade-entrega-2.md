# Prompt — Adaptar a configuração de qualidade para Game

Reaproveite os plugins JaCoCo 0.8.12 e PIT 1.17.4 da contribuição de Lucas, já integrada pela main. A configuração padrão mede MoveGenerator e seus testes. Adicione um perfil Maven `game-quality` para analisar a classe `com.github.bhlangonijr.chesslib.game.Game` com as suítes GameTest, GameIsolatedTest, GameProcessingTest e GameIntegrationTest.

Preserve a execução padrão de Lucas, suas versões e seus limites. No perfil novo, mantenha o mínimo de 80% de branches para Game e 80% de escore PIT; não diminua os limites para fazer a medição passar. Separe os relatórios e os dados de cobertura de Game para não misturar resultados. Os filtros de instrumentação do agente e de seleção dos arquivos do relatório devem selecionar a classe correta e não produzir uma análise vazia.

Valide a configuração efetiva e execute os testes, o JaCoCo e o PIT. Informe os resultados reais e a complexidade de Game conforme a ferramenta. Se alguma meta não for atingida, registre a pendência sem alterar a configuração para ocultá-la. Cobertura de branches não deve ser apresentada automaticamente como comprovação de um grafo todas-arestas; a relação com o critério ainda precisa ser justificada no relatório acadêmico.

Registre apenas a entrada, a saída da alteração de configuração e a decisão/validação no AI-LOG. Não acrescente scripts ou logs ao repositório e não altere o README principal nesta etapa.
