package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AddCameraScreen
import com.example.ui.screens.CameraDetailsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveGridScreen
import com.example.ui.screens.ManageLocationsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SingleCameraScreen
import com.example.ui.screens.UpgradeProScreen
import com.example.ui.theme.ElenioConnectTheme
import com.example.ui.theme.ElenioNavyPrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CctvViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val cctvViewModel: CctvViewModel = viewModel()
            val themeMode by cctvViewModel.themeMode.collectAsState()
            val currentScreen by cctvViewModel.currentScreen.collectAsState()
            val keepAwake by cctvViewModel.keepScreenAwake.collectAsState()

            LaunchedEffect(keepAwake) {
                if (keepAwake) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            val isDark = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            ElenioConnectTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ElenioNavyPrimary
                ) {
                    when (currentScreen) {
                        AppScreen.SPLASH,
                        AppScreen.ONBOARDING -> {
                            OnboardingScreen(viewModel = cctvViewModel)
                        }
                        AppScreen.CAMERAS_HOME -> {
                            HomeScreen(viewModel = cctvViewModel)
                        }
                        AppScreen.LIVE_GRID -> {
                            LiveGridScreen(viewModel = cctvViewModel)
                        }
                        AppScreen.SINGLE_CAMERA -> {
                            SingleCameraScreen(viewModel = cctvViewModel)
                        }
                        AppScreen.ADD_CAMERA_METHOD,
                        AppScreen.ONVIF_SCAN,
                        AppScreen.MANUAL_RTSP,
                        AppScreen.TEST_PREVIEW -> {
                            AddCameraScreen(viewModel = cctvViewModel)
                        }
                        AppScreen.CAMERA_DETAILS -> {
                            CameraDetailsScreen(viewModel = cctvViewModel)
                        }
                        AppScreen.MANAGE_LOCATIONS -> {
                            ManageLocationsScreen(viewModel = cctvViewModel)
                        }
                        AppScreen.UPGRADE_PRO -> {
                            UpgradeProScreen(viewModel = cctvViewModel)
                        }
                        AppScreen.SETTINGS -> {
                            SettingsScreen(viewModel = cctvViewModel)
                        }
                    }
                }
            }
        }
    }
}
