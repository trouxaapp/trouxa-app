package com.trouxaapp.app.data.local

import com.trouxaapp.app.data.model.GastoPublico
import com.trouxaapp.app.data.model.ImpactCard
import com.trouxaapp.app.data.model.LeiResumo
import com.trouxaapp.app.data.model.DidYouKnow

object HojeDataProvider {

    val impactCards = listOf(
        ImpactCard(1, "Seu salário mínimo compra 1,69 cestas básicas. Em 2014 comprava 2,19. Você ficou mais pobre trabalhando a mesma coisa.", "DIEESE, 2026"),
        ImpactCard(2, "O Congresso custa R\$ 40,8 milhões por dia. Incluindo sabado, domingo e feriado. Quanto custa a escola do seu bairro?", "Câmara dos Deputados, Senado Federal"),
        ImpactCard(3, "O Brasil produz 3,7 milhões de barris de petróleo por dia. Você paga R\$ 6,40 no litro como se importasse tudo.", "ANP, Petrobras"),
        ImpactCard(4, "11.469 obras públicas paradas. 70% são de saúde e educação. R\$ 15,9 bilhões jogados fora.", "TCU, 2025"),
        ImpactCard(5, "De cada R\$ 100 de gasolina, R\$ 44 e imposto. Você paga imposto no combustível pra ir trabalhar ganhar o salário que já teve imposto descontado pra comprar comida que tem imposto no preço.", "Receitá Federal"),
        ImpactCard(6, "90% dos assassinatos no Brasil ficam sem solução. Sua segurança custa caro no imposto e não funciona.", "FBSP, IBGE"),
        ImpactCard(7, "35 milhões de brasileiros sem água tratada. 100 milhões sem coletá de esgoto. Seu IPTU tá pagando o que?", "SNIS, 2024"),
        ImpactCard(8, "O arroz era R\$ 5 em 2000. Hoje custa R\$ 32. Aumento de 540%. Seuavô comprava sem pensar. Você pensa 3 vezes.", "DIEESE"),
        ImpactCard(9, "Um deputado federal custa R\$ 3,5 milhões por ano. Você ganha R\$ 18 mil por ano. Ele custa 194 vocês.", "Câmara dos Deputados"),
        ImpactCard(10, "O DIEESE calcula que o salário mínimo deveria ser R\$ 7.500. Você recebe R\$ 1.518. Isso e 20% do necessário.", "DIEESE, mar/2026"),
        ImpactCard(11, "Em 2022, a Petrobras distribuiu R\$ 190 bilhões em dividendos. No mesmo ano, você pagou R\$ 7,30 na gasolina.", "Petrobras, CVM"),
        ImpactCard(12, "O café subiu 833% desde 2000. O Brasil e o maior produtor de café do mundo. E você paga caro pelo que sobra da exportacao.", "CONAB, ICO"),
        ImpactCard(13, "A carne bovina subiu 700% desde 2000. O Brasil tem o maior rebanho comercial do mundo: 220 milhões de cabeças. O boi vai pra fora, o ovo fica pra você.", "IBGE, MDIC"),
        ImpactCard(14, "O gas de cozinha era R\$ 18 em 2000. Hoje e R\$ 115. O Brasil e o maior produtor de gas natural da America do Sul. Tem familia cozinhando com lenha.", "ANP"),
        ImpactCard(15, "4 leis de iniciativa popular em 35 anos de Constituição. E nenhuma foi tratada como se fosse do povo de verdade.", "DataSenado"),
        ImpactCard(16, "71 mil ideias enviadas ao Senado pelo e-Cidadania. 23 viraram projeto. 0,03%. Sua opiniao conta. Em 0,03% das vezes.", "Senado Federal"),
        ImpactCard(17, "O último plebiscito foi em 2005. Ha 21 anos ninguem te perguntou nada.", "TSE"),
        ImpactCard(18, "A cestá básica de SP consome 57% do salário mínimo. Metade do que você ganha vai SO pra comer. O basico.", "DIEESE"),
        ImpactCard(19, "O cartão corporativo da Presidencia gastou R\$ 55 milhões em 2025. Você sabe em que?", "Portal da Transparência"),
        ImpactCard(20, "Deputado ganha R\$ 46 mil/mes. Tem auxílio moradia de R\$ 4.253. Tem 25 assessores. Tem cotá de R\$ 57 mil pra gastar. E você?", "Câmara dos Deputados"),
        ImpactCard(21, "O ovo era R\$ 1,20 a dúzia em 2000. Hoje custa R\$ 13. Aumento de 983%. O basico do basico virou luxo.", "DIEESE"),
        ImpactCard(22, "O Brasil trabalha 110 horas por mês so pra pagar a cestá básica. No Uruguai são 50 horas. Na Argentina, 60.", "DIEESE, OIT"),
        ImpactCard(23, "Pedro Parente atrelou a gasolina ao dolar em 2016. Nenhum governo depois desfez. Nem esquerda, nem direita.", "Petrobras"),
        ImpactCard(24, "Roberto Campos Neto cortou a Selic pra 2% em 2020. O dolar foi de R\$ 4 pra R\$ 5,90. Tudo cotado em dolar ficou caro. Seu salário continuou em real.", "BCB"),
        ImpactCard(25, "Tereza Cristina não regulou exportacoes de arroz em 2020. A CONAB tinha zerado os estoques. O preço dobrou em meses.", "CONAB, MDIC"),
        ImpactCard(26, "O Orçamento Secreto distribuiu R\$ 53 bilhões sem que você soubesse pra onde foi. O STF declarou inconstitucional. Emendas Pix continuam.", "STF, TCU"),
        ImpactCard(27, "A PEC 95 congelou gastos com saúde e educação por 20 anos. A população cresceu. O investimento por pessoa diminuiu.", "Câmara dos Deputados, TCU"),
        ImpactCard(28, "A conta de luz subiu 428% desde 2000. O Brasil tem a 2a maior matriz de energia renovavel do mundo. E a conta não para de subir.", "ANEEL"),
        ImpactCard(29, "Em 2021, criaram a bandeira de 'escassez hidrica': R\$ 14,20 extras por 100kWh na sua conta. A pior em 91 anos.", "ANEEL, ONS"),
        ImpactCard(30, "A Eletrobras foi privatizada por R\$ 33 bilhões em 2022. Patrimonio estimado em muito mais. Quem lucrou não foi você.", "CVM, Congresso"),
        ImpactCard(31, "Em 2015, Dilma forcou reducao de tarifas de energia. Distribuidoras tiveram prejuízo. Quem pagou depois? Você, em reajustes maiores.", "ANEEL"),
        ImpactCard(32, "O etanol brasileiro e o mais barato de produzir do mundo. Mas as usinas preferem exportar acucar. Seu tanque e menos lucrativo que o mercado internacional.", "UNICA, ANP"),
        ImpactCard(33, "A reforma trabalhistá de 2017 criou trabalho intermitente: o patrao so paga quando você trabalha. Sindicatos perderam 90% da receita.", "Lei 13.467/2017, DIEESE"),
        ImpactCard(34, "O SUS tem fila de 1,3 milhao aguardando cirurgia. Tempo médio de espera: 2 anos e 4 meses. Seu imposto paga isso.", "Ministerio da Saude"),
        ImpactCard(35, "O Brasil investe US\$ 3.668 por aluno. A media da OCDE e US\$ 11.914. Menos de um terco. E o 3o pior entre 42 paises.", "OCDE"),
        ImpactCard(36, "33% de carga tributaria. Fila de 2 anos no SUS. 90% dos homicídios sem solução. Escola caindo aos pedacos. Ta valendo?", "Receitá Federal, IBGE"),
        ImpactCard(37, "A Dinamarca cobra 46% de imposto: saúde, educação e transporte de 1o mundo. O Brasil cobra 33% e não entrega nada parecido.", "OCDE"),
        ImpactCard(38, "Paulo Guedes prometeu dolar a R\$ 1 se as reformas passassem. O dolar foi a R\$ 5,90. Ele tinha offshore de US\$ 9,5 milhões.", "Pandora Papers, BCB"),
        ImpactCard(39, "53 e-mails da Pfizer ignorados. 70 milhões de doses recusadas. Quase 700 mil mortos. 'E dai? Nao sou coveiro.'", "CPI da COVID, Senado"),
        ImpactCard(40, "O leite era R\$ 0,95 em 2000. Em 2022 bateu R\$ 7. Entre 2015 e 2022 o Brasil perdeu 100 mil produtores de leite.", "IBGE, Embrapa"),
        ImpactCard(41, "O feijao chegou a R\$ 6,50 em 2016 por causa de seca. Nenhum governo criou estoque estrategico de feijao. Arroz tem. Feijao nao.", "CONAB"),
        ImpactCard(42, "Em 2019, a China teve febre suina e comecou a importar carne brasileira em volume recorde. O governo priorizou exportacao. Carne a R\$ 40/kg.", "MDIC, IBGE"),
        ImpactCard(43, "A Vale foi vendida por R\$ 3,3 bilhões em 1997. Hoje vale mais de R\$ 250 bilhões. Patrimonio público vendido a preço de banana.", "BNDES, CVM"),
        ImpactCard(44, "O apagao de 2001 aconteceu porque o governo FHC não investiu em geracao de energia. Prejuizo de R\$ 54 bilhões ao PIB. Você pagou a conta.", "ONS, ANEEL"),
        ImpactCard(45, "Dilma sofreu impeachment por pedaladas fiscais de R\$ 40 bilhões. O Orçamento Secreto movimentou R\$ 53 bilhões. Quem respondeu por isso?", "TCU, STF"),
        ImpactCard(46, "A Selic tá acima de 13% ha quase todo o governo Lula 3. Seu financiamento, seu cartao, seu cheque especial: tudo mais caro.", "BCB"),
        ImpactCard(47, "Lula prometeu picanha e cerveja. A carne subiu 20% no mandato. A cestá básica foi de R\$ 730 pra R\$ 860.", "DIEESE"),
        ImpactCard(48, "Michel Temer teve 5% de aprovacao. O presidente mais impopular da historia. Mesmo assim governou até o fim e ninguem pode fazer nada.", "Datafolha"),
        ImpactCard(49, "Temer foi denunciado 2 vezes por corrupção. Nao foi julgado. Foro privilegiado. A justica e a mesma pra todo mundo?", "STF, PGR"),
        ImpactCard(50, "Passagem de onibus em SP: R\$ 4,40 em 2018. Hoje: R\$ 5,50+. Quem paga o subsidio e você no IPTU.", "SPTrans"),
        ImpactCard(51, "60% das mortes no Brasil são por causas evitaveis. O orçamento do SUS per capitá e R\$ 4 mil/ano. No Reino Unido e R\$ 18 mil.", "OMS, NHS"),
        ImpactCard(52, "A meta do Plano Nacional de Educacao era investir 10% do PIB. Resultado real: 5%. Metade da meta. Ha 10 anos.", "MEC, INEP"),
        ImpactCard(53, "O dolar tá acima de R\$ 5,50 permanentemente desde 2020. Tudo que e cotado em dolar ficou caro pra sempre. Seu salário continua em real.", "BCB"),
        ImpactCard(54, "O arcabouco fiscal substituiu o teto de gastos. A meta de déficit zero foi flexibilizada no primeiro ano. Ou seja: já comecou furando.", "Congresso Nacional"),
        ImpactCard(55, "O PROCON resolve 29% dos casos em SP. A ANP recebe sua reclamacao sobre gasolina e transforma em estatistica. Ninguém te defende de verdade.", "PROCON-SP, ANP"),
        ImpactCard(56, "O Brasil e o 7o maior produtor de petróleo do mundo. E você paga gasolina como se importasse cada gota.", "ANP, OPEP"),
        ImpactCard(57, "Em 2020, o preço do arroz DOBROU em meses. Enquanto isso, o Brasil exportou arroz recorde pra China. O mercado interno ficou desabastecido.", "CONAB, MDIC"),
        ImpactCard(58, "Cada deputado tem direito a R\$ 57 mil por mês em cotá parlamentar. Pode gastar com 'divulgacao' e ninguem sabe o que divulgou.", "Câmara dos Deputados"),
        ImpactCard(59, "O salário mínimo subiu 905% desde 2000. Parece muito? O gas subiu 539%. A gasolina 300%. O café 833%. O ovo 983%. Você compra MENOS com mais dinheiro.", "DIEESE, ANP"),
        ImpactCard(60, "Entra governo, sai governo. O preço so sobe. Os nomes mudam. A conta fica. E quem paga e sempre você.", "IBGE, DIEESE"),
        ImpactCard(61, "O IPVA arrecada R\$ 70 bilhões por ano. Olhe pro asfalto da sua rua. Olhe pro bueiro entupido. Pra onde foi esse dinheiro?", "Confaz, CNT"),
        ImpactCard(62, "O Brasil tem 70 mil radares de velocidade. Arrecadam R\$ 12 bilhões por ano em multas. E as mortes no transito continuam em 33 mil por ano.", "SENATRAN, CNT"),
        ImpactCard(63, "Um iPhone custa US\$ 999 nos EUA. No Brasil: R\$ 10.499. Imposto de importação mais ICMS mais PIS mais COFINS. Você paga 100% a mais por tecnologia.", "Receitá Federal"),
        ImpactCard(64, "O FGTS rendeu 3% ao ano em 2024. A inflação foi de 4,83%. Seu dinheiro preso no fundo perdeu valor enquanto ficava la. O governo usa ele pra financiar habitação.", "Caixa Econômica, IBGE"),
        ImpactCard(65, "A tabela do Imposto de Renda não e corrigida de verdade desde 2015. A inflação acumulada desde entao passou de 60%. Quem ganha R\$ 2.824 já paga IR. Em 2015, so pagava quem ganhava R\$ 3.500 em valores de hoje.", "Receitá Federal, SINDIFISCO"),
        ImpactCard(66, "O Judiciário brasileiro custa R\$ 103 bilhões por ano. E o mais caro do mundo em proporcao ao PIB. Um juiz federal ganha R\$ 35 mil, mais auxilios que dobram o valor.", "CNJ, FGV"),
        ImpactCard(67, "Pensões de militares custam R\$ 58 bilhões por ano. O Bolsa Familia inteiro custa R\$ 55 bilhões. Menos gente recebe mais dinheiro.", "Tesouro Nacional"),
        ImpactCard(68, "A passagem de onibus em São Paulo era R\$ 1 em 2000. Hoje: R\$ 5,50. O salário mínimo subiu 905%. A passagem subiu 450%. Mas a qualidade caiu.", "SPTrans, DIEESE"),
        ImpactCard(69, "37% da água tratada no Brasil se perde no caminho até a sua torneira. Vazamento, roubo e infraestrutura velha. Você paga pela água que nunca chegou.", "SNIS, 2024"),
        ImpactCard(70, "A Lei de Drogas de 2006 não define quantidade pra diferenciar usuario de traficante. O juiz decide. 30% dos presos brasileiros estao la por drogas. A maioria e pobre, negro e sem antecedentes.", "CNJ, DEPEN"),
        ImpactCard(71, "6 famílias controlam 70% da mídia brasileira. TV, radio, jornal. A mesma informação, filtrada pelos mesmos donos. A Constituição proíbe monopolio. Ninguém fiscaliza.", "Repórteres Sem Fronteiras, MOM"),
        ImpactCard(72, "O Fundão Eleitoral foi de R\$ 4,9 bilhões em 2024. Em 2018 era R\$ 1,7 bilhao. Cresce mais que seu salário, mais que o orçamento da saúde, mais que tudo.", "TSE, Congresso"),
        ImpactCard(73, "Na ultima legislatura, 30% dos deputados trocaram de partido. Você votou num partido. Ele foi pra outro. Levou o seu voto junto, sem te perguntar.", "TSE, Câmara dos Deputados"),
        ImpactCard(74, "O aluguel médio em São Paulo e R\$ 3.200. O salário mínimo e R\$ 1.518. Faz as contas. Sobra o que pra comer?", "FipeZap, DIEESE"),
        ImpactCard(75, "Um aluno de universidade publica custa R\$ 40 mil por ano ao governo. Na particular com ProUni: R\$ 15 mil. Mas 67% das vagas publicas vao pra quem estudou em escola particular.", "MEC, INEP"),
        ImpactCard(76, "Tempo médio de espera pra uma consultá com especialistá no SUS: 6 meses. Pra cirurgia: 2 anos e 4 meses. Pra exame de imagem: 3 meses. Tem gente que morre na fila.", "Ministerio da Saude, CNS"),
        ImpactCard(77, "O agronegócio recebe R\$ 400 bilhões em credito subsidiado por ano. Paga menos imposto que qualquer setor. E a comida no seu prato não para de subir.", "Plano Safra, MAPA"),
        ImpactCard(78, "A taxa media de juros no cartão de credito brasileiro e 445% ao ano. A maior do mundo. Na Europa a media e 18%. Você paga 25 vezes mais por crédito.", "BCB, ECB"),
        ImpactCard(79, "O Banco Central virou independente em 2021. O presidente do BC tem mandato fixo. Nem o presidente da República pode demitir. Quem decide a Selic não responde a ninguém eleito.", "Lei Complementar 179/2021"),
        ImpactCard(80, "O Brasil e o 84o em educação, 77o em saúde, 128o em segurança publica. Mas e a 9a maior economia do mundo. A riqueza existe. Não chega em você.", "PNUD, OMS, FBSP"),
        ImpactCard(81, "Absorventes tinham 27% de imposto até 2022. Arroz tem 0%. Cachaca tem 20%. Produto de higiene feminina era tratado como artigo de luxo.", "Receitá Federal"),
        ImpactCard(82, "Juiz federal tem 60 dias de férias por ano. Servidor público tem estabilidade vitalícia. Auxílio paletó, auxílio livro, auxílio saúde, auxílio alimentação. Tudo junto pode passar de R\$ 70 mil por mês.", "CNJ, Portal da Transparência"),
        ImpactCard(83, "As eleições de 2022 custaram R\$ 6,4 bilhões. R\$ 4,9 bi do Fundão mais R\$ 1,5 bi do fundo partidario. Dinheiro público pra campanha. Você financiou a eleição de quem talvez nem conheça.", "TSE"),
        ImpactCard(84, "A reforma da previdência de 2019 aumentou o tempo de contribuição. Quem ganha salário mínimo vai se aposentar com 65 anos e contribuir por 40. Quem e militar manteve regras especiais.", "EC 103/2019, DIEESE"),
        ImpactCard(85, "A Lei FELCA exige CPF pra criar conta em rede social. Sua critica ao governo agora tem nome, CPF e endereço. Na China tem sistema parecido.", "Lei 15.103/2025"),
        ImpactCard(86, "O desmatamento da Amazônia entre 2019 e 2022 destruiu area equivalente a Belgica. Prejuizo estimado em serviços ambientais: R\$ 100 bilhões. A multá aplicada pelo IBAMA foi de R\$ 4 bilhões. Cobraram 5%.", "INPE, IBAMA, IPAM"),
        ImpactCard(87, "O Brasil paga R\$ 800 bilhões por ano so em juros da divida publica. Mais que saúde, educação, segurança e assistencia social somados. O dinheiro vai pra quem tem titulo do Tesouro.", "Tesouro Nacional, BCB"),
        ImpactCard(88, "Um professor da rede publica ganha em media R\$ 4.400 por mês. Um deputado federal ganha R\$ 46 mil. O professor forma o deputado. O deputado decide o salário do professor.", "MEC, Câmara dos Deputados"),
        ImpactCard(89, "O déficit habitacional do Brasil e de 6,2 milhões de moradias. Ao mesmo tempo, existem 6,1 milhões de imóveis vazios. Tem casa sem gente e gente sem casa.", "Fundação João Pinheiro, IBGE"),
        ImpactCard(90, "O Brasil desperdiça 27 milhões de toneladas de comida por ano. Dava pra alimentar 35 milhões de pessoas. O mesmo numero que passa fome.", "Embrapa, FAO")
    )

