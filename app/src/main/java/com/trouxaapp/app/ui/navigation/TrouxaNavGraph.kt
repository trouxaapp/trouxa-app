package com.trouxaapp.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.trouxaapp.app.data.remote.CamaraApiService
import com.trouxaapp.app.ui.screens.cidadao.CidadaoScreen
import com.trouxaapp.app.ui.screens.comparativo.ComparativoScreen
import com.trouxaapp.app.ui.screens.deputado.DeputadoProfileScreen
import com.trouxaapp.app.ui.screens.deputado.DeputadoScreen
import com.trouxaapp.app.ui.screens.detail.DetailScreen
import com.trouxaapp.app.ui.screens.entenda.EntendaScreen
import com.trouxaapp.app.ui.screens.hoje.BudgetDetailScreen
import com.trouxaapp.app.ui.screens.hoje.FactDetailScreen
import com.trouxaapp.app.ui.screens.hoje.HojeScreen
import com.trouxaapp.app.ui.screens.hoje.ImpactDetailScreen
import com.trouxaapp.app.ui.screens.hoje.LeisDoPovoScreen
import com.trouxaapp.app.ui.screens.hoje.PorQueNadaMudaScreen
import com.trouxaapp.app.ui.screens.hoje.StreakScreen
import com.trouxaapp.app.ui.screens.home.HomeScreen
import com.trouxaapp.app.ui.screens.leis.CobrarScreen
import com.trouxaapp.app.ui.screens.leis.LeisViewModelForDI
import com.trouxaapp.app.ui.screens.profile.ProfileScreen
import com.trouxaapp.app.ui.screens.settings.SettingsScreen
import com.trouxaapp.app.ui.screens.sobre.SobreScreen
import com.trouxaapp.app.ui.theme.Red600

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(NavRoutes.HOJE, "Hoje", Icons.Default.Bolt),
    BottomNavItem(NavRoutes.REPRESENTANTES, "Representantes", Icons.Default.Groups),
    BottomNavItem(NavRoutes.HISTORICO, "Histórico", Icons.Default.Timeline),
    BottomNavItem(NavRoutes.ENTENDA, "Entenda", Icons.Default.School)
)

