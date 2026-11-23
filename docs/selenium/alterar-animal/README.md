# Testes de sistema — alterar informações do animal

Responsável pela atividade: Pedro Piaes. Execução local: 22/11/2026.

Implementação: [PedroPetEditSeleniumTests.java](../../../spring-petclinic/src/test/java/org/springframework/samples/petclinic/owner/PedroPetEditSeleniumTests.java).

O teste inicia na página do proprietário, acessa a edição pelo link do animal, preenche o formulário e envia pela interface. O servidor, o controlador MVC, a validação, o banco H2 e o navegador são reais. Não há mocks no fluxo de edição.

## Cenários e evidências

`D` é a data de execução. Os dados de nascimento são calculados em cada caso.

| Caso | Entrada | Resultado verificado | Captura |
| --- | --- | --- | --- |
| 01 | Nome novo, nascimento válido e troca de cat para dog | Mensagem de sucesso; nome, data e tipo exatos no banco e na tela, inclusive após recarregar | [Edição válida](01-edicao-valida.png) |
| 02 | Mesmo nome do próprio animal, nascimento em D | Edição aceita; não há falso conflito de duplicidade | [Nascimento hoje](02-nascimento-hoje.png) |
| 03 | Nome com 30 caracteres | Edição aceita sem truncamento | [Limite aceito](03-nome-30.png) |
| 04 | Nome vazio | Erro required; banco preservado | [Nome vazio](04-nome-vazio.png) |
| 05 | Nome com três espaços | Erro required; banco preservado | [Nome em branco](05-nome-espacos.png) |
| 06 | Nome de outro animal do mesmo proprietário | Erro is already in use; ambos os animais preservados | [Duplicidade](06-nome-duplicado.png) |
| 07 | Nascimento em D+1 | Erro invalid date; banco preservado | [Data futura](07-data-futura.png) |
| 08 | Nascimento vazio | Erro required; banco preservado | [Data vazia](08-data-vazia.png) |
| 09 | Nome com 31 caracteres | Erro de tamanho; banco preservado | [Limite rejeitado](09-nome-31.png) |

Técnicas: partições de equivalência e análise de valores-limite, com 30/31 caracteres, hoje/amanhã, nome próprio/nome de outro animal e valores válidos/vazios/em branco. Os resultados esperados seguem PetValidator, PetController e as mensagens efetivamente renderizadas pela aplicação.

## Independência e verificações

Cada caso cria um proprietário e dois animais próprios por JDBC. O banco H2 tem nome exclusivo da suíte; servidor usa porta aleatória. Um navegador novo é aberto por caso. O teardown fecha o navegador e remove os dados criados.

Nos casos válidos, verificam-se nome, data, tipo, vínculo com o proprietário, quantidade de animais, apresentação na tela e persistência após atualizar a página. Nos casos inválidos, nome/data/tipo anteriores devem permanecer no banco. Em todos os casos, o segundo animal continua intacto.

O campo de data recebe valor ISO via Javascript para evitar dependência do formato regional do controle HTML. O envio é feito pelo botão real, sem retirar regras de validação da aplicação. As esperas usam elementos e navegação, sem pausas fixas. Não se usa staleness de um botão antigo como condição de sucesso: aguarda-se a mensagem de erro efetivamente renderizada.

## Ambiente, comandos e resultados

Ambiente: macOS ARM64, OpenJDK 21.0.11, Maven 3.9.15, Selenium 4.43.0, Chrome 157.0.8103.79 e ChromeDriver 157.0.8103.78, Spring Boot 4.1.0, H2 em memória. O compilador do PetClinic tem alvo Java 17.

Na raiz do repositório, com Maven usando JDK 21:

```sh
mvn -B -f spring-petclinic/pom.xml -Dtest=PedroPetEditSeleniumTests test
```

Resultado individual: **9 aprovados, zero falhas, erros ou ignorados**. O resumo da execução está em [resultado-execucao.txt](resultado-execucao.txt).

Validação conjunta com os testes existentes:

```sh
mvn -B -f spring-petclinic/pom.xml -Dtest=PedroPetEditSeleniumTests,WeslleyVisitSeleniumTests,LucasOwnerSeleniumTests,CadastrarAnimalSeleniumTest,LucasOwnerIntegrationTests,PetControllerTests,VisitControllerTests test
```

Resultado conjunto: **42 aprovados, zero falhas, erros ou ignorados** — [resumo real da execução](resultado-conjunto.txt). Inclui os nove casos de Pedro, nove de visitas, busca de proprietário, cadastro de animal, dois testes de integração de Lucas e as validações dos controladores. A seleção não representa a suíte inteira do PetClinic nem cinco funcionalidades Selenium. Posteriormente, a contribuição de cadastro de proprietário foi incorporada à main; os 42 casos aqui descritos permanecem como registro histórico dessa seleção. Uma reexecução da seleção apresentou erro transitório no teste de visitas de Weslley, que passou isoladamente; não se declara estabilidade absoluta a partir da rodada inicial.

As capturas são geradas em `spring-petclinic/target/selenium-evidence/alterar-animal/`; esta pasta contém cópias reais da execução conjunta aprovada. Os relatórios JUnit ficam em `spring-petclinic/target/surefire-reports/`.

Não houve alteração no código de produção do PetClinic nem nas suítes dos colegas. O Maven também executou as verificações de formatação e Checkstyle. O aviso de ausência de módulo CDP específico do Chrome não impediu esses testes WebDriver; nenhum caso usa a API DevTools.

## Revalidação individual

Em 23/11/2026, sobre a main `3934419`, PedroPetEditSeleniumTests foi executada novamente: nove testes aprovados, zero falhas, erros ou ignorados, com verificações de formatação e Checkstyle aprovadas. A revalidação foi restrita à suíte de edição de animal. O resumo está em [revalidacao-individual.txt](revalidacao-individual.txt).