    val voceSabia = listOf(
        DidYouKnow("Em 35 anos de Constituição, apenas 4 leis foram de iniciativa popular.", "DataSenado"),
        DidYouKnow("Existem 11.469 obras públicas paradas no Brasil. Metade de todas.", "TCU, 2025"),
        DidYouKnow("O Brasil e o 7o maior produtor de petróleo e você paga gasolina como se importasse.", "ANP"),
        DidYouKnow("70% dos presos brasileiros são negros. 30% nem foram condenados ainda.", "CNJ, DEPEN"),
        DidYouKnow("A taxa de resolução de homicídios e menor que 10%.", "FBSP"),
        DidYouKnow("6 famílias controlam a mídia brasileira.", "Reporters Sem Fronteiras"),
        DidYouKnow("O Brasil produz comida pra alimentar 1 bilhao de pessoas. 33 milhões de brasileiros passam fome.", "FAO, IBGE"),
        DidYouKnow("O custo médio de um senador e R\$ 5,2 milhões por ano. São 81 senadores.", "Senado Federal"),
        DidYouKnow("A bandeira tarifaria de energia pode adicionar R\$ 14,20 a cada 100kWh na sua conta.", "ANEEL"),
        DidYouKnow("O Brasil perdeu 100 mil produtores de leite entre 2015 e 2022.", "IBGE, Embrapa"),
        DidYouKnow("O etanol brasileiro e o mais barato de produzir do mundo. Mas o litro custa R\$ 4,50.", "UNICA"),
        DidYouKnow("Cerveja: 56% do preço e imposto. Você paga mais de imposto do que de cerveja.", "Receitá Federal"),
        DidYouKnow("O Brasil tem mais celulares do que habitantes. Mas 35 milhões não tem água tratada.", "SNIS, Anatel"),
        DidYouKnow("A CONAB zerou os estoques reguladores de arroz antes da pandemia. Preço dobrou.", "CONAB"),
        DidYouKnow("O último referendo no Brasil foi em 2005. Ha 21 anos ninguem te perguntou nada.", "TSE"),
        DidYouKnow("O BNDES emprestou R\$ 8 bilhões pra JBS entre 2007 e 2013. A familia Batistá foi presa por corrupção.", "BNDES, PF"),
        DidYouKnow("A Vale do Rio Doce foi privatizada por R\$ 3,3 bilhões. Hoje vale mais de R\$ 250 bilhões.", "BNDES"),
        DidYouKnow("O foro privilegiado protege mais de 55 mil autoridades no Brasil.", "STF, CNJ"),
        DidYouKnow("O Brasil gasta mais com juros da divida publica do que com saúde e educação somados.", "Tesouro Nacional"),
        DidYouKnow("Um preso no Brasil custa R\$ 3.500 por mês. Um aluno da rede publica custa R\$ 800.", "DEPEN, MEC"),
        DidYouKnow("O Brasil tem a maior frotá de helicópteros de SP do mundo. A maioria e privada.", "ANAC"),
        DidYouKnow("A greve dos caminhoneiros de 2018 aconteceu por causa do PPI que Pedro Parente criou.", "ANP"),
        DidYouKnow("O Brasil exportá 70% do café que produz. O brasileiro toma o que sobra.", "CONAB, ICO"),
        DidYouKnow("Em 2020, o auxílio emergencial custou R\$ 322 bilhões. O Orçamento Secreto custou R\$ 53 bilhões no mesmo período.", "Tesouro, TCU"),
        DidYouKnow("O IPVA arrecada R\$ 70 bilhões por ano. Olhe pro asfalto da sua rua.", "Confaz"),
        DidYouKnow("O Brasil tem 12% da água doce do mundo. E 35 milhões sem água tratada.", "ANA"),
        DidYouKnow("A carga tributaria brasileira e a maior da America Latina.", "OCDE"),
        DidYouKnow("O Bolsa Familia tirou 36 milhões da pobreza. A inflação de alimentos devolveu boa parte.", "Banco Mundial, DIEESE"),
        DidYouKnow("O presidencialismo de coalizao custa caro: o governo precisa comprar apoio no Congresso pra governar.", "Ciencia Politica, UERJ"),
        DidYouKnow("O Brasil e o país que mais matá ambientalistas no mundo.", "Global Witness"),
        DidYouKnow("O Fundão Eleitoral saiu de R\$ 1,7 bilhao em 2018 pra R\$ 4,9 bilhões em 2024. Crescimento de 188% em 6 anos.", "TSE"),
        DidYouKnow("Juizes federais têm 60 dias de férias por ano, auxílio moradia mesmo com imóvel próprio e penduricalhos que podem dobrar o salário.", "CNJ"),
        DidYouKnow("O FGTS rendeu menos que a inflação em 18 dos ultimos 25 anos. Seu dinheiro preso la perdeu poder de compra.", "Caixa Econômica, IBGE"),
        DidYouKnow("A tabela do IR não e corrigida pela inflação real desde 2015. Quem ganhava 2 salários mínimos não pagava IR. Hoje paga.", "SINDIFISCO, Receitá Federal"),
        DidYouKnow("Pensões militares custam R\$ 58 bilhões por ano. O programa Bolsa Familia inteiro custa R\$ 55 bilhões.", "Tesouro Nacional"),
        DidYouKnow("O desmatamento da Amazônia entre 2019 e 2022 causou prejuízo estimado de R\$ 100 bilhões em serviços ambientais. A multá cobrada foi 5% disso.", "INPE, IBAMA, IPAM"),
        DidYouKnow("O Brasil paga R\$ 800 bilhões por ano so de juros da divida publica. Mais do que gasta com saúde e educação juntos.", "Tesouro Nacional"),
        DidYouKnow("As eleições de 2022 custaram R\$ 6,4 bilhões em dinheiro público. Cada voto custou R\$ 43 ao contribuinte.", "TSE"),
        DidYouKnow("Das 11.469 obras publicas paradas, 40% foram abandonadas por desvio de recursos ou ma gestão. O TCU estima R\$ 15,9 bilhões perdidos.", "TCU, 2025"),
        DidYouKnow("Um professor da rede publica ganha em media R\$ 4.400 por mês. Um deputado federal ganha R\$ 46 mil, mais auxílios.", "MEC, Câmara dos Deputados"),
        DidYouKnow("O déficit habitacional brasileiro e de 6,2 milhões de moradias. Existem 6,1 milhões de imóveis vazios nopaís.", "Fundação João Pinheiro, IBGE"),
        DidYouKnow("O Brasil desperdiça 27 milhões de toneladas de comida por ano. Suficiente pra alimentar os 35 milhões que passam fome.", "Embrapa, FAO"),
        DidYouKnow("A taxa media de juros do cartão de crédito no Brasil e 445% ao ano. A maior do planeta. Na Europa a media e 18%.", "BCB"),
        DidYouKnow("37% da água tratada no Brasil se perde antes de chegar na torneira. Vazamentos, furtos e tubulações velhas.", "SNIS, 2024"),
        DidYouKnow("Um preso custa R\$ 3.500 por mês ao estado. Um aluno da rede publica custa R\$ 800. O Brasil tem a 3a maior população carceraria do mundo.", "DEPEN, MEC, ICPR"),
        DidYouKnow("Nas eleições de 2022, 207 deputados eleitos respondiam a processos criminais. Mais de 40% da Câmara.", "TSE, Transparencia Brasil"),
        DidYouKnow("Igrejas e templos são isentos de todos os impostos no Brasil. A renúncia fiscal estimada e de R\$ 25 bilhões por ano.", "Receitá Federal, IPEA"),
        DidYouKnow("O Brasil não tem lei que regulamente o lobby. Empresas financiam campanhas e depois recebem contratos. Tudo legal.", "Transparencia Internacional"),
        DidYouKnow("A Lei FELCA de 2025 exige CPF pra criar conta em rede social. A verificação em massa de identidade digital coloca o Brasil entre os paises com maior controle da internet.", "Lei 15.103/2025"),
        DidYouKnow("O transporte público brasileiro recebe subsidio de R\$ 8 bilhões por ano e a passagem continua subindo acima da inflação.", "NTU, DIEESE"),
        DidYouKnow("Os 5% mais ricos do Brasil concentram 30% de toda a renda dopaís. Os 50% mais pobres ficam com 10%.", "IBGE, Oxfam"),
        DidYouKnow("Apenas 4% dos filhos de país sem ensino superior conseguem diploma universitario no Brasil. Na OCDE a media e 22%.", "IBGE, OCDE")
    )

