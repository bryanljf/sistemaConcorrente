# Cadeia de Produção e Comercialização de Veículos

Trabalho da disciplina de **Programação Concorrente** — Ciência da Computação.

Sistema distribuído em Java que simula uma fábrica de veículos, lojas remotas
e clientes concorrentes, usando **somente semáforos** como mecanismo de
sincronização e **sockets TCP** para a comunicação cliente–servidor entre os
processos remotos.

## Estrutura do projeto

```
programacaoConcorrente/
├── src/
│   ├── fabrica/          fábrica, estações, esteira, logs
│   ├── loja/             servidores remotos das lojas
│   └── cliente/          clientes que compram veículos
├── scripts/              .bat para compilar e executar cada processo
├── docs/
│   ├── DOCUMENTACAO.md   documentação completa do código
│   └── GUIA_DEFESA.md    guia de estudos / perguntas e respostas
└── bin/                  classes compiladas (gerado pelo compile.bat)
```

## Como executar

1. Compilar:

   ```
   scripts\compile.bat
   ```

2. Em janelas separadas (ordem importa):

   ```
   scripts\run-fabrica.bat
   scripts\run-loja1.bat
   scripts\run-loja2.bat
   scripts\run-loja3.bat
   scripts\run-clientes.bat
   ```

3. Os arquivos de log são gerados na raiz do projeto:
   `log_producao.txt`, `log_venda_loja.txt`,
   `loja_<id>_recebimento.txt`, `loja_<id>_venda_cliente.txt`.

Para mais detalhes consulte [`docs/DOCUMENTACAO.md`](docs/DOCUMENTACAO.md) e o
[`docs/GUIA_DEFESA.md`](docs/GUIA_DEFESA.md).
