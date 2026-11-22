# Prompt — Melhoria dos testes isolados de Game

Revise a primeira versão de GameIsolatedTest. Considere o contrato de navegação de Game.java na base 3a87f15, os testes existentes em GameTest e a execução inicial de quatro casos aprovados.

Avalie criticamente se os quatro testes realmente verificam as decisões de Game, sem depender do processamento real de regras de xadrez. Não considere apenas BUILD SUCCESS como evidência de adequação.

Melhore a suíte para:
1. Verificar que o FEN inicial da lista é repassado ao Board antes das jogadas.
2. Verificar os movimentos executados, sua ordem, o argumento fullValidation=true e a interrupção no índice solicitado.
3. Cobrir os limites -1, 0, n-1, n e acima de n, além de lista vazia e ausência de Board.
4. Simular rejeição tanto da primeira quanto de uma jogada posterior, confirmando a exceção, o estado da posição e a ausência de tentativas posteriores.
5. Verificar os atalhos gotoFirst, gotoLast, gotoNext e gotoPrior, incluindo os limites da navegação e listas de tamanho diferente da linha principal.
6. Respeitar o contrato documentado de que a lista fornecida se torna ativa, mesmo quando o índice não provoca navegação. Não inventar a exigência de manter a lista anterior.
7. Evitar estado compartilhado entre testes e dependências novas desnecessárias. Explicar o que está e o que não está isolado.
8. Preservar o cuidado com a chamada virtual loadFromFen feita pelo construtor de Board: ela não pode poluir o histórico de interações ou acessar campos do stub antes da inicialização.
9. Demonstrar que a suíte detecta alterações defeituosas controladas, em cópias temporárias do código. Não apresentar esses experimentos como execução formal do PIT nem como escore de mutação.

Entregar a suíte revisada, a descrição das alterações aceitas ou rejeitadas, os resultados das execuções e a justificativa técnica no campo Validação do AI-LOG. Preservar a primeira versão. Não corrigir produção, comentários de linha ou achados do Sonar nesta etapa, e não afirmar que Weslley realizou uma revisão humana que ainda não ocorreu.
