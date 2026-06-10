package com.trouxaapp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.trouxaapp.app.ui.navigation.TrouxaNavGraph
import com.trouxaapp.app.ui.screens.onboarding.OnboardingScreen
import com.trouxaapp.app.ui.theme.Background
import com.trouxaapp.app.ui.theme.OnSurfaceFaint
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.ui.theme.TrouxaAppTheme
import com.trouxaapp.app.util.UserPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = UserPrefs(this)
        // Notification pushes carry the bill id so tapping one lands on it
        val deepLinkPlId = intent?.getIntExtra("plId", 0)?.takeIf { it > 0 }

        setContent {
            TrouxaAppTheme {
                var showSplash by remember { mutableStateOf(true) }
                var showOnboarding by remember { mutableStateOf(!prefs.onboardingDone) }

                when {
                    showSplash -> BrandSplash { showSplash = false }
                    showOnboarding -> OnboardingScreen(
                        onFinish = { uf, notifOptIn ->
                            prefs.onboardingDone = true
                            if (uf.isNotEmpty()) prefs.userUf = uf
                            if (notifOptIn) prefs.notifEventos = true
                            showOnboarding = false
                        }
                    )
                    else -> {
                        val navController = rememberNavController()
                        TrouxaNavGraph(navController = navController, startPlId = deepLinkPlId)
                    }
                }
            }
        }
    }
}

/**
 * Brand splash, shown on every open by design, kept under a second.
 * Logo only: factual claims have no place on a screen with no sources.
 */
@Composable
private fun BrandSplash(onDone: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(750)
        visible = false
        delay(180)
        onDone()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(180))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("te fizeram de", color = OnSurfaceFaint, fontSize = 14.sp)
                Text("TROUXA", color = Red600, fontSize = 42.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
