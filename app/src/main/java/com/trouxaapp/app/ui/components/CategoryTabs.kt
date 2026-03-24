package com.trouxaapp.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trouxaapp.app.data.model.ProductCategory
import com.trouxaapp.app.ui.theme.Red600

@Composable
fun CategoryTabs(
    selectedCategory: ProductCategory?,
    onCategorySelected: (ProductCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(null) + ProductCategory.entries
    val selectedIndex = tabs.indexOf(selectedCategory)

    ScrollableTabRow(
        selectedTabIndex = if (selectedIndex >= 0) selectedIndex else 0,
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        edgePadding = 16.dp,
        indicator = { tabPositions ->
            val index = if (selectedIndex >= 0) selectedIndex else 0
            if (index < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                    color = Red600
                )
            }
        }
    ) {
        tabs.forEachIndexed { index, category ->
            Tab(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = category?.label ?: "Todos",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                selectedContentColor = Red600,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
