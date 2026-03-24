package com.trouxaapp.app.data.local

import com.trouxaapp.app.data.model.Absurdo
import com.trouxaapp.app.data.model.BasicBasketEntry
import com.trouxaapp.app.data.model.Gabinete
import com.trouxaapp.app.data.model.Governante
import com.trouxaapp.app.data.model.LeiAprovada
import com.trouxaapp.app.data.model.CidadaoAction
import com.trouxaapp.app.data.model.MinimumWageEntry
import com.trouxaapp.app.data.model.PoliticalProfile
import com.trouxaapp.app.data.model.President
import com.trouxaapp.app.data.model.PresidentFacts
import com.trouxaapp.app.data.model.PriceEntry
import com.trouxaapp.app.data.model.Product
import com.trouxaapp.app.data.model.ProductCategory

private fun p(price: Double, motivo: String, quem: String = "") = PriceEntry(price, motivo, quem)

object DataProvider {

    val presidents = listOf(
        President(
            name = "Fernando Henrique Cardoso", party = "PSDB", startYear = 1995, endYear = 2002,
            photoUrl = "", drawableRes = "fhc", initials = "FHC",
            facts = PresidentFacts(
                positivo = "Plano Real estabilizou a moeda; privatizações modernizaram telecomunicações",
                negativo = "Privatizações a preço baixo; apagão energético de 2001; desemprego alto",
                contexto = "Pós-hiperinflação. Real criado em 1994. País se estabilizando",
                fonte = "IBGE, Banco Central"
            ),
            absurdos = listOf(
                Absurdo("Apagão de 2001: racionamento de energia por falta de planejamento.", "ONS, ANEEL", "https://www.aneel.gov.br"),
                Absurdo("Vale do Rio Doce vendida por R\$ 3,3 bilhões. Hoje vale +R\$ 250 bilhões.", "BNDES, CVM", "https://www.bndes.gov.br"),
                Absurdo("Reeleição aprovada sob denúncias de compra de votos.", "Folha de S.Paulo", "https://www1.folha.uol.com.br")
            ),
            gabinete = Gabinete(
                vice = "Marco Maciel (PFL)",
                ministroEconomia = "Pedro Malan (Min. Fazenda)",
                presBCB = "Armínio Fraga (1999-2002)",
                presPetrobras = "Henri Philippe Reichstul / Francisco Gros",
                decisoesPrecos = listOf(
                    "Plano Real (1994): estabilizou moeda mas câmbio flutuante trouxe volatilidade",
                    "Privatização da Vale (1997): R\$ 3,3 bi. Empresa hoje vale +R\$ 250 bi",
                    "Crise cambial 1999: dólar disparou de R\$ 1,20 para R\$ 2,00"
                )
            )
        ),
        President(
            name = "Luiz Inácio Lula da Silva", party = "PT", startYear = 2003, endYear = 2010,
            photoUrl = "", drawableRes = "lula", initials = "L1",
            facts = PresidentFacts(
                positivo = "Bolsa Família tirou 36 milhões da pobreza; salário mínimo dobrou em termos reais; boom de commodities",
                negativo = "Mensalão: esquema de compra de votos no Congresso",
                contexto = "Boom global de commodities (China crescendo 10% ao ano). Petróleo e soja em altá recorde",
                pontoCritico = "PIB cresceu 7,5% em 2010. Melhor desempenho em décadas",
                fonte = "IBGE, Banco Mundial, STF"
            ),
            absurdos = listOf(
                Absurdo("Mensalão: compra de votos de parlamentares. Condenados pelo STF.", "STF, AP 470", "https://portal.stf.jus.br"),
                Absurdo("Dívida pública quase dobrou em valores absolutos.", "Tesouro Nacional", "https://www.tesourotransparente.gov.br")
            ),
            gabinete = Gabinete(
                vice = "José Alencar (PRB)",
                ministroEconomia = "Antonio Palocci / Guido Mantega (Min. Fazenda)",
                presBCB = "Henrique Meirelles (2003-2010)",
                presPetrobras = "José Sergio Gabrielli",
                decisoesPrecos = listOf(
                    "Política de valorização do salário mínimo: dobrou em termos reais",
                    "Bolsa Família: 36 milhões saíram da pobreza, aumentou demanda por alimentos",
                    "Petrobras segurou preço de combustíveis abaixo do mercado internacional"
                )
            )
        ),
        President(
            name = "Dilma Rousseff", party = "PT", startYear = 2011, endYear = 2016,
            photoUrl = "", drawableRes = "dilma", initials = "DR",
            facts = PresidentFacts(
                positivo = "Manteve programas sociais; Copa do Mundo 2014; Olimpíadas 2016",
                negativo = "Pedaladas fiscais; recessão de 2015-2016 (-7% PIB); inflação voltou",
                contexto = "Fim do boom de commodities. Crise econômica global. Operação Lava Jato",
                pontoCritico = "Impeachment em 2016. PIB caiu 3,5% em 2015 e 3,3% em 2016",
                fonte = "IBGE, Câmara dos Deputados, STF"
            ),
            absurdos = listOf(
                Absurdo("Recessão: PIB caiu 7% em dois anos (2015-2016). Pior desde 1930.", "IBGE", "https://www.ibge.gov.br"),
                Absurdo("Pedaladas fiscais: maquiou contas públicas em R\$ 40 bilhões.", "TCU", "https://portal.tcu.gov.br"),
                Absurdo("Impeachment por crime de responsabilidade fiscal.", "Câmara dos Deputados", "https://www.camara.leg.br"),
                Absurdo("Desemprego saltou de 6% para 12% em dois anos.", "IBGE, PNAD", "https://www.ibge.gov.br")
            ),
            gabinete = Gabinete(
                vice = "Michel Temer (PMDB)",
                ministroEconomia = "Guido Mantega / Joaquim Levy / Nelson Barbosa (Min. Fazenda)",
                presBCB = "Alexandre Tombini",
                presPetrobras = "Graça Foster / Aldemir Bendine",
                decisoesPrecos = listOf(
                    "Segurou preço da gasolina gerando rombo na Petrobras",
                    "Pedaladas fiscais: R\$ 40 bi em manobras contábeis",
                    "Nova Matriz Econômica: intervenção em preços administrados"
                )
            )
        ),
        President(
            name = "Michel Temer", party = "MDB", startYear = 2016, endYear = 2018,
            photoUrl = "", drawableRes = "temer", initials = "MT",
            facts = PresidentFacts(
                positivo = "Teto de gastos (PEC 95) ajudou a controlar inflação no curto prazo",
                negativo = "Congelou saúde e educação por 20 anos; reforma trabalhistá flexibilizou direitos",
                contexto = "Assumiu após impeachment. 5% de aprovação popular",
                fonte = "Datafolha, Câmara dos Deputados, STF"
            ),
            absurdos = listOf(
                Absurdo("Aprovação popular de 5%. Presidente mais impopular da história.", "Datafolha, dez/2017", "https://datafolha.folha.uol.com.br"),
                Absurdo("PEC 95: congelou saúde e educação por 20 ANOS.", "Câmara dos Deputados", "https://www.camara.leg.br"),
                Absurdo("Denunciado 2x por corrupção. Não foi julgado. Foro privilegiado.", "STF, PGR", "https://portal.stf.jus.br"),
                Absurdo("Reforma trabalhista: sindicatos perderam 90% da receita.", "Lei 13.467/2017", "https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2017/lei/l13467.htm")
            ),
            gabinete = Gabinete(
                vice = "Não tinha (assumiu como vice de Dilma)",
                ministroEconomia = "Henrique Meirelles (Min. Fazenda)",
                presBCB = "Ilan Goldfajn",
                presPetrobras = "Pedro Parente",
                decisoesPrecos = listOf(
                    "Pedro Parente criou PPI (2016): preço de combustível atrelado ao dólar",
                    "PEC 95: teto de gastos congelou saúde e educação por 20 anos",
                    "Reforma trabalhista: flexibilizou contratos, sindicatos perderam 90% da receita"
                )
            )
        ),
        President(
            name = "Jair Bolsonaro", party = "PL", startYear = 2019, endYear = 2022,
            photoUrl = "", drawableRes = "bolsonaro", initials = "JB",
            facts = PresidentFacts(
                positivo = "Auxílio Emergencial (R\$ 600); Pix; Marco do Saneamento",
                negativo = "PPI: gasolina a R\$ 7,30; câmbio de R\$ 4 para R\$ 5,70",
                contexto = "Pandemia COVID-19, guerra Rússia-Ucrânia",
                pontoCritico = "Quase 700 mil mortos por COVID",
                fonte = "OMS, Petrobras, BCB, CPI da COVID"
            ),
            absurdos = listOf(
                Absurdo("53 e-mails da Pfizer ignorados. 70 milhões de doses recusadas.", "CPI da COVID, Senado Federal, 2021", "https://www12.senado.leg.br/noticias/materias/2021/10/26/relatorio-da-cpi-da-pandemia-e-lido-no-plenario"),
                Absurdo("Quase 700 mil mortos por COVID. 2º país do mundo.", "Ministério da Saúde, OMS", "https://covid.saúde.gov.br"),
                Absurdo("\"E daí? Não sou coveiro.\"", "Pronunciamento oficial, 28/abr/2020", "https://g1.globo.com/política/noticia/2020/04/28/e-dai-lamento-quer-que-eu-faca-o-que-diz-bolsonaro-sobre-mortes-por-coronavirus-no-brasil.ghtml"),
                Absurdo("Gasolina R\$ 7,30. Recorde histórico.", "ANP, jun/2022", "https://preços.anp.gov.br"),
                Absurdo("Orçamento Secreto: R\$ 53 bilhões. STF declarou inconstitucional.", "STF, ADPFs 850, 851, 854, 1.014", "https://portal.stf.jus.br"),
                Absurdo("Petrobras: R\$ 190 bilhões em dividendos em 2022.", "Petrobras, CVM", "https://www.investidorpetrobras.com.br")
            ),
            gabinete = Gabinete(
                vice = "Hamilton Mourão (PRTB)",
                ministroEconomia = "Paulo Guedes (Min. Economia)",
                presBCB = "Roberto Campos Neto",
                presPetrobras = "Roberto Castello Branco / Joaquim Silva e Luna / Caio Paes de Andrade",
                decisoesPrecos = listOf(
                    "Manteve PPI: gasolina atingiu R\$ 7,30 em 2022",
                    "Selic a 2%: dólar foi de R\$ 4 para R\$ 5,70. Tudo cotado em dólar ficou caro",
                    "Auxílio Emergencial R\$ 600 na pandemia (depois cortou pra R\$ 300 e R\$ 250)",
                    "Reduziu ICMS de combustíveis (LC 194/2022) em ano eleitoral. Temporário"
                )
            )
        ),
        President(
            name = "Luiz Inácio Lula da Silva", party = "PT", startYear = 2023, endYear = null,
            photoUrl = "", drawableRes = "lula", initials = "L",
            facts = PresidentFacts(
                positivo = "Valorização real do salário mínimo; Desenrola renegociou dívidas",
                negativo = "Inflação de alimentos alta; câmbio não voltou",
                contexto = "Herdou câmbio alto, Selic 13,75%, inflação acumulada",
                pontoCritico = "Selic em dois dígitos",
                fonte = "BCB, IBGE, Min. Fazenda"
            ),
            absurdos = listOf(
                Absurdo("Cesta básica: de R\$ 730 para R\$ 860 em 3 anos.", "DIEESE", "https://www.dieese.org.br/analisecestabásica/cestaBasica.html"),
                Absurdo("Selic acima de 13% durante quase todo o mandato.", "BCB", "https://www.bcb.gov.br/controleinflação/historicotaxasjuros"),
                Absurdo("Dólar acima de R\$ 5,50 permanentemente.", "BCB", "https://www.bcb.gov.br"),
                Absurdo("Prometeu picanha e cerveja. Carne subiu 20%.", "DIEESE, IBGE", "https://www.dieese.org.br"),
                Absurdo("Arcabouço fiscal: meta de déficit zero flexibilizada no 1º ano.", "Congresso Nacional", "https://www.camara.leg.br")
            ),
            gabinete = Gabinete(
                vice = "Geraldo Alckmin (PSB)",
                ministroEconomia = "Fernando Haddad (Min. Fazenda)",
                presBCB = "Roberto Campos Neto (até dez/2024) / Gabriel Galípolo (2025+)",
                presPetrobras = "Jean Paul Prates / Magda Chambriard",
                decisoesPrecos = listOf(
                    "Retomou valorização real do salário mínimo acima da inflação",
                    "Novo arcabouço fiscal substituiu teto de gastos (PEC 95)",
                    "Programa Desenrola: renegociou dívidas de 15 milhões de pessoas"
                )
            )
        )
    )

