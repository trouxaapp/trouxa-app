# Trouxa App

Android app that tracks bills moving through the Brazilian Congress and key economic indicators, with a hard rule: the app never invents data.

## Data integrity principles

These are not aspirations, they are enforced in code:

- **No invented data.** Every value on screen is either raw data from an official API, a value derived from raw data by a documented rule (e.g. the activity score, see `Temperatura.kt`), or human-curated analysis that carries sources.
- **Mandatory sources and reference dates.** Curated content must ship with `fonte`, `fonte_url` and `data_referencia`. Items missing any of these are dropped during sync (`FeedSyncWorker`), so sourceless content cannot reach the UI.
- **Honest staleness.** Fetch timestamps are stored per row and shown in the UI. When a bill has no recorded movement, the card says so instead of showing a fake status. Sync failures are counted and surfaced in a sync log (visible in Settings), not swallowed behind a green check.
- **Opinion only via human curation, with sources.** Pros/cons/context for a bill come from a curated feed maintained by humans. Curation entries without at least one source URL are rejected by the app. Nothing opinionated is auto-generated.
- **A alma, em três camadas.** Fato e opinião nunca se misturam:
  1. **Leitura** — o que o projeto faz, por regra sobre o texto oficial (`LeiExplicada.kt`).
  2. **Veredito** — o "e daí?", sempre amarrado num campo real ou numa regra declarada em `Alma.kt` (o carimbo e a frase saem de `seloDoProjeto`/`vereditoDoProjeto`, e o comentário de cada ramo diz em que fato ele se apoia). Curadoria humana vence a regra.
  3. **Esculacho** — piada de processo, sem número ("Quem paga a conta não é quem assina embaixo."). Não afirma fato novo, então não precisa de fonte.
- **Plain language by rules, not by guessing.** `LeiExplicada.kt` turns an official ementa into one sentence ("Libera R$ 300 milhões fora do Orçamento já aprovado para...") with fixed patterns: credit openings, amendments to a named law, decree sustations, and so on. When no pattern fits, it says nothing. The detail screen labels the result as an automatic reading and keeps the official text next to it. Hard terms (crédito extraordinário, sustar, quórum, maioria absoluta, relator, apensado...) link to the Senate or Congress glossary.

## Data sources

- **Camara dos Deputados open data API** (`dadosabertos.camara.leg.br`): bills (PL, PEC, PLP, MPV, MP, PDL), full author lists (deputies resolved by id, never by name search), tramitacao history, deputy profiles and CEAP expenses.
- **Banco Central do Brasil SGS API**: economic indicator series, cached with the official reference date as published by the BCB. The UI always renders the reference date next to the value.
- **Curated JSON feed** in [`/feed`](feed/) of this repo: highlights, "did you know" items and per-bill analysis. Every item carries mandatory sources (see "Content curation" below).

The daily panel on the Hoje tab (`data/painel/`) reads these directly, in parallel, and caches the last good answer per block:

| Block | Source |
|---|---|
| Dollar, Selic, IPCA 12 months, minimum wage | BCB SGS series 1, 432, 13522, 1619 |
| Ibovespa | B3 public quotation endpoint |
| What got more expensive | IBGE SIDRA table 7060 (IPCA by subitem) |
| Chamber today, recent floor votes, party orientation | Camara open data `/eventos`, `/votacoes`, `/proposicoes` |
| Senate floor results | Senado open data `/plenario/resultado/{date}` |
| Credits outside the budget, per year | Camara `/proposicoes?siglaTipo=MPV`, summing "abre crÃ©dito extraordinÃ¡rio" ementas |
| Election | TSE official results JSON |
| News | RSS from AgÃªncia CÃ¢mara, AgÃªncia Senado and AgÃªncia Brasil |

Nothing in the panel is computed beyond sums, differences and "when did this last change". Every card shows its source and reference date.

## Architecture

Single-module Android app. Kotlin, Jetpack Compose (Material 3), Hilt for DI, Room for storage, Retrofit/OkHttp for network, WorkManager for background sync.

- **Room cache is the source of truth.** The UI only observes the database via Flows (`TrouxaDatabase`: `leis`, `indicadores`, `deputados`, `despesas_deputado`, `feed_items`, `sync_log`). Network results are written to Room first, then rendered.
- **WorkManager sync workers** keep the cache fresh: `LawSyncWorker` (bills pipeline via `LeisRepository`), `FeedSyncWorker` (curated feed with source validation), `IndicadorSyncWorker` (BCB series), plus notification workers. Each run writes an entry to the `sync_log` table.
- **Bills pipeline** (`LeisRepository.syncAll`): list bills presented in the last 30 days, fetch detail + all authors + tramitacoes for new ones, classify theme, compute the activity score, then re-apply human curation to new and existing rows so corrections actually propagate. Old uncurated rows are purged.
- **No analytics, no trackers, no third-party SDKs phoning home.** Network traffic goes to the public sources listed above and this repo's raw feed files. That is it.

