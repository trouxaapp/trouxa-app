package com.trouxaapp.app.ui.screens.leis

import androidx.lifecycle.ViewModel
import com.trouxaapp.app.data.remote.CamaraApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Simple ViewModel to expose CamaraApiService via Hilt DI for CobrarScreen */
@HiltViewModel
class LeisViewModelForDI @Inject constructor(
    val camaraApi: CamaraApiService
) : ViewModel()