    val products = listOf(
        Product(
            id = "arroz", name = "Arroz 5kg", unit = "5kg", category = ProductCategory.ALIMENTOS,
            source = "DIEESE – Cesta Básica", sourceUrl = "https://www.dieese.org.br/analisecestabásica/cestaBasica.html",
            fraseCustom = "Seu avô comprava isso sem pensar. Você pensa 3x.",
            contextoCompleto = "Em 2000, com o salário mínimo de R\$ 151, você comprava 30 pacotes de arroz. " +
                "Em 2026, com R\$ 1.518, você compra 47. Parece melhor? Mas a cestá básica inteira subiu mais que o salário. " +
                "No fim, você compra MENOS com mais dinheiro.\n\n" +
                "Em 2020, o preço DOBROU em meses. Tereza Cristina (Ministra da Agricultura, governo Bolsonaro) " +
                "não regulou exportações. A CONAB tinha zerado os estoques reguladores. O Brasil exportou arroz " +
                "pra China enquanto faltava no mercado brasileiro.\n\nFonte: CONAB, MDIC",
            prices = mapOf(
                2000 to p(5.00, "Pós-Real, preços estabilizados"),
                2001 to p(5.50, "Estável"), 2002 to p(7.00, "Crise cambial, dólar a R\$ 4"),
                2003 to p(8.00, "Novo governo, câmbio alto"), 2004 to p(7.50, "Estabilizando"),
                2005 to p(6.50, "Safra boa, leve queda"), 2006 to p(6.00, "Estável"),
                2007 to p(7.00, "Leve alta"), 2008 to p(9.00, "Crise mundial de alimentos"),
                2009 to p(8.00, "Queda: safra recorde"), 2010 to p(7.50, "Demanda crescente"),
                2011 to p(8.00, "Estável"), 2012 to p(8.50, "Leve alta"),
                2013 to p(9.00, "Custos de produção"), 2014 to p(10.00, "Leve alta"),
                2015 to p(11.00, "Recessão + câmbio"), 2016 to p(11.50, "Crise econômica"),
                2017 to p(11.00, "Estabilizando"),
                2018 to p(13.50, "Altá acumulada"),
                2019 to p(13.00, "Leve queda"),
                2020 to p(26.00, "Pandemia + exportação recorde + câmbio a R\$ 5,70",
                    "Tereza Cristina (Min. Agricultura, gov. Bolsonaro) não regulou exportações. CONAB zerou estoques reguladores. Fonte: CONAB, MDIC"),
                2021 to p(28.00, "Efeito prolongado: estoques baixos"),
                2022 to p(22.00, "Queda: safra recorde"), 2023 to p(22.50, "Estabilizando"),
                2024 to p(27.00, "El Niño afetou RS"), 2025 to p(30.00, "Enchentes RS + demanda"),
                2026 to p(32.00, "Estimativa mar/2026: tendência de altá mantida")
            )
        ),
        Product(
            id = "feijao", name = "Feijão 1kg", unit = "1kg", category = ProductCategory.ALIMENTOS,
            source = "DIEESE – Cesta Básica", sourceUrl = "https://www.dieese.org.br/analisecestabásica/cestaBasica.html",
            fraseCustom = "Arroz e feijão. O prato do brasileiro. Cada vez mais caro.",
            contextoCompleto = "Em 2016, o feijão chegou a R\$ 6,50 por causa de seca severa. " +
                "Em 2022, os fertilizantes ficaram caros por causa da guerra Rússia-Ucrânia (a Rússia é grande exportadora). " +
                "Nenhum governo criou estoque estratégico de feijão como existe pra outros grãos.\n\nFonte: CONAB, IBGE",
            prices = mapOf(
                2000 to p(1.30, "Pós-Real"), 2001 to p(1.50, "Estável"),
                2002 to p(2.00, "Crise cambial"), 2003 to p(2.30, "Câmbio alto"),
                2004 to p(2.50, "Estável"), 2005 to p(2.20, "Safra boa"),
                2006 to p(2.00, "Boa oferta"), 2007 to p(2.50, "Leve alta"),
                2008 to p(3.50, "Crise mundial de alimentos"), 2009 to p(2.80, "Queda"),
                2010 to p(3.20, "Demanda"), 2011 to p(3.50, "Leve alta"),
                2012 to p(3.80, "Custos"), 2013 to p(3.50, "Safra boa"),
                2014 to p(3.20, "Ofertá alta"), 2015 to p(4.00, "Seca + recessão"),
                2016 to p(6.50, "Seca severa + crise"), 2017 to p(4.00, "Queda: safra recorde"),
                2018 to p(4.50, "Estável"), 2019 to p(5.00, "Leve alta"),
                2020 to p(5.50, "Pandemia"), 2021 to p(7.00, "Seca + insumos"),
                2022 to p(7.50, "Fertilizantes caros (guerra)"), 2023 to p(7.00, "Safra melhor"),
                2024 to p(8.50, "Clima adverso"), 2025 to p(9.50, "Custos crescentes"),
                2026 to p(10.00, "Estimativa mar/2026")
            )
        ),
        Product(
            id = "carne", name = "Carne bovina (patinho) 1kg", unit = "1kg", category = ProductCategory.ALIMENTOS,
            source = "IBGE – IPCA", sourceUrl = "https://sidra.ibge.gov.br/tabela/7060",
            fraseCustom = "O país do boi. Onde o brasileiro come ovo.",
            contextoCompleto = "O Brasil tem o maior rebanho comercial do mundo: 220 milhões de cabeças. " +
                "E o brasileiro come cada vez menos carne. Em 2019, a China teve surto de febre suína africana " +
                "e começou a importar carne brasileira em volume recorde. O governo priorizou exportação. " +
                "Em 2021, o kg de patinho bateu R\$ 40. Pico histórico.\n\n" +
                "Quem decidiu: a política de câmbio flutuante (mantida por TODOS os governos) faz a exportação " +
                "mais lucrativa que vender pro brasileiro. Enquanto o dólar estiver alto, a carne vai pra fora.\n\nFonte: IBGE, MDIC",
            prices = mapOf(
                2000 to p(6.50, "Pós-Real"), 2001 to p(7.00, "Estável"),
                2002 to p(8.50, "Crise cambial"), 2003 to p(9.50, "Câmbio"),
                2004 to p(10.00, "Exportação crescendo"), 2005 to p(10.50, "Estável"),
                2006 to p(11.00, "Demanda interna"), 2007 to p(12.00, "Leve alta"),
                2008 to p(14.00, "Crise de alimentos"), 2009 to p(13.00, "Ajuste"),
                2010 to p(14.50, "Demanda"), 2011 to p(16.00, "Exportação"),
                2012 to p(17.00, "Alta"), 2013 to p(18.00, "Custos"),
                2014 to p(19.00, "Estável"), 2015 to p(21.00, "Câmbio + recessão"),
                2016 to p(23.00, "Crise"), 2017 to p(24.00, "Estabilizando"),
                2018 to p(25.00, "Estável"),
                2019 to p(28.00, "China comprou tudo (febre suína)",
                    "China importou volume recorde. Governo priorizou exportação. Fonte: MDIC"),
                2020 to p(35.00, "Pandemia + exportação + câmbio"),
                2021 to p(40.00, "Pico: China comprando recorde"),
                2022 to p(35.00, "China reduziu compras"), 2023 to p(30.00, "Ofertá alta"),
                2024 to p(38.00, "Seca + exportação voltou"), 2025 to p(45.00, "Câmbio + demanda"),
                2026 to p(48.00, "Estimativa mar/2026: pressão de custos")
            )
        ),
        Product(
            id = "ovo", name = "Ovo dúzia", unit = "dúzia", category = ProductCategory.ALIMENTOS,
            source = "DIEESE – Cesta Básica", sourceUrl = "https://www.dieese.org.br/analisecestabásica/cestaBasica.html",
            fraseCustom = "Ovo era o básico do básico. Virou luxo.",
            contextoCompleto = "Ovo depende de milho e soja pra ração. Quando o dólar sobe, o milho e soja " +
                "são exportados (mais lucrativos em dólar), e o custo da ração dispara. Em 2021, com dólar a R\$ 5,70, " +
                "o custo da ração subiu 40%. Resultado: ovo de R\$ 4,50 pra R\$ 8,50 em 3 anos.\n\n" +
                "A dúzia custava R\$ 1,20 em 2000. Hoje custa R\$ 13,00. Aumento de 983%.\n\nFonte: DIEESE, CONAB",
            prices = mapOf(
                2000 to p(1.20, "Barato"), 2001 to p(1.30, "Estável"),
                2002 to p(1.50, "Crise cambial"), 2003 to p(1.80, "Câmbio"),
                2004 to p(2.00, "Estável"), 2005 to p(2.00, "Estável"),
                2006 to p(2.20, "Leve alta"), 2007 to p(2.30, "Estável"),
                2008 to p(2.80, "Crise alimentos"), 2009 to p(2.50, "Queda"),
                2010 to p(2.80, "Demanda"), 2011 to p(3.00, "Custos"),
                2012 to p(3.20, "Alta"), 2013 to p(3.50, "Custos de ração"),
                2014 to p(3.50, "Estável"), 2015 to p(3.80, "Recessão + câmbio"),
                2016 to p(4.00, "Crise"), 2017 to p(4.20, "Estabilizando"),
                2018 to p(4.50, "Estável"), 2019 to p(5.00, "Leve alta"),
                2020 to p(6.00, "Pandemia: consumo doméstico"), 2021 to p(8.50, "Ração cara (dólar)"),
                2022 to p(8.00, "Ajuste"), 2023 to p(7.50, "Ofertá melhorou"),
                2024 to p(10.00, "Milho caro"), 2025 to p(12.00, "Energia + ração"),
                2026 to p(13.00, "Estimativa mar/2026")
            )
        ),
        Product(
            id = "cafe", name = "Café 500g", unit = "500g", category = ProductCategory.ALIMENTOS,
            source = "DIEESE – Cesta Básica", sourceUrl = "https://www.dieese.org.br/analisecestabásica/cestaBasica.html",
            fraseCustom = "Até o cafezinho te custa mais do que deveria.",
            contextoCompleto = "O Brasil é o MAIOR produtor de café do mundo. E o brasileiro paga cada vez mais caro. " +
                "Em 2021, uma geada histórica destruiu lavouras em MG e SP. A safra caiu 20%. " +
                "Mas o preço não voltou: a recuperação leva 3-4 anos porque o pé de café demora pra produzir.\n\n" +
                "Enquanto isso, o Brasil exportá 70% da produção (mais lucrativo em dólar). " +
                "O cafezinho brasileiro é feito com o que sobra.\n\nFonte: CONAB, DIEESE, ICO",
            prices = mapOf(
                2000 to p(2.80, "Crise do café, preço baixo"), 2001 to p(2.50, "Preço mínimo histórico"),
                2002 to p(3.00, "Crise cambial ajudou exportador"), 2003 to p(3.50, "Recuperando"),
                2004 to p(4.00, "Estável"), 2005 to p(4.50, "Demanda crescendo"),
                2006 to p(5.00, "Estável"), 2007 to p(5.50, "Leve alta"),
                2008 to p(6.00, "Crise global"), 2009 to p(5.50, "Queda"),
                2010 to p(6.00, "Demanda global"), 2011 to p(7.00, "Geada + seca"),
                2012 to p(7.50, "Custos"), 2013 to p(7.50, "Estável"),
                2014 to p(8.00, "Seca em MG"), 2015 to p(8.00, "Estável"),
                2016 to p(8.00, "Estável"), 2017 to p(8.00, "Estável"),
                2018 to p(8.00, "Estável"), 2019 to p(9.00, "Leve alta"),
                2020 to p(12.00, "Pandemia + geada MG"), 2021 to p(14.00, "Geada histórica"),
                2022 to p(16.00, "Efeito geada 2021"), 2023 to p(18.00, "Recuperação lenta"),
                2024 to p(20.00, "Seca MG + demanda global"), 2025 to p(22.00, "Crise hídrica + câmbio"),
                2026 to p(26.00, "Estimativa mar/2026: demanda global recorde")
            )
        ),
        Product(
            id = "leite", name = "Leite 1L", unit = "1L", category = ProductCategory.ALIMENTOS,
            source = "IBGE – IPCA", sourceUrl = "https://sidra.ibge.gov.br/tabela/7060",
            fraseCustom = "Leite. Pra criança. E cada vez mais caro.",
            contextoCompleto = "Em 2022, o leite bateu R\$ 7,00 o litro. Uma seca histórica secou as pastagens " +
                "e o custo de ração disparou (milho e soja cotados em dólar). " +
                "Pequenos produtores abandonaram a atividade: entre 2015 e 2022, o Brasil perdeu 100 mil produtores de leite.\n\n" +
                "O leite que era R\$ 0,95 em 2000 hoje custa R\$ 6,50. Aumento de 584%.\n\nFonte: IBGE, Embrapa",
            prices = mapOf(
                2000 to p(0.95, "Barato pós-Real"), 2001 to p(1.00, "Estável"),
                2002 to p(1.10, "Crise cambial"), 2003 to p(1.20, "Câmbio"),
                2004 to p(1.30, "Estável"), 2005 to p(1.30, "Estável"),
                2006 to p(1.40, "Leve alta"), 2007 to p(1.50, "Demanda"),
                2008 to p(1.80, "Crise global de lácteos"), 2009 to p(1.60, "Queda"),
                2010 to p(1.80, "Demanda"), 2011 to p(2.00, "Custos"),
                2012 to p(2.20, "Alta"), 2013 to p(2.40, "Ração cara"),
                2014 to p(2.50, "Estável"), 2015 to p(2.80, "Recessão + câmbio"),
                2016 to p(3.00, "Crise"), 2017 to p(3.10, "Estabilizando"),
                2018 to p(3.20, "Estável"), 2019 to p(3.40, "Leve alta"),
                2020 to p(3.50, "Pandemia: consumo caiu"), 2021 to p(4.50, "Ração + seca"),
                2022 to p(7.00, "Pico: seca histórica"), 2023 to p(5.00, "Excesso de oferta"),
                2024 to p(5.50, "Leve alta"), 2025 to p(6.00, "Custos"),
                2026 to p(6.50, "Estimativa mar/2026")
            )
        ),
        Product(
            id = "gasolina", name = "Gasolina 1L", unit = "1L", category = ProductCategory.COMBUSTIVEL,
            source = "ANP – Levantamento de Preços", sourceUrl = "https://preços.anp.gov.br",
            fraseCustom = "O petróleo é nosso. O preço é deles.",
            hipocrisia = "O Brasil produz 3,7 milhões de barris de petróleo por dia. É o 7º maior produtor do mundo. E você paga R\$ 6,40 no litro.",
            contextoCompleto = "Em 2016, Pedro Parente (Presidente da Petrobras, governo Temer) criou o PPI: " +
                "Preço de Paridade de Importação. Isso atrelou o preço da gasolina brasileira ao preço " +
                "INTERNACIONAL em DÓLAR. Não importá que o petróleo é nosso. Você paga como se tivesse importando.\n\n" +
                "Em 2022, com a guerra na Ucrânia e o dólar a R\$ 5,50, a gasolina bateu R\$ 7,30. Recorde histórico. " +
                "No mesmo ano, a Petrobras distribuiu R\$ 190 bilhões em dividendos. " +
                "Acionistas lucraram. Você pagou R\$ 7,30.\n\n" +
                "Roberto Campos Neto (BCB, governo Bolsonaro) cortou a Selic pra 2%, o que fez o dólar disparar de R\$ 4 pra R\$ 5,90. " +
                "Gasolina cotada em dólar + dólar caro = você pagando em dólar e ganhando em real.\n\nFonte: ANP, Petrobras, Relatório Anual 2022",
            prices = mapOf(
                2000 to p(1.60, "Preço controlado"), 2001 to p(1.70, "Crise energética"),
                2002 to p(1.90, "Crise cambial"), 2003 to p(2.10, "Ajuste + câmbio"),
                2004 to p(2.20, "Petróleo subindo"), 2005 to p(2.40, "Altá do petróleo"),
                2006 to p(2.55, "Estável"), 2007 to p(2.50, "Controlado"),
                2008 to p(2.60, "Petróleo a US\$ 147, governo segurou preço"),
                2009 to p(2.55, "Queda do petróleo"), 2010 to p(2.60, "Estável"),
                2011 to p(2.80, "Petrobras subsidiando"), 2012 to p(2.90, "Subsídio"),
                2013 to p(3.00, "Reajuste parcial"), 2014 to p(3.10, "Reajuste"),
                2015 to p(3.40, "Câmbio + fim dos subsídios"), 2016 to p(3.60, "Crise + PPI criado"),
                2017 to p(3.80, "PPI entrando em vigor"),
                2018 to p(4.20, "PPI ativo",
                    "Pedro Parente (Pres. Petrobras, gov. Temer) criou o PPI em 2016: preço atrelado ao dólar. Fonte: Petrobras"),
                2019 to p(4.40, "Câmbio",
                    "Roberto Campos Neto (BCB, gov. Bolsonaro) cortou Selic pra 2%. Dólar disparou. Gasolina em dólar, salário em real. Fonte: BCB"),
                2020 to p(4.10, "Pandemia reduziu demanda"),
                2021 to p(5.80, "Petróleo + câmbio + PPI"),
                2022 to p(7.30, "Pico: guerra Rússia-Ucrânia",
                    "ICMS reduzido por LC 194/2022 em ano eleitoral. Medida temporária. Fonte: Congresso"),
                2023 to p(5.50, "Reoneração + petróleo estável"),
                2024 to p(5.90, "Reajuste Petrobras"), 2025 to p(6.20, "Câmbio + reajuste"),
                2026 to p(6.40, "Estimativa mar/2026")
            )
        ),
        Product(
            id = "gas", name = "Gás de cozinha 13kg", unit = "botijão 13kg", category = ProductCategory.COMBUSTIVEL,
            source = "ANP – Levantamento de Preços", sourceUrl = "https://preços.anp.gov.br",
            fraseCustom = "O Brasil produz gás. Brasileiro cozinha com lenha.",
            hipocrisia = "O Brasil é o maior produtor de gás natural da América do Sul. Tem família cozinhando com lenha. Em 2026.",
            contextoCompleto = "O botijão era R\$ 18 em 2000. Hoje é R\$ 115. Aumento de 539%.\n\n" +
                "O GLP segue o mesmo PPI da gasolina: preço internacional + dólar. Mesmo sendo produzido aqui. " +
                "O subsídio ao GLP foi cortado governo após governo:\n\n" +
                "• Governo FHC: começou a liberar preços\n" +
                "• Governo Temer: Pedro Parente atrelou ao PPI\n" +
                "• Governo Bolsonaro: PPI rígido + dólar alto = pico de R\$ 115\n" +
                "• Governo Lula 3: não reverteu o PPI\n\n" +
                "Todos. Nenhum resolveu.\n\nFonte: ANP, Petrobras",
            prices = mapOf(
                2000 to p(18.00, "Subsidiado"), 2001 to p(20.00, "Leve alta"),
                2002 to p(22.00, "Crise cambial"), 2003 to p(25.00, "Reajuste"),
                2004 to p(28.00, "Altá do petróleo"), 2005 to p(30.00, "Reajuste"),
                2006 to p(32.00, "Estável"), 2007 to p(33.00, "Controlado"),
                2008 to p(35.00, "Petróleo alto, governo segurou"), 2009 to p(35.00, "Estável"),
                2010 to p(37.00, "Leve alta"), 2011 to p(38.00, "Subsídio Petrobras"),
                2012 to p(42.00, "Reajuste"), 2013 to p(45.00, "Reajuste"),
                2014 to p(48.00, "Alta"), 2015 to p(50.00, "Câmbio + fim subsídio"),
                2016 to p(52.00, "Crise + PPI"), 2017 to p(53.00, "PPI"),
                2018 to p(55.00, "Estável"), 2019 to p(60.00, "Leve alta"),
                2020 to p(70.00, "Pandemia + câmbio"),
                2021 to p(100.00, "PPI + câmbio + petróleo",
                    "Pedro Parente (Petrobras, gov. Temer) atrelou gás ao dólar via PPI. Botijão de R\$ 45 para R\$ 100. R\$ 190 bi em dividendos em 2022. Fonte: Petrobras, CVM"),
                2022 to p(115.00, "Pico: guerra"), 2023 to p(100.00, "Subsídio parcial"),
                2024 to p(105.00, "Reajuste"), 2025 to p(110.00, "Câmbio"),
                2026 to p(115.00, "Estimativa mar/2026")
            )
        ),
        Product(
            id = "etanol", name = "Etanol 1L", unit = "1L", category = ProductCategory.COMBUSTIVEL,
            source = "ANP – Levantamento de Preços", sourceUrl = "https://preços.anp.gov.br",
            fraseCustom = "Maior produtor do mundo. E o litro custa R\$ 4,50.",
            hipocrisia = "O Brasil é o MAIOR produtor de etanol do mundo. E o litro custa R\$ 4,50+. Se a gente produz, por que é caro?",
            contextoCompleto = "Etanol compete com açúcar pela cana. Quando o açúcar sobe no mercado internacional, " +
                "as usinas preferem produzir açúcar pra EXPORTAR do que etanol pra você ABASTECER. " +
                "Seu tanque é menos lucrativo que a exportação.\n\n" +
                "Resultado: o maior produtor de etanol do mundo tem etanol caro.\n\nFonte: UNICA, ANP",
            prices = mapOf(
                2000 to p(0.70, "Barato, pouca demanda"), 2001 to p(0.75, "Estável"),
                2002 to p(0.85, "Crise cambial"), 2003 to p(0.95, "Ajuste"),
                2004 to p(1.00, "Flex fuel começando"), 2005 to p(1.10, "Demanda crescendo"),
                2006 to p(1.30, "Boom de flex fuel"), 2007 to p(1.20, "Safra boa"),
                2008 to p(1.40, "Crise alimentos"), 2009 to p(1.25, "Queda"),
                2010 to p(1.40, "Demanda"), 2011 to p(1.80, "Entressafra"),
                2012 to p(1.60, "Estável"), 2013 to p(1.70, "Custos"),
                2014 to p(1.80, "Estável"), 2015 to p(2.20, "Câmbio"),
                2016 to p(2.50, "Crise + safra ruim"), 2017 to p(2.60, "Estável"),
                2018 to p(2.80, "Altá do açúcar"), 2019 to p(2.90, "Estável"),
                2020 to p(2.70, "Pandemia: demanda caiu"), 2021 to p(3.80, "Açúcar disparou + câmbio"),
                2022 to p(4.50, "Pico: tudo subiu"), 2023 to p(3.50, "Estabilizou"),
                2024 to p(3.80, "Reajuste"), 2025 to p(4.20, "Câmbio + custos"),
                2026 to p(4.50, "Estimativa mar/2026")
            )
        ),
        Product(
            id = "energia_kwh", name = "Contá de luz (kWh)", unit = "kWh", category = ProductCategory.ENERGIA,
            source = "ANEEL – Tarifas", sourceUrl = "https://portalrelatorios.aneel.gov.br",
            fraseCustom = "Sua conta de luz financia lucro garantido de distribuidora.",
            hipocrisia = "O Brasil tem a 2ª maior matriz de energia renovável do mundo (hidrelétrica). E a conta de luz não para de subir.",
            contextoCompleto = "Por quê é tão caro?\n\n" +
                "1. BANDEIRAS TARIFÁRIAS: criadas em 2015 (ANEEL, governo Dilma), cobram EXTRA quando falta chuva. " +
                "Bandeira vermelha pode adicionar R\$ 7,877 a cada 100kWh na sua conta.\n\n" +
                "2. IMPOSTOS: ICMS, PIS, COFINS. Dependendo do estado, até 30% da sua conta é imposto.\n\n" +
                "3. DISTRIBUIDORAS COM LUCRO GARANTIDO: a ANEEL garante margem de lucro. " +
                "Se a distribuidora tem prejuízo operacional, quem paga é VOCÊ no reajuste do ano seguinte.\n\n" +
                "Distribuidoras com mais reclamações (2025):\n" +
                "• Enel RJ: 165 mil reclamações\n" +
                "• Light: fila de atendimento de horas\n" +
                "• CPFL: reajustes acima da inflação\n\n" +
                "PRIVATIZAÇÃO DA ELETROBRAS (2022, governo Bolsonaro):\n" +
                "Vendida por R\$ 33 bilhões. Patrimônio público vendido abaixo do valor estimado. " +
                "Impacto na sua conta: a ser visto nos próximos anos.\n\n" +
                "APAGÃO DE 2001 (governo FHC):\n" +
                "O governo não investiu em geração de energia. Resultado: racionamento forçado. " +
                "Prejuízo estimado de R\$ 54 bilhões ao PIB.\n\n" +
                "Quem decidiu ao longo dos anos:\n" +
                "• Governo FHC: não investiu em geração → apagão 2001\n" +
                "• Governo Dilma: criou bandeiras tarifárias (2015)\n" +
                "• Governo Temer: tentou privatizar Eletrobras\n" +
                "• Governo Bolsonaro: privatizou Eletrobras (2022)\n" +
                "• Governo Lula 3: manteve bandeiras tarifárias\n\n" +
                "Fonte: ANEEL, Portal da Transparência, Eletrobras",
            prices = mapOf(
                2000 to p(0.18, "Preço controlado, energia barata"),
                2001 to p(0.22, "Apagão + racionamento + reajuste emergencial",
                    "Governo FHC não investiu em geração. Racionamento forçado. Prejuízo de R\$ 54 bi ao PIB. Fonte: ONS, ANEEL"),
                2002 to p(0.24, "Reajuste pós-apagão"), 2003 to p(0.26, "Normalização"),
                2004 to p(0.28, "Reajuste"), 2005 to p(0.30, "Reajuste"),
                2006 to p(0.32, "Estável"), 2007 to p(0.33, "Leve alta"),
                2008 to p(0.34, "Estável"), 2009 to p(0.35, "Estável"),
                2010 to p(0.36, "Leve alta"), 2011 to p(0.38, "Custos"),
                2012 to p(0.35, "MP 579: Dilma forçou redução de tarifas",
                    "Dilma Rousseff forçou redução de tarifas pela MP 579 (2012). Distribuidoras tiveram prejuízo bilionário. O consumidor pagou depois em reajustes maiores. Fonte: ANEEL"),
                2013 to p(0.33, "Efeito da MP 579"), 2014 to p(0.38, "Reajuste de compensação"),
                2015 to p(0.50, "Bandeiras tarifárias criadas + seca + reajuste de 50%",
                    "ANEEL (governo Dilma) criou bandeiras tarifárias em 2015. Bandeira vermelha: até R\$ 7,877 extras por 100kWh. Fonte: ANEEL"),
                2016 to p(0.55, "Bandeira vermelha + crise"),
                2017 to p(0.52, "Bandeira verde, leve alívio"),
                2018 to p(0.55, "Reajuste"), 2019 to p(0.58, "Reajuste"),
                2020 to p(0.55, "Pandemia: desconto temporário"),
                2021 to p(0.75, "Crise hídrica histórica + bandeira escassez criada",
                    "Bandeira de 'escassez hídrica' criada: R\$ 14,20 extras por 100kWh. Pior crise hídrica em 91 anos. Fonte: ONS, ANEEL"),
                2022 to p(0.80, "Privatização Eletrobras + reajustes",
                    "Governo Bolsonaro privatizou Eletrobras por R\$ 33 bilhões. Patrimônio estimado em muito mais. Fonte: CVM, Congresso Nacional"),
                2023 to p(0.78, "Bandeira verde, leve alívio"),
                2024 to p(0.85, "Reajustes das distribuidoras"),
                2025 to p(0.90, "Custos crescentes + reajustes"),
                2026 to p(0.95, "Estimativa mar/2026: tendência de alta")
            )
        ),
        Product(
            id = "cesta_básica", name = "Cesta básica SP", unit = "mensal", category = ProductCategory.ALIMENTOS,
            source = "DIEESE – Pesquisa Nacional", sourceUrl = "https://www.dieese.org.br/analisecestabásica/cestaBasica.html",
            fraseCustom = "Metade do salário mínimo. Só pra comer.",
            contextoCompleto = "Em 2000, a cestá básica custava R\$ 150 e o salário mínimo era R\$ 151. " +
                "A cestá consumia 99% do salário. Em 2026, a cestá custa R\$ 860 e o salário é R\$ 1.518. " +
                "A cestá consome 57% do salário. Parece melhor?\n\n" +
                "Mas o DIEESE calcula que o salário mínimo NECESSÁRIO é R\$ 7.500. " +
                "Ou seja: o salário atual é 20% do que deveria ser. " +
                "A cestá 'melhorou' porque o salário subiu, mas TUDO MAIS também subiu: " +
                "aluguel, transporte, energia, saúde, educação. No fim, sobra menos.\n\nFonte: DIEESE",
            prices = mapOf(
                2000 to p(150.00, "Pós-Real"), 2001 to p(170.00, "Estável"),
                2002 to p(200.00, "Crise cambial"), 2003 to p(230.00, "Câmbio alto"),
                2004 to p(220.00, "Estabilizando"), 2005 to p(210.00, "Leve queda"),
                2006 to p(205.00, "Estável"), 2007 to p(220.00, "Leve alta"),
                2008 to p(260.00, "Crise mundial de alimentos"), 2009 to p(250.00, "Queda"),
                2010 to p(270.00, "Demanda"), 2011 to p(290.00, "Custos"),
                2012 to p(310.00, "Alta"), 2013 to p(330.00, "Inflação"),
                2014 to p(350.00, "Estável"), 2015 to p(380.00, "Recessão + câmbio"),
                2016 to p(410.00, "Crise econômica"), 2017 to p(400.00, "Estabilizando"),
                2018 to p(430.00, "Referência DIEESE"), 2019 to p(460.00, "Leve alta"),
                2020 to p(498.00, "Pandemia",
                    "PEC 95 (Meirelles, gov. Temer) congelou investimento em saúde/educação por 20 anos. Você gasta mais do bolso com o que o governo deveria pagar. Fonte: Câmara"),
                2021 to p(610.00, "Efeito pandemia + câmbio"),
                2022 to p(700.00, "Guerra + câmbio"), 2023 to p(730.00, "Estabilização parcial"),
                2024 to p(790.00, "Altas específicas"), 2025 to p(830.00, "Acumulado"),
                2026 to p(860.00, "Estimativa mar/2026")
            )
        ),
        Product(
            id = "salario_mínimo", name = "Salário mínimo", unit = "mensal", category = ProductCategory.SERVICOS,
            source = "Governo Federal", sourceUrl = "https://www.gov.br/trabalho-e-emprego/pt-br",
            fraseCustom = "Subiu no papel. Caiu no mercado.",
            contextoCompleto = "O salário mínimo subiu 905% desde 2000 (de R\$ 151 para R\$ 1.518). " +
                "Parece muito? A cestá básica subiu 473%. O gás subiu 539%. A gasolina subiu 300%.\n\n" +
                "O salário mínimo deveria ser R\$ 7.500 segundo o DIEESE. " +
                "Você recebe 20% do necessário para viver com dignidade.\n\n" +
                "Qual governo valorizou mais?\n" +
                "• Lula 1 (2003-2010): +113% (de R\$ 240 para R\$ 510)\n" +
                "• Dilma (2011-2016): +72% (de R\$ 545 para R\$ 880)\n" +
                "• Bolsonaro (2019-2022): +21% (de R\$ 998 para R\$ 1.212) — menor valorização\n" +
                "• Lula 3 (2023-2026): +15% até agora (de R\$ 1.320 para R\$ 1.518)\n\nFonte: DIEESE, Governo Federal",
            prices = mapOf(
                2000 to p(151.00, "Pós-Real"), 2001 to p(180.00, "Reajuste"),
                2002 to p(200.00, "Reajuste"), 2003 to p(240.00, "Reajuste (gov. Lula)"),
                2004 to p(260.00, "Valorização começando"), 2005 to p(300.00, "Valorização real"),
                2006 to p(350.00, "Política de valorização"), 2007 to p(380.00, "Valorização"),
                2008 to p(415.00, "Valorização"), 2009 to p(465.00, "Valorização"),
                2010 to p(510.00, "Valorização real forte"), 2011 to p(545.00, "Reajuste"),
                2012 to p(622.00, "Valorização"), 2013 to p(678.00, "Valorização"),
                2014 to p(724.00, "Valorização"), 2015 to p(788.00, "Reajuste"),
                2016 to p(880.00, "Reajuste"), 2017 to p(937.00, "Reajuste pela inflação"),
                2018 to p(954.00, "Reajuste pela inflação"), 2019 to p(998.00, "Reajuste"),
                2020 to p(1045.00, "Reajuste pela inflação"), 2021 to p(1100.00, "Reajuste"),
                2022 to p(1212.00, "Reajuste"), 2023 to p(1320.00, "Valorização real retomada"),
                2024 to p(1412.00, "Valorização real"), 2025 to p(1518.00, "Valorização real"),
                2026 to p(1518.00, "Mesmo valor de 2025. Reajuste sai em maio/2026")
            )
        ),
        Product(
            id = "impostos", name = "Carga tributária (% do PIB)", unit = "% PIB", category = ProductCategory.SERVICOS,
            source = "Receitá Federal, TCU, IBGE", sourceUrl = "https://www.gov.br/receitafederal",
            fraseCustom = "De cada R\$ 3, R\$ 1 vai pro governo. E o retorno?",
            hipocrisia = "Dinamarca cobra 46% de imposto: saúde, educação e transporte de 1º mundo. " +
                "Brasil cobra 33% e tem fila de 2 anos no SUS, escola caindo e ônibus lotado.",
            contextoCompleto = "CARGA TRIBUTÁRIA BRASILEIRA: 33,7% do PIB (2024).\n" +
                "De cada R\$ 3 que o país produz, R\$ 1 vai pro governo.\n\n" +
                "IMPOSTO SOBRE IMPOSTO:\n" +
                "• Gasolina: ~44% do preço é imposto (ICMS + PIS + COFINS + CIDE)\n" +
                "• Contá de luz: até 30% é imposto\n" +
                "• Carro novo: até 30% é imposto (IPI + ICMS + PIS/COFINS)\n" +
                "• Celular importado: imposto pode ser 100%+ do valor\n" +
                "• Cerveja: ~56% do preço é imposto\n\n" +
                "Você paga imposto no combustível pra ir trabalhar. " +
                "Pra ganhar o salário que já teve IR descontado. " +
                "Pra comprar comida que tem imposto no preço. " +
                "Imposto sobre imposto sobre imposto.\n\n" +
                "E o que você recebe?\n" +
                "• 11.469 obras públicas paradas (TCU, 2025)\n" +
                "• R\$ 15,9 bilhões jogados fora em obras inacabadas\n" +
                "• 70% das obras paradas são de saúde e educação\n\n" +
                "Fonte: Receitá Federal, TCU, IBGE",
            prices = mapOf(
                2000 to p(30.40, "Carga tributária moderada"), 2001 to p(31.00, "Estável"),
                2002 to p(31.90, "Alta"), 2003 to p(31.40, "Estável"),
                2004 to p(32.00, "Crescendo"), 2005 to p(33.40, "Pico"),
                2006 to p(33.30, "Estável"), 2007 to p(33.70, "Alta"),
                2008 to p(33.50, "Estável"), 2009 to p(32.30, "Crise reduziu"),
                2010 to p(32.40, "Recuperando"), 2011 to p(33.30, "Alta"),
                2012 to p(33.00, "Estável"), 2013 to p(33.70, "Pico"),
                2014 to p(32.40, "Estável"), 2015 to p(32.10, "Recessão"),
                2016 to p(32.30, "Crise"), 2017 to p(32.30, "Estável"),
                2018 to p(33.00, "Recuperando"), 2019 to p(33.00, "Estável"),
                2020 to p(31.60, "Pandemia reduziu arrecadação"),
                2021 to p(33.90, "Recuperação + inflação"), 2022 to p(33.70, "Estável"),
                2023 to p(32.40, "Reforma tributária aprovada"), 2024 to p(33.70, "Alta"),
                2025 to p(33.50, "Reforma tributária em implementação"),
                2026 to p(33.50, "Estimativa: transição tributária")
            )
        ),
        Product(
            id = "custo_congresso", name = "Custo do Congresso", unit = "por dia", category = ProductCategory.SERVICOS,
            source = "Câmara dos Deputados, Senado, Portal da Transparência",
            sourceUrl = "https://www.camara.leg.br/transparencia/gastos-parlamentares",
            fraseCustom = "R\$ 40,8 milhões por dia. Incluindo sábado e domingo.",
            hipocrisia = "Deputado ganha R\$ 46 mil/mês. Você ganha R\$ 1.518. " +
                "Ele tem 25 assessores. Você tem você mesmo. " +
                "Ele tem auxílio moradia de R\$ 4.253. Seu aluguel sai do seu bolso.",
            contextoCompleto = "QUANTO CUSTA CADA DEPUTADO FEDERAL:\n" +
                "• Salário: R\$ 46.366/mês\n" +
                "• Verba de gabinete: R\$ 165.806/mês (até 25 assessores)\n" +
                "• Cota parlamentar (CEAP): R\$ 41.600 a R\$ 57.300/mês\n" +
                "• Auxílio moradia: R\$ 4.253/mês\n" +
                "• Total por deputado: ~R\$ 260.000/mês\n\n" +
                "São 513 deputados + 81 senadores = 594 parlamentares.\n\n" +
                "TOTAL DO CONGRESSO: R\$ 40,8 milhões POR DIA.\n" +
                "Incluindo sábado, domingo e feriado.\n" +
                "R\$ 14,9 bilhões por ano.\n\n" +
                "PRESIDENTE DA REPÚBLICA:\n" +
                "• Salário: R\$ 44.008/mês\n" +
                "• Cartão corporativo: R\$ 55 milhões em 2025\n\n" +
                "Eles ganham R\$ 46 mil por mês. Você ganha R\$ 1.518.\n" +
                "Eles têm 25 assessores. Você tem você mesmo.\n" +
                "Eles têm auxílio moradia de R\$ 4.253. Seu aluguel sai do seu bolso.\n\n" +
                "Fonte: Câmara, Senado, Portal da Transparência",
            prices = mapOf(
                2000 to p(15.00, "R\$ 15 milhões/dia"), 2001 to p(16.00, "Reajuste"),
                2002 to p(17.00, "Estável"), 2003 to p(18.00, "Reajuste"),
                2004 to p(19.00, "Estável"), 2005 to p(20.00, "Reajuste"),
                2006 to p(22.00, "Alta"), 2007 to p(23.00, "Reajuste"),
                2008 to p(24.00, "Estável"), 2009 to p(25.00, "Reajuste"),
                2010 to p(26.00, "Estável"), 2011 to p(28.00, "Reajuste"),
                2012 to p(30.00, "Alta"), 2013 to p(31.00, "Estável"),
                2014 to p(32.00, "Reajuste"), 2015 to p(33.00, "Crise mas custo mantido"),
                2016 to p(34.00, "Impeachment + custo"), 2017 to p(35.00, "Reajuste"),
                2018 to p(36.00, "Estável"), 2019 to p(37.00, "Reajuste"),
                2020 to p(38.00, "Pandemia mas custo mantido"), 2021 to p(38.50, "Estável"),
                2022 to p(39.00, "Orçamento secreto R\$ 53 bi"), 2023 to p(39.50, "Reajuste"),
                2024 to p(40.00, "Emendas Pix"), 2025 to p(40.50, "Custo crescente"),
                2026 to p(40.80, "Estimativa mar/2026: R\$ 40,8 mi/dia")
            )
        ),
        Product(
            id = "serviços_públicos", name = "Serviços públicos", unit = "índice", category = ProductCategory.SERVICOS,
            source = "TCU, IBGE, OCDE, Ministério da Saúde",
            sourceUrl = "https://portal.tcu.gov.br",
            fraseCustom = "Você paga imposto pra isso. Tá recebendo?",
            contextoCompleto = "O QUE SEU IMPOSTO (NÃO) COMPRA:\n\n" +
                "SAÚDE:\n" +
                "• Fila do SUS: 1,3 milhão aguardando cirurgia\n" +
                "• Tempo médio de espera: 2 anos e 4 meses\n" +
                "• 60% das mortes são por causas evitáveis\n" +
                "• Orçamento SUS per capita: R\$ 4.000/ano\n" +
                "• Comparação: Reino Unido (NHS): R\$ 18.000/ano\n\n" +
                "EDUCAÇÃO:\n" +
                "• Brasil investe US\$ 3.668/aluno. OCDE: US\$ 11.914. Menos de 1/3\n" +
                "• 3º pior entre 42 países no relatório OCDE\n" +
                "• Meta do PNE: 10% do PIB. Resultado real: 5%\n\n" +
                "TRANSPORTE:\n" +
                "• Passagem de ônibus SP: R\$ 4,40 (2018) → R\$ 5,50+ (2026)\n" +
                "• Quem paga o subsídio é você no IPTU\n\n" +
                "SEGURANÇA:\n" +
                "• Taxa de homicídio: 22 por 100 mil habitantes\n" +
                "• Taxa de resolução: menos de 10%\n" +
                "• 90% dos assassinatos ficam sem solução\n\n" +
                "SANEAMENTO:\n" +
                "• 35 milhões sem água tratada\n" +
                "• 100 milhões sem coletá de esgoto\n" +
                "• Meta de universalizar até 2033. Tá no caminho? Não.\n\n" +
                "OBRAS INACABADAS:\n" +
                "• 11.469 obras paradas (TCU, 2025). Metade de todas.\n" +
                "• 70% são de saúde e educação\n" +
                "• R\$ 15,9 bilhões investidos em obras que pararam\n" +
                "• \"Investidos\" = jogados fora\n\n" +
                "Fonte: TCU, IBGE, OCDE, Ministério da Saúde, PNAD",
            prices = mapOf(
                2000 to p(30.00, "Serviços precários, início de investimento"),
                2001 to p(32.00, "Apagão energético"), 2002 to p(33.00, "Crise"),
                2003 to p(35.00, "Bolsa Família criado"), 2004 to p(37.00, "Melhoria"),
                2005 to p(39.00, "Investimento crescendo"), 2006 to p(41.00, "FUNDEB"),
                2007 to p(43.00, "PAC lançado"), 2008 to p(45.00, "Investimento"),
                2009 to p(47.00, "Minha Casa Minha Vida"), 2010 to p(50.00, "Pico de investimento"),
                2011 to p(52.00, "Copa e Olimpíadas"), 2012 to p(55.00, "Obras"),
                2013 to p(57.00, "Protestos de junho: transporte"), 2014 to p(58.00, "Copa"),
                2015 to p(55.00, "Recessão: cortes"), 2016 to p(52.00, "Crise + PEC 95"),
                2017 to p(50.00, "Teto de gastos congelou"), 2018 to p(50.00, "Estagnado"),
                2019 to p(48.00, "Cortes"), 2020 to p(55.00, "Pandemia: gasto emergencial"),
                2021 to p(52.00, "Auxílio mas serviços piores"), 2022 to p(50.00, "Obras paradas"),
                2023 to p(53.00, "Retomada parcial"), 2024 to p(55.00, "PAC novo"),
                2025 to p(56.00, "11.469 obras paradas (TCU)"),
                2026 to p(57.00, "Estimativa")
            )
        )
    )

