# Registro de Uso de Inteligência Artificial

Este documento registra as interações com ferramentas de Inteligência Artificial Generativa que contribuíram de forma relevante para os artefatos do trabalho de Qualidade e Teste de Software.

As respostas da IA não foram aceitas automaticamente. Todo conteúdo utilizado foi revisado pelo integrante responsável e validado por meio de inspeção do código, execução dos testes e comparação dos resultados esperados e obtidos.

---

## Interação 01 — Comparação dos testes de `MoveList`

| Informação | Registro |
|---|---|
| **Data** | 19/09/2026 |
| **Responsável** | Lucio |
| **Atividade** | Revisão dos testes unitários da classe `MoveList` do Chesslib |
| **Ferramenta** | ChatGPT — GPT-5.6 SOL Média |
| **Artefato relacionado** | `MoveListQetTest.java` |
| **Código utilizado como referência** | `MoveListTest.java`, presente no repositório original do Chesslib |

### Prompt/instrução utilizada

> Compare os testes unitários que desenvolvi para a classe `MoveList` com os testes já existentes no arquivo `MoveListTest.java` do repositório original do Chesslib. Verifique se os novos testes são suficientemente diferentes, se apenas repetem os testes existentes e se os cenários possuem complexidade adequada para o trabalho. Considere os testes de movimento simples de peão, captura, movimento ambíguo de cavalo, promoção, roque pequeno, roque grande, captura en passant e movimento inválido. Explique quais testes são complementares, quais possuem possível sobreposição e quais ajustes seriam necessários.

### Resultado apresentado pela IA

A IA indicou que existe alguma sobreposição natural, pois os dois arquivos exercitam a mesma classe e as mesmas regras de xadrez. Entretanto, concluiu que os testes desenvolvidos pelo grupo não eram cópias diretas dos testes originais.

Os testes originais de `MoveList` utilizam, entre outros elementos, sequências maiores de movimentos, conversões completas entre SAN e FAN, posições FEN, numeração de jogadas e partidas completas. Os novos testes foram organizados como cenários menores e isolados, com uma regra específica por método de teste.

A comparação apontou como diferenciais dos testes desenvolvidos:

- utilização de posições FEN controladas para promoção e roques;
- validação direta do movimento interno, como `e1g1` e `e1c1`;
- validação da notação SAN gerada;
- conferência do estado FEN final após a captura en passant;
- teste específico de desambiguação do movimento `Nbd2`;
- separação de cada regra especial em um método de teste independente.

A IA também recomendou que um caso de movimento inválido somente fosse mantido se a entrada escolhida realmente provocasse a exceção esperada pela implementação.

### Comparação realizada

| Aspecto | Testes originais de `MoveList` | Testes desenvolvidos |
|---|---|---|
| Organização | Alguns testes utilizam sequências maiores ou partidas completas | Cada método concentra-se em um comportamento específico |
| Movimento simples | Avaliado dentro de sequências e conversões | Cenário isolado para `e4` |
| Captura | Presente em sequências de movimentos | Cenário curto para `exd5` |
| Ambiguidade | Há verificações de notação e desambiguação | Validação específica de `Nbd2` e do movimento `b1d2` |
| Promoção | Cobertura relacionada à conversão de movimentos | Posição FEN controlada e promoção `e8=Q` |
| Roques | Avaliados em contextos da suíte original | Casos independentes para `O-O` e `O-O-O` |
| En passant | Pode aparecer em sequências de movimentos | Validação do movimento, da SAN e do FEN final |
| Resultado verificado | SAN, FAN, FEN e sequências completas | Movimento interno, tamanho da lista, SAN e FEN conforme o caso |

### Decisão


Foram mantidos na versão final os seguintes cenários:

1. movimento simples de peão;
2. captura de peão;
3. movimento ambíguo de cavalo;
4. promoção de peão;
5. roque pequeno;
6. roque grande;
7. captura en passant.
8. rejeição do movimento ilegal Qh5, com lançamento de MoveConversionException.

O cenário de movimento inválido foi revisado durante o desenvolvimento. Após confirmar que Qh5 não pode ser executado a partir da posição inicial, pois o caminho da dama está bloqueado, mantive o teste com @Test(expected = MoveConversionException.class). A execução confirmou que a biblioteca lança a exceção esperada.


### Alterações realizadas após a revisão

- organização dos testes em métodos pequenos e independentes;
- conferência de mais de um resultado nos cenários relevantes, como movimento, SAN e FEN;
- manutenção somente de resultados esperados confirmados pela execução;
- inclusão do teste negativo somente após confirmar o lançamento de MoveConversionException;
- documentação da diferença entre a suíte criada e a suíte original do projeto.

### Validação

O resultado da IA foi validado das seguintes formas:

1. leitura do arquivo original `MoveListTest.java` e comparação com `MoveListQetTest.java`;
2. conferência dos dados de entrada, posições FEN e resultados esperados;
3. execução dos testes pelo Eclipse utilizando JUnit 4;
4. execução pelo Maven para confirmar que os testes também funcionavam no processo de build;
5. análise das mensagens de falha durante o desenvolvimento e correção ou remoção das expectativas incorretas;
6. revisão do arquivo antes do envio por pull request.

### Evidências

- [Teste desenvolvido](../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/MoveListQetTest.java)
- [Repositório original do Chesslib](https://github.com/bhlangonijr/chesslib)
- [Evidências de execução dos testes unitários](../evidencias/testes-unitarios/)
- [Evidências da execução pelo Maven](../evidencias/maven/)

### Conclusão

A IA foi utilizada como apoio à revisão e à comparação dos testes, não como substituição da análise feita. A decisão final foi tomada após a leitura dos testes existentes e a execução dos novos casos. A principal contribuição da interação foi ajudar a identificar e documentar por que os novos testes complementam a suíte original e quais expectativas precisavam ser verificadas antes de serem aceitas.

