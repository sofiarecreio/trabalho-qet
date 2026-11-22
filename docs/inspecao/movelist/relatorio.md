# Inspeção e correção da classe `MoveList` — Entrega 2

**Responsável:** Lucio  
**Data:** 22/11/2026  
**Classe analisada:** `chesslib/src/main/java/com/github/bhlangonijr/chesslib/move/MoveList.java`

## 1. Objetivo e escopo

Este relatório apresenta a inspeção e a correção da classe não CRUD `MoveList`, pertencente ao Chesslib. A classe foi escolhida por concentrar regras de conversão e manipulação de movimentos de xadrez e por possuir métodos com complexidade cognitiva elevada.

O trabalho contemplou:

- inspeção estática com o Sonar;
- correção de problemas de manutenibilidade;
- refatoração dos métodos `encode` e `decodeSan`;
- execução dos testes após as alterações;
- registro da pendência técnica que não pôde ser corrigida com segurança nesta entrega.

As alterações foram limitadas à classe `MoveList`, aos testes utilizados para validar as correções e aos artefatos de documentação associados.

## 2. Ferramentas utilizadas

- Eclipse;
- JDK 17;
- Maven;
- JUnit 4;
- SonarQube for IDE/Eclipse.

## 3. Resultado inicial da inspeção

A análise inicial do Sonar apresentou **10 apontamentos** na classe `MoveList`. Entre eles estavam:

- dois métodos com complexidade cognitiva acima do limite permitido;
- uso de estruturas condicionais aninhadas;
- uso excessivo de `break` e `continue` em um laço;
- recomendações de uso de `isEmpty()`;
- construtor de cópia sem inicialização explícita de campos elegíveis;
- comentário `TODO` pendente;
- recomendação de executar `remove()` no `ThreadLocal` `boardHolder`.

Os problemas de maior impacto eram:

| Método | Complexidade inicial | Limite configurado |
| --- | ---: | ---: |
| `encode` | 33 | 15 |
| `decodeSan` | 88 | 15 |

### Evidência inicial

- [Abrir a captura do Sonar antes das correções](https://github.com/sofiarecreio/trabalho-qet/blob/lucio/entrega2/docs/inspecao/movelist/sonar_antes.png)

## 4. Correções e refatorações realizadas

### 4.1 Método `encode`

O método reunia diferentes responsabilidades, como tratamento de roque, resolução de ambiguidade, execução do movimento, captura, promoção e indicação de xeque.

Foram extraídos os métodos auxiliares:

```text
encodeCastleMove(...)
appendAmbiguityResolution(...)
```

Com isso, a lógica de roque e a resolução da origem de movimentos ambíguos passaram a ficar isoladas. O método `encode` permaneceu responsável por coordenar a formação da notação SAN ou FAN.

### 4.2 Método `decodeSan`

A maior parte da complexidade estava na identificação da casa de origem do movimento. Essa lógica foi distribuída entre métodos auxiliares menores:

```text
findPawnSourceSquare(...)
findNonPawnSourceSquare(...)
determineSourcePieceType(...)
extractSourceLocation(...)
findAmbiguousSourceSquare(...)
filterCandidatesByLocation(...)
```

Também foram utilizados retornos antecipados para o tratamento de roque e removida uma atribuição local desnecessária. As validações de posição, promoção e movimento inválido foram preservadas.

### 4.3 Construtor de cópia e demais ajustes

O construtor de cópia passou a inicializar explicitamente os campos relevantes. Os vetores de notação são clonados quando existentes, evitando o compartilhamento indevido de estruturas mutáveis entre instâncias.

Também foram realizados ajustes menores indicados pelo Sonar, como simplificação de condições, uso apropriado de `isEmpty()` e reorganização de trechos aninhados.

## 5. Evolução da complexidade

| Método | Valor inicial | Valores intermediários | Resultado final |
| --- | ---: | --- | --- |
| `encode` | 33 | 26 após a extração da lógica de roque | Apontamento eliminado |
| `decodeSan` | 88 | 81 após separar movimentos de peão; 17 após separar a busca da origem | Apontamento eliminado |

As alterações foram aplicadas de forma incremental. Após cada etapa relevante, os testes foram executados para reduzir o risco de introdução de regressões.

## 6. Resultado final e validação

Depois das correções, os avisos de complexidade cognitiva e os demais problemas de manutenção tratados deixaram de ser apresentados.

O único apontamento restante foi a recomendação de executar `remove()` no `ThreadLocal` `boardHolder`. Essa alteração não foi aplicada automaticamente porque o objeto mantém o contexto do tabuleiro durante a conversão de movimentos consecutivos. Inserir a remoção em um ponto incorreto poderia eliminar um estado ainda necessário e provocar regressões.

A pendência foi documentada na [issue nº 12 — Avaliar remoção segura do `ThreadLocal boardHolder`](https://github.com/sofiarecreio/trabalho-qet/issues/12). Sua correção futura exige a identificação do ciclo de vida completo do objeto e a criação de testes específicos para garantir um ponto seguro de liberação.

Após as refatorações, `MoveListTest` e `MoveListQetTest` foram executados pelo Maven. A execução terminou com `BUILD SUCCESS`, sem falhas ou erros nas suítes selecionadas, confirmando que os comportamentos exercitados pelos testes foram preservados.

### Evidências finais

- [Abrir a captura do Sonar depois das correções](https://github.com/sofiarecreio/trabalho-qet/blob/lucio/entrega2/docs/inspecao/movelist/sonar_depois.png)
- [Abrir a captura da execução com `BUILD SUCCESS`](https://github.com/sofiarecreio/trabalho-qet/blob/lucio/entrega2/docs/inspecao/movelist/build_sucess.png)
