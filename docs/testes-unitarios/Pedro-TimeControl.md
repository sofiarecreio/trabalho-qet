# Casos de teste unitários - classe TimeControl

Responsável: Pedro Piaz  
Projeto: Chesslib  
Arquivo sob teste: `chesslib/src/main/java/com/github/bhlangonijr/chesslib/game/TimeControl.java`

## Objetivo

Verificar a interpretação e a conversão dos principais formatos de controle de tempo usados no PGN, incluindo controles com incremento, controles por número de movimentos, múltiplos estágios e valores desconhecidos.

## Casos implementados

| ID | Cenário | Resultado esperado |
|---|---|---|
| CTU-TC-01 | Interpretar `3000+3` | Identificar controle com bônus, armazenar 3.000.000 ms e incremento de 3.000 ms. |
| CTU-TC-02 | Interpretar `3000|3` | Aceitar a barra vertical e converter a representação para `3000+3`. |
| CTU-TC-03 | Interpretar `40/9000` | Identificar controle por movimentos, com 40 movimentos e 9.000.000 ms. |
| CTU-TC-04 | Interpretar múltiplos estágios | Armazenar o primeiro estágio e uma etapa adicional em `getMovePerTime()`. |
| CTU-TC-05 | Interpretar controle desconhecido `-` | Retornar tipo desconhecido, representação PGN `?` e texto `Custom...`. |

## Implementação

Os casos estão implementados em:

`chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/PedroTimeControlTest.java`

Para executar os testes:

```bash
cd chesslib
mvn test
```

O arquivo usa JUnit 4, que já é uma dependência do projeto. O resultado da execução local foi de 192 testes aprovados, sem falhas ou erros.