    val gastosPublicos = listOf(
        GastoPublico(
            "O cartão corporativo da Presidencia gastou R\$ 55 milhões em 2025.",
            "Quanto gastaram no seu bairro?",
            "Portal da Transparência"
        ),
        GastoPublico(
            "Congresso: R\$ 14,9 bilhões por ano. São 594 parlamentares.",
            "Cada um custa R\$ 3,5 milhões por ano. Você ganha R\$ 18 mil por ano.",
            "Câmara dos Deputados, Senado"
        ),
        GastoPublico(
            "R\$ 15,9 bilhões investidos em obras que pararam. Metade de saúde e educação.",
            "Esse dinheiro podia ter construido 1.590 escolas.",
            "TCU, 2025"
        )
    )

    val leisResumo = listOf(
        LeiResumo(
            tag = "APROVADA",
            tituloPopular = "CPF obrigatório pra rede social",
            resumo = "Se você postar algo, o governo sabe que foi você. Críticas ficam rastreáveis.",
            autor = "Sen. Alessandro Vieira (MDB-SE)",
            detalhes = "Lei FELCA exige CPF pra criar conta em redes sociais. Na teoria protege crianças. Na prática: vigilância estatal."
        ),
        LeiResumo(
            tag = "EM PAUTA",
            tituloPopular = "Reforma Tributária em regulamentação",
            resumo = "Simplifica impostos mas pode encarecer serviços. IBS e CBS substituem 5 impostos.",
            autor = "Min. Fernando Haddad (PT)",
            detalhes = "Na teoria é melhor. Na prática, depende da regulamentação que está sendo discutida agora."
        ),
        LeiResumo(
            tag = "EM VIGOR",
            tituloPopular = "Taxação de compras internacionais até US\$ 50",
            resumo = "Compras na Shein e Shopee ficaram 20% mais caras.",
            autor = "Governo Federal",
            detalhes = "Desde 2024, compras internacionais abaixo de US\$ 50 pagam 20% de imposto de importação. Antes eram isentas."
        )
    )

