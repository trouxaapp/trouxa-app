package com.trouxaapp.app.data.local

data class IdeiaCidadao(
    val id: String,
    val titulo: String,
    val resumo: String,
    val explicacao: String,
    val comoTeAfeta: String,
    val prosArgs: List<String>,
    val contrasArgs: List<String>,
    val comparacaoInternacional: String,
    val apoiosECidadania: Int,
    val statusOriginal: String,
    val categoria: String,
    val linkECidadania: String
)

object LeisDoPovoProvider {

    val ideias: List<IdeiaCidadao> = listOf(

        // =====================================================================
        // SEGURANCA
        // =====================================================================

        IdeiaCidadao(
            id = "seg_001",
            titulo = "Acabar com saidinha de presos em feriados",
            resumo = "Hoje presos do regime semiaberto podem sair temporariamente em feriados como Natal, Dia das M\u00e3es e P\u00e1scoa. A proposta quer acabar com essas sa\u00eddas autom\u00e1ticas e s\u00f3 liberar com avalia\u00e7\u00e3o individual de cada caso.",
            explicacao = "A chamada \"saidinha\" \u00e9 um benef\u00edcio previsto na Lei de Execu\u00e7\u00e3o Penal que permite que presos do regime semiaberto saiam temporariamente da cadeia em at\u00e9 5 datas por ano. Na teoria, o objetivo \u00e9 manter o v\u00ednculo familiar e ajudar na ressocializa\u00e7\u00e3o.\n\nO problema \u00e9 que muitos presos n\u00e3o voltam. Em S\u00e3o Paulo, por exemplo, cerca de 20% dos presos que saem em feriados simplesmente n\u00e3o retornam. Al\u00e9m disso, h\u00e1 registros de crimes cometidos durante essas sa\u00eddas, o que gera revolta na popula\u00e7\u00e3o.\n\nA proposta n\u00e3o acaba com a progress\u00e3o de regime nem com a ressocializa\u00e7\u00e3o. Ela s\u00f3 quer que cada sa\u00edda seja avaliada individualmente pelo juiz, levando em conta o comportamento do preso, a gravidade do crime e o risco de fuga.\n\nEm 2024, o Congresso chegou a aprovar mudan\u00e7as nesse sentido, mas com vetos presidenciais que enfraqueceram a lei. O debate continua porque muita gente acha que a mudan\u00e7a n\u00e3o foi longe o suficiente.",
            comoTeAfeta = "Se voc\u00ea mora em regi\u00e3o com pres\u00eddio perto, sabe que feriado \u00e9 sin\u00f4nimo de medo. Fim da saidinha autom\u00e1tica significa menos chance de encontrar na rua algu\u00e9m que deveria estar preso. Tamb\u00e9m significa que o sistema vai ter que avaliar caso a caso, o que pode sobrecarregar o Judici\u00e1rio.",
            prosArgs = listOf(
                "Dados mostram que uma parcela significativa dos presos n\u00e3o volta da saidinha, o que prova que o sistema atual falha",
                "Crimes cometidos durante sa\u00eddas tempor\u00e1rias geram inseguran\u00e7a real na popula\u00e7\u00e3o, especialmente em cidades com pres\u00eddios",
                "Avalia\u00e7\u00e3o individual \u00e9 mais justa do que liberar todo mundo automaticamente, sem olhar o hist\u00f3rico de cada um",
                "Pa\u00edses que restringiram sa\u00eddas autom\u00e1ticas viram queda nos \u00edndices de fuga e reincid\u00eancia durante feriados"
            ),
            contrasArgs = listOf(
                "A saidinha \u00e9 uma ferramenta de ressocializa\u00e7\u00e3o. Manter o v\u00ednculo familiar reduz a chance de reincid\u00eancia no longo prazo",
                "O Judici\u00e1rio j\u00e1 est\u00e1 sobrecarregado. Avaliar individualmente cada pedido pode travar o sistema e criar mais injusti\u00e7as",
                "Sem sa\u00eddas, a tens\u00e3o dentro dos pres\u00eddios aumenta, o que pode gerar rebeli\u00f5es e motins",
                "O problema real \u00e9 a falta de monitoramento eletr\u00f4nico, n\u00e3o a sa\u00edda em si. Com tornozeleira funcionando, o risco cai muito"
            ),
            comparacaoInternacional = "Na Alemanha, presos em regime aberto t\u00eam sa\u00eddas regulares, mas com monitoramento rigoroso e avalia\u00e7\u00e3o psicol\u00f3gica obrigat\u00f3ria. A taxa de n\u00e3o retorno \u00e9 de menos de 2%. No Jap\u00e3o, sa\u00eddas tempor\u00e1rias praticamente n\u00e3o existem, mas o sistema prisional investe pesado em ressocializa\u00e7\u00e3o interna. Nos EUA varia por estado, mas a maioria exige avalia\u00e7\u00e3o individual.",
            apoiosECidadania = 87432,
            statusOriginal = "em vota\u00e7\u00e3o",
            categoria = "seguran\u00e7a",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=156021"
        ),

        IdeiaCidadao(
            id = "seg_002",
            titulo = "Fim do foro privilegiado",
            resumo = "Acabar com a regra que permite que pol\u00edticos e autoridades sejam julgados apenas pelo STF ou tribunais superiores, em vez de ju\u00edzes comuns. Isso faz com que processos demorem d\u00e9cadas e quase ningu\u00e9m seja condenado.",
            explicacao = "O foro privilegiado (ou foro por prerrogativa de fun\u00e7\u00e3o) \u00e9 uma regra que diz que certas autoridades s\u00f3 podem ser julgadas por tribunais superiores. Deputados e senadores, por exemplo, s\u00f3 podem ser processados pelo STF. Governadores pelo STJ. E assim por diante.\n\nO problema \u00e9 que o STF j\u00e1 tem uma carga enorme de trabalho com quest\u00f5es constitucionais. Quando precisa julgar crimes comuns de pol\u00edticos, os processos ficam parados anos. E quando o pol\u00edtico sai do cargo, o processo volta pra primeira inst\u00e2ncia e recome\u00e7a do zero. \u00c9 a famosa \"pizza\".\n\nDados do STF mostram que entre 2002 e 2016, o tribunal recebeu 536 inqu\u00e9ritos e a\u00e7\u00f5es penais contra parlamentares. Desses, apenas 7 resultaram em condena\u00e7\u00e3o. Ou seja: 1,3% de condena\u00e7\u00e3o. Na justi\u00e7a comum, a taxa de condena\u00e7\u00e3o passa de 60%.\n\nA proposta \u00e9 simples: pol\u00edtico comete crime, \u00e9 julgado pelo juiz da vara comum como qualquer cidad\u00e3o. O foro s\u00f3 se manteria para crimes diretamente ligados ao exerc\u00edcio do cargo (como corrup\u00e7\u00e3o no mandato), e olhe l\u00e1.\n\nO pr\u00f3prio STF, em 2018, decidiu limitar o foro para crimes cometidos durante o mandato e em fun\u00e7\u00e3o do cargo. Mas na pr\u00e1tica, a interpreta\u00e7\u00e3o ainda \u00e9 muito ampla e os processos continuam travando.",
            comoTeAfeta = "Enquanto o foro existir, pol\u00edtico investigado por corrup\u00e7\u00e3o vai continuar sendo julgado por um tribunal que n\u00e3o tem tempo pra isso. Resultado: impunidade. Voc\u00ea paga imposto, pol\u00edtico rouba, e ningu\u00e9m vai preso. Sem foro, o julgamento seria mais r\u00e1pido e com mais chance de condena\u00e7\u00e3o real.",
            prosArgs = listOf(
                "A taxa de condena\u00e7\u00e3o no STF \u00e9 ridiculamente baixa (1,3%) comparada com a justi\u00e7a comum (mais de 60%)",
                "Processos prescrevem porque ficam parados no STF, gerando impunidade institucionalizada",
                "O pr\u00f3prio STF j\u00e1 reconheceu que o foro atrapalha. Ministros como Barroso pediram publicamente o fim",
                "Outros pa\u00edses democr\u00e1ticos n\u00e3o t\u00eam esse tipo de prote\u00e7\u00e3o. \u00c9 uma jabuticaba brasileira"
            ),
            contrasArgs = listOf(
                "O foro existe para proteger autoridades de persegui\u00e7\u00e3o judicial local. Um juiz de cidade pequena poderia ser pressionado a condenar um governador por motiva\u00e7\u00e3o pol\u00edtica",
                "Sem o foro, ju\u00edzes de primeira inst\u00e2ncia poderiam usar processos contra pol\u00edticos para ganhar fama e promo\u00e7\u00e3o, como aconteceu em casos pol\u00eamicos recentes",
                "A Constitui\u00e7\u00e3o criou o foro por uma raz\u00e3o: separar poderes. Acabar com ele pode fragilizar a independ\u00eancia entre Judici\u00e1rio, Legislativo e Executivo",
                "O problema n\u00e3o \u00e9 o foro em si, mas a lentid\u00e3o do Judici\u00e1rio. Resolver isso seria mais eficaz do que mudar quem julga"
            ),
            comparacaoInternacional = "Na maioria dos pa\u00edses europeus, o foro privilegiado ou n\u00e3o existe ou \u00e9 extremamente limitado. Na Fran\u00e7a, existe um tribunal especial para julgar o presidente, mas parlamentares s\u00e3o julgados pela justi\u00e7a comum. Na It\u00e1lia, parlamentares tinham imunidade ampla, mas ela foi reduzida drasticamente ap\u00f3s esc\u00e2ndalos de corrup\u00e7\u00e3o nos anos 90. Nos EUA, n\u00e3o existe foro: senadores e congressistas s\u00e3o julgados por tribunais comuns.",
            apoiosECidadania = 142987,
            statusOriginal = "morreu",
            categoria = "seguran\u00e7a",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=93177"
        ),

        IdeiaCidadao(
            id = "seg_003",
            titulo = "Proibir cargo p\u00fablico pra condenado em segunda inst\u00e2ncia",
            resumo = "Se algu\u00e9m foi condenado em segunda inst\u00e2ncia (ou seja, dois tribunais diferentes concordaram que a pessoa \u00e9 culpada), essa pessoa n\u00e3o poderia mais ocupar cargo p\u00fablico nenhum. Nem nomeado, nem concursado, nem eleito.",
            explicacao = "Hoje no Brasil, uma pessoa pode ser condenada em segunda inst\u00e2ncia e continuar exercendo cargo p\u00fablico, inclusive cargo eletivo. Isso acontece porque o STF decidiu em 2019 que a pris\u00e3o s\u00f3 pode acontecer ap\u00f3s o tr\u00e2nsito em julgado, ou seja, quando todos os recursos se esgotam. E no Brasil, recursos podem durar d\u00e9cadas.\n\nA Lei da Ficha Limpa j\u00e1 impede que condenados em segunda inst\u00e2ncia se candidatem, mas ela tem brechas. E para cargos n\u00e3o eletivos (como cargos de confian\u00e7a, nomea\u00e7\u00f5es pol\u00edticas e at\u00e9 concursos), a restri\u00e7\u00e3o \u00e9 praticamente inexistente.\n\nA proposta quer fechar essas brechas: condenou em segunda inst\u00e2ncia, perdeu o direito de ocupar qualquer cargo p\u00fablico. Ponto final. A l\u00f3gica \u00e9 que se dois tribunais independentes concordam que voc\u00ea cometeu um crime, n\u00e3o faz sentido voc\u00ea continuar decidindo a vida dos outros.\n\nIsso afetaria desde o pol\u00edtico nomeado para secretaria at\u00e9 o servidor concursado que cometeu crime grave. A ideia \u00e9 criar uma barreira real entre pessoas condenadas e o dinheiro p\u00fablico.",
            comoTeAfeta = "Quantas vezes voc\u00ea j\u00e1 viu um pol\u00edtico condenado por corrup\u00e7\u00e3o continuar no cargo porque ainda tem recurso pendente? Essa proposta acabaria com isso. Se dois tribunais dizem que a pessoa \u00e9 culpada, ela sai. Simples. Isso significa mais gente honesta em cargo p\u00fablico e menos bandido administrando seu dinheiro.",
            prosArgs = listOf(
                "Se dois tribunais independentes concordam que algu\u00e9m \u00e9 culpado, a chance de erro judicial \u00e9 muito pequena",
                "Fecha brechas da Ficha Limpa que hoje permitem condenados em cargos de confian\u00e7a e nomea\u00e7\u00f5es",
                "Manda uma mensagem clara: quem comete crime n\u00e3o pode gerir coisa p\u00fablica",
                "Protege o cidad\u00e3o de ter seu imposto administrado por algu\u00e9m condenado por desvio de verba"
            ),
            contrasArgs = listOf(
                "A Constitui\u00e7\u00e3o garante presun\u00e7\u00e3o de inoc\u00eancia at\u00e9 o tr\u00e2nsito em julgado. Punir antes disso viola um direito fundamental",
                "O Judici\u00e1rio brasileiro n\u00e3o \u00e9 perfeito. Condena\u00e7\u00f5es em segunda inst\u00e2ncia s\u00e3o revertidas em inst\u00e2ncias superiores com certa frequ\u00eancia",
                "Pode ser usada como arma pol\u00edtica: basta processar algu\u00e9m e conseguir duas condena\u00e7\u00f5es para tir\u00e1-lo do jogo",
                "Servidores concursados que cometeram crimes menores (como briga de tr\u00e2nsito) perderiam o cargo, o que parece desproporcional"
            ),
            comparacaoInternacional = "Nos EUA, condena\u00e7\u00e3o criminal geralmente impede contrata\u00e7\u00e3o federal, mas varia por estado e gravidade do crime. Na Alemanha, condenados por crimes graves perdem automaticamente o direito a cargos p\u00fablicos por 5 anos. Em Portugal, a condena\u00e7\u00e3o definitiva por certos crimes acarreta perda de cargo p\u00fablico. A maioria dos pa\u00edses europeus tem alguma restri\u00e7\u00e3o, mas geralmente atrelada a condena\u00e7\u00e3o definitiva, n\u00e3o em segunda inst\u00e2ncia.",
            apoiosECidadania = 65210,
            statusOriginal = "morreu",
            categoria = "seguran\u00e7a",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=124588"
        ),

        // =====================================================================
        // ECONOMIA
        // =====================================================================

        IdeiaCidadao(
            id = "eco_001",
            titulo = "Taxa\u00e7\u00e3o de grandes fortunas",
            resumo = "O Artigo 153 da Constitui\u00e7\u00e3o de 1988 j\u00e1 prev\u00ea o imposto sobre grandes fortunas, mas em 38 anos ele nunca foi regulamentado. A proposta \u00e9 finalmente colocar isso pra funcionar, cobrando imposto de quem tem patrim\u00f4nio acima de R$ 10 milh\u00f5es.",
            explicacao = "Essa \u00e9 talvez a proposta mais antiga e mais frustrada da Constitui\u00e7\u00e3o brasileira. Em 1988, os constituintes escreveram no Artigo 153, inciso VII, que a Uni\u00e3o pode cobrar imposto sobre grandes fortunas. S\u00f3 que para isso funcionar, precisa de uma lei complementar. E essa lei nunca foi votada. S\u00e3o 38 anos de espera.\n\nNesse per\u00edodo, mais de 30 projetos de lei foram apresentados no Congresso para regulamentar o imposto. Nenhum foi aprovado. A bancada do Congresso, que em grande parte \u00e9 composta por milion\u00e1rios, sempre encontra um jeito de engavetar a discuss\u00e3o.\n\nA proposta mais recente sugere cobrar entre 0,5% e 1,5% ao ano sobre patrim\u00f4nios acima de R$ 10 milh\u00f5es. Isso atingiria menos de 0,1% da popula\u00e7\u00e3o brasileira, mas geraria uma arrecada\u00e7\u00e3o estimada entre R$ 40 e R$ 80 bilh\u00f5es por ano.\n\nPra ter uma ideia: os 1% mais ricos do Brasil det\u00eam quase 50% de toda a riqueza do pa\u00eds. \u00c9 uma das concentra\u00e7\u00f5es de renda mais altas do mundo. E esses mesmos 1% pagam proporcionalmente menos imposto do que a classe m\u00e9dia, porque a maior parte da renda deles vem de dividendos, que s\u00e3o isentos.\n\nO debate \u00e9 leg\u00edtimo dos dois lados. Mas o fato de existir na Constitui\u00e7\u00e3o e nunca ter sido votado diz muito sobre quem manda nesse pa\u00eds.",
            comoTeAfeta = "Se voc\u00ea n\u00e3o tem R$ 10 milh\u00f5es de patrim\u00f4nio, n\u00e3o paga nada. Zero. Mas a arrecada\u00e7\u00e3o poderia ser usada pra reduzir a carga tribut\u00e1ria sobre consumo, que \u00e9 o que mais pesa no bolso de quem ganha pouco. Hoje, quem ganha um sal\u00e1rio m\u00ednimo paga proporcionalmente mais imposto do que um bilion\u00e1rio, porque imposto sobre comida, roupa e transporte pesa mais no or\u00e7amento de quem ganha menos.",
            prosArgs = listOf(
                "Est\u00e1 na Constitui\u00e7\u00e3o desde 1988. N\u00e3o \u00e9 inven\u00e7\u00e3o nova, \u00e9 cumprir o que j\u00e1 foi decidido pelo povo",
                "Atingiria menos de 0,1% da popula\u00e7\u00e3o, mas geraria dezenas de bilh\u00f5es que poderiam reduzir impostos sobre consumo",
                "O Brasil \u00e9 um dos pa\u00edses mais desiguais do mundo. Tributar fortunas \u00e9 uma forma de reduzir essa dist\u00e2ncia",
                "A maior parte dos pa\u00edses desenvolvidos (Fran\u00e7a, Noruega, Espanha) cobra algum tipo de imposto sobre patrim\u00f4nio"
            ),
            contrasArgs = listOf(
                "Quando a Fran\u00e7a implementou o imposto sobre fortunas, mais de 40 mil milion\u00e1rios deixaram o pa\u00eds em 10 anos. O mesmo pode acontecer aqui",
                "Ricos t\u00eam contadores e advogados para esconder patrim\u00f4nio. Na pr\u00e1tica, quem acaba pagando \u00e9 o empres\u00e1rio m\u00e9dio que n\u00e3o tem como fugir",
                "A arrecada\u00e7\u00e3o real pode ser muito menor do que a estimada, porque fortunas s\u00e3o facilmente transferidas para offshores",
                "Pode desestimular investimento e cria\u00e7\u00e3o de empresas no Brasil, levando capital produtivo para outros pa\u00edses"
            ),
            comparacaoInternacional = "A Noruega cobra imposto sobre patrim\u00f4nio e \u00e9 um dos pa\u00edses mais ricos e igualit\u00e1rios do mundo. A Fran\u00e7a implementou e depois revogou parcialmente, porque houve fuga de capitais, mas manteve uma vers\u00e3o sobre im\u00f3veis de luxo. A Espanha cobra entre 0,2% e 3,5% sobre patrim\u00f4nios altos. Os EUA n\u00e3o t\u00eam imposto sobre patrim\u00f4nio federal, mas v\u00e1rios estados cobram. A Argentina implementou um imposto tempor\u00e1rio durante a pandemia. O debate \u00e9 global e n\u00e3o tem resposta f\u00e1cil.",
            apoiosECidadania = 198340,
            statusOriginal = "morreu",
            categoria = "economia",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=103471"
        ),

        IdeiaCidadao(
            id = "eco_002",
            titulo = "Fim dos penduricalhos do Judici\u00e1rio",
            resumo = "Acabar com aux\u00edlios absurdos que ju\u00edzes recebem por cima do sal\u00e1rio: aux\u00edlio moradia (mesmo tendo casa pr\u00f3pria), aux\u00edlio livro, aux\u00edlio sa\u00fade, aux\u00edlio alimenta\u00e7\u00e3o, retroativos milion\u00e1rios. Tudo isso faz o sal\u00e1rio real ultrapassar o teto constitucional.",
            explicacao = "O teto do funcionalismo p\u00fablico no Brasil \u00e9 o sal\u00e1rio de ministro do STF: R$ 44.008,52 (em 2024). Ningu\u00e9m no servi\u00e7o p\u00fablico deveria ganhar mais do que isso. Pelo menos \u00e9 o que diz a Constitui\u00e7\u00e3o.\n\nNa pr\u00e1tica, ju\u00edzes recebem muito mais. O truque s\u00e3o os chamados \"penduricalhos\": aux\u00edlio moradia (R$ 4.377), aux\u00edlio alimenta\u00e7\u00e3o, aux\u00edlio sa\u00fade, aux\u00edlio livro, gratifica\u00e7\u00f5es por ac\u00famulo de fun\u00e7\u00e3o, abonos, indeniza\u00e7\u00f5es por f\u00e9rias n\u00e3o tiradas, e o mais absurdo: retroativos que podem chegar a centenas de milhares de reais de uma vez.\n\nSegundo o CNJ, em 2023, mais de 60% dos magistrados brasileiros receberam acima do teto constitucional quando se somam todos os penduricalhos. Alguns contracheques chegaram a R$ 200 mil em um \u00fanico m\u00eas.\n\nE o mais revoltante: quando o Congresso tenta aprovar uma lei limitando esses extras, o pr\u00f3prio Judici\u00e1rio contesta a constitucionalidade. Ou seja, quem ganha os penduricalhos \u00e9 quem decide se eles s\u00e3o legais ou n\u00e3o.\n\nA proposta \u00e9 clara: sal\u00e1rio \u00e9 sal\u00e1rio. Todo e qualquer benef\u00edcio deve ser inclu\u00eddo no c\u00e1lculo do teto. Se o teto \u00e9 R$ 44 mil, ningu\u00e9m leva pra casa mais do que R$ 44 mil. Acabou.",
            comoTeAfeta = "Cada real gasto com penduricalho de juiz \u00e9 um real que n\u00e3o vai pra sa\u00fade, educa\u00e7\u00e3o ou seguran\u00e7a. O Judici\u00e1rio brasileiro custa mais de R$ 100 bilh\u00f5es por ano, sendo um dos mais caros do mundo proporcionalmente ao PIB. Acabar com penduricalhos economizaria bilh\u00f5es que poderiam ir pra \u00e1reas que realmente afetam sua vida.",
            prosArgs = listOf(
                "A Constitui\u00e7\u00e3o \u00e9 clara: ningu\u00e9m no servi\u00e7o p\u00fablico pode ganhar mais que ministro do STF. Penduricalhos burlam isso",
                "O Judici\u00e1rio brasileiro \u00e9 um dos mais caros do mundo. Cortar excessos liberaria bilh\u00f5es para outras \u00e1reas",
                "\u00c9 uma quest\u00e3o de igualdade: um professor ganha R$ 4 mil e um juiz leva R$ 200 mil em um m\u00eas com retroativos",
                "O pr\u00f3prio CNJ j\u00e1 reconheceu que os penduricalhos s\u00e3o um problema e tentou limitar, mas foi barrado por liminares"
            ),
            contrasArgs = listOf(
                "Ju\u00edzes argumentam que os aux\u00edlios compensam riscos da profiss\u00e3o (amea\u00e7as, press\u00e3o, carga de trabalho)",
                "Reduzir remunera\u00e7\u00e3o pode afastar bons profissionais do Judici\u00e1rio e empurr\u00e1-los para a advocacia privada, onde ganhariam mais",
                "Alguns benef\u00edcios (como aux\u00edlio sa\u00fade) s\u00e3o comuns em outras carreiras p\u00fablicas. Cortar s\u00f3 do Judici\u00e1rio seria desigual",
                "A solu\u00e7\u00e3o pode ser pior que o problema: ju\u00edzes podem simplesmente reajustar o sal\u00e1rio base para compensar, o que custaria ainda mais"
            ),
            comparacaoInternacional = "Nos EUA, ju\u00edzes federais ganham entre US$ 220 mil e US$ 285 mil por ano, sem penduricalhos significativos. Na Alemanha, ju\u00edzes ganham bem, mas dentro de uma escala transparente sem extras ocultos. No Reino Unido, ju\u00edzes da Suprema Corte ganham cerca de 250 mil libras anuais, tudo inclu\u00eddo. Em nenhum pa\u00eds desenvolvido existe o sistema brasileiro de \"sal\u00e1rio mais penduricalhos que dobram o valor\".",
            apoiosECidadania = 112890,
            statusOriginal = "morreu",
            categoria = "economia",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=138742"
        ),

        IdeiaCidadao(
            id = "eco_003",
            titulo = "Teto REAL pra sal\u00e1rio de servidor, incluindo STF",
            resumo = "Criar um teto salarial que realmente funcione: incluir tudo (sal\u00e1rio, benef\u00edcios, aux\u00edlios, gratifica\u00e7\u00f5es) no c\u00e1lculo e punir quem ultrapassar. Hoje o teto existe no papel, mas na pr\u00e1tica ningu\u00e9m respeita.",
            explicacao = "O artigo 37 da Constitui\u00e7\u00e3o diz que nenhum servidor p\u00fablico pode receber mais do que um ministro do STF. O problema \u00e9 que os pr\u00f3prios ministros do STF definem quanto ganham. E os penduricalhos ficam de fora do c\u00e1lculo.\n\nEm 2023, o STF decidiu que verbas de \"natureza indenizat\u00f3ria\" n\u00e3o entram no teto. Isso abriu a porteira: qualquer benef\u00edcio batizado de \"indeniza\u00e7\u00e3o\" escapa do limite. Aux\u00edlio moradia? Indeniza\u00e7\u00e3o. Aux\u00edlio livro? Indeniza\u00e7\u00e3o. F\u00e9rias vendidas? Indeniza\u00e7\u00e3o.\n\nO resultado \u00e9 que milhares de servidores do alto escal\u00e3o recebem acima do teto todo m\u00eas, de forma perfeitamente \"legal\". E quem define o que \u00e9 legal? Os mesmos que se beneficiam.\n\nA proposta quer mudar a Constitui\u00e7\u00e3o para deixar claro: TODA remunera\u00e7\u00e3o, de qualquer natureza, entra no teto. Sem exce\u00e7\u00e3o. E quem autorizar pagamento acima do teto responde por improbidade administrativa.\n\nAl\u00e9m disso, prop\u00f5e que o teto n\u00e3o seja mais definido pelo pr\u00f3prio STF, mas por um \u00f3rg\u00e3o independente ou pelo Congresso, para acabar com o conflito de interesses.",
            comoTeAfeta = "O dinheiro p\u00fablico \u00e9 limitado. Cada vez que um servidor de alto escal\u00e3o recebe R$ 100 mil num m\u00eas, esse dinheiro sai de algum lugar. Pode ser do hospital que n\u00e3o tem leito, da escola sem professor, da estrada sem asfalto. Um teto real significaria mais dinheiro dispon\u00edvel para servi\u00e7os que chegam at\u00e9 voc\u00ea.",
            prosArgs = listOf(
                "O teto j\u00e1 existe na Constitui\u00e7\u00e3o, mas \u00e9 burlado sistematicamente. A proposta \u00e9 fazer valer o que j\u00e1 foi decidido",
                "Ningu\u00e9m precisa de R$ 200 mil por m\u00eas de dinheiro p\u00fablico pra viver. \u00c9 desproporcional e imoral",
                "A economia seria de bilh\u00f5es por ano, que poderiam ir pra servi\u00e7os b\u00e1sicos",
                "Acaba com o conflito de interesses de quem define o pr\u00f3prio sal\u00e1rio"
            ),
            contrasArgs = listOf(
                "Se o teto for r\u00edgido demais, profissionais de alt\u00edssimo n\u00edvel (ju\u00edzes, m\u00e9dicos, engenheiros) podem preferir o setor privado",
                "Definir o teto pelo Congresso pode politizar ainda mais a quest\u00e3o, com parlamentares usando o tema para ganhar votos",
                "Alguns benef\u00edcios realmente t\u00eam natureza indenizat\u00f3ria (como di\u00e1rias de viagem). Incluir tudo no teto pode gerar injusti\u00e7as pontuais",
                "Mudar a Constitui\u00e7\u00e3o exige 3/5 dos votos do Congresso. Os pr\u00f3prios parlamentares que se beneficiam teriam que votar contra si mesmos"
            ),
            comparacaoInternacional = "Na maioria dos pa\u00edses da OCDE, h\u00e1 limites claros e transparentes para remunera\u00e7\u00e3o p\u00fablica. Na Su\u00e9cia, todos os sal\u00e1rios p\u00fablicos s\u00e3o abertos e acess\u00edveis, o que cria press\u00e3o social contra excessos. No Canad\u00e1, existe uma comiss\u00e3o independente que define remunera\u00e7\u00e3o de ju\u00edzes. Na Austr\u00e1lia, o tribunal de remunera\u00e7\u00e3o \u00e9 independente do Judici\u00e1rio. O modelo brasileiro, onde o benefici\u00e1rio define o pr\u00f3prio benef\u00edcio, \u00e9 praticamente \u00fanico no mundo.",
            apoiosECidadania = 73458,
            statusOriginal = "morreu",
            categoria = "economia",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=145903"
        ),

        IdeiaCidadao(
            id = "eco_004",
            titulo = "Reduzir n\u00famero de deputados",
            resumo = "O Brasil tem 513 deputados federais e 81 senadores, sem contar os milhares de deputados estaduais e vereadores. A proposta \u00e9 reduzir para algo entre 300 e 400 deputados, cortando custos e melhorando a representatividade real.",
            explicacao = "Cada deputado federal custa em m\u00e9dia R$ 3,6 milh\u00f5es por ano ao contribuinte. Isso inclui sal\u00e1rio, cota parlamentar, assessores, gabinete, viagens, moradia funcional e aux\u00edlios diversos. Com 513 deputados, s\u00e3o quase R$ 2 bilh\u00f5es por ano s\u00f3 com a C\u00e2mara.\n\nDefensores do n\u00famero atual dizem que mais deputados significa mais representatividade. Mas ser\u00e1? Na pr\u00e1tica, boa parte dos deputados n\u00e3o faz absolutamente nada de relevante. Pesquisas mostram que cerca de 30% dos deputados n\u00e3o apresentam nenhum projeto de lei significativo durante todo o mandato.\n\nAl\u00e9m disso, o excesso de partidos (mais de 20 com representa\u00e7\u00e3o) fragmenta tanto o Congresso que o governo precisa negociar com dezenas de legendas para aprovar qualquer coisa. Isso gera o famoso \"toma l\u00e1 d\u00e1 c\u00e1\" e aumenta o custo da governabilidade.\n\nA proposta sugere reduzir para um n\u00famero entre 300 e 400, proporcional \u00e0 popula\u00e7\u00e3o mas com um piso m\u00ednimo por estado. Isso manteria a representatividade regional mas eliminaria deputados redundantes.\n\nJunto com a redu\u00e7\u00e3o, viria uma reforma no sistema de vota\u00e7\u00e3o para garantir que os deputados restantes realmente representem seus eleitores, e n\u00e3o apenas seus partidos.",
            comoTeAfeta = "Menos deputados significa menos gasto p\u00fablico direto (bilh\u00f5es economizados) e potencialmente um Congresso mais funcional, onde \u00e9 mais f\u00e1cil aprovar reformas que o pa\u00eds precisa. Tamb\u00e9m facilita pra voc\u00ea cobrar seu representante, porque com menos gente, fica mais claro quem \u00e9 respons\u00e1vel por qu\u00ea.",
            prosArgs = listOf(
                "Economia direta de centenas de milh\u00f5es de reais por ano com corte de gabinetes, assessores e benef\u00edcios",
                "Um Congresso menor tende a ser mais \u00e1gil e funcional, com menos fragmenta\u00e7\u00e3o e mais governabilidade",
                "Muitos deputados atuais n\u00e3o fazem nada relevante. Reduzir o n\u00famero obriga que os eleitos trabalhem mais",
                "Facilita a fiscaliza\u00e7\u00e3o cidad\u00e3: com menos deputados, \u00e9 mais f\u00e1cil acompanhar o que cada um faz"
            ),
            contrasArgs = listOf(
                "Estados menores perderiam representatividade. Roraima, Amap\u00e1 e outros j\u00e1 t\u00eam poucos deputados",
                "Menos deputados significa menos diversidade de vozes no Congresso, prejudicando minorias e grupos espec\u00edficos",
                "O problema n\u00e3o \u00e9 a quantidade, mas a qualidade. Reduzir o n\u00famero n\u00e3o garante que os restantes ser\u00e3o melhores",
                "Pode concentrar poder em poucos partidos grandes, sufocando partidos menores que representam nichos importantes da sociedade"
            ),
            comparacaoInternacional = "Os EUA t\u00eam 435 deputados para 330 milh\u00f5es de habitantes (1 para cada 760 mil). O Brasil tem 513 para 215 milh\u00f5es (1 para cada 420 mil). A Alemanha tem 736 deputados, mas \u00e9 o maior parlamento da Europa e h\u00e1 debate interno para reduzir. O Reino Unido tem 650 deputados (House of Commons). A propor\u00e7\u00e3o brasileira n\u00e3o \u00e9 a pior do mundo, mas o custo por deputado \u00e9 um dos mais altos.",
            apoiosECidadania = 54872,
            statusOriginal = "morreu",
            categoria = "economia",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=109234"
        ),

        IdeiaCidadao(
            id = "eco_005",
            titulo = "Acabar com aposentadoria especial de pol\u00edtico",
            resumo = "Pol\u00edticos brasileiros podem se aposentar com regras muito mais vantajosas do que o cidad\u00e3o comum. A proposta \u00e9 igualar: pol\u00edtico se aposenta pelas mesmas regras do INSS ou do regime pr\u00f3prio, sem privil\u00e9gio nenhum.",
            explicacao = "Enquanto o trabalhador comum precisa contribuir por 35 anos e esperar at\u00e9 os 65 para se aposentar com um teto de pouco mais de R$ 7 mil, um ex-presidente da Rep\u00fablica se aposenta com sal\u00e1rio integral ap\u00f3s um \u00fanico mandato de 4 anos. Ex-governadores e ex-parlamentares de v\u00e1rios estados tamb\u00e9m t\u00eam regras especiais.\n\nA Reforma da Previd\u00eancia de 2019 avan\u00e7ou em alguns pontos, acabando com a aposentadoria especial para novos parlamentares federais. Mas manteve direitos adquiridos (quem j\u00e1 tinha o benef\u00edcio continua recebendo) e n\u00e3o atingiu estados e munic\u00edpios, onde as regras continuam absurdas.\n\nEm muitos estados, ex-governadores recebem pens\u00e3o vital\u00edcia. Ex-deputados estaduais de estados como Rio de Janeiro e Minas Gerais tamb\u00e9m t\u00eam regimes pr\u00f3prios extremamente vantajosos. E o custo disso \u00e9 pago pelo contribuinte.\n\nA proposta \u00e9 radical e simples: todo pol\u00edtico, de vereador a presidente, contribui pro mesmo sistema que o cidad\u00e3o comum. Se quiser aposentadoria melhor, faz previd\u00eancia privada com o pr\u00f3prio dinheiro. O regime especial acaba, inclusive para quem j\u00e1 tem \"direito adquirido\".\n\nEssa \u00faltima parte \u00e9 a mais pol\u00eamica, porque mexer com direito adquirido \u00e9 complicado juridicamente. Mas os defensores argumentam que privil\u00e9gio n\u00e3o \u00e9 direito.",
            comoTeAfeta = "Voc\u00ea trabalha 35 anos pra ganhar R$ 7 mil de aposentadoria. Um pol\u00edtico trabalha 4 anos e ganha sal\u00e1rio integral pro resto da vida. Essa conta fecha? A economia com o fim das aposentadorias especiais seria de bilh\u00f5es por ano, que poderiam fortalecer o INSS ou reduzir a idade m\u00ednima de aposentadoria do cidad\u00e3o comum.",
            prosArgs = listOf(
                "\u00c9 uma quest\u00e3o b\u00e1sica de igualdade: se o cidad\u00e3o trabalha 35 anos pra se aposentar, o pol\u00edtico n\u00e3o pode se aposentar com 4",
                "A economia seria enorme, especialmente nos estados onde as regras s\u00e3o mais abusivas",
                "Mandato \u00e9 servi\u00e7o p\u00fablico tempor\u00e1rio, n\u00e3o carreira. N\u00e3o faz sentido ter aposentadoria especial por algo tempor\u00e1rio",
                "Desestimularia a profissionaliza\u00e7\u00e3o da pol\u00edtica, onde gente entra na vida p\u00fablica s\u00f3 pelo benef\u00edcio financeiro"
            ),
            contrasArgs = listOf(
                "Mexer com direito adquirido \u00e9 inconstitucional segundo o entendimento majorit\u00e1rio do STF",
                "Sem uma remunera\u00e7\u00e3o atrativa p\u00f3s mandato, apenas ricos poderiam se dar ao luxo de entrar na pol\u00edtica",
                "A Reforma de 2019 j\u00e1 resolveu boa parte do problema para novos parlamentares federais. O restante vai se resolver com o tempo",
                "Pol\u00edtico deixa a carreira original para servir. Sem alguma prote\u00e7\u00e3o previdenci\u00e1ria, ningu\u00e9m qualificado vai querer se candidatar"
            ),
            comparacaoInternacional = "Nos EUA, congressistas contribuem para o mesmo sistema previdenci\u00e1rio federal (FERS) que outros funcion\u00e1rios p\u00fablicos, sem regime especial. Na Alemanha, parlamentares recebem pens\u00e3o proporcional ao tempo de mandato, mas sem privil\u00e9gios absurdos. No Reino Unido, parlamentares contribuem para um fundo de pens\u00e3o parlamentar com regras mais pr\u00f3ximas do mercado privado. Aposentadoria vital\u00edcia integral ap\u00f3s um mandato \u00e9 raridade no mundo.",
            apoiosECidadania = 167523,
            statusOriginal = "morreu",
            categoria = "economia",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=118976"
        ),

        // =====================================================================
        // SAUDE
        // =====================================================================

        IdeiaCidadao(
            id = "sau_001",
            titulo = "SUS com tempo m\u00e1ximo obrigat\u00f3rio pra consulta e cirurgia",
            resumo = "Definir por lei prazos m\u00e1ximos para atendimento no SUS: consulta com especialista em at\u00e9 30 dias, exames em at\u00e9 15 dias, cirurgia eletiva em at\u00e9 90 dias. Quem n\u00e3o conseguir no SUS, o governo paga no particular.",
            explicacao = "Hoje, a fila do SUS \u00e9 uma roleta russa. Dependendo do estado e da cidade, uma consulta com especialista pode demorar 6 meses, 1 ano, ou simplesmente nunca chegar. Cirurgias eletivas como pr\u00f3tese de quadril podem ter fila de 3 a 5 anos. Muita gente morre esperando.\n\nEm 2023, mais de 1,5 milh\u00e3o de brasileiros estavam na fila para cirurgias eletivas. O tempo m\u00e9dio de espera para consulta com especialista era de 180 dias em muitas cidades. Para oncologia (c\u00e2ncer), a lei j\u00e1 define prazo de 60 dias entre diagn\u00f3stico e in\u00edcio do tratamento, mas \u00e9 descumprida rotineiramente.\n\nA proposta quer criar prazos m\u00e1ximos obrigat\u00f3rios para todos os tipos de atendimento, n\u00e3o apenas oncologia. E mais importante: se o SUS n\u00e3o conseguir atender no prazo, o governo \u00e9 obrigado a pagar o atendimento na rede particular. Isso cria um incentivo real para o poder p\u00fablico investir e organizar o sistema.\n\nCr\u00edticos dizem que \u00e9 uma proposta cara e que for\u00e7aria gastos enormes com rede particular. Mas defensores argumentam que o custo de n\u00e3o tratar \u00e9 maior: pacientes que pioram na fila acabam custando mais caro quando finalmente s\u00e3o atendidos, al\u00e9m do custo humano inaceit\u00e1vel.\n\nAlguns munic\u00edpios j\u00e1 implementaram sistemas parecidos com sucesso, como Curitiba, que tem tempos de espera muito menores que a m\u00e9dia nacional.",
            comoTeAfeta = "Se voc\u00ea depende do SUS, sabe o que \u00e9 esperar meses por uma consulta enquanto a dor piora. Essa proposta significaria que voc\u00ea teria garantia de ser atendido dentro de um prazo definido. Se o SUS n\u00e3o der conta, o governo paga pra voc\u00ea ir no particular. Chega de morrer na fila.",
            prosArgs = listOf(
                "Pessoas morrem esperando atendimento no SUS. Prazos obrigat\u00f3rios salvam vidas literalmente",
                "Cria incentivo real para o governo investir no SUS, porque descumprir o prazo sai mais caro do que investir",
                "Outros pa\u00edses com sa\u00fade p\u00fablica universal j\u00e1 fazem isso com sucesso (Portugal, Reino Unido, Canad\u00e1 em parte)",
                "A lei de 60 dias para c\u00e2ncer j\u00e1 existe e, onde \u00e9 cumprida, salvou milhares de vidas. Expandir isso \u00e9 l\u00f3gico"
            ),
            contrasArgs = listOf(
                "O custo seria gigantesco. Pagar atendimento particular para milh\u00f5es de pessoas que o SUS n\u00e3o consegue atender pode quebrar o or\u00e7amento",
                "Pode criar um incentivo perverso: gestores deixariam o SUS piorar de prop\u00f3sito para direcionar dinheiro p\u00fablico para cl\u00ednicas particulares de amigos",
                "O problema do SUS \u00e9 estrutural (falta de m\u00e9dicos, infraestrutura). Prazo obrigat\u00f3rio sem investimento \u00e9 promessa vazia",
                "Regi\u00f5es remotas (Norte, interior do Nordeste) n\u00e3o t\u00eam rede particular para absorver a demanda, ent\u00e3o a lei seria in\u00fatil onde mais \u00e9 necess\u00e1ria"
            ),
            comparacaoInternacional = "Portugal implementou o sistema SIGIC, onde pacientes que esperam al\u00e9m do prazo recebem voucher para atendimento particular pago pelo Estado. O tempo de espera caiu significativamente. O Reino Unido tem metas de atendimento no NHS (18 semanas para cirurgia eletiva), embora nem sempre cumpra. O Canad\u00e1 tem prazos recomendados, mas n\u00e3o obrigat\u00f3rios, e as filas ainda s\u00e3o um problema. A Su\u00e9cia garante consulta com especialista em 90 dias.",
            apoiosECidadania = 45230,
            statusOriginal = "em vota\u00e7\u00e3o",
            categoria = "sa\u00fade",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=162874"
        ),

        IdeiaCidadao(
            id = "sau_002",
            titulo = "Proibir reajuste abusivo de plano de sa\u00fade",
            resumo = "Limitar o reajuste anual de planos de sa\u00fade ao IPCA (infla\u00e7\u00e3o oficial) e proibir reajustes por faixa et\u00e1ria acima de 200% entre a menor e a maior faixa. Hoje, idosos pagam at\u00e9 6 vezes mais que jovens pelo mesmo plano.",
            explicacao = "Todo ano, milh\u00f5es de brasileiros levam um susto quando chega o reajuste do plano de sa\u00fade. Enquanto a infla\u00e7\u00e3o fica em torno de 4% a 5% ao ano, os reajustes de planos individuais chegam a 15%, 20%, \u00e0s vezes mais.\n\nMas o pior \u00e9 o reajuste por faixa et\u00e1ria. Quando voc\u00ea contrata um plano jovem e saud\u00e1vel, paga pouco. Conforme envelhece, o pre\u00e7o sobe em degraus. Ao chegar aos 59 anos (\u00faltima faixa), voc\u00ea pode estar pagando at\u00e9 6 vezes mais do que pagava aos 18. Exatamente quando mais precisa do plano, menos consegue pagar.\n\nA ANS regula os reajustes de planos individuais, mas os planos coletivos (empresariais e por ades\u00e3o), que representam mais de 80% do mercado, t\u00eam reajuste livre. As operadoras simplesmente migraram seus clientes para planos coletivos para fugir da regula\u00e7\u00e3o.\n\nA proposta quer tr\u00eas coisas: limitar todo reajuste (individual e coletivo) ao IPCA mais um percentual m\u00e1ximo definido pela ANS; limitar a varia\u00e7\u00e3o entre faixas et\u00e1rias a no m\u00e1ximo 200% (hoje pode chegar a 600%); e proibir cancelamento unilateral de plano de idoso.\n\nAs operadoras argumentam que os custos m\u00e9dicos sobem acima da infla\u00e7\u00e3o e que limitar o reajuste pode inviabilizar o setor. Mas os lucros das grandes operadoras crescem ano ap\u00f3s ano, ent\u00e3o a conta n\u00e3o fecha.",
            comoTeAfeta = "Se voc\u00ea tem plano de sa\u00fade, paga reajustes absurdos todo ano. Se n\u00e3o tem, n\u00e3o consegue contratar porque \u00e9 caro demais. Se \u00e9 idoso, vive com medo de perder o plano porque n\u00e3o consegue pagar. Essa proposta limitaria os reajustes e protegeria especialmente quem mais precisa: os mais velhos.",
            prosArgs = listOf(
                "\u00c9 imoral que o plano fique impag\u00e1vel justamente quando a pessoa mais precisa (na velhice)",
                "Operadoras de sa\u00fade lucram bilh\u00f5es. H\u00e1 margem para absorver regula\u00e7\u00e3o sem quebrar",
                "Mais de 80% dos planos s\u00e3o coletivos e n\u00e3o t\u00eam regula\u00e7\u00e3o de reajuste. Isso \u00e9 uma falha grave que a proposta corrige",
                "A Constitui\u00e7\u00e3o garante direito \u00e0 sa\u00fade. Permitir reajustes que expulsam idosos do plano viola esse direito"
            ),
            contrasArgs = listOf(
                "Custos m\u00e9dicos realmente sobem acima da infla\u00e7\u00e3o (novas tecnologias, medicamentos, envelhecimento da popula\u00e7\u00e3o). Limitar reajuste ao IPCA pode ser irreal",
                "Se as operadoras n\u00e3o conseguirem repassar custos, podem reduzir a qualidade do servi\u00e7o ou sair do mercado, deixando milh\u00f5es sem cobertura",
                "Limitar reajuste por faixa et\u00e1ria pode encarecer o plano para os jovens, que subsidiariam os idosos. Jovens podem simplesmente n\u00e3o contratar",
                "Regula\u00e7\u00e3o excessiva pode matar a concorr\u00eancia. S\u00f3 sobreviveriam as grandes operadoras, formando um oligop\u00f3lio ainda pior"
            ),
            comparacaoInternacional = "Na Alemanha, planos de sa\u00fade n\u00e3o podem discriminar por idade nem por condi\u00e7\u00e3o pr\u00e9-existente. Na Holanda, o sistema \u00e9 privado mas altamente regulado: todos pagam o mesmo valor base. Nos EUA, o Obamacare proibiu discrimina\u00e7\u00e3o por condi\u00e7\u00e3o pr\u00e9-existente e limitou a varia\u00e7\u00e3o por idade a 3:1. No Jap\u00e3o, o sistema universal \u00e9 complementado por seguros privados acess\u00edveis e regulados. O modelo brasileiro de reajuste livre para coletivos \u00e9 incomum entre pa\u00edses que regulam sa\u00fade privada.",
            apoiosECidadania = 38750,
            statusOriginal = "morreu",
            categoria = "sa\u00fade",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=151287"
        ),

        IdeiaCidadao(
            id = "sau_003",
            titulo = "Gen\u00e9rico obrigat\u00f3rio quando existir equivalente",
            resumo = "Obrigar farm\u00e1cias a oferecer e m\u00e9dicos a prescrever o medicamento gen\u00e9rico quando existir equivalente aprovado pela ANVISA. O gen\u00e9rico custa em m\u00e9dia 60% menos que o de marca e tem a mesma efic\u00e1cia comprovada.",
            explicacao = "Desde 1999, o Brasil tem a lei dos gen\u00e9ricos. Mas na pr\u00e1tica, a participa\u00e7\u00e3o dos gen\u00e9ricos no mercado brasileiro ainda \u00e9 de cerca de 35%, muito abaixo de pa\u00edses como EUA (90%), Reino Unido (80%) e Alemanha (80%).\n\nPor qu\u00ea? Primeiro, muitos m\u00e9dicos ainda prescrevem pela marca, seja por h\u00e1bito, seja por influ\u00eancia de laborat\u00f3rios que oferecem benef\u00edcios (viagens, congressos, brindes). Segundo, farm\u00e1cias empurram o similar (que n\u00e3o \u00e9 gen\u00e9rico) porque a margem de lucro \u00e9 maior. Terceiro, muita gente ainda acha que gen\u00e9rico \u00e9 \"pior\", o que n\u00e3o \u00e9 verdade: para ser aprovado pela ANVISA como gen\u00e9rico, o medicamento precisa provar bioequival\u00eancia.\n\nA proposta quer tr\u00eas mudan\u00e7as: obrigar o m\u00e9dico a incluir o nome gen\u00e9rico em toda prescri\u00e7\u00e3o (mesmo que tamb\u00e9m escreva a marca); obrigar a farm\u00e1cia a informar a exist\u00eancia do gen\u00e9rico e seu pre\u00e7o antes da venda; e proibir laborat\u00f3rios de oferecer qualquer tipo de benef\u00edcio a m\u00e9dicos em troca de prescri\u00e7\u00e3o.\n\nIsso n\u00e3o pro\u00edbe o paciente de escolher a marca se preferir. Apenas garante que ele saiba que existe uma op\u00e7\u00e3o mais barata e igualmente eficaz.\n\nO impacto no bolso \u00e9 enorme: um tratamento de hipertens\u00e3o com rem\u00e9dio de marca pode custar R$ 150/m\u00eas. O gen\u00e9rico equivalente custa R$ 40. Para quem toma rem\u00e9dio todo dia, isso \u00e9 a diferen\u00e7a entre tratar ou abandonar o tratamento.",
            comoTeAfeta = "Se voc\u00ea compra rem\u00e9dio, vai pagar menos. Simples. O gen\u00e9rico tem a mesma f\u00f3rmula, a mesma efic\u00e1cia, mas custa at\u00e9 60% menos. A proposta garante que voc\u00ea saiba que a op\u00e7\u00e3o existe e que o m\u00e9dico n\u00e3o receite marca s\u00f3 porque o laborat\u00f3rio pagou um congresso em Canc\u00fan pra ele.",
            prosArgs = listOf(
                "Gen\u00e9ricos s\u00e3o comprovadamente equivalentes ao rem\u00e9dio de marca. A ANVISA garante isso com testes rigorosos",
                "Economia m\u00e9dia de 60% no pre\u00e7o do medicamento. Para quem toma rem\u00e9dio cr\u00f4nico, isso muda a vida",
                "Reduz o gasto do SUS com medicamentos, liberando recursos para outras \u00e1reas da sa\u00fade",
                "Pa\u00edses que adotaram gen\u00e9ricos de forma massiva (EUA, Alemanha) viram redu\u00e7\u00e3o significativa nos custos de sa\u00fade sem perda de qualidade"
            ),
            contrasArgs = listOf(
                "Obrigar prescri\u00e7\u00e3o de gen\u00e9rico interfere na autonomia m\u00e9dica. O m\u00e9dico deve ter liberdade de prescrever o que achar melhor",
                "Em casos raros, pode haver diferen\u00e7a de absor\u00e7\u00e3o entre gen\u00e9rico e marca em pacientes espec\u00edficos (por exemplo, medicamentos de margem terap\u00eautica estreita)",
                "Laborat\u00f3rios nacionais que investem em pesquisa de novas drogas podem ser prejudicados se gen\u00e9ricos dominarem o mercado",
                "For\u00e7ar a farm\u00e1cia a oferecer gen\u00e9rico pode ser visto como interfer\u00eancia na liberdade comercial"
            ),
            comparacaoInternacional = "Nos EUA, 90% das prescri\u00e7\u00f5es s\u00e3o gen\u00e9ricos. No Reino Unido, o NHS prioriza gen\u00e9ricos em todas as prescri\u00e7\u00f5es e consegue economizar bilh\u00f5es de libras por ano. Na \u00cdndia, o programa Jan Aushadhi oferece gen\u00e9ricos a pre\u00e7os at\u00e9 90% menores. Na Alemanha, farm\u00e1cias s\u00e3o obrigadas a oferecer o gen\u00e9rico mais barato quando existe. O Brasil, com 35% de participa\u00e7\u00e3o de gen\u00e9ricos, ainda tem muito a avan\u00e7ar.",
            apoiosECidadania = 28940,
            statusOriginal = "morreu",
            categoria = "sa\u00fade",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=147632"
        ),

        // =====================================================================
        // EDUCACAO
        // =====================================================================

        IdeiaCidadao(
            id = "edu_001",
            titulo = "Piso salarial real pra professor, equiparado a engenheiro",
            resumo = "Equiparar o piso salarial do professor ao de outras profiss\u00f5es de n\u00edvel superior, como engenheiro. Hoje um professor com forma\u00e7\u00e3o completa ganha em m\u00e9dia R$ 3.800 por m\u00eas. Um engenheiro com a mesma forma\u00e7\u00e3o ganha R$ 9.500.",
            explicacao = "O Brasil tem um piso salarial nacional para professores da educa\u00e7\u00e3o b\u00e1sica, criado em 2008. Em 2024, esse piso \u00e9 de R$ 4.580 para 40 horas semanais. Parece razo\u00e1vel, mas na pr\u00e1tica, muitos estados e munic\u00edpios n\u00e3o pagam nem isso, e o valor \u00e9 muito abaixo de outras profiss\u00f5es com mesma forma\u00e7\u00e3o.\n\nPara ter uma ideia da dist\u00e2ncia: segundo dados do IBGE e CAGED, um professor da educa\u00e7\u00e3o b\u00e1sica com forma\u00e7\u00e3o superior ganha em m\u00e9dia R$ 3.800 por m\u00eas. Um engenheiro civil ganha R$ 9.500. Um analista de sistemas ganha R$ 8.200. Um advogado ganha R$ 7.800. O professor \u00e9 a profiss\u00e3o de n\u00edvel superior mais mal paga do Brasil.\n\nO resultado \u00e9 previs\u00edvel: os melhores alunos n\u00e3o querem ser professores. Em 2023, as licenciaturas (cursos que formam professores) tiveram a menor nota de corte do SISU. Em muitas regi\u00f5es, sobram vagas de professor porque ningu\u00e9m quer. E quando falta professor, quem sofre \u00e9 o aluno.\n\nA proposta \u00e9 equiparar gradualmente o piso do professor ao sal\u00e1rio m\u00e9dio de engenheiro (ou de outras profiss\u00f5es equivalentes), num prazo de 10 anos. O dinheiro viria de remanejamento de gastos p\u00fablicos e aumento da vincula\u00e7\u00e3o do or\u00e7amento com educa\u00e7\u00e3o.\n\nA l\u00f3gica \u00e9 simples: se voc\u00ea quer um pa\u00eds melhor, precisa de educa\u00e7\u00e3o melhor. E educa\u00e7\u00e3o melhor come\u00e7a com professor valorizado.",
            comoTeAfeta = "Se voc\u00ea tem filho em escola p\u00fablica (ou pretende ter), o professor dele \u00e9 mal pago, desmotivado e provavelmente dando aula em 3 escolas pra fechar as contas. Valorizar o professor significa atrair gente boa pra profiss\u00e3o, o que melhora diretamente a qualidade da educa\u00e7\u00e3o do seu filho. No longo prazo, um pa\u00eds com educa\u00e7\u00e3o melhor \u00e9 um pa\u00eds com mais emprego, menos viol\u00eancia e mais oportunidade pra todo mundo.",
            prosArgs = listOf(
                "Professor \u00e9 a base de tudo. Sem professor bom, n\u00e3o existe engenheiro, m\u00e9dico, advogado nem nada",
                "Os melhores alunos fogem da profiss\u00e3o porque o sal\u00e1rio \u00e9 humilhante. Equiparar atrairia talento",
                "Pa\u00edses com os melhores sistemas educacionais do mundo (Finl\u00e2ndia, Coreia do Sul, Singapura) pagam bem e valorizam o professor",
                "O custo de n\u00e3o investir em professor \u00e9 muito maior: mais criminalidade, menos produtividade, mais desigualdade"
            ),
            contrasArgs = listOf(
                "O custo seria enorme. S\u00e3o mais de 2 milh\u00f5es de professores na rede p\u00fablica. Dobrar o sal\u00e1rio de todos custaria dezenas de bilh\u00f5es por ano",
                "N\u00e3o adianta s\u00f3 aumentar sal\u00e1rio sem reformar a forma\u00e7\u00e3o dos professores. Tem gente que ganha mal E ensina mal",
                "Munic\u00edpios pequenos mal conseguem pagar o piso atual. Exigir equipara\u00e7\u00e3o com engenheiro pode quebrar prefeituras",
                "Engenheiro ganha mais porque o mercado privado paga mais. A compara\u00e7\u00e3o n\u00e3o \u00e9 totalmente justa porque as din\u00e2micas s\u00e3o diferentes"
            ),
            comparacaoInternacional = "Na Finl\u00e2ndia, professor ganha equivalente a engenheiro e a profiss\u00e3o \u00e9 uma das mais disputadas do pa\u00eds. Na Coreia do Sul, professor do ensino b\u00e1sico est\u00e1 entre os 10% mais bem pagos da popula\u00e7\u00e3o. Na Alemanha, professor \u00e9 funcion\u00e1rio p\u00fablico com sal\u00e1rio competitivo e estabilidade. Nos EUA, o sal\u00e1rio m\u00e9dio de professor \u00e9 razo\u00e1vel (US$ 65 mil/ano), mas ainda considerado baixo para o n\u00edvel de forma\u00e7\u00e3o, e o pa\u00eds debate o mesmo problema que o Brasil. O Jap\u00e3o paga bem e d\u00e1 status social alt\u00edssimo ao professor.",
            apoiosECidadania = 52610,
            statusOriginal = "morreu",
            categoria = "educa\u00e7\u00e3o",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=134587"
        ),

        IdeiaCidadao(
            id = "edu_002",
            titulo = "Educa\u00e7\u00e3o financeira obrigat\u00f3ria no ensino fundamental",
            resumo = "Incluir educa\u00e7\u00e3o financeira como disciplina obrigat\u00f3ria desde o 6\u00ba ano do ensino fundamental. Ensinar juros, infla\u00e7\u00e3o, investimento, or\u00e7amento familiar, como funciona o imposto e por que n\u00e3o se deve parcelar em 48 vezes.",
            explicacao = "O Brasil tem 72 milh\u00f5es de inadimplentes (dados Serasa, 2024). Isso \u00e9 mais de um ter\u00e7o da popula\u00e7\u00e3o adulta. A maioria dessas pessoas n\u00e3o ficou endividada por luxo, mas por falta de conhecimento b\u00e1sico sobre como o dinheiro funciona.\n\nPesquisa do SPC Brasil mostra que 46% dos brasileiros n\u00e3o sabem quanto ganham exatamente. 58% n\u00e3o fazem nenhum tipo de controle financeiro. 76% n\u00e3o sabem calcular juros compostos. E 82% n\u00e3o entendem como funciona a infla\u00e7\u00e3o.\n\nEssas pessoas s\u00e3o presa f\u00e1cil para parcelamentos intermin\u00e1veis, empr\u00e9stimos com juros abusivos, golpes financeiros e decis\u00f5es ruins que comprometem a vida inteira. E n\u00e3o \u00e9 culpa delas: ningu\u00e9m ensinou.\n\nA BNCC (Base Nacional Comum Curricular) de 2017 incluiu educa\u00e7\u00e3o financeira como tema transversal, mas na pr\u00e1tica quase nenhuma escola implementou de verdade. O tema fica dilu\u00eddo em aulas de matem\u00e1tica que j\u00e1 n\u00e3o d\u00e3o conta do conte\u00fado b\u00e1sico.\n\nA proposta quer criar uma disciplina espec\u00edfica e obrigat\u00f3ria, com carga hor\u00e1ria definida, material did\u00e1tico pr\u00f3prio e professores capacitados. O conte\u00fado incluiria: or\u00e7amento pessoal e familiar, juros simples e compostos, infla\u00e7\u00e3o, tipos de investimento, funcionamento do imposto, direitos do consumidor, como funciona o cr\u00e9dito e por que financiar um celular em 24 vezes a 3% ao m\u00eas \u00e9 uma p\u00e9ssima ideia.",
            comoTeAfeta = "Se voc\u00ea j\u00e1 caiu em golpe, j\u00e1 parcelou algo em 48 vezes sem saber quanto ia pagar no total, j\u00e1 pegou empr\u00e9stimo sem entender os juros, ou simplesmente n\u00e3o sabe quanto gasta por m\u00eas, essa proposta \u00e9 pra voc\u00ea. E mais importante: \u00e9 para seus filhos n\u00e3o repetirem os mesmos erros.",
            prosArgs = listOf(
                "72 milh\u00f5es de inadimplentes \u00e9 um problema social, n\u00e3o individual. A raiz \u00e9 falta de educa\u00e7\u00e3o financeira",
                "Ensinar cedo cria h\u00e1bitos. Uma crian\u00e7a que aprende a poupar aos 12 anos tem muito mais chance de ter estabilidade financeira na vida adulta",
                "Reduz vulnerabilidade a golpes, fraudes e decis\u00f5es financeiras ruins que destroem fam\u00edlias inteiras",
                "Pa\u00edses que implementaram educa\u00e7\u00e3o financeira obrigat\u00f3ria (como Austr\u00e1lia e Est\u00f4nia) viram melhora significativa nos \u00edndices de endividamento"
            ),
            contrasArgs = listOf(
                "O curr\u00edculo escolar j\u00e1 est\u00e1 sobrecarregado. Adicionar mais uma disciplina obrigat\u00f3ria pode tirar espa\u00e7o de conte\u00fados essenciais",
                "N\u00e3o h\u00e1 professores formados em educa\u00e7\u00e3o financeira. Quem vai dar essas aulas? O professor de matem\u00e1tica que j\u00e1 est\u00e1 sobrecarregado?",
                "A BNCC j\u00e1 prev\u00ea educa\u00e7\u00e3o financeira como tema transversal. O problema \u00e9 implementa\u00e7\u00e3o, n\u00e3o falta de lei",
                "Educa\u00e7\u00e3o financeira sem mudan\u00e7a estrutural (juros altos, sal\u00e1rios baixos) \u00e9 como ensinar a nadar em piscina sem \u00e1gua"
            ),
            comparacaoInternacional = "A Austr\u00e1lia tem educa\u00e7\u00e3o financeira obrigat\u00f3ria desde o ensino fundamental e \u00e9 refer\u00eancia mundial no tema. A Est\u00f4nia, pa\u00eds com menor taxa de analfabetismo financeiro da Europa, ensina o assunto desde os 7 anos. O Reino Unido incluiu cidadania financeira no curr\u00edculo nacional em 2014. Nos EUA, 25 estados exigem algum tipo de educa\u00e7\u00e3o financeira no ensino m\u00e9dio, e os estados que implementaram viram queda de 8% no inadimplemento entre jovens adultos. O Jap\u00e3o recentemente tornou obrigat\u00f3rio no ensino m\u00e9dio.",
            apoiosECidadania = 19870,
            statusOriginal = "morreu",
            categoria = "educa\u00e7\u00e3o",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=158943"
        ),

        IdeiaCidadao(
            id = "edu_003",
            titulo = "Merenda escolar com valor m\u00ednimo real, n\u00e3o R$ 0,36",
            resumo = "Aumentar substancialmente o repasse federal para merenda escolar. Hoje o governo federal repassa R$ 0,36 por aluno por dia para a alimenta\u00e7\u00e3o. Com esse valor, muitas escolas servem comida de baix\u00edssima qualidade nutricional.",
            explicacao = "O Programa Nacional de Alimenta\u00e7\u00e3o Escolar (PNAE) \u00e9 o maior programa de alimenta\u00e7\u00e3o escolar do mundo, atendendo mais de 40 milh\u00f5es de alunos. Mas o repasse federal \u00e9 ridiculamente baixo: R$ 0,36 por aluno por dia para o ensino fundamental. Para o ensino m\u00e9dio, R$ 0,36. Para creches, R$ 1,07.\n\nEstados e munic\u00edpios complementam esse valor, mas a realidade \u00e9 que muitas escolas, especialmente em regi\u00f5es pobres, dependem quase exclusivamente do repasse federal. E com R$ 0,36, o que d\u00e1 pra servir? Biscoito com suco artificial. Arroz com feij\u00e3o aguado. Sopa rala. Nada que garanta a nutri\u00e7\u00e3o que uma crian\u00e7a em fase de crescimento precisa.\n\nPara muitas crian\u00e7as de fam\u00edlias em situa\u00e7\u00e3o de vulnerabilidade, a merenda \u00e9 a principal (\u00e0s vezes a \u00fanica) refei\u00e7\u00e3o do dia. Servir algo de baixa qualidade nutricional para essas crian\u00e7as \u00e9 conden\u00e1-las a problemas de desenvolvimento f\u00edsico e cognitivo.\n\nA proposta quer estabelecer um valor m\u00ednimo real por aluno, atualizado anualmente pela infla\u00e7\u00e3o, que garanta uma refei\u00e7\u00e3o completa e nutritiva. A sugest\u00e3o \u00e9 de pelo menos R$ 2,00 por aluno por dia, com acompanhamento nutricional obrigat\u00f3rio e fiscaliza\u00e7\u00e3o r\u00edgida.\n\nO custo adicional seria significativo, mas estamos falando de alimenta\u00e7\u00e3o de crian\u00e7as. Se o pa\u00eds gasta R$ 2 bilh\u00f5es por ano com a C\u00e2mara dos Deputados, n\u00e3o deveria gastar pelo menos isso com a comida dos filhos do povo?\n\nAl\u00e9m disso, a proposta exige que pelo menos 50% dos alimentos venham da agricultura familiar local, o que gera emprego e renda na comunidade.",
            comoTeAfeta = "Se seu filho estuda em escola p\u00fablica, a comida que ele come no almo\u00e7o pode ser a refei\u00e7\u00e3o mais importante do dia. Com R$ 0,36, essa refei\u00e7\u00e3o \u00e9 prec\u00e1ria. Com um valor real, seu filho teria garantia de uma refei\u00e7\u00e3o nutritiva que ajuda no aprendizado e no desenvolvimento. Crian\u00e7a bem alimentada aprende melhor, adoece menos e tem mais chance na vida.",
            prosArgs = listOf(
                "R$ 0,36 por refei\u00e7\u00e3o \u00e9 desumano. N\u00e3o d\u00e1 pra garantir nutri\u00e7\u00e3o m\u00ednima com esse valor",
                "Para milh\u00f5es de crian\u00e7as, a merenda \u00e9 a \u00fanica refei\u00e7\u00e3o completa do dia. Investir nela \u00e9 investir no futuro do pa\u00eds",
                "Crian\u00e7a bem alimentada aprende melhor, comprovado por dezenas de estudos cient\u00edficos",
                "Comprar da agricultura familiar gera emprego local e melhora a economia de munic\u00edpios pequenos"
            ),
            contrasArgs = listOf(
                "Multiplicar o repasse por 40 milh\u00f5es de alunos resulta em custo de dezenas de bilh\u00f5es adicionais por ano. De onde sai esse dinheiro?",
                "O problema em muitos munic\u00edpios n\u00e3o \u00e9 o valor, mas a corrup\u00e7\u00e3o: prefeitos desviam o dinheiro da merenda. Aumentar o repasse pode aumentar o desvio",
                "Estados e munic\u00edpios j\u00e1 complementam o valor. O repasse federal \u00e9 uma parte, n\u00e3o o todo. A m\u00e9dia real gasta por aluno j\u00e1 \u00e9 maior que R$ 0,36",
                "Fiscalizar qualidade nutricional em mais de 150 mil escolas \u00e9 logisticamente quase imposs\u00edvel"
            ),
            comparacaoInternacional = "O Jap\u00e3o investe cerca de US$ 3 por aluno por dia em merenda escolar e \u00e9 refer\u00eancia mundial em alimenta\u00e7\u00e3o escolar nutritiva. A Fran\u00e7a gasta cerca de 2 euros por refei\u00e7\u00e3o e oferece menu balanceado com nutricionista em cada escola. A \u00cdndia, com o programa Mid Day Meal, atende 120 milh\u00f5es de crian\u00e7as com repasse maior que o brasileiro proporcionalmente. Os EUA gastam em m\u00e9dia US$ 4 por refei\u00e7\u00e3o escolar. O Brasil, com R$ 0,36 (menos de US$ 0,07), tem um dos menores investimentos por aluno do mundo.",
            apoiosECidadania = 31280,
            statusOriginal = "morreu",
            categoria = "educa\u00e7\u00e3o",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=163102"
        ),

        // =====================================================================
        // DIREITOS
        // =====================================================================

        IdeiaCidadao(
            id = "dir_001",
            titulo = "Candidatura independente, sem partido",
            resumo = "Permitir que cidad\u00e3os se candidatem a cargos eletivos sem precisar de filia\u00e7\u00e3o partid\u00e1ria. Hoje \u00e9 obrigat\u00f3rio estar filiado a um partido para concorrer a qualquer elei\u00e7\u00e3o no Brasil, mesmo que voc\u00ea n\u00e3o concorde com nenhum.",
            explicacao = "O Brasil tem mais de 30 partidos registrados. E mesmo assim, muita gente n\u00e3o se sente representada por nenhum deles. Pesquisa Datafolha de 2024 mostrou que 47% dos brasileiros n\u00e3o t\u00eam simpatia por nenhum partido pol\u00edtico.\n\nMas aqui est\u00e1 o problema: para se candidatar a qualquer cargo, de vereador a presidente, voc\u00ea \u00e9 obrigado a se filiar a um partido. Isso significa que uma pessoa honesta e competente que queira servir ao p\u00fablico, mas n\u00e3o concorde com nenhum partido, simplesmente n\u00e3o pode se candidatar.\n\nO resultado \u00e9 que muita gente se filia a partidos nanicos por conveni\u00eancia, sem nenhum compromisso ideol\u00f3gico. Partidos de aluguel que existem s\u00f3 para vender tempo de TV e fundo eleitoral. Isso enfraquece a democracia em vez de fortalec\u00ea-la.\n\nA proposta permite candidatura independente, com algumas salvaguardas: o candidato precisaria coletar um n\u00famero m\u00ednimo de assinaturas de apoio (por exemplo, 1% do eleitorado da circunscri\u00e7\u00e3o) para comprovar que tem apoio popular real. N\u00e3o teria acesso a fundo eleitoral nem tempo de TV gratuito, para n\u00e3o parasitar o sistema.\n\nO candidato independente seria um representante direto dos seus eleitores, sem dever favores a cacique de partido nem se submeter a decis\u00f5es de diret\u00f3rio.\n\nCr\u00edticos alertam que sem estrutura partid\u00e1ria, candidatos independentes teriam dificuldade de governar, j\u00e1 que no Congresso tudo funciona por partidos e bancadas. Mas defensores dizem que essa \u00e9 justamente a mentalidade que precisa mudar.",
            comoTeAfeta = "Se voc\u00ea \u00e9 daqueles que vota \"no menos pior\" porque nenhum partido te representa, essa proposta \u00e9 feita pra voc\u00ea. Imagina poder votar em algu\u00e9m da sua comunidade que voc\u00ea conhece e confia, sem ele ter que se filiar a um partido que n\u00e3o representa ningu\u00e9m. Mais op\u00e7\u00e3o de voto significa mais chance de encontrar algu\u00e9m que realmente pense como voc\u00ea.",
            prosArgs = listOf(
                "47% dos brasileiros n\u00e3o se identificam com nenhum partido. Candidatura independente amplia a democracia",
                "Acaba com partidos de aluguel que s\u00f3 existem para vender tempo de TV e fundo eleitoral",
                "Candidatos independentes devem lealdade ao eleitor, n\u00e3o ao cacique do partido",
                "Pa\u00edses como Chile, Col\u00f4mbia, Portugal e EUA permitem candidaturas independentes e a democracia funciona"
            ),
            contrasArgs = listOf(
                "Sem partido, o candidato eleito fica isolado no Legislativo. N\u00e3o tem bancada, n\u00e3o negocia, n\u00e3o aprova nada",
                "Partidos s\u00e3o fundamentais para a democracia representativa. Enfraquecer partidos pode enfraquecer a democracia",
                "Candidatos independentes com muito dinheiro teriam vantagem enorme, porque n\u00e3o teriam fundo eleitoral mas poderiam se autofinanciar",
                "A coleta de assinaturas pode ser burlada ou dificultar a candidatura de gente honesta que n\u00e3o tem estrutura para coletar milhares de apoios"
            ),
            comparacaoInternacional = "No Chile, candidaturas independentes s\u00e3o permitidas e representam uma parcela significativa do Congresso. Na Col\u00f4mbia, candidatos independentes podem se apresentar com coleta de assinaturas. Nos EUA, candidatos independentes podem concorrer em todos os n\u00edveis, embora raramente ganhem. Em Portugal, candidatos independentes podem concorrer a presidente e a autarquias locais. Na Fran\u00e7a, qualquer cidad\u00e3o pode se candidatar a presidente sem filia\u00e7\u00e3o partid\u00e1ria. O modelo brasileiro de obrigatoriedade de filia\u00e7\u00e3o \u00e9 minoria no mundo.",
            apoiosECidadania = 89310,
            statusOriginal = "morreu",
            categoria = "direitos",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=101456"
        ),

        IdeiaCidadao(
            id = "dir_002",
            titulo = "Recall: poder de destituir pol\u00edtico antes do mandato acabar",
            resumo = "Criar o mecanismo de recall no Brasil: se um pol\u00edtico eleito n\u00e3o cumpre o que prometeu ou faz um p\u00e9ssimo trabalho, os eleitores podem organizar uma vota\u00e7\u00e3o para tir\u00e1-lo do cargo antes do fim do mandato.",
            explicacao = "Hoje no Brasil, se voc\u00ea elege um deputado, prefeito ou governador e ele faz um trabalho terr\u00edvel, voc\u00ea n\u00e3o pode fazer nada por 4 anos. Tem que esperar a pr\u00f3xima elei\u00e7\u00e3o e torcer para que o povo n\u00e3o esque\u00e7a.\n\nO recall (ou revoga\u00e7\u00e3o de mandato) \u00e9 um mecanismo que existe em v\u00e1rios pa\u00edses e permite que os pr\u00f3prios eleitores pe\u00e7am a sa\u00edda de um pol\u00edtico antes do fim do mandato. Funciona assim: se um percentual m\u00ednimo do eleitorado (por exemplo, 20% dos eleitores daquela circunscri\u00e7\u00e3o) assinar um pedido de recall, \u00e9 convocada uma vota\u00e7\u00e3o especial. Se a maioria votar pela sa\u00edda, o pol\u00edtico \u00e9 destitu\u00eddo e novas elei\u00e7\u00f5es s\u00e3o convocadas.\n\nN\u00e3o \u00e9 impeachment. Impeachment depende do Congresso e envolve processo jur\u00eddico. O recall \u00e9 uma decis\u00e3o direta do povo: votamos em voc\u00ea, n\u00e3o gostamos do que est\u00e1 fazendo, queremos outro.\n\nA proposta inclui salvaguardas para evitar abuso: o recall s\u00f3 pode ser solicitado ap\u00f3s o primeiro ano de mandato; s\u00f3 pode acontecer uma vez por mandato; e a maioria necess\u00e1ria para destituir \u00e9 de 60%, n\u00e3o simples maioria.\n\nCr\u00edticos dizem que o recall pode gerar instabilidade, com pol\u00edticos sendo amea\u00e7ados de recall a cada decis\u00e3o impopular. Mas defensores argumentam que justamente esse medo de perder o cargo for\u00e7aria os pol\u00edticos a cumprirem suas promessas.",
            comoTeAfeta = "Quantas vezes voc\u00ea votou em algu\u00e9m que prometeu uma coisa e fez o oposto? Com o recall, voc\u00ea teria o poder real de cobrar. Se o prefeito prometeu asfaltar sua rua e gastou o dinheiro em outra coisa, voc\u00ea e seus vizinhos podem iniciar um processo para tir\u00e1-lo do cargo. \u00c9 democracia de verdade: o povo d\u00e1 e o povo tira.",
            prosArgs = listOf(
                "D\u00e1 poder real ao eleitor. N\u00e3o \u00e9 s\u00f3 votar de 4 em 4 anos e torcer. \u00c9 poder cobrar e punir em tempo real",
                "For\u00e7aria pol\u00edticos a cumprir promessas, porque saberiam que podem perder o cargo a qualquer momento",
                "Funciona em v\u00e1rios pa\u00edses e estados dos EUA h\u00e1 mais de 100 anos sem causar instabilidade",
                "As salvaguardas (20% de assinaturas, 60% de votos, prazo m\u00ednimo) evitam uso fr\u00edvolo do mecanismo"
            ),
            contrasArgs = listOf(
                "Pode gerar instabilidade pol\u00edtica. Prefeitos e governadores viveriam sob amea\u00e7a constante, dificultando governar",
                "Decis\u00f5es impopulares mas necess\u00e1rias (aumento de imposto, corte de gastos) poderiam gerar recall injusto",
                "O custo de organizar vota\u00e7\u00f5es especiais a cada recall seria alto, especialmente em cidades grandes",
                "Grupos com muito dinheiro e organiza\u00e7\u00e3o (como igrejas ou sindicatos) poderiam manipular o processo para derrubar pol\u00edticos que contrariam seus interesses"
            ),
            comparacaoInternacional = "Nos EUA, 19 estados permitem recall de governador e outros cargos. Em 2021, houve um recall do governador da Calif\u00f3rnia, Gavin Newsom, que sobreviveu com 62% dos votos. Na Venezuela, o mecanismo existe e foi usado contra Ch\u00e1vez em 2004 (que sobreviveu). Na Su\u00ed\u00e7a, v\u00e1rios cant\u00f5es t\u00eam recall. No Peru, recall de prefeitos \u00e9 relativamente comum. Na Col\u00f4mbia e no Equador tamb\u00e9m existe previs\u00e3o legal. O Brasil seria mais um pa\u00eds a adotar um mecanismo j\u00e1 consolidado em democracias ao redor do mundo.",
            apoiosECidadania = 41670,
            statusOriginal = "morreu",
            categoria = "direitos",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=127893"
        ),

        IdeiaCidadao(
            id = "dir_003",
            titulo = "Voto distrital: saber exatamente quem te representa",
            resumo = "Mudar o sistema eleitoral para que cada deputado represente uma regi\u00e3o espec\u00edfica (distrito). Hoje, com o voto proporcional, voc\u00ea n\u00e3o faz a menor ideia de quem \u00e9 seu deputado e ele n\u00e3o tem compromisso nenhum com sua regi\u00e3o.",
            explicacao = "No sistema atual (proporcional de lista aberta), os deputados s\u00e3o eleitos por estado inteiro. Em S\u00e3o Paulo, por exemplo, voc\u00ea escolhe entre mais de 1.000 candidatos e o eleito representa os 46 milh\u00f5es de paulistas indistintamente. Na pr\u00e1tica, n\u00e3o representa ningu\u00e9m especificamente.\n\nVoc\u00ea sabe quem \u00e9 seu deputado federal? Provavelmente n\u00e3o. Pesquisa mostra que 93% dos brasileiros n\u00e3o lembram em quem votaram para deputado na \u00faltima elei\u00e7\u00e3o. Isso n\u00e3o \u00e9 acidente: \u00e9 consequ\u00eancia do sistema.\n\nNo voto distrital, o estado seria dividido em distritos (regi\u00f5es menores). Cada distrito elege um deputado. Voc\u00ea sabe exatamente quem \u00e9 seu representante e ele sabe que precisa atender SUA regi\u00e3o para ser reeleito.\n\nA vers\u00e3o mais discutida \u00e9 o sistema misto (como na Alemanha): metade dos deputados \u00e9 eleita por voto distrital e metade por voto proporcional. Isso combina a representa\u00e7\u00e3o local com a diversidade ideol\u00f3gica.\n\nO voto distrital puro tem riscos, como o gerrymandering (manipula\u00e7\u00e3o dos limites do distrito para favorecer um partido). Mas o sistema misto mitiga esses riscos e \u00e9 considerado um dos mais justos do mundo.\n\nAl\u00e9m de melhorar a representatividade, o sistema reduz a influ\u00eancia do dinheiro: em vez de fazer campanha no estado inteiro (o que custa milh\u00f5es), o candidato faz campanha no seu distrito (o que custa muito menos). Isso abre espa\u00e7o para candidatos menos ricos e mais conectados com a comunidade.",
            comoTeAfeta = "Hoje seu deputado pode ignorar completamente sua cidade porque n\u00e3o depende dela pra se reeleger. Com voto distrital, ele precisa da sua regi\u00e3o. Se o bairro tem problema, voc\u00ea sabe a quem cobrar. Se ele n\u00e3o resolver, voc\u00ea vota em outro na pr\u00f3xima. \u00c9 representa\u00e7\u00e3o de verdade, com nome e endere\u00e7o.",
            prosArgs = listOf(
                "93% dos brasileiros n\u00e3o sabem quem \u00e9 seu deputado. O voto distrital resolve isso ao dar um representante espec\u00edfico a cada regi\u00e3o",
                "O deputado passa a ter compromisso direto com a regi\u00e3o que o elegeu, n\u00e3o com o partido ou com quem financiou a campanha",
                "Barateia a campanha eleitoral: fazer campanha num distrito \u00e9 muito mais barato do que no estado inteiro",
                "O sistema misto (metade distrital, metade proporcional) \u00e9 usado na Alemanha, Jap\u00e3o e Nova Zel\u00e2ndia com sucesso"
            ),
            contrasArgs = listOf(
                "O voto distrital puro pode prejudicar minorias e partidos menores, que se diluem nos distritos e perdem representa\u00e7\u00e3o",
                "Gerrymandering: quem desenha os distritos tem poder enorme. No Brasil, esse desenho provavelmente seria manipulado politicamente",
                "Pode fortalecer coronelismo local: em regi\u00f5es onde um pol\u00edtico j\u00e1 domina, o distrito viraria feudo particular",
                "O sistema proporcional, apesar dos problemas, garante diversidade no Congresso. O distrital tende a beneficiar s\u00f3 dois ou tr\u00eas grandes partidos"
            ),
            comparacaoInternacional = "A Alemanha usa o sistema misto desde 1949 e \u00e9 considerada refer\u00eancia. O Jap\u00e3o adotou o sistema misto em 1994 e viu melhora na representatividade. A Nova Zel\u00e2ndia mudou do distrital puro para o misto em 1996 e a satisfa\u00e7\u00e3o popular com o sistema eleitoral aumentou. Os EUA e o Reino Unido usam distrital puro, mas enfrentam problemas graves de gerrymandering (EUA) e sub-representa\u00e7\u00e3o (UK). O Chile adotou sistema proporcional em 2015 para corrigir problemas do antigo sistema binominal. N\u00e3o existe sistema perfeito, mas o misto parece ser o menos imperfeito.",
            apoiosECidadania = 36480,
            statusOriginal = "morreu",
            categoria = "direitos",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=115672"
        ),

        IdeiaCidadao(
            id = "dir_004",
            titulo = "Or\u00e7amento participativo obrigat\u00f3rio",
            resumo = "Obrigar todas as cidades com mais de 50 mil habitantes a destinar pelo menos 5% do or\u00e7amento para decis\u00e3o direta da popula\u00e7\u00e3o. Os moradores votam em assembleias ou plataformas digitais para decidir onde o dinheiro vai ser gasto.",
            explicacao = "Porto Alegre inventou o or\u00e7amento participativo em 1989. A ideia \u00e9 simples: parte do dinheiro p\u00fablico \u00e9 decidida diretamente pela popula\u00e7\u00e3o, n\u00e3o pelos pol\u00edticos. Os moradores de cada bairro se re\u00fanem, discutem as prioridades (asfalto? posto de sa\u00fade? creche?) e votam. O governo \u00e9 obrigado a executar.\n\nA experi\u00eancia de Porto Alegre foi t\u00e3o bem-sucedida que virou refer\u00eancia mundial. Mais de 3.000 cidades no mundo adotaram alguma forma de or\u00e7amento participativo inspirada no modelo brasileiro. Paris, Nova York, Lisboa, Barcelona, todas copiaram a ideia.\n\nIronicamente, no pr\u00f3prio Brasil, o or\u00e7amento participativo perdeu for\u00e7a. Poucas cidades ainda praticam de forma s\u00e9ria. Quando um prefeito novo entra, geralmente abandona o programa do antecessor. Sem obriga\u00e7\u00e3o legal, o or\u00e7amento participativo vira capricho de gest\u00e3o.\n\nA proposta quer tornar obrigat\u00f3rio por lei federal. Toda cidade com mais de 50 mil habitantes teria que destinar no m\u00ednimo 5% do or\u00e7amento de investimento para decis\u00e3o popular. As assembleias seriam presenciais e tamb\u00e9m digitais (para incluir quem n\u00e3o pode ir pessoalmente). O resultado seria vinculante: o prefeito \u00e9 obrigado a executar.\n\nAl\u00e9m de melhorar a aloca\u00e7\u00e3o de recursos (afinal, quem sabe melhor o que o bairro precisa do que quem mora l\u00e1?), o or\u00e7amento participativo aumenta o engajamento c\u00edvico e reduz a corrup\u00e7\u00e3o, porque a popula\u00e7\u00e3o fiscaliza de perto o dinheiro que ela mesma decidiu gastar.",
            comoTeAfeta = "Imagina poder votar diretamente para decidir se o dinheiro da sua cidade vai para asfaltar sua rua, construir uma creche no seu bairro ou reformar o posto de sa\u00fade. Com o or\u00e7amento participativo obrigat\u00f3rio, voc\u00ea teria esse poder. N\u00e3o \u00e9 escolher pol\u00edtico a cada 4 anos. \u00c9 decidir diretamente, todo ano, onde o seu dinheiro vai.",
            prosArgs = listOf(
                "Porto Alegre provou que funciona. Mais de 3.000 cidades no mundo copiaram a ideia. Agora \u00e9 hora de tornar obrigat\u00f3rio no Brasil",
                "Quem mora no bairro sabe melhor do que o prefeito o que o bairro precisa. Decis\u00e3o local \u00e9 decis\u00e3o melhor",
                "Reduz corrup\u00e7\u00e3o: \u00e9 muito mais dif\u00edcil desviar dinheiro quando a popula\u00e7\u00e3o est\u00e1 de olho porque ela mesma decidiu o gasto",
                "Aumenta o engajamento c\u00edvico. Pesquisas mostram que moradores de cidades com OP participam mais da vida p\u00fablica em geral"
            ),
            contrasArgs = listOf(
                "5% do or\u00e7amento pode parecer pouco, mas \u00e9 um valor significativo que pode comprometer planejamento de longo prazo se a popula\u00e7\u00e3o priorizar demandas imediatas",
                "Assembleias tendem a ser dominadas por grupos organizados (associa\u00e7\u00f5es, igrejas, sindicatos), n\u00e3o representando a maioria silenciosa",
                "Obrigar por lei federal pode ferir a autonomia municipal. Cada cidade deveria decidir se quer ou n\u00e3o adotar",
                "A participa\u00e7\u00e3o popular em assembleias \u00e9 tipicamente baixa (5% a 10% da popula\u00e7\u00e3o). Decis\u00f5es acabam sendo tomadas por uma minoria engajada"
            ),
            comparacaoInternacional = "Paris destina mais de 100 milh\u00f5es de euros por ano ao or\u00e7amento participativo. Nova York tem o programa PBNYC desde 2011. Lisboa destina 2,5% do or\u00e7amento de investimento desde 2008. Barcelona, Madri, Seul e dezenas de capitais mundiais t\u00eam programas ativos. A Pol\u00f4nia tornou o OP obrigat\u00f3rio para cidades com mais de 20 mil habitantes em 2018. O Peru obriga OP em todos os n\u00edveis de governo desde 2003. O Brasil inventou a pr\u00e1tica, mas est\u00e1 ficando para tr\u00e1s.",
            apoiosECidadania = 15430,
            statusOriginal = "morreu",
            categoria = "direitos",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=142678"
        ),

        IdeiaCidadao(
            id = "dir_005",
            titulo = "Fim da reelei\u00e7\u00e3o para cargos executivos",
            resumo = "Proibir a reelei\u00e7\u00e3o de prefeitos, governadores e presidente. A reelei\u00e7\u00e3o foi criada em 1997 e desde ent\u00e3o virou uma m\u00e1quina de usar a estrutura do cargo para se perpetuar no poder. A proposta \u00e9 mandato \u00fanico de 5 anos.",
            explicacao = "A reelei\u00e7\u00e3o para cargos executivos foi aprovada no Brasil em 1997, durante o governo FHC, que foi o primeiro presidente a se beneficiar da mudan\u00e7a. Desde ent\u00e3o, o debate sobre se ela \u00e9 boa ou ruim nunca parou.\n\nO argumento original era que 4 anos \u00e9 pouco para implementar um projeto de governo e que o eleitor deveria ter o direito de escolher continuar com o mesmo governante. Na teoria, faz sentido.\n\nNa pr\u00e1tica, a reelei\u00e7\u00e3o virou uma m\u00e1quina de perpetua\u00e7\u00e3o no poder. O governante em exerc\u00edcio usa toda a estrutura do Estado para se reeleger: inaugura obras \u00e0s v\u00e9speras da elei\u00e7\u00e3o, distribui cargos em troca de apoio pol\u00edtico, usa a m\u00e1quina de comunica\u00e7\u00e3o do governo para se promover. O candidato que tenta competir contra o incumbente parte em desvantagem brutal.\n\nDados mostram que desde 1997, mais de 70% dos prefeitos que tentaram a reelei\u00e7\u00e3o conseguiram. Para governadores, a taxa \u00e9 parecida. Isso n\u00e3o \u00e9 porque s\u00e3o bons gestores. \u00c9 porque a m\u00e1quina p\u00fablica \u00e9 uma ferramenta de campanha podeross\u00edssima.\n\nA proposta quer acabar com a reelei\u00e7\u00e3o e, para compensar, aumentar o mandato de 4 para 5 anos. Isso daria mais tempo para governar sem a preocupa\u00e7\u00e3o de se reeleger. O governante n\u00e3o precisaria ficar fazendo campanha desde o segundo ano de mandato.\n\nAlguns pa\u00edses da Am\u00e9rica Latina (M\u00e9xico, Col\u00f4mbia, Paraguai) pro\u00edbem reelei\u00e7\u00e3o imediata ou qualquer reelei\u00e7\u00e3o, e a democracia funciona.",
            comoTeAfeta = "Se o prefeito da sua cidade est\u00e1 mais preocupado em se reeleger do que em governar, voc\u00ea perde. Obras de \u00faltima hora que n\u00e3o servem pra nada, contrata\u00e7\u00e3o de apadrinhados, uso da prefeitura como comit\u00ea de campanha. Sem reelei\u00e7\u00e3o, o prefeito governa de verdade nos 5 anos porque sabe que n\u00e3o tem segundo mandato pra se preocupar. Foca no trabalho, n\u00e3o na campanha.",
            prosArgs = listOf(
                "Desde 1997, a reelei\u00e7\u00e3o virou sin\u00f4nimo de uso da m\u00e1quina p\u00fablica para se perpetuar no poder",
                "Mandato \u00fanico de 5 anos d\u00e1 tempo de governar sem a press\u00e3o de fazer campanha desde o segundo ano",
                "Promove renova\u00e7\u00e3o pol\u00edtica: mais gente tem chance de governar quando n\u00e3o existe incumbente tentando ficar",
                "V\u00e1rios pa\u00edses democr\u00e1ticos pro\u00edbem reelei\u00e7\u00e3o (M\u00e9xico, Col\u00f4mbia, Coreia do Sul) e funcionam bem"
            ),
            contrasArgs = listOf(
                "Tirar o direito do eleitor de reeleger um bom governante \u00e9 antidemocrr\u00e1tico. Se o povo quer, por que proibir?",
                "5 anos com um governante ruim \u00e9 pior do que 4 com a possibilidade de troca na reelei\u00e7\u00e3o",
                "O problema n\u00e3o \u00e9 a reelei\u00e7\u00e3o em si, mas a falta de fiscaliza\u00e7\u00e3o do uso da m\u00e1quina p\u00fablica. Resolver isso seria mais eficaz",
                "Sem possibilidade de reelei\u00e7\u00e3o, o governante pode n\u00e3o ter incentivo para fazer um bom trabalho, j\u00e1 que n\u00e3o precisa do voto novamente"
            ),
            comparacaoInternacional = "O M\u00e9xico pro\u00edbe reelei\u00e7\u00e3o para presidente desde 1917 e \u00e9 uma das democracias mais est\u00e1veis da Am\u00e9rica Latina. A Col\u00f4mbia proibiu reelei\u00e7\u00e3o presidencial em 2015 ap\u00f3s a experi\u00eancia conturbada da reelei\u00e7\u00e3o de Uribe. A Coreia do Sul tem mandato \u00fanico de 5 anos para presidente e funciona bem. O Chile pro\u00edbe reelei\u00e7\u00e3o imediata. Na Fran\u00e7a, o presidente pode se reeleger uma vez (mandato de 5 anos). Nos EUA, presidente pode se reeleger uma vez (mandato de 4 anos). A tend\u00eancia na Am\u00e9rica Latina \u00e9 de restringir a reelei\u00e7\u00e3o, n\u00e3o ampliar.",
            apoiosECidadania = 72340,
            statusOriginal = "morreu",
            categoria = "direitos",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=136721"
        ),

        IdeiaCidadao(
            id = "dir_006",
            titulo = "Redu\u00e7\u00e3o do n\u00famero de minist\u00e9rios",
            resumo = "Reduzir os minist\u00e9rios para um n\u00famero m\u00e1ximo fixo (entre 15 e 20), acabando com a pr\u00e1tica de criar minist\u00e9rios para acomodar aliados pol\u00edticos. Hoje o n\u00famero varia conforme o governo precisa de apoio no Congresso.",
            explicacao = "O n\u00famero de minist\u00e9rios no Brasil varia conforme a conveni\u00eancia pol\u00edtica. No governo Dilma, chegou a 39. Temer cortou para 29. Bolsonaro come\u00e7ou com 22 e terminou com 23. Lula 3 come\u00e7ou com 37. Cada novo minist\u00e9rio vem com estrutura, funcion\u00e1rios, cargos de confian\u00e7a e or\u00e7amento.\n\nO problema \u00e9 que muitos minist\u00e9rios n\u00e3o existem por necessidade t\u00e9cnica, mas por necessidade pol\u00edtica. O presidente precisa de votos no Congresso, ent\u00e3o cria um minist\u00e9rio e entrega para um partido aliado. O partido indica quem vai ocupar o cargo e os cargos de confian\u00e7a abaixo dele. \u00c9 o famoso \"toma l\u00e1 d\u00e1 c\u00e1\".\n\nO resultado \u00e9 uma m\u00e1quina p\u00fablica inchada, com sobreposi\u00e7\u00e3o de fun\u00e7\u00f5es, burocracia desnecess\u00e1ria e gasto p\u00fablico enorme. Minist\u00e9rios que fazem praticamente a mesma coisa existem lado a lado. Secretarias especiais com status de minist\u00e9rio proliferam sem crit\u00e9rio.\n\nA proposta quer fixar na Constitui\u00e7\u00e3o um n\u00famero m\u00e1ximo de minist\u00e9rios (entre 15 e 20), definidos por \u00e1reas essenciais (Economia, Sa\u00fade, Educa\u00e7\u00e3o, Defesa, Justi\u00e7a, Infraestrutura, etc.). O presidente teria flexibilidade dentro desse limite, mas n\u00e3o poderia ultrapass\u00e1-lo.\n\nIsso n\u00e3o resolve o presidencialismo de coaliz\u00e3o (o presidente ainda precisaria negociar com o Congresso), mas limita o custo dessa negocia\u00e7\u00e3o. Se s\u00f3 tem 15 minist\u00e9rios, o pre\u00e7o do apoio pol\u00edtico n\u00e3o pode ser um minist\u00e9rio novo.\n\nAl\u00e9m disso, a proposta exige que todo ministro tenha qualifica\u00e7\u00e3o t\u00e9cnica m\u00ednima comprovada na \u00e1rea, acabando com a pr\u00e1tica de colocar um pol\u00edtico de carreira sem nenhum conhecimento para comandar uma pasta t\u00e9cnica.",
            comoTeAfeta = "Cada minist\u00e9rio novo significa mais gasto p\u00fablico com estrutura, cargos e burocracia. Dinheiro que poderia estar na sa\u00fade, na educa\u00e7\u00e3o ou na seguran\u00e7a vai pra manter uma estrutura criada s\u00f3 pra acomodar aliado pol\u00edtico. Menos minist\u00e9rios significa governo mais enxuto, menos gasto e potencialmente mais efici\u00eancia. Tamb\u00e9m significa que quem vai comandar a pasta precisa entender do assunto, n\u00e3o apenas do jogo pol\u00edtico.",
            prosArgs = listOf(
                "Muitos minist\u00e9rios existem por conveni\u00eancia pol\u00edtica, n\u00e3o por necessidade. Reduzir \u00e9 cortar gordura",
                "Economia de bilh\u00f5es por ano com estrutura, cargos de confian\u00e7a e burocracia desnecess\u00e1ria",
                "Exigir qualifica\u00e7\u00e3o t\u00e9cnica melhora a gest\u00e3o p\u00fablica e acaba com a farra de indica\u00e7\u00e3o pol\u00edtica pura",
                "Pa\u00edses com governos eficientes (Alemanha, Canad\u00e1, Jap\u00e3o) t\u00eam entre 14 e 20 minist\u00e9rios. N\u00e3o precisa de 37"
            ),
            contrasArgs = listOf(
                "No presidencialismo de coaliz\u00e3o, o presidente precisa negociar com o Congresso. Se n\u00e3o pode oferecer minist\u00e9rios, vai ter que oferecer outra coisa, talvez pior (emendas, cargos)",
                "Juntar minist\u00e9rios pode reduzir a aten\u00e7\u00e3o dada a \u00e1reas espec\u00edficas. Um megaminist\u00e9rio de 'Desenvolvimento Social' pode negligenciar pautas de minorias",
                "Fixar n\u00famero na Constitui\u00e7\u00e3o tira flexibilidade. Uma pandemia ou crise pode exigir estrutura tempor\u00e1ria que o limite n\u00e3o permite",
                "Qualifica\u00e7\u00e3o t\u00e9cnica \u00e9 subjetiva. Quem define se algu\u00e9m \u00e9 'qualificado'? Pode virar mais um crit\u00e9rio para barrar advers\u00e1rios pol\u00edticos"
            ),
            comparacaoInternacional = "A Alemanha tem 15 minist\u00e9rios. O Jap\u00e3o tem 15. O Canad\u00e1 tem 20. Os EUA t\u00eam 15 departamentos com status ministerial. A Fran\u00e7a tem entre 15 e 20, dependendo do governo. O Reino Unido tem cerca de 20 secretarias de Estado. Nenhum pa\u00eds desenvolvido tem 37 minist\u00e9rios. Na Am\u00e9rica Latina, a Argentina de Milei reduziu de 18 para 9 em 2024, embora com pol\u00eamica. O Brasil, com seus 37, \u00e9 um ponto fora da curva.",
            apoiosECidadania = 48920,
            statusOriginal = "morreu",
            categoria = "direitos",
            linkECidadania = "https://www12.senado.leg.br/ecidadania/visualizacaoideia?id=155234"
        )
    )
}
