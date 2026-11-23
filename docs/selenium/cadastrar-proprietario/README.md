# Testes de sistema — cadastro de proprietário

Responsável: Sofia Recreio. Execução local: 23/11/2026, sobre o commit `9adb790` com a nova classe de teste ainda não commitada. Ambiente: Windows, Java 21.0.12, Chrome headless via Selenium Manager.

Implementação: [SofiaOwnerSeleniumTests.java](../../../spring-petclinic/src/test/java/org/springframework/samples/petclinic/owner/SofiaOwnerSeleniumTests.java).

O teste abre a página inicial, segue **Find Owners → Add Owner**, preenche o formulário e envia pela interface. Servidor (porta aleatória), controlador MVC, Bean Validation, banco H2 e navegador são reais; não há mocks. Cada teste usa um sobrenome exclusivo (`Recreio` + 8 caracteres aleatórios) e remove o registro criado ao final. A aplicação não foi modificada.

## Técnicas

Partições de equivalência e análise de valores-limite sobre as regras do `Owner`: todos os campos com `@NotBlank` e telefone com `@Pattern(\d{10})`. O nome tem coluna `VARCHAR(30)` no H2.

## Cenários e evidências

| # | Cenário | Partição / limite | Verificações | Evidência |
| --- | --- | --- | --- | --- |
| 1 | Cadastro válido | Todos os campos válidos; telefone com 10 dígitos | Redireciona para `/owners/{id}`; mensagem "New Owner Created"; nome, endereço, cidade e telefone na tela; linha exata no banco; total de proprietários +1; dados mantidos após recarregar; busca pelo sobrenome leva ao mesmo proprietário | [01](01-cadastro-valido.png), [02](02-busca-proprietario-cadastrado.png) |
| 2 | Nome com 30 caracteres | Limite superior aceito pela coluna | Cadastro concluído; valor completo na tela e no banco, sem truncamento | [03](03-nome-30-caracteres.png) |
| 3 | Telefone com 9 dígitos | Abaixo do limite de 10 | Somente `telephone` com erro "Telephone must be a 10-digit number"; mesma URL `/owners/new`; valores preservados; nada gravado | [04](04-telefone-nove-digitos.png) |
| 4 | Telefone com 11 dígitos | Acima do limite de 10 | Idem | [04](04-telefone-onze-digitos.png) |
| 5 | Telefone com letra | 10 caracteres, não numérico | Idem | [04](04-telefone-letra.png) |
| 6 | Telefone com símbolos | 10 caracteres com parênteses | Idem | [04](04-telefone-simbolos.png) |
| 7 | Formulário vazio | Todos os campos obrigatórios em branco | Os cinco campos marcados com erro; nada gravado | [05](05-formulario-vazio.png) |
| 8 | Endereço e cidade só com espaços | Texto em branco não vazio | Somente `address` e `city` com erro; nada gravado | [06](06-campos-com-espacos.png) |

Os cenários 3 a 6 são um único teste parametrizado (`@CsvSource`) com quatro execuções; a classe totaliza **oito casos executados**.

## Execução

Na raiz do repositório, com Chrome instalado:

```text
mvn -B -f spring-petclinic/pom.xml "-Dtest=SofiaOwnerSeleniumTests" test
```

Resultado individual: **8 aprovados, zero falhas, erros ou ignorados** — [resultado-execucao.txt](resultado-execucao.txt).

Execução conjunta com os demais testes Selenium do grupo e o controlador de proprietários:

```text
mvn -B -f spring-petclinic/pom.xml "-Dtest=SofiaOwnerSeleniumTests,WeslleyVisitSeleniumTests,PedroPetEditSeleniumTests,LucasOwnerSeleniumTests,CadastrarAnimalSeleniumTest,OwnerControllerTests" test
```

Resultado conjunto: **43 aprovados, zero falhas, erros ou ignorados** (8 de Sofia, 9 de visitas, 9 de edição de animal, 1 de busca, 1 de cadastro de animal e 15 do controlador). A seleção não corresponde à suíte inteira do PetClinic.

Para confirmar que as asserções podem reprovar, a mensagem esperada do telefone foi alterada temporariamente para "11-digit": as quatro execuções parametrizadas falharam (`BUILD FAILURE`). O texto original foi restaurado antes da execução final.

As capturas são geradas em `spring-petclinic/target/selenium-evidence/cadastrar-proprietario/`; esta pasta contém cópias da execução individual aprovada.

## Limites

- Não foi avaliado nome com 31 caracteres ou mais. O `Owner` não declara `@Size`, e o comportamento acima do limite da coluna não foi verificado nesta contribuição.
- O controlador registra a mensagem geral "There was an error in creating the owner." como atributo flash, mas devolve o formulário sem redirecionar, e o template do formulário não tem área para essa mensagem. Por isso os testes verificam apenas as mensagens por campo.
- Execução local em um único navegador e sistema operacional.