## How to build

Requirements: Android Studio (current stable), JDK 17, Android SDK 35.

Debug build:

```
./gradlew assembleDebug
```

Or just open the project in Android Studio and run. No API keys or secrets are needed for a debug build; all data sources are public.

On Windows, prefer the wrapper:

```
./tools/build.ps1 testDebugUnitTest assembleDebug
```

It exists for two real machine problems: the JVM derives `-Djava.library.path` from
`PATH`, and a stray quote or a space in `PATH` makes the Java launcher split the
argument and kill the test worker before any test runs; and a stale `JAVA_HOME`
(a JDK that is no longer installed) makes `gradlew` refuse to start. The wrapper
sanitizes `PATH` for its own process and falls back to the `java` on `PATH`. On
Linux/CI plain `./gradlew` is fine.

### Release build

Release builds are signed with a keystore that is not in the repo. Create a `keystore.properties` file at the project root (it is gitignored) with:

```
storeFile=/absolute/path/to/release.jks
storePassword=...
keyAlias=...
keyPassword=...
```

Then:

```
./gradlew assembleRelease
```

## How content curation works

Curated content lives in this repo under `feed/` and is fetched by the app at sync time. Updating these files updates the app without shipping a new APK.

### `feed/feed.json`

Highlights and "did you know" items for the home feed:

```json
{
  "versao": 6,
  "atualizado": "2026-11-10",
  "itens": [
    {
      "id": 1,
      "tipo": "destaque",
      "titulo": "...",
      "numero": "R$ 265 → R$ 923",
      "selo": "SUBIU 248%",
      "veredito": "Uma linha com o “e daí?”, vencendo a regra automática.",
      "texto": "...",
      "fonte": "Name of the source",
      "fonte_url": "https://...",
      "data_referencia": "2026-06-01",
      "imagem": { "url": "https://...", "credito": "arte: @trouxaapp" },
      "ativo": true
    }
  ]
}
```

`numero`, `selo`, `veredito` and `imagem` are optional: a short headline figure,
a stamp, the curated punch line and an artwork. `fonte`, `fonte_url` and
`data_referencia` are mandatory, and the veredito inherits that contract — it
only appears next to a visible source. The app drops any item missing them
during sync and logs how many were discarded. An emptied remote feed empties
the app, so retracting bad content actually works.

### `feed/curadoria.json`

Per-bill human analysis, keyed by the bill id from the Camara API:

```json
{
  "versao": 1,
  "atualizado": "2026-06-10",
  "leis": {
    "2345678": {
      "resumo": "...",
      "pros": ["..."],
      "contras": ["..."],
      "contexto": "...",
      "fontes": [
        { "titulo": "Name of the source", "url": "https://..." }
      ]
    }
  }
}
```

At least one entry in `fontes` with a non-empty `url` is mandatory. Entries without sources are rejected by the app. Curation is re-applied on every sync, so fixing a wrong entry in the JSON fixes it on every device.

## Site

Static site in `docs/`, published by GitHub Pages. It reads the official sources straight from the browser, no backend.

## Shareable cards

Every highlight and every bill can be shared as a 1080x1920 image (`util/ShareCard.kt`), drawn on device. A card always carries the figure, the source, the source domain and the reference date together, so it still holds up when it is forwarded without context. The same file is the only place card layout lives. When there is a stamp (`selo`) or a verdict, they go on the card too: the stamp as a crooked stamp, the verdict behind a red bar, so the reader never confuses the data with the reading.

## Publicação no X

The account [@trouxaapp](https://x.com/trouxaapp) is fed by `automation/`, three
times a day, from a hand-curated queue in [`feed/posts.json`](feed/posts.json).
The post is not the raw JSON: the script assembles stamp + figure + sentence +
source + link, caps it at 280 characters (dropping hashtag, then the verdict,
then a sentence — never the stamp, figure, source or link) and fails instead of
cutting a sentence in half. The poster is composed on the spot: uncensored AI
background ("sea of shit"), the sitting deputy's official photo stylized in
black-and-white with a red duotone, crooked stamp, giant figure and the source
at the foot. If the image provider falls over, the poster still ships with
procedural brown waves. Details and secrets in
[`automation/README.md`](automation/README.md).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Short version: factual error reports are the highest priority issue type, and every curated claim needs a verifiable source.

## License

MIT. See [LICENSE](LICENSE).