    val basicBasket = (2000..2026).map { year ->
        val product = products.find { it.id == "cesta_básica" }
        BasicBasketEntry(year, product?.prices?.get(year)?.price ?: 0.0)
    }

    val minimumWage = (2000..2026).map { year ->
        val product = products.find { it.id == "salario_mínimo" }
        MinimumWageEntry(year, product?.prices?.get(year)?.price ?: 0.0)
    }

    val dieeseSalary = mapOf(
        2000 to 1200.00, 2001 to 1350.00, 2002 to 1500.00, 2003 to 1700.00,
        2004 to 1800.00, 2005 to 1900.00, 2006 to 2050.00, 2007 to 2200.00,
        2008 to 2400.00, 2009 to 2550.00, 2010 to 2700.00, 2011 to 2900.00,
        2012 to 3100.00, 2013 to 3300.00, 2014 to 3500.00, 2015 to 3700.00,
        2016 to 3800.00, 2017 to 3700.00, 2018 to 3752.65, 2019 to 3978.63,
        2020 to 4347.61, 2021 to 5330.68, 2022 to 6388.55, 2023 to 6652.80,
        2024 to 6833.14, 2025 to 7200.00, 2026 to 7500.00
    )

    fun getContextBanner(year: Int): String? = when (year) {
        2001 -> "Apagão energético. Racionamento forçado por falta de investimento em geração."
        2002 -> "Crise cambial: dólar disparou para R\$ 4. Incerteza eleitoral + crise Argentina."
        2003 -> "Novo governo Lula. Câmbio ainda alto. Bolsa Família criado."
        2008 -> "Crise financeira global. Quebra do Lehman Brothers. Alimentos dispararam mundialmente."
        2013 -> "Protestos de junho. 2 milhões nas ruas. Transporte, saúde, educação."
        2014 -> "Copa do Mundo. Operação Lava Jato começa. Recessão à vista."
        2015, 2016 -> "Recessão histórica: PIB caiu 7% em dois anos. Impeachment de Dilma. Temer assume."
        2017 -> "Reforma trabalhista aprovada. PEC 95 congelou saúde e educação por 20 anos."
        2018 -> "Greve dos caminhoneiros. País parou 10 dias. PPI causou a crise."
        2019 -> "Novo governo Bolsonaro. Paulo Guedes prometeu dólar a R\$ 1."
        2020 -> "Pandemia COVID-19. 700 mil mortos. Arroz dobrou. Auxílio de R\$ 600."
        2021 -> "Pandemia prolongada. Gasolina a R\$ 7. Inflação de 10%. Câmbio a R\$ 5,70."
        2022 -> "Guerra Rússia-Ucrânia. Petróleo a US\$ 100+. Gasolina R\$ 7,30 (recorde). Eleições."
        2023 -> "Novo governo Lula 3. Selic acima de 13%. Arcabouço fiscal substitui teto de gastos."
        2024 -> "Regulamentação das bets. Taxação de compras internacionais. Câmbio alto."
        2025 -> "Lei FELCA (CPF nas redes). Selic em 13,25%. Café disparou."
        2026 -> "Dólar acima de R\$ 5,50. Cesta básica R\$ 860. Regulamentação da reforma tributária."
        else -> null
    }

