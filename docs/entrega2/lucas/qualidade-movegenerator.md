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
sucesso, registrando 95,6% de cobertura de branches no recorte analisado.

```bash
cd chesslib
mvn -Dtest=MoveGeneratorTest,LucasMoveGeneratorTest -Dmaven.javadoc.skip=true test
```

Também foi executada uma análise no SonarQube Community Build 26.11.0.134175, usando
o projeto `qet-lucas-movegenerator` e o perfil `Sonar way core`. A análise considerou
o `MoveGenerator.java` e o respectivo teste do Lucas. O processamento terminou com
sucesso e o resultado foi:

- Quality Gate: `OK`;
- bugs: `0`;
- vulnerabilidades: `0`;
- code smells: `0`;
- security hotspots: `0`;
- cobertura: `96,8%`;
- cobertura de branches: `95,6%`;
- duplicação: `0,0%`.

O painel da análise ficou disponível em:
`http://localhost:9000/dashboard?id=qet-lucas-movegenerator`.
