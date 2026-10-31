package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.VideoPlayerSurface
import com.example.ui.theme.ElenioElectricBlue
import com.example.ui.theme.ElenioNavyBorder
import com.example.ui.theme.ElenioNavyCard
import com.example.ui.theme.ElenioNavyPrimary
import com.example.ui.theme.ElenioNavySurface
import com.example.ui.theme.ElenioOffWhite
import com.example.ui.theme.ElenioSecondaryText
import com.example.ui.theme.ElenioSuccess
import com.example.ui.theme.VideoPlayerBlack
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CctvViewModel

@Composable
fun SingleCameraScreen(
    viewModel: CctvViewModel,
    modifier: Modifier = Modifier
) {
    val activeCamera by viewModel.activeCamera.collectAsState()
    val allCameras by viewModel.allCameras.collectAsState()
    val isMuted by viewModel.isAudioMuted.collectAsState()
    val streamQuality by viewModel.currentStreamQuality.collectAsState()
    val isFullscreen by viewModel.isFullscreen.collectAsState()
    val snapshotMsg by viewModel.snapshotMessage.collectAsState()
    val keepScreenAwake by viewModel.keepScreenAwake.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snapshotMsg) {
        snapshotMsg?.let { snackbarHostState.showSnackbar(it) }
    }

    BackHandler {
        if (isFullscreen) {
            viewModel.toggleFullscreen()
        } else {
            viewModel.navigateBack()
        }
    }

    if (activeCamera == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ElenioNavyPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text("Tidak ada kamera yang dipilih.", color = ElenioSecondaryText)
        }
        return
    }

    val camera = activeCamera!!

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = VideoPlayerBlack,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Bar in non-fullscreen or overlay in fullscreen
            if (!isFullscreen) {
                Surface(
                    color = ElenioNavyPrimary,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.navigateBack() },
                                modifier = Modifier.testTag("single_camera_back_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = camera.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "${camera.locationName} • ${camera.manufacturer}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElenioSecondaryText
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.openCameraDetails(camera) },
                            modifier = Modifier.testTag("camera_info_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Detail Kamera",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Main Video Surface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isFullscreen) 1f else 0.55f)
            ) {
                VideoPlayerSurface(
                    camera = camera,
                    isMuted = isMuted,
                    streamQuality = streamQuality,
                    modifier = Modifier.fillMaxSize()
                )

                // Fullscreen floating toggle button
                IconButton(
                    onClick = { viewModel.toggleFullscreen() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .background(Color(0x88000000), CircleShape)
                        .testTag("fullscreen_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = Color.White
                    )
                }
            }

            // Controls & Switcher Panel (Only shown when not in full-screen)
            if (!isFullscreen) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.45f)
                        .background(ElenioNavySurface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Quick Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Snapshot
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { viewModel.captureSnapshot(camera.name) }
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = ElenioNavyCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("snapshot_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Screenshot",
                                        tint = ElenioElectricBlue
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Snapshot", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }

                        // HD/SD Quality Toggle
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                viewModel.setStreamQuality(if (streamQuality == "HD") "SD" else "HD")
                            }
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (streamQuality == "HD") ElenioElectricBlue else ElenioNavyCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("quality_toggle_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = streamQuality,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Kualitas", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }

                        // Audio Mute
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { viewModel.toggleAudioMute() }
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (!isMuted) ElenioElectricBlue else ElenioNavyCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("audio_toggle_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                        contentDescription = "Audio",
                                        tint = if (!isMuted) Color.White else ElenioSecondaryText
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (isMuted) "Bisu" else "Aktif", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }

                        // Reconnect
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                viewModel.captureSnapshot(camera.name)
                            }
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = ElenioNavyCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("reconnect_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh Feed",
                                        tint = ElenioOffWhite
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Segarkan", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }

                    // Technical Stream Diagnostics Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ElenioNavyCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "RTSP Over ${camera.transport} • Latensi 42ms",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = "${camera.ipAddress}:${camera.port} | Format: ${camera.codec}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                                    color = ElenioSecondaryText
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ElenioSuccess.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = ElenioSuccess,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Stabil",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ElenioSuccess,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Quick Switch Camera Carousel
                    Column {
                        Text(
                            text = "Beralih Kamera:",
                            style = MaterialTheme.typography.labelMedium,
                            color = ElenioSecondaryText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(allCameras) { cam ->
                                Surface(
                                    onClick = { viewModel.openSingleCamera(cam) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (cam.id == camera.id) ElenioElectricBlue else ElenioNavyCard,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (cam.id == camera.id) ElenioElectricBlue else ElenioNavyBorder
                                    )
                                ) {
                                    Text(
                                        text = cam.name,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