    val provocativePhrases = listOf(
        "Não é crise. É projeto.",
        "Entra governo, sai governo. O preço só sobe.",
        "Até quando você vai aceitando a conta?",
        "A alma da inflação é política.",
        "O Congresso custa R\$ 40,8 milhões por dia. Incluindo feriado.",
        "R\$ 190 bilhões em dividendos da Petrobras. Quanto você recebeu?",
        "11.469 obras paradas. 70% são saúde e educação.",
        "O gás era R\$ 18. Hoje é R\$ 115. O Brasil produz gás.",
        "4 leis de iniciativa popular em 35 anos.",
        "Um deputado custa R\$ 3,5 milhões por ano. Seu salário: R\$ 18 mil.",
        "33% de imposto. Fila de 2 anos no SUS. Tá valendo?",
        "O Brasil produz petróleo, etanol e gás. Tudo caro. Por quê?",
        "A menos que você entenda de política, faz ideia de quem te governa?",
        "Dados do governo. Contra o governo. Irônico, né?"
    )

    val splashPhrases = listOf(
        "Enquanto você lê isso, o Congresso está custando R\$ 28 mil por minuto.",
        "Seu deputado ganha em 1 dia o que você ganha no mês.",
        "90% dos assassinatos ficam sem solução. Seu imposto paga a polícia.",
        "O Brasil produz petróleo. Você paga gasolina como se importasse.",
        "35 milhões sem água tratada. Seu IPTU tá pagando o quê?",
        "38 anos sem votar a taxação de grandes fortunas.",
        "4 leis de iniciativa popular em 35 anos. Ninguém te perguntou nada.",
        "Tem família cozinhando com lenha. O Brasil produz gás.",
        "Seu FGTS rende 3%. A inflação come 5%. Você fica mais pobre guardando.",
        "Juiz ganha R\$ 35 mil com 60 dias de férias. Professor ganha R\$ 4.580.",
        "R\$ 190 bilhões em dividendos da Petrobras. Quanto você recebeu?",
        "A cesta básica custa metade do salário mínimo. Só pra comer.",
        "O cartão corporativo da Presidência gastou R\$ 55 milhões em 2025.",
        "11.469 obras paradas. R\$ 15,9 bilhões jogados fora.",
        "O americano trabalha 17 minutos pra comprar arroz. Você trabalha 4 horas."
    )

