# Entrega 2 — Explicação da contribuição de Lucas

Este roteiro resume o que foi feito por Lucas e pode ser usado na apresentação.
Os comandos e resultados abaixo correspondem às execuções registradas na
documentação do repositório.

## 1. Testes unitários do `MoveGenerator`

Minha classe foi a `MoveGenerator`, do Chesslib. Ela contém a lógica responsável
por gerar movimentos de xadrez, então os testes precisavam verificar regras
normais e situações de limite.

Foram criados 16 testes próprios, além dos 4 testes já existentes da classe. Os
cenários cobrem:

- movimentos iniciais de peões brancos e pretos;
- bloqueio e captura de peões;
- promoções para as quatro peças possíveis;
- movimentos de cavalo, bispo, torre, rainha e rei;
- máscaras de casas permitidas;
- movimentos pseudo-legais;
- roque pequeno, roque grande, restrição de direitos e rei em xeque.

Cada teste cria uma posição controlada, muitas vezes usando FEN, chama o método
do `MoveGenerator` e verifica a quantidade e o conteúdo dos movimentos gerados.
Assim, o teste não verifica somente se o método executou: ele confere se os
movimentos corretos foram produzidos.

Comando principal:

```bash
cd chesslib
mvn -Dtest=MoveGeneratorTest,LucasMoveGeneratorTest -Dmaven.javadoc.skip=true clean verify
```

Resultado: 20 testes aprovados.

## 2. Cobertura, mutação e qualidade

Depois dos testes unitários, foram utilizadas três verificações complementares:

- JaCoCo mede quais branches do código foram exercitados;
- PIT altera temporariamente o código para verificar se os testes detectam as
  mudanças;
- SonarQube procura problemas de qualidade, como complexidade e code smells.

Na execução focada, o JaCoCo registrou 65 de 68 branches, ou 95,59%. Na
regressão completa usada pelo grupo, o `MoveGenerator` ficou com 67 de 68
branches, ou 98,53%.

No PIT, o cálculo estrito usado na consolidação foi de 64 mutantes mortos em 78,
ou 82,05%. Também foram registrados 3 sobreviventes, 5 mutantes sem cobertura,
4 encerramentos por erro de memória e 2 timeouts. Esses estados foram mantidos
separados para não considerar erro de execução como se fosse uma asserção de
teste.

Na análise local do SonarQube, o Quality Gate ficou como `OK`, sem bugs,
vulnerabilidades ou code smells no recorte analisado.

## 3. Teste de integração do PetClinic

Também foi criado o `LucasOwnerIntegrationTests`. Esse teste inicia a aplicação
com Spring Boot e banco H2 em memória e acessa as rotas reais do sistema.

Os dois cenários verificam:

1. a busca por `Davis`, confirmando Betty e Harold Davis;
2. a página `/owners/2`, confirmando Betty Davis e o animal Basil.

O objetivo é testar a comunicação entre controlador MVC, repositório JPA, banco
H2 e renderização Thymeleaf. Por isso, não foram usados mocks nesse fluxo.

Resultado verificado: 2 testes aprovados.

## 4. Teste de sistema com Selenium

No teste de sistema, o fluxo foi realizado na interface web do PetClinic sob a
responsabilidade de Lucas. O cenário acessa a busca de proprietários, informa o
sobrenome `Davis`, envia o formulário e confere os resultados exibidos na tabela.

O Selenium usa o Chrome em modo headless. O `WebDriverWait` aguarda a tabela ser
carregada antes das verificações, evitando que o teste dependa apenas de um tempo
fixo de espera.

O teste confirmou a presença de Betty Davis e Harold Davis e gerou uma captura
de tela versionada em
`docs/selenium/busca-proprietario/01-busca-davis-resultados.png`.

Resultado verificado: 1 teste aprovado.

## 5. Teste manual no TestLink

Na Entrega 1, o cenário manual de Lucas foi a busca de proprietário pelo
sobrenome `Davis`. O caso foi executado no TestLink e passou. As evidências do
formulário e dos resultados estão na pasta do caso manual.

## Fala curta para a apresentação

> Minha contribuição foi testar o `MoveGenerator` do Chesslib e o fluxo de busca
> de proprietários do PetClinic. No Chesslib, criei testes para movimentos
> normais, capturas, promoções, máscaras e roques. Depois medi a cobertura com
> JaCoCo e a força dos testes com PIT. A cobertura consolidada ficou em 98,53%
> de branches e a mutação estrita em 82,05%. No PetClinic, fiz um teste de
> integração com as camadas reais e um teste Selenium pela interface, ambos
> aprovados. A IA foi utilizada como apoio para organizar os cenários e revisar
> os resultados; as decisões, execuções e validações foram conferidas no código
> e nos relatórios do projeto.

## Artefatos relacionados

- [Testes do MoveGenerator](../../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/LucasMoveGeneratorTest.java)
- [Cobertura](movegenerator-cobertura.md)
- [Mutação](mutacao-movegenerator.md)
- [Qualidade do MoveGenerator](qualidade-movegenerator.md)
- [Integração PetClinic](integracao-petclinic.md)
- [Selenium](selenium-petclinic.md)
- [Registro de uso de IA](../../ai/AI-LOG.md)