    // Notification phrases by time slot (30+ each)
    val notifCafe = listOf(
        "Bom dia. O café que você tá tomando custava R\$ 3. Hoje custa R\$ 28 o pacote.",
        "Bom dia. Seu deputado ganha R\$ 46 mil/mes. Você ganha R\$ 1.518. Bom trabalho hoje.",
        "O Congresso já gastou R\$ 40,8 milhões desde que você acordou. Incluindo o domingo.",
        "Bom dia. O arroz do seu café da manha subiu 540% desde 2000.",
        "O leite era R\$ 0,95 em 2000. Hoje custa R\$ 6,50. Bom café da manha.",
        "Bom dia. De cada R\$ 3 que o país produz, R\$ 1 vai pro governo. E o cafe?",
        "O pao frances subiu mais que seu salário nos ultimos 6 anos. Bom dia.",
        "Bom dia. O gas pra fazer seu café custava R\$ 18. Hoje: R\$ 115.",
        "Seuavô tomava café sem se preocupar com o preço. Você não pode mais.",
        "Bom dia. 35 milhões sem água tratada. 100 milhões sem esgoto. Seu IPTU tá pagando o que?",
        "O ovo da sua manha era R\$ 1,20 a dúzia em 2000. Hoje: R\$ 13. Aumento de 983%.",
        "Bom dia. O Brasil produz café pro mundo. Você paga caro pelo que sobra.",
        "Bom dia. A bandeira tarifaria da sua conta de luz pode custar R\$ 14,20 extras por 100kWh.",
        "Enquanto você dormia, o Congresso custou R\$ 40,8 milhões. Mesmo sendo feriado.",
        "Bom dia. A Petrobras lucrou R\$ 190 bilhões em 2022. Você pagou R\$ 7,30 na gasolina.",
        "Seu café da manha inteiro custa mais que o dobro de 6 anos atras. Bom dia.",
        "Bom dia. 11.469 obras paradas. 70% de saúde e educação.",
        "O acucar do seu café subiu, o leite subiu, o pao subiu. Seu salario? Nem tanto.",
        "Bom dia. O salário mínimo deveria ser R\$ 7.500. Você recebe R\$ 1.518.",
        "Bom dia. A taxa de resolução de homicídios e menor que 10%. Sua segurança custa caro.",
        "O gas pra esquentar sua água custava R\$ 18. Hoje custa R\$ 115. O Brasil produz gas.",
        "Bom dia. Em 35 anos, so 4 leis foram de iniciativa popular. Ninguém te ouve.",
        "Bom dia. 33% do que você vai ganhar hoje vai pro governo. Olhe o SUS da sua cidade.",
        "O mercado que sua mae ia comprar tranquila? Esse tempo acabou. Bom dia.",
        "Bom dia. Você gasta 57% do salário mínimo so pra comer. O resto e pro resto do mês.",
        "O café subiu 833%. O Brasil e o maior produtor do mundo. Faz sentido?",
        "Bom dia. Seu deputado tem auxílio moradia de R\$ 4.253. E o seu aluguel?",
        "A energia que você usou pra tomar banho subiu 428% desde 2000. Bom dia.",
        "Bom dia. Pedro Parente atrelou seu combustível ao dolar em 2016. Ninguém desfez.",
        "Você vai trabalhar hoje. 5h30 do seu dia são so pra pagar a cestá básica."
    )