    fun getProvocativePhrase(product: Product, variation: Double): String {
        if (product.fraseCustom.isNotEmpty()) return product.fraseCustom
        return getGenericPhrase(variation)
    }

    fun getGenericPhrase(variation: Double): String = when {
        variation > 100 -> "Dobrou. E seu salário?"
        variation > 70 -> "Quase dobrou. Você sentiu."
        variation > 50 -> "Metade a mais. Metade a menos no prato."
        variation > 30 -> "Subiu mais que seu salário."
        variation > 10 -> "Subiu. De novo."
        variation > 5 -> "Leve alta. Leve mesmo?"
        variation > -5 -> "Estável. Por enquanto."
        else -> "Caiu. Dessa vez."
    }

    val onboardingTexts = listOf(
        "Esse app não é de economia.", "Não é de política.",
        "É sobre o seu prato.", "Sobre o seu bolso.",
        "Sobre o que fizeram com você.", "Devagarinho.", "Sem você perceber.",
        "", "Até agora."
    )

    val cidadaoActions = listOf(
        CidadaoAction("Gastos do seu deputado", "CNPJ, valor, fornecedor. Tudo público.", "SIM. Dados reais. Problema: ver não significa poder impedir.", "https://www.camara.leg.br/transparencia/gastos-parlamentares", "O dinheiro é seu. Confere.", nota = 8),
        CidadaoAction("Consumidor.gov.br", "Resolve problemas com empresas.", "SIM. 86% de resolução. Melhor que PROCON.", "https://consumidor.gov.br", "Funciona. De verdade.", nota = 8),
        CidadaoAction("ANEEL (energia)", "Resolver conta de luz.", "SIM. Distribuidoras temem a ANEEL.", "https://portalrelatorios.aneel.gov.br", "Contá absurda? Reclame.", nota = 7),
        CidadaoAction("LAI (informação)", "Governo obrigado a responder em 20 dias.", "MAIS OU MENOS. 75% tem algum acesso. 70% dos recursos atrasados negados.", "https://falabr.cgu.gov.br", "O governo te deve transparência.", nota = 5),
        CidadaoAction("Acompanhar votações", "Ver o que votam no Congresso.", "Funciona, mas linguagem técnica impenetrável.", "https://www.camara.leg.br/atividade-legislativa", "Seu deputado trabalha pra você. Será?", nota = 5),
        CidadaoAction("PROCON", "Preço abusivo.", "POUCO. 29% resolução em SP. Não serve pra inflação.", "https://www.procon.sp.gov.br", "Funciona pra empresa. Não pra política.", nota = 3),
        CidadaoAction("ANP (combustível)", "Fiscalizar gasolina.", "Sua reclamação vira estatística. ANP REDUZIU monitoramento por falta de verba.", "https://preços.anp.gov.br", "Reclame. Mesmo que vire estatística.", nota = 2),
        CidadaoAction("e-Cidadania", "Propor lei ao Senado.", "71.000 ideias. 23 projetos. 0,03%.", "https://www12.senado.leg.br/ecidadania", "Sua opinião conta. Em 0,03% das vezes.", nota = 2),
        CidadaoAction("Iniciativa popular", "Lei feitá pelo povo. 1,5 milhão de assinaturas.", "4 leis em 35 anos. Nenhuma tratada como do povo.", "https://www.camara.leg.br", "Quase impossível. Mas existe.", nota = 1),
        CidadaoAction("Plebiscito", "Sua opinião sobre leis.", "NÃO. Último: 2005. Há 21 anos ninguém te perguntou nada.", "https://www.tse.jus.br", "Há 21 anos ninguém te perguntou nada.", nota = 0)
    )

