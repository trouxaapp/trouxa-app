package com.trouxaapp.app.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.LaunchedEffect
import com.trouxaapp.app.data.local.DataProvider
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trouxaapp.app.ui.components.CategoryTabs
import com.trouxaapp.app.ui.components.ContextBanner
import com.trouxaapp.app.ui.components.LiveIndicator
import com.trouxaapp.app.ui.components.MonthSelector
import com.trouxaapp.app.ui.components.PresidentCard
import com.trouxaapp.app.ui.components.ProductCard
import com.trouxaapp.app.ui.components.YearSlider
import com.trouxaapp.app.ui.theme.Red600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductClick: (String) -> Unit,
    onComparativoClick: () -> Unit,
    onSobreClick: () -> Unit,
    onCidadaoClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "TROUXA",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Red600,
                            fontWeight = FontWeight.Black
                        )
                        // Rotating phrase with crossfade every 5s
                        var phraseIndex by remember { mutableIntStateOf(0) }
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(5000)
                                phraseIndex = (phraseIndex + 1) % DataProvider.provocativePhrases.size
                            }
                        }
                        AnimatedContent(
                            targetState = phraseIndex,
                            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                            label = "phraseRotation"
                        ) { idx ->
                            Text(
                                text = DataProvider.provocativePhrases[idx],
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFA3A3A3),
                                maxLines = 1
                            )
                        }
                    }
                },
                // Navigation is via bottom bar now
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // STICKY HEADER
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp)
            ) {
                // AO VIVO indicator
                LiveIndicator(modifier = Modifier.padding(bottom = 4.dp))

                YearSlider(
                    selectedYear = state.selectedYear,
                    onYearChanged = viewModel::onYearChanged,
                    onYearTapped = { viewModel.toggleMonthSelector() },
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Month selector (2020-2026)
                MonthSelector(
                    visible = state.showMonthSelector,
                    year = state.selectedYear,
                    hasMonthlyData = state.hasMonthlyData,
                    selectedMonth = state.selectedMonth,
                    onMonthSelected = viewModel::onMonthSelected,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedContent(
                    targetState = state.president,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "presidentAnimation"
                ) { president ->
                    if (president != null) {
                        PresidentCard(president = president, selectedYear = state.selectedYear)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Context banner (pandemic 2020-2021, war 2022)
                ContextBanner(text = state.contextBanner)

                if (state.contextBanner != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Category Tabs
            CategoryTabs(
                selectedCategory = state.selectedCategory,
                onCategorySelected = viewModel::onCategorySelected
            )

            Spacer(modifier = Modifier.height(4.dp))

            // SCROLLABLE product cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = state.priceComparisons, key = { it.product.id }) { comparison ->
                    ProductCard(
                        comparison = comparison,
                        selectedMonth = state.selectedMonth,
                        onClick = { onProductClick(comparison.product.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}
