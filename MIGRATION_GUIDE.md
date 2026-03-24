# Trouxa App - Guia de Migração para Backend

## Status Atual (v2.4)
Todos os dados estão hardcoded em `DataProvider.kt`.
Funciona 100% offline. Sem dependência de servidor.

---

## FASE 1: JSON Estático (GitHub Pages / Vercel)

### O que fazer:
1. Criar repositório `trouxaapp/data` no GitHub
2. Colocar o arquivo `data.json` na raiz (estrutura abaixo)
3. Ativar GitHub Pages no repositório
4. URL final: `https://trouxaapp.github.io/data/data.json`

### No app:
1. Editar `di/AppModule.kt` - adicionar Retrofit apontando pra URL do JSON:
```kotlin
@Provides @Singleton
fun provideRetrofit(): Retrofit = Retrofit.Builder()
    .baseUrl("https://trouxaapp.github.io/data/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = Level.BODY })
        .build())
    .build()

@Provides @Singleton
fun provideApiService(retrofit: Retrofit): TrouxaApiService =
    retrofit.create(TrouxaApiService::class.java)

@Provides @Singleton
fun provideDatabase(@ApplicationContext context: Context): CacheDatabase =
    Room.databaseBuilder(context, CacheDatabase::class.java, "trouxa_cache").build()
```

2. Editar `TrouxaRepository.kt` - adicionar lógica de fetch + cache:
```kotlin
// Pseudo-código da lógica:
suspend fun loadData() {
    val cached = cacheDao.get("data_json")
    val isStale = cached == null ||
        (System.currentTimeMillis() - cached.updatedAt) > 7 * 24 * 3600 * 1000 // 7 dias

    if (isStale) {
        try {
            val remote = apiService.getData()
            val json = Gson().toJson(remote)
            cacheDao.insert(CachedDataEntity("data_json", json))
            // converter ApiData -> modelos locais
        } catch (e: Exception) {
            // sem internet: usar cache ou fallback hardcoded
        }
    } else {
        // usar cache
    }
}
```

3. Manter `DataProvider.kt` como **fallback** - se não tem internet E não tem cache, usa hardcoded

### Estrutura do `data.json`:
```json
{
  "updated_at": "2026-03-19",
  "version": 1,
  "products": [
    {
      "id": "arroz_5kg",
      "name": "Arroz 5kg",
      "unit": "5kg",
      "category": "alimentos",
      "source": "DIEESE – Cesta Básica",
      "source_url": "https://www.dieese.org.br/analisecestabasica/cestaBasica.html",
      "frase": "Seu avô comprava isso sem pensar. Você pensa 3x.",
      "prices": {
        "2000": { "price": 5.00, "reason": "Pós-Real, preços estabilizados" },
        "2020": {
          "price": 26.00,
          "reason": "Pandemia + exportação recorde + câmbio a R$ 5,70",
          "responsible": {
            "name": "Tereza Cristina",
            "role": "Ministra da Agricultura",
            "government": "Bolsonaro",
            "action": "Não regulou exportações. CONAB zerou estoques reguladores."
          }
        }
      }
    }
  ],
  "presidents": [
    {
      "name": "Fernando Henrique Cardoso",
      "nickname": "FHC",
      "party": "PSDB",
      "start_year": 1995,
      "end_year": 2002,
      "photo": "fhc.jpg",
      "cabinet": {
        "vice": "Marco Maciel (PFL)",
        "economy_minister": "Pedro Malan",
        "bc_president": "Armínio Fraga (1999-2002)",
        "petrobras_president": "Henri Philippe Reichstul"
      },
      "facts": {
        "positive": "Plano Real estabilizou a moeda",
        "negative": "Privatizações a preço baixo; apagão 2001",
        "context": "Pós-hiperinflação"
      },
      "scandals": [
        {
          "text": "Vale do Rio Doce vendida por R$ 3,3 bilhões. Hoje vale +R$ 250 bi.",
          "source": "BNDES, CVM",
          "url": "https://www.bndes.gov.br"
        }
      ],
      "price_decisions": [
        "Plano Real (1994): estabilizou moeda",
        "Privatização da Vale (1997): R$ 3,3 bi"
      ]
    }
  ],
  "context_banners": {
    "2002": "Crise cambial: dólar disparou para R$ 4.",
    "2008": "Crise financeira global.",
    "2015": "Recessão histórica: PIB caiu 7% em dois anos.",
    "2020": "Pandemia COVID-19.",
    "2022": "Guerra Rússia-Ucrânia."
  }
}
```

