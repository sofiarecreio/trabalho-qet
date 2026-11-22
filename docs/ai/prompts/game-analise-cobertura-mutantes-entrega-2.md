# Revisão dos testes de qualidade de Game

Analise os relatórios de Game: JaCoCo com 189/218 branches e PIT com 180/208 mutantes mortos, 23 sobreviventes e cinco sem cobertura. Revise os cinco casos iniciais de GameQualityTest e melhore os testes para detectar defeitos observáveis, principalmente estados de navegação, metadados, comentários e numeração/fechamento de variações PGN. Preserve a produção e a configuração compartilhada.

Use resultados esperados explícitos e independentes do algoritmo de produção. Distinga mutante equivalente comprovável, possível equivalência e lacuna de teste; não exclua mutantes do denominador. Explique a relação e os limites entre branches JaCoCo e todas-arestas, incluindo curto-circuito e exceções. Execute novamente regressão, cobertura e mutação. Registre a saída, a decisão e uma validação técnica que justifique as escolhas, sem depender apenas de testes aprovados. Não atribua autoria ou revisão humana não informada.