    val notifAlmoco = listOf(
        "Hora do almoco. A carne subiu 700% desde 2000. Se e que tem carne no prato.",
        "Seu deputado gastou R\$ 57 mil em 'alimentacao e divulgacao' esse mês. E o seu almoco?",
        "Esse prato custava menos em 2020. O arroz subiu, o feijao subiu, a carne subiu.",
        "Almoco. O patinho era R\$ 6,50 em 2000. Hoje: R\$ 48. O boi vai pra exportacao.",
        "O feijao do seu prato subiu 533% desde 2000. Arroz e feijao tá virando luxo.",
        "Hora do almoco. O oleo de soja que fritou sua comida subiu junto com o dolar.",
        "Seu almoco hoje custou mais que o almoco inteiro do seuavô em 2000.",
        "A cestá básica de SP e R\$ 900. So pra comer o basico do basico.",
        "Hora de comer. O Brasil alimentá 1 bilhao de pessoas. 33 milhões de brasileiros passam fome.",
        "Esse arroz do prato? Era R\$ 5 o pacote. Hoje: R\$ 32. Seu salário não acompanhou.",
        "O almoco do deputado sai da cotá parlamentar. O seu sai do seu bolso. Bom apetite.",
        "A carne que você não compra mais vai pra China. O maior rebanho do mundo e nosso.",
        "Hora do almoco. 57% do salário mínimo so pra cestá básica. Sobra pouco pro resto.",
        "O gas pra cozinhar seu almoco custava R\$ 18. Hoje: R\$ 115. Bom apetite.",
        "Esse feijao do prato? Nenhum governo criou estoque estrategico pra ele. Pro arroz sim.",
        "Hora do almoco. O Brasil exportá comida pro mundo e o brasileiro come cada vez menos carne.",
        "Seu almoco custou mais imposto do que você imagina. Até no sal tem imposto.",
        "A carne que sobra da exportacao e a que você consegue comprar. Se consegue.",
        "Hora do almoco. Em 2019 a China comprou toda a carne. O governo deixou.",
        "O ovo que tá no prato era o basico do basico. Subiu 983%. Virou luxo.",
        "Você tá comendo arroz e feijao porque a carne tá cara. Isso não era assim.",
        "Hora do almoco. O etanol pro carro que te trouxe: R\$ 4,50. Somos o maior produtor.",
        "O Brasil e o país do boi. Onde o brasileiro come ovo. Bom almoco.",
        "Seu almoco de hoje custaria R\$ 3 em 2000. Ajustado pela inflação real, não a oficial.",
        "O DIEESE diz que você deveria ganhar R\$ 7.500 pra comer com dignidade. Bom apetite.",
        "Hora do almoco. De cada R\$ 100 que você gasta em comida, R\$ 20 e imposto.",
        "Tereza Cristina zerou os estoques de arroz antes da pandemia. Preço dobrou. Bom almoco.",
        "O salário mínimo compra 1,69 cestas básicas. Em 2010 comprava 2,12. Você come menos.",
        "Hora do almoco. O tomate, a cebola, o alho. Tudo subiu mais que seu salário.",
        "A comida do seu prato viajou o Brasil inteiro. O frete subiu porque a gasolina subiu. Tudo conectado."
    )

