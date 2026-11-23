# Defeitos reproduzidos de PgnHolder

Os defeitos foram reproduzidos contra a fonte original do commit `4750daa` e tratados nesta contribuição. Estão registrados nas Issues [#21](https://github.com/sofiarecreio/trabalho-qet/issues/21) e [#22](https://github.com/sofiarecreio/trabalho-qet/issues/22), que permanecem abertas até a integração das correções.

## Issue #21 — arquivo não fechado quando a criação do iterador falha

**Componente:** `PgnHolder.loadPgn(LargeFile)`.

**Passos:** fornecer um LargeFile cujo método iterator lance uma exceção; chamar loadPgn; verificar se close foi executado. O teste usa um recurso controlado que registra o fechamento.

**Esperado:** liberar o recurso, preservando a falha original.

**Obtido na fonte anterior:** a exceção acontece na construção de PgnIterator, antes de entrar no try/finally; close não é chamado.

**Correção nesta contribuição:** construir o iterador dentro do try-with-resources que controla o LargeFile.

**Teste:** `PedroPgnHolderQualityTest.falhaNaCriacaoDoIteradorTambemFechaRecurso`.

## Issue #22 — savePgn retorna normalmente quando o destino não pode ser aberto

**Componente:** `PgnHolder.savePgn()`.

**Passos:** configurar arquivo dentro de um diretório inexistente; chamar savePgn; observar retorno e erro.

**Esperado no contrato corrigido:** informar a falha ao chamador por exceção com causa e contexto do destino.

**Obtido na fonte anterior:** a IOException é capturada e impressa no console, mas o método retorna normalmente. O chamador não consegue distinguir sucesso e falha pelo retorno.

**Correção nesta contribuição:** propagar UncheckedIOException, preservar a causa e o destino, fechar os recursos automaticamente e conferir erros suprimidos pelo PrintWriter por checkError. A propagação é uma mudança intencional de contrato e está documentada.

**Teste:** `PedroPgnHolderQualityTest.salvarEmDestinoInexistentePropagaCausaDeIO`.

## Evidência comum

Os oito testes de inspeção apresentam duas falhas na fonte anterior e zero na fonte corrigida. [Execução anterior](../../inspecao/pgnholder/testes-fonte-anterior.txt), [execução corrigida](../../inspecao/pgnholder/testes-regressao.txt) e [relatório de inspeção](../../inspecao/pgnholder/README.md).