    val cidadaoFooterText = "Os canais de transparência funcionam: você consegue VER.\n" +
        "Os canais de participação são quase todos teatro: você NÃO consegue MUDAR.\n\n" +
        "O sistema é desenhado pra você assistir, não participar.\n" +
        "Dados: DataSenado, CGU, Câmara dos Deputados.\n\n" +
        "Saber disso já é um passo.\nPorque quem não sabe, aceita.\nE quem aceita, paga."

    val políticalProfiles = listOf(
        PoliticalProfile(
            id = "pedro_parente", nome = "Pedro Parente",
            cargo = "Presidente da Petrobras (2016-2018)", partido = "Sem partido", estado = "DF",
            mandatos = listOf("Pres. Petrobras 2016-2018", "Min. Casa Civil gov. FHC"),
            decisoes = listOf(
                "Criou o PPI (2016): preço de combustível atrelado ao dólar internacional",
                "Gasolina saiu de R\$ 3,50 para acima de R\$ 4,20 sob sua gestão",
                "Gás de cozinha passou a seguir preço internacional"
            ),
            custos = "Salário de presidente da Petrobras: ~R\$ 120 mil/mês + bônus",
            escandalos = listOf("Renunciou em 2018 após greve dos caminhoneiros causada pelo PPI"),
            twitter = "@PedroParente"
        ),
        PoliticalProfile(
            id = "campos_neto", nome = "Roberto Campos Neto",
            cargo = "Presidente do Banco Central (2019-2024)", partido = "Sem partido", estado = "SP",
            mandatos = listOf("Pres. BCB 2019-2024 (mandato fixo, indicado por Bolsonaro)"),
            decisoes = listOf(
                "Cortou Selic para 2% (2020): dólar disparou de R\$ 4 para R\$ 5,90",
                "Subiu Selic para 13,75% (2022): crédito ficou caro, economia desacelerou",
                "Manteve Selic altá durante 2023-2024 apesar de pressão do governo Lula"
            ),
            custos = "Salário presidente BCB: ~R\$ 33 mil/mês",
            escandalos = listOf("Investigado por manter offshores no exterior enquanto presidente do BCB"),
            twitter = "@CamposNeto"
        ),
        PoliticalProfile(
            id = "paulo_guedes", nome = "Paulo Guedes",
            cargo = "Ministro da Economia (2019-2022)", partido = "Sem partido", estado = "RJ",
            mandatos = listOf("Min. Economia gov. Bolsonaro 2019-2022"),
            decisoes = listOf(
                "Prometeu dólar a R\$ 1 se reformas passassem. Dólar foi a R\$ 5,90",
                "Manteve PPI: gasolina bateu R\$ 7,30",
                "Auxílio Emergencial R\$ 600 (depois cortou para R\$ 250)",
                "Teto de gastos flexibilizado no final do mandato para caber Auxílio Brasil"
            ),
            custos = "Salário de ministro: ~R\$ 39 mil/mês",
            escandalos = listOf("Pandora Papers: offshore de US\$ 9,5 milhões nas Ilhas Virgens Britânicas enquanto era ministro"),
            twitter = "@PauloGuedesBR"
        ),
        PoliticalProfile(
            id = "tereza_cristina", nome = "Tereza Cristina",
            cargo = "Ministra da Agricultura (2019-2022)", partido = "PP", estado = "MS",
            mandatos = listOf("Min. Agricultura gov. Bolsonaro 2019-2022", "Senadora 2023-presente"),
            decisoes = listOf(
                "Não regulou exportações de arroz em 2020: preço dobrou em meses",
                "CONAB zerou estoques reguladores sob sua gestão",
                "Brasil exportou arroz enquanto faltava no mercado interno",
                "Liberou 1.682 agrotóxicos no mandato (recorde)"
            ),
            custos = "Salário de ministra: ~R\$ 39 mil/mês. Agora senadora: R\$ 46 mil/mês",
            twitter = "@TesijaCristina", email = "tereza.cristina@senado.leg.br"
        ),
        PoliticalProfile(
            id = "arthur_lira", nome = "Arthur Lira",
            cargo = "Presidente da Câmara (2021-2025)", partido = "PP", estado = "AL",
            mandatos = listOf("Deputado Federal 2003-presente", "Pres. Câmara 2021-2025"),
            decisoes = listOf(
                "Orçamento Secreto: R\$ 53 bilhões distribuídos sem transparência",
                "Emendas Pix: novo mecanismo de distribuição sem rastreio",
                "Controlou pautá da Câmara: decidia o que era votado e quando"
            ),
            custos = "Salário: R\$ 46 mil/mês + CEAP até R\$ 57 mil/mês + verba gabinete R\$ 165 mil/mês",
            escandalos = listOf(
                "Réu por corrupção passiva (Lava Jato). Processo em andamento",
                "Orçamento Secreto declarado inconstitucional pelo STF"
            ),
            twitter = "@ArthurLira_", email = "dep.arthurlira@camara.leg.br"
        ),
        PoliticalProfile(
            id = "pazuello", nome = "Eduardo Pazuello",
            cargo = "Ministro da Saúde (2020-2021)", partido = "PL", estado = "RJ",
            mandatos = listOf("Min. Saúde interino/efetivo 2020-2021", "Deputado Federal 2023-presente"),
            decisoes = listOf(
                "Ignorou 53 e-mails da Pfizer oferecendo vacinas",
                "Recusou 70 milhões de doses inicialmente",
                "Ministério recomendou cloroquina sem eficácia comprovada",
                "Adiou início da vacinação em meses em relação a outros países"
            ),
            custos = "Agora deputado: R\$ 46 mil/mês + benefícios",
            escandalos = listOf("CPI da COVID recomendou indiciamento por prevaricação e comunicação falsa de epidemia"),
            twitter = "@GeneralPazuello", email = "dep.eduardopazuello@camara.leg.br"
        ),
        PoliticalProfile(
            id = "meirelles", nome = "Henrique Meirelles",
            cargo = "Ministro da Fazenda (2016-2018)", partido = "PSD", estado = "GO",
            mandatos = listOf("Min. Fazenda gov. Temer 2016-2018", "Pres. BCB gov. Lula 1 2003-2010"),
            decisoes = listOf(
                "Propôs PEC 95 (Teto de Gastos): congelou saúde e educação por 20 anos",
                "Como pres. BCB (2003-2010): manteve juros altos mas estabilizou economia",
                "Reforma trabalhistá tramitou sob sua gestão na Fazenda"
            ),
            custos = "Patrimônio declarado: R\$ 377 milhões (um dos políticos mais ricos do Brasil)"
        ),
        PoliticalProfile(
            id = "haddad", nome = "Fernando Haddad",
            cargo = "Ministro da Fazenda (2023-presente)", partido = "PT", estado = "SP",
            mandatos = listOf("Min. Fazenda gov. Lula 3 2023-presente", "Prefeito de SP 2013-2016", "Min. Educação 2005-2012"),
            decisoes = listOf(
                "Novo arcabouço fiscal substituiu Teto de Gastos",
                "Meta de déficit zero: flexibilizada no primeiro ano",
                "Reforma tributária aprovada (IBS + CBS substituem PIS/COFINS/ICMS)"
            ),
            custos = "Salário de ministro: ~R\$ 39 mil/mês",
            twitter = "@Haborad"
        ),
        PoliticalProfile(
            id = "galipolo", nome = "Gabriel Galípolo",
            cargo = "Presidente do Banco Central (2025-presente)", partido = "Sem partido", estado = "SP",
            mandatos = listOf("Pres. BCB 2025-presente (indicado por Lula)", "Diretor de Política Monetária BCB 2023-2024"),
            decisoes = listOf(
                "Herdou Selic a 13,25%. Manteve taxa alta",
                "Primeira indicação de presidente do BCB pelo governo Lula 3"
            ),
            custos = "Salário presidente BCB: ~R\$ 33 mil/mês"
        )
    )

