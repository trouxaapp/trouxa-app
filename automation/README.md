# Automação do X

Publica no [@trouxaapp](https://x.com/trouxaapp) as verdades do app, com cartaz
montado na hora. Nada de post cru de JSON: cada post é curado em
[`../feed/posts.json`](../feed/posts.json) e o cartaz é composto aqui.

## Como o post é montado

1. `lib/legendas.mjs` monta o texto — carimbo, número, frase, fonte e link —
   e garante que caiba em 280 caracteres. A ordem de sacrifício é: hashtag,
   veredito, e só então uma frase do texto. Carimbo, número, fonte e link
   **nunca** saem.
2. `lib/imagem.mjs` gera o fundo por IA **sem censura de prompt**
   (`pollinations` grátis por padrão, `venice` se quiser e tiver chave).
3. `lib/camara.mjs` resolve a foto oficial do político quando o post traz
   `imagem.politico` — e loga qual id achou, porque busca por nome pode errar.
4. `lib/cartaz.mjs` monta o PNG 1080x1350: mar de merda (ondas procedurais ou
   arte de IA), foto do político em P&B com duotone vermelho, carimbo torto,
   número gigante e a fonte no pé. Se a IA cair, o cartaz sai com as ondas
   procedurais — nunca sai vazio.
5. `lib/x.mjs` publica com OAuth 1.0a em `node:crypto` puro (o upload de mídia
   da v1.1 não aceita Bearer) e o tweet na v2.

## Rodar na mão

```bash
cd automation
npm install
node publicar.mjs --dry-run --todos     # monta a fila inteira, não publica
node publicar.mjs --dry-run             # só o post desta data/slot
node publicar.mjs --id=cesta-2010-2026  # publica um post específico
```

O `--dry-run` grava tudo em `automation/_out/` (`.txt` + `.png`) para revisar.
Ele não exige credenciais; a publicação sim.

Flags: `--slot=0|1|2`, `--provider=pollinations|venice|nenhum`,
`--sem-imagem`, `--hashtags`, `--data=YYYY-MM-DD`.

## Rodar sozinho

`.github/workflows/publicar-x.yml` roda 3x por dia (cron em UTC) e chama o
script. Ele precisa destes secrets no repositório:

| Secret | Onde pegar |
|---|---|
| `X_API_KEY` | portal de desenvolvedor do X, app com permissão de escrita |
| `X_API_SECRET` | idem |
| `X_ACCESS_TOKEN` | idem, token da conta @trouxaapp |
| `X_ACCESS_SECRET` | idem |
| `VENICE_API_KEY` | imagem por IA sem censura (provedor padrão) |
| `POLLINATIONS_TOKEN` | opcional; sem chave também funciona, mas leva 402 em pico |

Variáveis opcionais (Settings > Variables): `VENICE_IMAGE_MODEL` (padrão
`venice-sd35`), `SITE_URL` (link que entra no post).

O app do X precisa de **write** e de **OAuth 1.0a** ligados, e a conta precisa
de **créditos**: a API do X é pré-paga, e com saldo zerado o post volta
`402 credits depleted` mesmo com tudo certo.

Se o provedor de imagem cair, o cartaz sai mesmo assim, com as ondas
procedurais (marrom). Nunca sai vazio, nunca trava a publicação.

## Adicionar um post

Acrescente um objeto em `feed/posts.json`. Campos: `id`, `numero`, `selo`,
`texto` (~120 caracteres), `veredito` (opcional, curto), `fonte`, `fonte_url`,
`data_referencia` e `imagem`:

```json
"imagem": {
  "politico": "Nome do deputado",
  "uf": "SP",
  "prompt": "sea of brown mud, cartoon, no text",
  "foto_url": "opcional: crava a foto e ignora a busca por nome"
}
```

Regras que o script cobra: `texto` + `fonte` + `fonte_url` + `data_referencia`
são obrigatórios, e o post final tem que caber em 280 — se não couber, ele
falha em vez de cortar no meio da frase.
