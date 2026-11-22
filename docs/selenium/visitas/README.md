# Testes de sistema — cadastro de visitas

Responsável: Weslley Ribeiro. Execução: 22/11/2026.

Implementação: [WeslleyVisitSeleniumTests.java](../../../spring-petclinic/src/test/java/org/springframework/samples/petclinic/owner/WeslleyVisitSeleniumTests.java).

O fluxo começa nos detalhes do proprietário, acessa Add Visit, preenche o formulário e verifica a resposta pela interface. Os casos válidos também conferem data e descrição no H2 e recarregam a página para confirmar a persistência. Os casos inválidos verificam ausência de gravação e de visita na tela do proprietário.

## Casos executados

`D` representa a data de execução. As datas são calculadas a cada teste.

| Caso | Entrada | Resultado verificado | Captura |
| --- | --- | --- | --- |
| 01 | D+1, descrição válida | Mensagem de sucesso; exatamente uma visita, com data e descrição exatas, preservada após atualizar a página | [Cadastro válido](01-visita-valida.png) |
| 02 | D+3, descrição com 255 caracteres | Cadastro aceito, sem truncar a descrição na página ou no banco | [Limite aceito](02-descricao-255.png) |
| 03 | D+1, descrição vazia | Erro no campo; nenhum cadastro | [Descrição vazia](03-descricao-vazia.png) |
| 04 | D+1, descrição com três espaços | Erro no campo; nenhum cadastro | [Descrição em branco](04-descricao-espacos.png) |
| 05 | D+1, descrição com 256 caracteres | Erro de tamanho; descrição e data preservadas; nenhum cadastro | [Limite rejeitado](05-descricao-256.png) |
| 06a | D-1, restrição HTML ativa | Navegador bloqueia o envio; nenhum cadastro | [Data passada no navegador](06-data-navegador--1.png) |
| 06b | D, restrição HTML ativa | Navegador bloqueia o envio; nenhum cadastro | [Data atual no navegador](06-data-navegador-0.png) |
| 07a | D-1, limite HTML removido no teste | Servidor devolve erro de data; campos preservados; nenhum cadastro | [Data passada no servidor](07-data-servidor--1.png) |
| 07b | D, limite HTML removido no teste | Servidor devolve erro de data; campos preservados; nenhum cadastro | [Data atual no servidor](07-data-servidor-0.png) |

As técnicas usadas são partições de equivalência e análise de valores-limite: descrições válidas/vazias/em branco, tamanhos 255/256 e datas antes/no/depois do limite permitido. Os dois testes parametrizados de data produzem quatro execuções; a suíte totaliza **nove casos executados**.

## Ambiente e independência

- Spring Boot 4.1.0, servidor real em porta aleatória e H2 exclusivo desta classe.
- Selenium 4.43.0 e Chrome 158.0.8127.36 em modo headless, aproveitando a dependência e o padrão disponibilizados por Lucas.
- Java 21.0.9, testes compilados para Java 17.
- Proprietário e animal próprios por caso; preparação e limpeza por JDBC, sem mocks no fluxo de cadastro.
- Um navegador novo por execução, esperas explícitas por navegação e elementos, sem pausas fixas.

O campo de data recebe valor ISO para evitar dependência do formato regional do Chrome. Nos casos 06, o atributo `min` permanece ativo. Nos casos 07, somente esse atributo é retirado pelo teste para verificar que o servidor também rejeita a entrada, independentemente da restrição do navegador. A aplicação não é modificada.

O redirecionamento válido pode incluir `;jsessionid=...`; a espera aceita essa forma sem deixar de exigir a URL exata do proprietário criado pelo caso.

## Execução e resultado

Na raiz do repositório:

```text
mvn -B -f spring-petclinic/pom.xml "-Dtest=WeslleyVisitSeleniumTests" test
```

Execução conjunta com as regressões relacionadas:

```text
mvn -B -f spring-petclinic/pom.xml "-Dtest=WeslleyVisitSeleniumTests,LucasOwnerSeleniumTests,LucasOwnerIntegrationTests,VisitControllerTests" test
```

Resultado final da execução conjunta: **18 testes aprovados**, zero falhas, zero erros e zero ignorados: nove casos novos de visitas, um Selenium de Lucas, dois de integração de Lucas e seis do controlador de visitas. Essa seleção não corresponde à suíte inteira do PetClinic.

As capturas são geradas em `spring-petclinic/target/selenium-evidence/visitas/`; esta pasta contém cópias da execução final. Os relatórios JUnit ficam em `spring-petclinic/target/surefire-reports/`. Não há alterações de produção, do POM ou dos testes de Lucas nesta implementação.