    val notifTrabalho = listOf(
        "Saiu do trabalho. Das 8 horas que você trabalhou, 5h30 foram so pra pagar a cestá básica.",
        "Você trabalhou o dia inteiro. 33% do seu salário foi pra imposto. Veja pra onde foi.",
        "O gas era R\$ 18 em 2000. Hoje R\$ 115. O Brasil produz gas. E você paga caro.",
        "Saiu do trabalho. Seu deputado trabalhou? Confere a presenca dele.",
        "Você trabalhou 8 horas. Seu deputado tem cotá de R\$ 57 mil/mes. Você tem seu salário.",
        "Fim do expediente. A gasolina pra voltar pra casa subiu 300% desde 2000.",
        "Você produziu valor hoje. 33% já foi pro governo. O que você recebeu de volta?",
        "Saiu do trabalho. O transporte público subiu. A gasolina subiu. O estacionamento subiu.",
        "Fim do dia. A reforma trabalhistá flexibilizou seus direitos em 2017. Você sentiu?",
        "Você trabalhou. O deputado tambem? Em media, 40% faltam nas votacoes.",
        "Saiu do trabalho. O DIEESE diz que você deveria ganhar R\$ 7.500. Você ganha quanto?",
        "Você trabalhou o dia inteiro pra ganhar em real. Mas a gasolina e cotada em dolar.",
        "Fim do expediente. A Selic tá acima de 13%. Seu cartão de credito tá em quanto?",
        "Saiu do trabalho. 5 meses do seu salário vao so pra pagar imposto por ano.",
        "Você trabalhou 8 horas. Seu deputado tem 25 assessores. Você tem você mesmo.",
        "Fim do dia. A passagem do onibus subiu. O combustível subiu. Chegar em casa custa mais.",
        "Saiu do trabalho. Quanto do seu salário sobra depois de comida, aluguel, transporte e luz?",
        "Você trabalhou. O Congresso custou R\$ 40,8 milhões so hoje. Incluindo seu imposto.",
        "Fim do expediente. Em 2000, você comprava 8 botijoes de gas com o salário mínimo. Hoje: 13. Mas tudo mais subiu junto.",
        "Saiu do trabalho. A inflação oficial diz 5%. No mercado você sente 30%.",
        "Você trabalhou pra pagar imposto. O imposto pagou obra parada. 11.469 delas.",
        "Fim do dia. A energia da sua casa subiu 428%. O chuveiro eletrico tá mais caro.",
        "Saiu do trabalho. O cartão corporativo do presidente gastou R\$ 55 milhões. E o seu limite?",
        "Você trabalhou 8 horas. Paulo Guedes tinha offshore de US\$ 9,5 milhões. E você?",
        "Fim do expediente. O salário mínimo subiu 905%. Parece muito? O ovo subiu 983%.",
        "Saiu do trabalho. O país que mais produz petróleo, cafe, carne, gas. E tudo caro.",
        "Você trabalhou hoje. Um preso custa R\$ 3.500/mes pro estado. Um aluno: R\$ 800.",
        "Fim do dia. O imposto sobre a gasolina que você usou: 44%. Imposto sobre imposto.",
        "Saiu do trabalho. 90% dos homicídios sem solução. Sua segurança e uma ilusao cara.",
        "Você voltou pra casa. O aluguel subiu, a conta de luz subiu, o mercado subiu. Seu salario?"
    )

