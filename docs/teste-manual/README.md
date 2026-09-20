# Evidências dos testes manuais

As evidências são organizadas por caso de teste para manter a rastreabilidade entre o TestLink, a execução e os defeitos encontrados.

```text
docs/teste-manual/
├── CTM-03-cadastrar-animal/
│   ├── caso-teste-testlink.pdf
│   └── evidencias/
└── CTM-05-registrar-visita/
    └── evidencias/
```

## CTM-03 — Cadastrar novo animal

- `caso-teste-testlink.pdf`: especificação exportada do TestLink.
- `evidencias/formulario-cadastro.png`: formulário utilizado na execução.
- `evidencias/animal-cadastrado.png`: resultado do cadastro.

## CTM-05 — Registrar visita veterinária

- `evidencias/erro-500-descricao-256-caracteres.png`: erro interno reproduzido ao informar uma descrição com 256 caracteres. A mensagem mostra que a coluna `DESCRIPTION` aceita no máximo 255 caracteres.

Novos casos devem seguir o padrão `CTM-NN-nome-do-caso/`, mantendo documentos do caso na raiz e capturas em `evidencias/`.
