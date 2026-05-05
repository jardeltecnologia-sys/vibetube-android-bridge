package br.com.vibetube.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import br.com.vibetube.app.ui.navigation.VibeTubeNavHost
import br.com.vibetube.app.ui.theme.VibeTubeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            VibeTubeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().background(Color.Black),
                    color = Color.Black
                ) {
                    VibeTubeNavHost()
                }
            }
        }
    }
}
