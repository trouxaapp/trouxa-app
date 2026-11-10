# Ferramentas do portal do X

Scripts que dirigiram o portal de desenvolvedor do X em 10/11/2026 para criar a
conta de developer, o app e as chaves do @trouxaapp. Ficam aqui para o dia em
que for preciso rotacionar chave, refazer o app ou conferir configuração.

Não fazem parte do app nem da publicação: a publicação é
[`../../automation/`](../../automation/), que só usa os secrets do GitHub.

## O que existe lá, criado por estes scripts

- Conta de developer "Trouxa" (id `2120217077688811569`), projeto
  `Default Project (Pay Per Use)`.
- App id `33627632`, permissão **Read and write**, callback e site
  `https://trouxaapp.github.io/trouxa-app/`.
- Segredos no cofre (`Chaves/X API (OAuth 1.0a)`) e nos secrets do repositório:
  `X_API_KEY`, `X_API_SECRET`, `X_ACCESS_TOKEN`, `X_ACCESS_SECRET`.

## Como usar

Precisa do navegador do Playwright (instalado globalmente). `pw.cjs` acha o
módulo sozinho — procura em `PW_PATH`, depois no npm global e por fim num
`playwright` local — e guarda o perfil do browser em `~/.trouxa-x`, para não
depender do nome do usuário da máquina. A senha da conta fica em `config.json`
(e no cofre, em `Contas/X`).

```bash
node x.cjs dump [url]             # url, título e texto visível
node x.cjs links [url]            # links da página
node x.cjs click "<texto>" [url]  # clique real no elemento com esse texto
node x.cjs eval "<js>" [url]      # roda JS e imprime o retorno
node fluxo.cjs                    # login no X, lê o código no Proton e confirma
node auth.cjs                     # permissões do app + URLs exigidas
node chaves.cjs consumer|token|bearer   # gera e captura chaves (aparecem 1x)
node pagar.cjs --cartao=... --validade=... --cvc=... --nome=... --cep=...
```

As capturas ficam em `out/` (ignorado no git, porque contém segredo em claro).

## Pegadinhas aprendidas

- O login do X manda código por e-mail: `fluxo.cjs` entra no Proton pelo mesmo
  browser e lê o código sozinho. A janela do código é curta, então é tudo num
  run só.
- O campo de e-mail é controlado por React: `fill()` sozinho não basta, precisa
  do setter nativo + eventos (`input`/`change`), senão a tela responde
  "E-mail inválido".
- O botão de confirmar de um diálogo tem que ser procurado DENTRO do diálogo;
  `document.querySelector("button")` pega botão de fundo e a ação se perde.
- A permissão do app precisa estar em **Read and write** ANTES de gerar o Access
  Token; token gerado antes nasce só de leitura.
- A API do X é pré-paga: com saldo zerado o post volta
  `402 credits depleted`, mesmo com tudo certo. O crédito é comprado em
  Billing > Credits (mínimo US$ 5, com a primeira compra dobrada).
