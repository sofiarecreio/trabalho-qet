# Casos de teste unitários - classe MoveGenerator

Responsável: Lucas Ardelino  
Projeto: Chesslib  
Arquivo sob teste: `chesslib/src/main/java/com/github/bhlangonijr/chesslib/move/MoveGenerator.java`

## Objetivo

Verificar se a classe `MoveGenerator` gera movimentos de xadrez de acordo com a posição do tabuleiro e com as regras do tipo de peça analisado.

## Casos implementados

| ID | Cenário | Resultado esperado |
|---|---|---|
| CTU-MG-01 | Gerar os movimentos dos peões na posição inicial | Cada peão branco deve ter seu avanço de uma e de duas casas, totalizando 16 movimentos. |
| CTU-MG-02 | Gerar captura de peão | O peão branco em `D5` deve capturar a peça preta em `C6`, sem criar captura para uma casa vazia. |
| CTU-MG-03 | Gerar promoção | O peão branco em `A7` deve gerar quatro opções em `A8`: dama, torre, bispo e cavalo. |
| CTU-MG-04 | Aplicar máscara aos movimentos do cavalo | Com a máscara limitada a `C6`, deve ser gerado somente o movimento `D4-C6`. |
| CTU-MG-05 | Gerar roques | Com o caminho livre e os direitos de roque ativos, devem ser gerados os roques pequeno e grande das brancas. |

## Implementação

Os casos estão implementados em:

`chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/MoveGeneratorTest.java`

Para executar os testes:

```bash
cd chesslib
mvn test
```

O arquivo de teste usa JUnit 4, que já é uma dependência do projeto. Antes de abrir o Pull Request, registrar no PR o resultado do comando `mvn test`.
