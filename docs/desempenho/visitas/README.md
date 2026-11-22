# Teste não funcional — desempenho do cadastro de visitas

Responsável: Weslley Ribeiro. Execução: 22/11/2026.

Implementação: [WeslleyVisitPerformanceTests.java](../../../spring-petclinic/src/test/java/org/springframework/samples/petclinic/owner/WeslleyVisitPerformanceTests.java).

## Requisito verificado

| Identificador | Atributo ISO/IEC 25010 | Requisito |
| --- | --- | --- |
| RNF-01 | Eficiência de desempenho — comportamento em relação ao tempo | Abrir o formulário de visita e concluir o cadastro em até **1 s no percentil 95**, em 30 requisições sequenciais |

O PetClinic não define requisitos de desempenho. O limite de 1 s foi escolhido pelo grupo como tempo de resposta aceitável para uma tela de cadastro usada por um atendente. Ele é uma decisão do grupo, não um requisito do sistema original.

## Procedimento

1. A aplicação sobe com servidor real em porta aleatória e banco H2 exclusivo da classe. Não há mocks: o fluxo passa pelo controlador, pela validação, pelo repositório JPA e pela renderização Thymeleaf.
2. Antes do teste, um proprietário e um animal próprios são criados por JDBC. Depois do teste, eles e suas visitas são removidos.
3. Cada iteração mede duas operações com `System.nanoTime()`:
   - **formulário:** GET em `/owners/{id}/pets/{id}/visits/new`;
   - **cadastro:** POST com data D+1 e descrição válida, incluindo o redirecionamento até a página do proprietário.
4. As 5 primeiras iterações são aquecimento e são descartadas: a primeira chamada inclui carregamento de classes e compilação JIT, que não representam o uso normal.
5. São medidas as 30 iterações seguintes. O percentil 95 é calculado pelo método do posto mais próximo.

O teste também verifica o funcionamento durante a medição. Cada resposta deve ter status 200, o formulário deve conter o campo de descrição e o cadastro deve terminar na página do proprietário com a mensagem "Your visit has been booked". Ao final, o banco precisa conter exatamente 35 visitas. Assim, uma resposta rápida de erro não é aceita como bom desempenho.

## Resultados

Ambiente: AMD Ryzen 5 7600 (6 núcleos, 12 threads), 15,7 GB de RAM, Windows 11 Pro, Java 21.0.9, Spring Boot 4.1.0, H2 em memória. Cliente e servidor na mesma máquina.

| Execução | Operação | Mínimo | Média | p95 | Máximo | Limite p95 | Resultado |
| --- | --- | ---: | ---: | ---: | ---: | ---: | --- |
| 1 | Formulário (GET) | 6 ms | 7,6 ms | 9 ms | 10 ms | 1000 ms | Aprovado |
| 1 | Cadastro (POST + redirecionamento) | 14 ms | 15,9 ms | 19 ms | 19 ms | 1000 ms | Aprovado |
| 2 | Formulário (GET) | 6 ms | 7,4 ms | 9 ms | 9 ms | 1000 ms | Aprovado |
| 2 | Cadastro (POST + redirecionamento) | 13 ms | 15,8 ms | 18 ms | 18 ms | 1000 ms | Aprovado |
| 3 | Formulário (GET) | 6 ms | 7,5 ms | 9 ms | 9 ms | 1000 ms | Aprovado |
| 3 | Cadastro (POST + redirecionamento) | 13 ms | 15,5 ms | 18 ms | 18 ms | 1000 ms | Aprovado |

Na execução conjunta com as suítes de visitas e de Lucas, os **19 testes** foram aprovados (o teste de desempenho, nove Selenium de visitas, um Selenium e dois de integração de Lucas e seis do controlador de visitas). Nessa execução, o p95 foi de 11 ms no formulário e 19 ms no cadastro.

Para confirmar que o teste pode reprovar, o limite foi reduzido temporariamente para 5 ms. As duas verificações de tempo falharam, e o limite voltou a 1000 ms.

## Limitações

- É um teste de tempo de resposta com **um usuário**, em requisições sequenciais. Não é um teste de carga nem de concorrência e não permite conclusões sobre muitos usuários simultâneos.
- Os tempos dependem da máquina e não incluem latência de rede, pois cliente e servidor estão no mesmo computador.
- O H2 em memória é mais rápido que um banco em disco ou remoto, como o MySQL ou o PostgreSQL suportados pelo PetClinic.
- O tempo é medido no cliente HTTP, sem o tempo de renderização da página no navegador.

## Execução

Na raiz do repositório:

```text
mvn -B -f spring-petclinic/pom.xml "-Dtest=WeslleyVisitPerformanceTests" test
```

Os tempos de cada execução são exibidos no console e gravados em `spring-petclinic/target/performance-evidence/visitas-desempenho.txt`.
