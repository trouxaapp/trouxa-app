# Trouxa App

Android app that tracks bills moving through the Brazilian Congress and key economic indicators, with a hard rule: the app never invents data.

## Data integrity principles

These are not aspirations, they are enforced in code:

- **No invented data.** Every value on screen is either raw data from an official API, a value derived from raw data by a documented rule (e.g. the activity score, see `Temperatura.kt`), or human-curated analysis that carries sources.
- **Mandatory sources and reference dates.** Curated content must ship with `fonte`, `fonte_url` and `data_referencia`. Items missing any of these are dropped during sync (`FeedSyncWorker`), so sourceless content cannot reach the UI.
- **Honest staleness.** Fetch timestamps are stored per row and shown in the UI. When a bill has no recorded movement, the card says so instead of showing a fake status. Sync failures are counted and surfaced in a sync log (visible in Settings), not swallowed behind a green check.
- **Opinion only via human curation, with sources.** Pros/cons/context for a bill come from a curated feed maintained by humans. Curation entries without at least one source URL are rejected by the app. Nothing opinionated is auto-generated.
- **Neutral tone.** The app presents facts and sourced analysis. No partisan framing, no editorializing in UI strings.

## Data sources

- **Camara dos Deputados open data API** (`dadosabertos.camara.leg.br`): bills (PL, PEC, PLP, MPV, MP, PDL), full author lists (deputies resolved by id, never by name search), tramitacao history, deputy profiles and CEAP expenses.
- **Banco Central do Brasil SGS API**: economic indicator series, cached with the official reference date as published by the BCB. The UI always renders the reference date next to the value.
- **Curated JSON feed** in [`/feed`](feed/) of this repo: highlights, "did you know" items and per-bill analysis. Every item carries mandatory sources (see "Content curation" below).

## Architecture

Single-module Android app. Kotlin, Jetpack Compose (Material 3), Hilt for DI, Room for storage, Retrofit/OkHttp for network, WorkManager for background sync.

- **Room cache is the source of truth.** The UI only observes the database via Flows (`TrouxaDatabase`: `leis`, `indicadores`, `deputados`, `despesas_deputado`, `feed_items`, `sync_log`). Network results are written to Room first, then rendered.
- **WorkManager sync workers** keep the cache fresh: `LawSyncWorker` (bills pipeline via `LeisRepository`), `FeedSyncWorker` (curated feed with source validation), `IndicadorSyncWorker` (BCB series), plus notification workers. Each run writes an entry to the `sync_log` table.
- **Bills pipeline** (`LeisRepository.syncAll`): list bills presented in the last 30 days, fetch detail + all authors + tramitacoes for new ones, classify theme, compute the activity score, then re-apply human curation to new and existing rows so corrections actually propagate. Old uncurated rows are purged.
- **No analytics, no trackers, no third-party SDKs phoning home.** Network traffic goes to the Camara API, the BCB API and this repo's raw feed files. That is it.

## How to build

Requirements: Android Studio (current stable), JDK 17, Android SDK 35.

Debug build:

```
./gradlew assembleDebug
```

Or just open the project in Android Studio and run. No API keys or secrets are needed for a debug build; all data sources are public.

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
  "versao": 3,
  "atualizado": "2026-06-10",
  "itens": [
    {
      "id": 1,
      "tipo": "destaque",
      "titulo": "...",
      "numero": "R$ 265 → R$ 923",
      "texto": "...",
      "fonte": "Name of the source",
      "fonte_url": "https://...",
      "data_referencia": "2026-06-01",
      "ativo": true
    }
  ]
}
```

`numero` is optional: a short headline figure shown big on the card and on the shareable image. `fonte`, `fonte_url` and `data_referencia` are mandatory. The app drops any item missing them during sync and logs how many were discarded. An emptied remote feed empties the app, so retracting bad content actually works.

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

## Shareable cards

Every highlight and every bill can be shared as a 1080x1920 image (`util/ShareCard.kt`), drawn on device. A card always carries the figure, the source, the source domain and the reference date together, so it still holds up when it is forwarded without context. The same file is the only place card layout lives.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Short version: factual error reports are the highest priority issue type, and every curated claim needs a verifiable source.

## License

MIT. See [LICENSE](LICENSE).
