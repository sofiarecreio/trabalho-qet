# Entrega 2 — Lucas: correção de qualidade no MoveGenerator

## Achado analisado

No relatório do Sonar compartilhado pelo grupo, o método `generateCastleMoves` foi
identificado com complexidade cognitiva 25, acima do limite 15. O mesmo relatório
também indicava dois `if` aninhados que poderiam ser simplificados.

## Correção realizada

O método foi dividido em responsabilidades menores, sem alterar as regras de roque:

- `addCastleMove` concentra a validação e a inclusão de um roque;
- `hasCastleRight` verifica o direito correspondente ao lado do roque;
- `getCastleOccupancy` trata a ocupação especial do Chess960;
- a condição de casas livres e casas ameaçadas foi combinada em um único `if`.

Com isso, `generateCastleMoves` ficou responsável apenas por verificar o xeque do
rei e delegar os roques pequeno e grande. A alteração reduz a complexidade local e
evita duplicação entre os dois lados, mantendo o comportamento coberto pelos testes.

## Validação

Foram executados os 20 testes do `MoveGenerator` — 16 testes do Lucas e 4 testes já
existentes — e todos passaram. A verificação do JaCoCo também foi concluída com
sucesso, mantendo a cobertura de branches acima do mínimo de 80% configurado para a
classe.

```bash
cd chesslib
mvn -Dtest=MoveGeneratorTest,LucasMoveGeneratorTest -Dmaven.javadoc.skip=true clean verify
```

O servidor/analisador Sonar usado pela equipe não está configurado neste ambiente;
por isso, a confirmação local foi feita pelos testes, pela cobertura e pela revisão
do achado registrado no relatório compartilhado.
