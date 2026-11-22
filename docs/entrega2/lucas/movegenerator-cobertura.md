# Entrega 2 — Lucas: testes do MoveGenerator

## Objetivo

Ampliar os testes unitários da classe `MoveGenerator` e configurar a medição de cobertura estrutural para acompanhar o critério de todas-arestas por meio da cobertura de branches do JaCoCo.

## Alterações realizadas

- Criação de `chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/LucasMoveGeneratorTest.java` com 16 testes do Lucas.
- Inclusão de cenários para:
  - movimentos iniciais de peões brancos e pretos;
  - bloqueio de peão;
  - captura de peão;
  - promoção branca e preta;
  - movimentos de cavalo, bispo, torre, rainha e rei usando máscaras;
  - geração de movimentos pseudo-legais da posição inicial;
  - roque pequeno e grande;
  - restrição de direitos de roque;
  - impossibilidade de rocar quando o rei está em xeque.
- Configuração do `jacoco-maven-plugin` no `chesslib/pom.xml`.
- Inclusão de uma verificação automática no Maven com mínimo de 80% de cobertura de branches para `MoveGenerator`.

## Execução

Comando usado no módulo `chesslib`:

```bash
mvn -Dtest=MoveGeneratorTest,LucasMoveGeneratorTest -Dmaven.javadoc.skip=true clean verify
```

O filtro executa os 16 testes do Lucas e os 4 testes já existentes da classe `MoveGeneratorTest`, totalizando 20 testes.

## Resultado

| Métrica do `MoveGenerator` | Resultado |
|---|---:|
| Branches | 65 de 68 — **95,59%** |
| Linhas | 175 de 180 — **97,22%** |
| Instruções | 741 de 766 — **96,74%** |

Resultado da execução: **BUILD SUCCESS** e todos os 20 testes passaram.

O relatório HTML detalhado é gerado automaticamente em `chesslib/target/site/jacoco/index.html` após a execução do comando. A pasta `target` é um artefato gerado pelo Maven e não precisa ser versionada.
