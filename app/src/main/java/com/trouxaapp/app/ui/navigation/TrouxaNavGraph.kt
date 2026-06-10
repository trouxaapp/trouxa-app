package com.trouxaapp.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.trouxaapp.app.ui.screens.deputado.DeputadoProfileScreen
import com.trouxaapp.app.ui.screens.deputado.DeputadosScreen
import com.trouxaapp.app.ui.screens.entenda.EntendaScreen
import com.trouxaapp.app.ui.screens.hoje.HojeScreen
import com.trouxaapp.app.ui.screens.leis.LeiDetailScreen
import com.trouxaapp.app.ui.screens.leis.LeisFeedScreen
import com.trouxaapp.app.ui.screens.settings.SettingsScreen
import com.trouxaapp.app.ui.screens.sobre.PrivacidadeScreen
import com.trouxaapp.app.ui.screens.sobre.SobreScreen
import com.trouxaapp.app.ui.theme.OnSurfaceVariant
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.ui.theme.Surface

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(NavRoutes.LEIS, "Leis", Icons.Default.Gavel),
    BottomNavItem(NavRoutes.HOJE, "Hoje", Icons.Default.Today),
    BottomNavItem(NavRoutes.DEPUTADOS, "Deputados", Icons.Default.Groups),
    BottomNavItem(NavRoutes.ENTENDA, "Entenda", Icons.AutoMirrored.Filled.MenuBook)
)

@Composable
fun TrouxaNavGraph(navController: NavHostController, startPlId: Int? = null) {

    // Push deep link: land on the bill the notification was about
    LaunchedEffect(startPlId) {
        if (startPlId != null && startPlId > 0) {
            navController.navigate(NavRoutes.leiDetailRoute(startPlId))
        }
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val onTab = bottomNavItems.any { item ->
                currentDestination?.hierarchy?.any { it.route == item.route } == true
            }
            if (onTab) {
                NavigationBar(containerColor = Surface) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Red600,
                                selectedTextColor = Red600,
                                unselectedIconColor = OnSurfaceVariant,
                                unselectedTextColor = OnSurfaceVariant,
                                indicatorColor = Surface
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.LEIS,
            modifier = Modifier.padding(padding)
        ) {
            composable(NavRoutes.LEIS) {
                LeisFeedScreen(
                    onLeiClick = { plId -> navController.navigate(NavRoutes.leiDetailRoute(plId)) },
                    onSettingsClick = { navController.navigate(NavRoutes.SETTINGS) }
                )
            }
            composable(NavRoutes.HOJE) {
                HojeScreen(
                    onSettingsClick = { navController.navigate(NavRoutes.SETTINGS) }
                )
            }
            composable(NavRoutes.DEPUTADOS) {
                DeputadosScreen(
                    onDeputadoClick = { id -> navController.navigate(NavRoutes.deputadoProfileRoute(id)) }
                )
            }
            composable(NavRoutes.ENTENDA) {
                EntendaScreen()
            }
            composable(
                NavRoutes.LEI_DETAIL,
                arguments = listOf(navArgument("plId") { type = NavType.StringType })
            ) {
                LeiDetailScreen(onBack = { navController.popBackStack() })
            }
            composable(
                NavRoutes.DEPUTADO_PROFILE,
                arguments = listOf(navArgument("deputadoId") { type = NavType.IntType })
            ) { backStackEntry ->
                DeputadoProfileScreen(
                    deputadoId = backStackEntry.arguments?.getInt("deputadoId") ?: 0,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(NavRoutes.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onSobreClick = { navController.navigate(NavRoutes.SOBRE) },
                    onPrivacidadeClick = { navController.navigate(NavRoutes.PRIVACIDADE) }
                )
            }
            composable(NavRoutes.SOBRE) {
                SobreScreen(
                    onBack = { navController.popBackStack() },
                    onPrivacidadeClick = { navController.navigate(NavRoutes.PRIVACIDADE) }
                )
            }
            composable(NavRoutes.PRIVACIDADE) {
                PrivacidadeScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
