package com.trouxaapp.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import android.content.Context
import com.trouxaapp.app.data.local.SyncLogDao
import com.trouxaapp.app.data.local.SyncLogEntity
import com.trouxaapp.app.data.local.TrouxaDatabase
import com.trouxaapp.app.util.UserPrefs
import com.trouxaapp.app.worker.FeedSyncWorker
import com.trouxaapp.app.worker.IndicadorSyncWorker
import com.trouxaapp.app.worker.LawSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userUf: String = "",
    val notifEventos: Boolean = false,
    val notifResumoDiario: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: UserPrefs,
    private val db: TrouxaDatabase,
    syncLogDao: SyncLogDao
) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsUiState(
            userUf = prefs.userUf,
            notifEventos = prefs.notifEventos,
            notifResumoDiario = prefs.notifResumoDiario
        )
    )
    val state: StateFlow<SettingsUiState> = _state

    // One row per data source (latest run of each worker). The old raw list
    // of the last 12 rows filled up with retry entries of a single worker.
    val syncLogs: StateFlow<List<SyncLogEntity>> = syncLogDao.observeLatestPerWorker()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setUf(uf: String) {
        prefs.userUf = uf
        _state.value = _state.value.copy(userUf = uf)
    }

    fun setNotifEventos(on: Boolean) {
        prefs.notifEventos = on
        _state.value = _state.value.copy(notifEventos = on)
    }

    fun setNotifResumoDiario(on: Boolean) {
        prefs.notifResumoDiario = on
        _state.value = _state.value.copy(notifResumoDiario = on)
    }

    fun forceSyncNow() {
        val wm = WorkManager.getInstance(context)
        wm.enqueueUniqueWork(
            "lei_sync_manual", ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<LawSyncWorker>().build()
        )
        wm.enqueueUniqueWork(
            "indicador_sync_manual", ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<IndicadorSyncWorker>().build()
        )
        wm.enqueueUniqueWork(
            "feed_sync_manual", ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<FeedSyncWorker>().build()
        )
    }

    /**
     * Wipes every Room table and resets the on-device preferences.
     * onboardingDone is kept on purpose: clearing data should not
     * throw the user back into the first-run flow.
     */
    fun clearLocalData() {
        viewModelScope.launch(Dispatchers.IO) {
            db.clearAllTables()
            prefs.userUf = ""
            prefs.notifEventos = false
            prefs.notifResumoDiario = false
            prefs.lastSeenLeiId = 0
            prefs.lastSeenSelic = 0f
            prefs.lastSeenDolar = 0f
            prefs.lastEventNotifTimestamp = 0L
            prefs.lastDigestDay = 0
            _state.value = SettingsUiState()
        }
    }
}