### Como atualizar dados:
- Editar o `data.json` no GitHub
- Commit direto na main
- GitHub Pages atualiza em ~1 minuto
- App pega na próxima abertura (se cache > 7 dias)

---

## FASE 2: Backend FastAPI

### Por que migrar:
- JSON estático precisa de edição manual
- Não tem dados em tempo real
- Não consegue enviar notificações push

### Stack:
- **FastAPI** (Python) - API REST
- **PostgreSQL** - banco de dados
- **Redis** - cache de APIs externas
- **Celery** - tasks agendadas (buscar dados IBGE/ANP semanalmente)
- **Firebase Cloud Messaging** - notificações push
- **Vercel** ou **Railway** - hospedagem

### Endpoints:
```
GET /api/v1/products              → todos os produtos com preços
GET /api/v1/products/{id}         → produto específico
GET /api/v1/presidents            → todos os presidentes
GET /api/v1/presidents/{year}     → presidente do ano
GET /api/v1/compare?a=2002&b=2026 → comparação
GET /api/v1/salary?value=3000     → análise salarial
GET /api/v1/news                  → notícias/leis em pauta
```

### Cron jobs semanais:
1. IBGE SIDRA API → atualizar IPCA e preços
2. ANP preços → gasolina, gás
3. DIEESE → cesta básica (scraping, não tem API)
4. BCB SGS → Selic, câmbio, inflação
5. Câmara API → leis em votação

### No app:
- Trocar `baseUrl` do Retrofit pra URL do FastAPI
- Adicionar header de autenticação (API key simples)
- Cache Room continua funcionando igual

---

## FASE 3: APIs em Tempo Real

### APIs disponíveis:
| API | URL | Dados |
|-----|-----|-------|
| IBGE SIDRA | sidra.ibge.gov.br/tabela/7060 | IPCA, preços |
| BCB SGS | api.bcb.gov.br/dados/serie/bcdata.sgs | Selic, câmbio, IPCA |
| ANP | precos.anp.gov.br | Gasolina, gás |
| Câmara | dadosabertos.camara.leg.br/api/v2 | Leis, votações, gastos |
| Senado | legis.senado.leg.br/dadosabertos/v1 | Projetos, votações |
| Portal Transparência | api.portaldatransparencia.gov.br | Gastos federais |
| TSE | dadosabertos.tse.jus.br | Candidatos, eleições |

### Notificações push:
```
Tipo A: "O arroz subiu 5% esse mês. Em 2020 custava R$ 15. Hoje: R$ 32."
Tipo B: "Lei em pauta: PL XXXX quer [X]. Autor: Dep. Fulano (PP-AL). Votação amanhã."
Tipo C: "Seu deputado gastou R$ 45.000 em cota parlamentar esse mês."
```

### Firebase setup:
1. Criar projeto Firebase
2. Adicionar google-services.json ao app
3. Implementar FirebaseMessagingService
4. Backend envia notificação via Firebase Admin SDK

---

## Arquivos relevantes no projeto:

```
app/src/main/java/com/trouxaapp/app/
├── data/
│   ├── local/
│   │   ├── DataProvider.kt      ← DADOS HARDCODED (fallback)
│   │   └── CacheDatabase.kt     ← ROOM CACHE (pronto)
│   ├── remote/
│   │   └── TrouxaApiService.kt  ← RETROFIT INTERFACE (pronto)
│   ├── model/
│   │   └── Models.kt            ← MODELOS (compartilhados)
│   └── repository/
│       └── TrouxaRepository.kt  ← LÓGICA DE NEGÓCIO (editar aqui)
├── di/
│   └── AppModule.kt             ← INJEÇÃO (adicionar Retrofit + Room)
```

## Ordem de execução:
1. [ ] Criar data.json com todos os dados atuais
2. [ ] Hospedar no GitHub Pages
3. [ ] Adicionar Retrofit + Room no AppModule
4. [ ] Implementar fetch + cache no Repository
5. [ ] Testar offline (desligar wifi, ver se usa cache)
6. [ ] Testar online (dados atualizam)
7. [ ] Criar script Python pra gerar data.json automaticamente
8. [ ] Migrar pra FastAPI quando precisar de dados em tempo real