    fun getProfileById(id: String) = políticalProfiles.find { it.id == id }

    val leisAprovadas = listOf(
        LeiAprovada(
            id = "felca", nome = "Lei FELCA", nomePopular = "CPF obrigatório pra rede social",
            ano = 2026,
            oQueE = "Exige CPF pra criar conta em redes sociais. Na teoria: proteger crianças. Na prática: vigilância estatal. O governo sabe tudo que você posta.",
            quemFez = listOf("Autor: Sen. Alessandro Vieira (MDB-SE)", "Relator: Dep. Orlando Silva (PCdoB-SP)", "Governo Lula 3 sancionou"),
            oQueMudou = "Medo de se expressar. Autocensura. O governo pode vincular suas opiniões ao seu CPF. Críticas ao governo ficam rastreáveis.",
            aindaEstaLa = listOf("Alessandro Vieira: Senador (2019-2027)", "Orlando Silva: Deputado Federal"),
            nivelTrouxa = 9, fonte = "Senado Federal, Congresso Nacional"
        ),
        LeiAprovada(
            id = "ppi", nome = "PPI - Paridade Internacional", nomePopular = "Gasolina em dólar num país que produz petróleo",
            ano = 2016,
            oQueE = "Pedro Parente (Petrobras, governo Temer) atrelou o preço da gasolina brasileira ao preço INTERNACIONAL em DÓLAR. Você paga como se importasse, mesmo o petróleo sendo nosso.",
            quemFez = listOf("Pedro Parente (Pres. Petrobras, gov. Temer)", "Michel Temer (Presidente)", "Todos os governos depois mantiveram"),
            oQueMudou = "Gasolina de R\$ 3,50 para R\$ 7,30 (pico). Gás de R\$ 45 para R\$ 115. Frete subiu, comida subiu, tudo subiu.",
            aindaEstaLa = listOf("Pedro Parente: fora do governo", "A POLÍTICA PPI continua vigente até hoje. Nenhum governo desfez."),
            nivelTrouxa = 10, fonte = "Petrobras, ANP"
        ),
        LeiAprovada(
            id = "pec95", nome = "PEC 95 - Teto de Gastos", nomePopular = "Saúde e educação congeladas por 20 anos",
            ano = 2016,
            oQueE = "Congelou gastos do governo com saúde, educação e assistência social por 20 anos. A população cresce, o investimento por pessoa DIMINUI.",
            quemFez = listOf("Henrique Meirelles (Min. Fazenda, gov. Temer)", "Michel Temer (Presidente)", "Substituída pelo arcabouço fiscal em 2023 (gov. Lula 3)"),
            oQueMudou = "SUS sucateado: fila de 2 anos pra cirurgia. Universidades sem verba. Você paga mais do bolso com saúde e escola particular.",
            aindaEstaLa = listOf("Henrique Meirelles: sem cargo público", "A PEC foi substituída mas o dano de 7 anos (2016-2023) já foi feito"),
            nivelTrouxa = 9, fonte = "Câmara dos Deputados, TCU"
        ),
        LeiAprovada(
            id = "reforma_trab", nome = "Reforma Trabalhista", nomePopular = "Seus direitos trabalhistas flexibilizados",
            ano = 2017,
            oQueE = "Trabalho intermitente (paga só quando trabalha), terceirização total, intervalo de almoço reduzível, sindicatos perderam 90% da receita.",
            quemFez = listOf("Rogério Marinho (Relator, gov. Temer)", "Michel Temer (sancionou)", "Lei 13.467/2017"),
            oQueMudou = "Precarização do trabalho. Mais gente em subemprego. Sindicatos enfraquecidos. Patrão com mais poder de negociação individual.",
            aindaEstaLa = listOf("Rogério Marinho: Senador (2023-2031)", "A lei continua vigente"),
            nivelTrouxa = 8, fonte = "Congresso Nacional, DIEESE"
        ),
        LeiAprovada(
            id = "orc_secreto", nome = "Orçamento Secreto", nomePopular = "R\$ 53 bilhões sem transparência",
            ano = 2020,
            oQueE = "Sistema de emendas de relator que distribuía bilhões a deputados e senadores sem transparência. Ninguém sabia pra onde ia o dinheiro.",
            quemFez = listOf("Arthur Lira (Pres. Câmara, PP-AL) operou", "Governo Bolsonaro usou pra comprar apoio no Congresso", "STF declarou inconstitucional em dez/2022"),
            oQueMudou = "R\$ 53 bilhões distribuídos sem que o cidadão soubesse pra onde foi. Dinheiro que deveria ser de saúde e educação virou moeda de troca política.",
            aindaEstaLa = listOf("Arthur Lira: Deputado Federal (saiu da presidência em 2025)", "Emendas Pix continuam como substituto parcial"),
            nivelTrouxa = 10, fonte = "STF (ADPFs 850, 851, 854, 1.014), TCU"
        ),
        LeiAprovada(
            id = "bets", nome = "Regulamentação das Bets", nomePopular = "Liberaram as apostas online",
            ano = 2024,
            oQueE = "O governo regulamentou sites de apostas. Agora é legal. Brasileiro gastou R\$ 240 bilhões em bets em 2024. Mais que o Bolsa Família inteiro.",
            quemFez = listOf("Governo Lula 3 (regulamentação 2024)", "Congresso Nacional aprovou o marco"),
            oQueMudou = "Mais fácil se viciar em apostas (legalizadas). 30% das apostas vêm de dinheiro do Bolsa Família. O governo cobra 30% de imposto sobre ganhos.",
            aindaEstaLa = listOf("A regulamentação está em vigor", "Empresas de apostas operam legalmente no Brasil"),
            nivelTrouxa = 8, fonte = "Banco Central, Ministério da Fazenda"
        ),
        LeiAprovada(
            id = "grandes_fortunas", nome = "Imposto sobre Grandes Fortunas", nomePopular = "Imposto pros super ricos que NUNCA foi votado",
            ano = 1988,
            oQueE = "Está na Constituição desde 1988 (Art. 153, VII). Nunca foi regulamentada. Há 38 anos esperando votação. Mais de 30 projetos apresentados. Todos arquivados.",
            quemFez = listOf("Constituição de 1988 previu", "Nenhum Congresso em 38 anos regulamentou"),
            oQueMudou = "Nada. Porque nunca foi votada. Estimativa de arrecadação: R\$ 40-80 bilhões/ano. O Congresso decidiu que NÃO vai taxar quem financia campanha.",
            aindaEstaLa = listOf("A previsão constitucional existe desde 1988", "O Congresso se recusa a regulamentar há 38 anos"),
            nivelTrouxa = 10, fonte = "Constituição Federal, Art. 153 VII"
        ),
        LeiAprovada(
            id = "reforma_prev", nome = "Reforma da Previdência", nomePopular = "Mudaram sua aposentadoria",
            ano = 2019,
            oQueE = "Idade mínima pra aposentar subiu. Tempo de contribuição subiu. Cálculo do benefício ficou pior. Homem: 65 anos + 20 anos de contribuição. Mulher: 62 anos + 15 anos.",
            quemFez = listOf("Paulo Guedes (Min. Economia, gov. Bolsonaro)", "Congresso aprovou com 379 votos", "EC 103/2019"),
            oQueMudou = "Você trabalha mais e recebe menos. O cálculo agora usa 100% das contribuições (antes era as 80% melhores). Na prática, a aposentadoria é menor.",
            aindaEstaLa = listOf("A reforma está em vigor desde novembro de 2019", "Nenhum governo propôs reverter"),
            nivelTrouxa = 7, fonte = "Congresso Nacional, EC 103/2019"
        ),
        LeiAprovada(
            id = "autonomia_bc", nome = "Autonomia do Banco Central", nomePopular = "BC não obedece mais o presidente",
            ano = 2021,
            oQueE = "O presidente do BC tem mandato fixo de 4 anos. Não pode ser demitido pelo presidente da República. Quem define se seu financiamento é caro ou barato é alguém que você NÃO elegeu.",
            quemFez = listOf("Governo Bolsonaro sancionou", "Roberto Campos Neto foi o primeiro com mandato fixo", "LC 179/2021"),
            oQueMudou = "O BC define a Selic (juros). Juros altos = crédito caro. O presidente eleito NÃO controla mais os juros. Você vota no presidente, mas quem decide seu financiamento é outro.",
            aindaEstaLa = listOf("A lei está em vigor", "Gabriel Galípolo (indicado por Lula) assumiu em 2025 com mandato até 2029"),
            nivelTrouxa = 6, fonte = "Congresso Nacional, LC 179/2021"
        )
    )

