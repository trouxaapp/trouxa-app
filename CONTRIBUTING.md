# Contributing

Thanks for helping. A few ground rules keep this project trustworthy.

## Factual errors come first

If the app shows something wrong (wrong author, wrong status, wrong number, misleading summary), open an issue. This is the highest priority issue type. Include:

- What the app shows and what it should show
- Links to the sources that prove it (Camara API endpoint, official publication, news article)

We would rather show less data than wrong data.

## Curation contributions

Changes to `feed/feed.json` and `feed/curadoria.json` are welcome, with one non-negotiable rule: every claim needs a verifiable source. Each feed item needs `fonte`, `fonte_url` and `data_referencia`; each curation entry needs at least one entry in `fontes` with a working URL. The app silently drops anything sourceless, so a PR without sources is a PR that does nothing.

Keep the tone neutral. Pros and contras should be arguments that actually exist in the public debate, attributed via the sources, not your own take.

## Code PRs

- Code, comments, commit messages and PR descriptions in English.
- UI strings shown to users are in pt-BR, with correct accents (acentuacao correta: "tramitação", not "tramitacao", in user-facing text).
- Match the existing architecture: data goes through Room, the UI observes the database, sync happens in WorkManager workers.
- The no-invented-data rule is enforced in review. Any change that renders a value without a real origin (API data, documented derivation, or sourced curation) will be rejected, no matter how nice it looks.

## Reporting other issues

Crashes, sync problems and UI bugs: open an issue with steps to reproduce, device/Android version, and the sync log from Settings if relevant.