    val notifNoite = listOf(
        "O jornal vai dizer que a inflação foi de 0,5%. O café subiu 833% em 26 anos. Quem tá mentindo?",
        "Resumo: o Congresso custa R\$ 40,8 mi/dia. Incluindo hoje. Incluindo feriado.",
        "11.469 obras paradas. 70% de saúde e educação. R\$ 15,9 bi jogados fora.",
        "Antes de dormir: você sabe o nome do seu deputado? Sabe quanto ele gastou esse mes?",
        "Boa noite. Amanha você acorda e trabalha de novo. 33% vai pro imposto. De novo.",
        "O jornal vai falar do PIB. Você quer saber do preço do arroz. Ninguém mostra os dois juntos.",
        "Boa noite. Enquanto você dorme, o Congresso custa R\$ 1,7 milhao por hora.",
        "Antes de dormir: 35 milhões sem agua. 100 milhões sem esgoto. Em 2026.",
        "O noticiario mostra o presidente viajando. Você pagou a passagem. Boa noite.",
        "Boa noite. O IPCA diz que a inflação e 5%. O DIEESE diz que você deveria ganhar R\$ 7.500.",
        "Antes de dormir: o Brasil e o país do boi, do cafe, do petróleo, do gas. E tudo caro pra você.",
        "Boa noite. Amanha você acorda e a gasolina continua em dolar. Seu salário em real.",
        "O jornal vai dizer 'economia cresceu'. Você vai sentir o preço do ovo. Boa noite.",
        "Antes de dormir: a conta de luz desse mês foi mais cara. A bandeira tarifaria tá vermelha.",
        "Boa noite. 4 leis de iniciativa popular em 35 anos. Ninguém te ouviu hoje.",
        "O dado tá aqui. O caminho tá aqui. E você?",
        "Boa noite. Você sabe mais sobre o país do que a maioria. Isso incomoda alguem.",
        "Antes de dormir: entra governo, sai governo. O preço so sobe.",
        "Boa noite. Amanha tem mais numeros pra ver. O sistema não quer que você saiba.",
        "Cada dia que passa, seu dinheiro vale menos. Os dados provam.",
        "Nao e opiniao. São dados do IBGE, DIEESE, ANP e Banco Central.",
        "Boa noite. O Orçamento Secreto era de R\$ 53 bilhões. Você soube pra onde foi?",
        "Antes de dormir: a Petrobras lucrou R\$ 190 bilhões. Você pagou R\$ 7,30 na gasolina.",
        "Boa noite. O foro privilegiado protege mais de 55 mil autoridades. E você?",
        "Amanha você acorda e a realidade continua a mesma. Mas agora você sabe.",
        "Boa noite. 90% dos assassinatos sem solução. Sua segurança e uma estatistica.",
        "O jornal vai mostrar o politico sorrindo. Os numeros não sorriem. Boa noite.",
        "Antes de dormir: o Brasil gasta mais com juros da divida do que com saúde e educação.",
        "Boa noite. Um homem informado vira ameaça pro estado. Dorme bem.",
        "Amanha tem mais. Você já sabe mais que 90% dos brasileiros. Isso e perigoso. Pra eles."
    )

    fun getImpactCardForDay(dayOfYear: Int): ImpactCard {
        val index = (dayOfYear - 1) % impactCards.size
        return impactCards[index]
    }

    fun getVoceSabiaForWeek(dayOfYear: Int): DidYouKnow {
        val weekIndex = ((dayOfYear - 1) / 7) % voceSabia.size
        return voceSabia[weekIndex]
    }

    fun getGastoPublicoRotating(dayOfYear: Int): GastoPublico {
        val index = (dayOfYear / 3) % gastosPublicos.size
        return gastosPublicos[index]
    }

    fun getNotifByHourV2(hour: Int, dayOfYear: Int): String {
        val list = when {
            hour in 6..9 -> notifCafe
            hour in 11..13 -> notifAlmoco
            hour in 17..19 -> notifTrabalho
            hour in 20..23 -> notifNoite
            else -> notifNoite
        }
        return list[dayOfYear % list.size]
    }
}