@Composable
fun TrouxaNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(NavRoutes.HOJE) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Red600, selectedTextColor = Red600,
                                indicatorColor = Red600.copy(alpha = 0.1f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.HOJE,
            modifier = Modifier.padding(paddingValues)
        ) {
            // === TAB 1: HOJE ===
            composable(NavRoutes.HOJE) {
                HojeScreen(
                    onSettingsClick = { navController.navigate(NavRoutes.SETTINGS) },
                    onImpactClick = { dayOfYear -> navController.navigate(NavRoutes.impactDetailRoute(dayOfYear)) },
                    onBudgetClick = { navController.navigate(NavRoutes.BUDGET_DETAIL) },
                    onFactClick = { dayOfYear -> navController.navigate(NavRoutes.factDetailRoute(dayOfYear)) },
                    onStreakClick = { navController.navigate(NavRoutes.STREAK) },
                    onProductClick = { navController.navigate(NavRoutes.detailRoute(it)) },
                    onRepresentantesClick = {
                        navController.navigate(NavRoutes.REPRESENTANTES) {
                            popUpTo(NavRoutes.HOJE) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLeisDoPovoClick = { navController.navigate(NavRoutes.LEIS_DO_POVO) },
                    onPorQueNadaMudaClick = { navController.navigate(NavRoutes.POR_QUE_NADA_MUDA) }
                )
            }

            // === TAB 2: REPRESENTANTES ===
            composable(NavRoutes.REPRESENTANTES) {
                DeputadoScreen(
                    onDeputadoClick = { depId -> navController.navigate(NavRoutes.deputadoProfileRoute(depId)) }
                )
            }

            // === TAB 3: HISTORICO ===
            composable(NavRoutes.HISTORICO) {
                HomeScreen(
                    onProductClick = { navController.navigate(NavRoutes.detailRoute(it)) },
                    onComparativoClick = { navController.navigate(NavRoutes.COMPARATIVO) },
                    onSobreClick = { navController.navigate(NavRoutes.SETTINGS) },
                    onCidadaoClick = { navController.navigate(NavRoutes.CIDADAO) }
                )
            }

            // === TAB 4: ENTENDA ===
            composable(NavRoutes.ENTENDA) {
                EntendaScreen(
                    onBack = {},
                    onCidadaoClick = { navController.navigate(NavRoutes.CIDADAO) }
                )
            }

            // === DETAIL SCREENS ===
            composable(
                route = NavRoutes.IMPACT_DETAIL,
                arguments = listOf(navArgument("impactId") { type = NavType.IntType })
            ) { entry ->
                val impactId = entry.arguments?.getInt("impactId") ?: 0
                ImpactDetailScreen(
                    impactId = impactId,
                    onBack = { navController.popBackStack() },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(NavRoutes.BUDGET_DETAIL) {
                BudgetDetailScreen(onBack = { navController.popBackStack() })
            }

            composable(
                route = NavRoutes.FACT_DETAIL,
                arguments = listOf(navArgument("factId") { type = NavType.IntType })
            ) { entry ->
                val factId = entry.arguments?.getInt("factId") ?: 0
                FactDetailScreen(factId = factId, onBack = { navController.popBackStack() })
            }

            composable(NavRoutes.STREAK) {
                StreakScreen(onBack = { navController.popBackStack() })
            }

            composable(NavRoutes.POR_QUE_NADA_MUDA) {
                PorQueNadaMudaScreen(onBack = { navController.popBackStack() })
            }

            composable(NavRoutes.LEIS_DO_POVO) {
                LeisDoPovoScreen(onBack = { navController.popBackStack() })
            }

            composable(NavRoutes.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onAboutClick = { navController.navigate(NavRoutes.SOBRE) }
                )
            }

            composable(
                route = NavRoutes.DEPUTADO_PROFILE,
                arguments = listOf(navArgument("deputadoId") { type = NavType.IntType })
            ) { entry ->
                val depId = entry.arguments?.getInt("deputadoId") ?: return@composable
                DeputadoProfileScreen(deputadoId = depId, onBack = { navController.popBackStack() })
            }

            composable(
                route = NavRoutes.DETAIL,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { entry ->
                val productId = entry.arguments?.getString("productId") ?: return@composable
                DetailScreen(productId = productId, onBack = { navController.popBackStack() })
            }

            composable(NavRoutes.COMPARATIVO) { ComparativoScreen(onBack = { navController.popBackStack() }) }
            composable(NavRoutes.SOBRE) { SobreScreen(onBack = { navController.popBackStack() }) }
            composable(NavRoutes.CIDADAO) { CidadaoScreen(onBack = { navController.popBackStack() }) }

            composable(
                route = NavRoutes.PROFILE,
                arguments = listOf(navArgument("profileId") { type = NavType.StringType })
            ) { entry ->
                ProfileScreen(profileId = entry.arguments?.getString("profileId") ?: "", onBack = { navController.popBackStack() })
            }

            composable(
                route = NavRoutes.COBRAR,
                arguments = listOf(
                    navArgument("depId") { type = NavType.IntType },
                    navArgument("depNome") { type = NavType.StringType },
                    navArgument("partido") { type = NavType.StringType },
                    navArgument("uf") { type = NavType.StringType },
                    navArgument("contexto") { type = NavType.StringType }
                )
            ) { entry ->
                val camaraApi: CamaraApiService = hiltViewModel<LeisViewModelForDI>().camaraApi
                CobrarScreen(
                    deputadoId = entry.arguments?.getInt("depId") ?: 0,
                    deputadoNome = java.net.URLDecoder.decode(entry.arguments?.getString("depNome") ?: "", "UTF-8"),
                    partido = java.net.URLDecoder.decode(entry.arguments?.getString("partido") ?: "", "UTF-8"),
                    uf = java.net.URLDecoder.decode(entry.arguments?.getString("uf") ?: "", "UTF-8"),
                    contexto = java.net.URLDecoder.decode(entry.arguments?.getString("contexto") ?: "", "UTF-8"),
                    camaraApi = camaraApi,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