    val governantesAtuais = listOf(
        Governante("Luiz Inácio Lula da Silva", "Presidente da República", "PT", "Salário: R\$ 44.008/mês + cartão corporativo R\$ 55 mi/ano", "@LulaOficial", ""),
        Governante("Geraldo Alckmin", "Vice-Presidente", "PSB", "Salário: R\$ 44.008/mês", "@geraboralckmin", ""),
        Governante("Hugo Motta", "Presidente da Câmara", "Republicanos-PB", "Salário: R\$ 46.366/mês + CEAP + gabinete = ~R\$ 260 mil/mês", "@hugomaborotta", "dep.hugomotta@camara.leg.br"),
        Governante("Davi Alcolumbre", "Presidente do Senado", "União-AP", "Salário: R\$ 46.366/mês + verbas = ~R\$ 300 mil/mês", "@davialcolumbre", ""),
        Governante("Gabriel Galípolo", "Presidente do Banco Central", "Sem partido", "Salário: ~R\$ 33 mil/mês. Define a Selic que afeta seu financiamento.", "", ""),
        Governante("Fernando Haddad", "Ministro da Fazenda", "PT", "Salário: ~R\$ 39 mil/mês. Decide impostos e gastos.", "@Haddad_Fernando", "")
    )

    // Smart notification messages by time of day
    val notifManha = listOf(
        "Bom dia. O arroz subiu 540% desde 2000. O café que você toma custava R\$ 2. Hoje: R\$ 5.",
        "Bom dia. Seu deputado ganha R\$ 46 mil/mês. Você ganha R\$ 1.518. Bom trabalho hoje.",
        "O Congresso já gastou R\$ 40,8 milhões desde que você acordou. Incluindo o domingo.",
    )
    val notifAlmoco = listOf(
        "Esse prato custava menos em 2020. O arroz subiu, o feijão subiu, a carne subiu. Seu salário? Nem tanto.",
        "Seu deputado gastou R\$ 57 mil em 'alimentação e divulgação' esse mês. E o seu almoço?",
    )
    val notifTarde = listOf(
        "Você trabalhou o dia inteiro. 33% do seu salário foi pra imposto. Veja pra onde foi.",
        "O gás era R\$ 18 em 2000. Hoje R\$ 115. O Brasil produz gás. E você paga caro.",
    )
    val notifNoite = listOf(
        "Resumo: o Congresso custa R\$ 40,8 mi/dia. Incluindo hoje. Incluindo feriado.",
        "11.469 obras paradas. 70% de saúde e educação. R\$ 15,9 bi jogados fora.",
    )

    fun getNotifByHour(hour: Int): String = when {
        hour in 6..9 -> notifManha.random()
        hour in 11..13 -> notifAlmoco.random()
        hour in 17..19 -> notifTarde.random()
        hour in 20..23 -> notifNoite.random()
        else -> provocativePhrases.random()
    }

    fun getPresidentForYear(year: Int): President? {
        return presidents.find { year >= it.startYear && (it.endYear == null || year <= it.endYear) }
    }
    fun getProductsByCategory(category: ProductCategory) = products.filter { it.category == category }
    fun getProductById(id: String) = products.find { it.id == id }

    const val CURRENT_YEAR = 2026
    const val CURRENT_MONTH = 3
    const val START_YEAR = 2000
    const val MONTHLY_START_YEAR = 2020

    fun hasMonthlyData(year: Int): Boolean = year in MONTHLY_START_YEAR..CURRENT_YEAR

    /**
     * Generates monthly prices by interpolating between this year's annual price
     * and next year's annual price, with a slight seasonal curve.
     */
    fun getMonthlyPrices(productId: String, year: Int): Map<Int, Double>? {
        if (!hasMonthlyData(year)) return null
        val product = getProductById(productId) ?: return null
        val thisPrice = product.prices[year]?.price ?: return null
        val nextPrice = product.prices[year + 1]?.price ?: thisPrice
        val prevPrice = product.prices[year - 1]?.price ?: thisPrice

        val maxMonth = if (year == CURRENT_YEAR) CURRENT_MONTH else 12

        return (1..maxMonth).associateWith { month ->
            // Smooth interpolation from prevYear end -> thisYear annual -> nextYear start
            val t = (month - 1.0) / 11.0
            val startVal = (prevPrice + thisPrice) / 2.0
            val endVal = (thisPrice + nextPrice) / 2.0
            val base = startVal + (endVal - startVal) * t
            // Keep within ±8% of annual price for realism
            val clamped = base.coerceIn(thisPrice * 0.92, thisPrice * 1.08)
            (clamped * 100).toLong() / 100.0 // round to 2 decimals
        }
    }

    val monthNames = listOf(
        "", "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
        "Jul", "Ago", "Set", "Out", "Nov", "Dez"
    )
}
