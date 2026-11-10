# Changelog

## 5.0.0 (2026-11-10)

- A alma voltou. O app deixou de só mostrar o número e passou a dizer o que ele significa, em três camadas separadas: leitura (o que o projeto faz), veredito (o "e daí?") e esculacho (piada de processo, sem número).
- Carimbo e veredito em cada projeto, derivados só de campo real da Câmara (MPV, urgência, crédito extraordinário, PEC, cria gasto, mexe no bolso, só homenagem). Curadoria humana, quando existe, vence a regra — `Alma.kt` documenta cada regra.
- "Na prática, pra quem paga a conta": bloco novo no detalhe do projeto, separado do texto oficial.
- A aba Hoje abre com a verdade do dia, com carimbo, número, veredito e a ofensiva (dias seguidos abrindo o app). Só o dispositivo sabe disso: nenhum servidor.
- Glossário anti-juridiquês muito maior: medida provisória, urgência, quórum, maioria absoluta, votação nominal, orientação de bancada, sanção, veto, promulgação, apensado, prejudicado, arquivamento, requerimento, relator, substitutivo, parecer, obstrução, destaque, cota parlamentar.
- Push com carimbo no título e veredito no corpo. O resumo diário agora é a verdade curada do dia, em rodízio determinístico.
- Cartão compartilhável agora é cartaz: carimbo torto, veredito com barra vermelha, hachura de fundo e assinatura anônima (@trouxaapp).
- Feed passa ao esquema v4: `selo`, `veredito` e `imagem` (opcional). Fonte, link e data continuam obrigatórios, e a mesma regra vale para o veredito curado.
- Publicação automática no X 3x por dia, em `automation/`: texto apertado em 280 na ordem certa de sacrifício e cartaz montado na hora — fundo por IA sem censura (mar de merda), foto oficial do político estilizada em P&B com duotone vermelho, carimbo e fonte. Se a IA cair, o cartaz sai com ondas procedurais.
- Site com a verdade do dia e o tom novo.

## 4.2.0 (2026-11-03)

- Daily panel on the Hoje tab, straight from official sources: dollar and Ibovespa, what changed and what did not (Selic, IPCA, minimum wage, with the date and direction of the last change), the IPCA items that rose and fell the most, the Chamber's agenda today, recent floor votes with score and party orientation, Senate floor results, credits opened outside the budget per year with the presidency, the official election result and the day's news from the public agencies.
- Each panel block keeps its last good value when a source fails, still showing its own reference date. Server errors are retried once.
- Floor votes name the bill and explain it in one sentence; procedural votes that do not name a bill are left out.
- Bills now come with an "Em portuguÃªs" section: one sentence on what the bill does, the amount for budget credits, the stated objective and notes (MP deadlines, PEC and PLP quorums, no presidential sanction for decree sustations).
- "Palavras difÃ­ceis": the hard terms found in the ementa, each with a short explanation and an official glossary link. The same terms were added to the Entenda glossary.
- Bill cards in the feed show the amount and the plain sentence under the title.
- The long-run comparisons moved below the daily panel and are now called "Antes e agora".
- Search in Entenda and Deputados ignores accents ("credito" finds "CrÃ©dito extraordinÃ¡rio", "aecio" finds "AÃ©cio").
- Deputy list, profile and expenses retry once on a CÃ¢mara timeout or server error.
- An empty expenses answer from the CÃ¢mara no longer wipes the cached expenses, and the screen no longer reads as "no spending" when the API returns nothing.
- The Hoje tab opens with the cached panel instead of flashing the empty state.
- Long vote titles are capped at four lines.

## 4.1.0 (2026-11-03)

- Share highlights and bills as story images (1080x1920) with figure, source and reference date on the image itself.
- Highlights moved to the top of the Hoje tab and can show a headline figure (`numero`, optional in the feed).
- Feed refreshed with September 2026 figures (DIEESE basic basket and necessary minimum wage, ANP gasoline and LPG) plus the deputy salary and the SÃ£o Paulo CEAP ceiling.
- A newer feed bundled in the APK now replaces older local content even while the remote feed is unreachable; an older bundle never overrides a newer remote feed.
- Bill cards no longer say "no movement" when the Camara already logged one; they show the last movement instead.
- Currency values use thousands separators (R$ 1.621,00).
- Removed five unused president photos from the resources.

## 4.0.0 (2026-06-10)

- Full rewrite around the no-invented-data rule.

