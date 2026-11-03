package com.trouxaapp.app.ui.screens.deputado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trouxaapp.app.data.local.DeputadoDao
import com.trouxaapp.app.data.local.DeputadoEntity
import com.trouxaapp.app.data.local.DespesaDao
import com.trouxaapp.app.data.local.DespesaEntity
import com.trouxaapp.app.data.remote.CamaraApiService
import com.trouxaapp.app.util.UserPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Calendar
import javax.inject.Inject

// Everything rendered from these states is either raw Camara API data or a
// sum over it. No estimates, no presence scores, no per-deputy cost claims.

data class DeputadosListState(
    val selectedUf: String = "",
    val deputados: List<DeputadoEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class DeputadoProfileState(
    val deputado: DeputadoEntity? = null,
    // Official contact exactly as the detail endpoint returns it; blank means
    // the API did not return one and the UI says so instead of guessing.
    val email: String = "",
    val telefoneGabinete: String = "",
    val despesas: List<DespesaEntity> = emptyList(),
    // Sum of the most recent (ano, mes) present in the data, nothing else
    val totalGastoUltimoMes: Double = 0.0,
    val mesMaisRecente: Int = 0,
    val anoMaisRecente: Int = 0,
    val lastUpdate: Long = 0L,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DeputadoViewModel @Inject constructor(
    private val camaraApi: CamaraApiService,
    private val deputadoDao: DeputadoDao,
    private val despesaDao: DespesaDao,
    private val userPrefs: UserPrefs
) : ViewModel() {

    companion object {
        private const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L
    }

    // The Camara API throws an occasional 504 or stalls on a request that
    // answers in 0.2 s a moment later; one retry hides most of those
    private suspend fun <T> comRetentativa(chamada: suspend () -> T): T =
        try {
            chamada()
        } catch (e: Exception) {
            if (e is CancellationException || (e is HttpException && e.code() < 500)) throw e
            delay(1500)
            chamada()
        }

    private val _listState = MutableStateFlow(DeputadosListState())
    val listState: StateFlow<DeputadosListState> = _listState.asStateFlow()

    private val _profileState = MutableStateFlow(DeputadoProfileState())
    val profileState: StateFlow<DeputadoProfileState> = _profileState.asStateFlow()

    private var profileId: Int = 0

    init {
        val savedUf = userPrefs.userUf
        if (savedUf.isNotBlank()) {
            _listState.value = DeputadosListState(selectedUf = savedUf)
            loadDeputados(savedUf)
        }
    }

    fun setUf(uf: String) {
        if (uf.isBlank()) return
        userPrefs.userUf = uf
        _listState.value = DeputadosListState(selectedUf = uf, isLoading = true)
        loadDeputados(uf)
    }

    fun refreshList() {
        val uf = _listState.value.selectedUf
        if (uf.isNotBlank()) loadDeputados(uf, force = true)
    }

    fun retryList() = refreshList()

    private fun loadDeputados(uf: String, force: Boolean = false) {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)

            val cached = runCatching { deputadoDao.getByUf(uf) }.getOrDefault(emptyList())
            if (cached.isNotEmpty()) {
                _listState.value = _listState.value.copy(deputados = cached)
            }

            val lastUpdate = runCatching { deputadoDao.getLastUpdateForUf(uf) }.getOrNull() ?: 0L
            val cacheFresh = lastUpdate > 0 &&
                System.currentTimeMillis() - lastUpdate < CACHE_TTL_MS
            if (cacheFresh && cached.isNotEmpty() && !force) {
                _listState.value = _listState.value.copy(isLoading = false)
                return@launch
            }

            try {
                val now = System.currentTimeMillis()
                val entities = comRetentativa { camaraApi.getDeputadosByUf(uf) }.dados.orEmpty()
                    .filter { it.id > 0 }
                    .map {
                        DeputadoEntity(
                            id = it.id,
                            nome = it.nome ?: "",
                            siglaPartido = it.siglaPartido ?: "",
                            siglaUf = it.siglaUf ?: uf,
                            urlFoto = it.urlFoto ?: "",
                            email = it.email ?: "",
                            atualizadoEm = now
                        )
                    }
                if (entities.isNotEmpty()) {
                    deputadoDao.deleteByUf(uf)
                    deputadoDao.insertAll(entities)
                }
                _listState.value = _listState.value.copy(
                    deputados = entities,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                // Stale cache beats an empty screen; only surface the error
                // when there is nothing at all to show.
                _listState.value = _listState.value.copy(
                    isLoading = false,
                    error = if (cached.isNotEmpty()) null else mapError(e)
                )
            }
        }
    }

    fun loadProfile(deputadoId: Int) {
        if (deputadoId <= 0) return
        profileId = deputadoId
        viewModelScope.launch {
            _profileState.value = DeputadoProfileState(isLoading = true)

            val cachedDep = runCatching { deputadoDao.getById(deputadoId) }.getOrNull()
            if (cachedDep != null) {
                _profileState.value = _profileState.value.copy(deputado = cachedDep)
            }

            // Detail endpoint is the only source for contact data. If it
            // fails the fields stay blank and the UI says there is no data.
            var detailError: Exception? = null
            try {
                val status = comRetentativa { camaraApi.getDeputado(deputadoId) }.dados?.ultimoStatus
                val header = cachedDep ?: DeputadoEntity(
                    id = deputadoId,
                    nome = status?.nome ?: "",
                    siglaPartido = status?.siglaPartido ?: "",
                    siglaUf = status?.siglaUf ?: "",
                    urlFoto = status?.urlFoto ?: "",
                    email = status?.email ?: "",
                    atualizadoEm = System.currentTimeMillis()
                )
                _profileState.value = _profileState.value.copy(
                    deputado = header,
                    email = status?.email ?: "",
                    telefoneGabinete = status?.gabinete?.telefone ?: ""
                )
            } catch (e: Exception) {
                detailError = e
            }

            try {
                val cachedTs = runCatching { despesaDao.getLastUpdate(deputadoId) }.getOrNull() ?: 0L
                val cacheFresh = cachedTs > 0 &&
                    System.currentTimeMillis() - cachedTs < CACHE_TTL_MS

                val despesas: List<DespesaEntity>
                val updateTs: Long
                if (cacheFresh) {
                    despesas = despesaDao.getByDeputado(deputadoId)
                    updateTs = cachedTs
                } else {
                    val anoAtual = Calendar.getInstance().get(Calendar.YEAR)
                    val now = System.currentTimeMillis()
                    val fetched = comRetentativa {
                        camaraApi.getDespesas(id = deputadoId, ano = listOf(anoAtual - 1, anoAtual))
                    }.dados.orEmpty().map { d ->
                        DespesaEntity(
                            deputadoId = deputadoId,
                            ano = d.ano,
                            mes = d.mes,
                            tipoDespesa = d.tipoDespesa ?: "",
                            valor = d.valorLiquido,
                            fornecedor = d.nomeFornecedor ?: "",
                            atualizadoEm = now
                        )
                    }
                    // The API sometimes answers 200 with an empty list for every
                    // deputy (seen 03/11/2026); never let that wipe good data
                    if (fetched.isNotEmpty()) {
                        despesaDao.deleteByDeputado(deputadoId)
                        despesaDao.insertAll(fetched)
                    }
                    despesas = despesaDao.getByDeputado(deputadoId)
                    updateTs = if (fetched.isNotEmpty()) now else cachedTs
                }

                publishDespesas(despesas, updateTs, error = null)
            } catch (e: Exception) {
                // Network failed: fall back to whatever Room has, even stale
                val fallback = runCatching { despesaDao.getByDeputado(deputadoId) }
                    .getOrDefault(emptyList())
                val fallbackTs = runCatching { despesaDao.getLastUpdate(deputadoId) }
                    .getOrNull() ?: 0L
                val error = when {
                    fallback.isNotEmpty() -> null
                    _profileState.value.deputado == null -> mapError(detailError ?: e)
                    else -> mapError(e)
                }
                publishDespesas(fallback, fallbackTs, error)
            }
        }
    }

    fun retryProfile() {
        if (profileId > 0) loadProfile(profileId)
    }

    private fun publishDespesas(despesas: List<DespesaEntity>, updateTs: Long, error: String?) {
        val maisRecente = despesas.maxWithOrNull(compareBy({ it.ano }, { it.mes }))
        val totalUltimoMes = if (maisRecente != null) {
            despesas
                .filter { it.ano == maisRecente.ano && it.mes == maisRecente.mes }
                .sumOf { it.valor }
        } else 0.0
        _profileState.value = _profileState.value.copy(
            despesas = despesas,
            totalGastoUltimoMes = totalUltimoMes,
            mesMaisRecente = maisRecente?.mes ?: 0,
            anoMaisRecente = maisRecente?.ano ?: 0,
            lastUpdate = updateTs,
            isLoading = false,
            error = error
        )
    }

    private fun mapError(e: Exception): String = when (e) {
        is UnknownHostException -> "Sem conexão. Verifique sua internet e tente de novo."
        is SocketTimeoutException -> "A conexão demorou demais. Tente novamente."
        is HttpException -> when {
            e.code() == 404 -> "Registro não encontrado na API da Câmara."
            e.code() >= 500 -> "A API da Câmara não respondeu. Tente mais tarde."
            else -> "Não foi possível carregar os dados agora."
        }
        else -> "Não foi possível carregar os dados agora."
    }
}
