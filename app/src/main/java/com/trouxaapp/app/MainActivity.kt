package com.trouxaapp.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.data.local.DataProvider
import kotlinx.coroutines.delay
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.trouxaapp.app.ui.navigation.TrouxaNavGraph
import com.trouxaapp.app.ui.screens.onboarding.OnboardingScreen
import com.trouxaapp.app.ui.theme.TrouxaAppTheme
import com.trouxaapp.app.util.EngagementManager
import com.trouxaapp.app.worker.DailyNotificationWorker
import com.trouxaapp.app.worker.DataSyncWorker
import com.trouxaapp.app.worker.LawSyncWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notifPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or not, we proceed */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("trouxa_prefs", Context.MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("first_run", true)

        // Schedule workers
        DailyNotificationWorker.schedule(this)
        LawSyncWorker.schedule(this)
        DataSyncWorker.schedule(this)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Init engagement tracking
        val engagement = EngagementManager(this)

        setContent {
            TrouxaAppTheme {
                var showOnboarding by remember { mutableStateOf(isFirstRun) }
                var showSplash by remember { mutableStateOf(!isFirstRun) }

                if (showSplash) {
                    SplashWithPhrase { showSplash = false }
                } else if (showOnboarding) {
                    OnboardingScreen(
                        onFinish = { uf ->
                            prefs.edit().putBoolean("first_run", false).apply()
                            if (uf.isNotEmpty()) {
                                engagement.userUf = uf
                            }
                            showOnboarding = false
                        }
                    )
                } else {
                    val navController = rememberNavController()
                    TrouxaNavGraph(navController = navController)
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun SplashWithPhrase(onDone: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val phrase = remember {
        val prefs = context.getSharedPreferences("trouxa_prefs", Context.MODE_PRIVATE)
        val lastIdx = prefs.getInt("last_splash_idx", -1)
        val phrases = DataProvider.splashPhrases
        val available = phrases.filterIndexed { idx, _ -> idx != lastIdx }
        val chosen = available.random()
        prefs.edit().putInt("last_splash_idx", phrases.indexOf(chosen)).apply()
        chosen
    }
    var showLabel by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showPhrase by remember { mutableStateOf(false) }
    var fadeAll by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(300)
        showLabel = true
        delay(300)
        showTitle = true
        delay(500)
        showPhrase = true
        delay(2500)
        fadeAll = false
        delay(500)
        onDone()
    }

    AnimatedVisibility(visible = fadeAll, enter = fadeIn(tween(0)), exit = fadeOut(tween(500))) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 40.dp)
            ) {
                AnimatedVisibility(visible = showLabel, enter = fadeIn(tween(300))) {
                    Text("te fizeram de", color = Color(0xFF737373), fontSize = 14.sp)
                }
                AnimatedVisibility(visible = showTitle, enter = fadeIn(tween(500))) {
                    Text("TROUXA", color = Color(0xFFDC2626), fontSize = 42.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(24.dp))
                AnimatedVisibility(visible = showPhrase, enter = fadeIn(tween(800))) {
                    Text(
                        phrase,
                        color = Color(0xFF9CA3AF),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
