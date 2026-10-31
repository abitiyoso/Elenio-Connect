package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CameraEntity
import com.example.ui.components.ElenioBottomNavigation
import com.example.ui.components.ElenioTopHeader
import com.example.ui.components.LiveStatusBadge
import com.example.ui.theme.ElenioBlueContainer
import com.example.ui.theme.ElenioElectricBlue
import com.example.ui.theme.ElenioNavyBorder
import com.example.ui.theme.ElenioNavyCard
import com.example.ui.theme.ElenioNavyPrimary
import com.example.ui.theme.ElenioOffWhite
import com.example.ui.theme.ElenioSecondaryText
import com.example.ui.theme.VideoPlayerBlack
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CctvViewModel

@Composable
fun LiveGridScreen(
    viewModel: CctvViewModel,
    modifier: Modifier = Modifier
) {
    val allCameras by viewModel.allCameras.collectAsState()
    val isPro by viewModel.isProUser.collectAsState()
    val gridLayoutMode by viewModel.gridLayoutMode.collectAsState()

    // Free users can display up to 2 cameras simultaneously in grid; Pro users up to 8
    val maxGridFeeds = if (isPro) 8 else 2
    val gridCameras = allCameras.take(maxGridFeeds)

    Scaffold(
        topBar = {
            ElenioTopHeader(
                title = "LIVE MULTI-GRID",
                subtitle = if (isPro) "Monitoring Hingga 8 Kamera" else "Maks. 2 Kamera Live (Paket Gratis)",
                isPro = isPro,
                onProClick = { viewModel.navigateTo(AppScreen.UPGRADE_PRO) },
                onSettingsClick = { viewModel.navigateTo(AppScreen.SETTINGS) }
            )
        },
        bottomBar = {
            ElenioBottomNavigation(
                selectedTab = 1,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> viewModel.navigateTo(AppScreen.CAMERAS_HOME)
                        1 -> { /* Already on grid */ }
                        2 -> viewModel.navigateTo(AppScreen.CAMERA_DETAILS)
                    }
                }
            )
        },
        containerColor = ElenioNavyPrimary,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Layout Mode Switcher Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tata Letak Grid:",
                        style = MaterialTheme.typography.labelMedium,
                        color = ElenioOffWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // 1x1 mode
                    Surface(
                        onClick = { viewModel.setGridLayout(1) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (gridLayoutMode == 1) ElenioElectricBlue else ElenioNavyCard,
                        modifier = Modifier.testTag("grid_mode_1x1")
                    ) {
                        Text(
                            text = "1×1",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    // 2x2 mode
                    Surface(
                        onClick = { viewModel.setGridLayout(2) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (gridLayoutMode == 2) ElenioElectricBlue else ElenioNavyCard,
                        modifier = Modifier.testTag("grid_mode_2x2")
                    ) {
                        Text(
                            text = "2×2",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    // 3x3 mode (PRO)
                    Surface(
                        onClick = { viewModel.setGridLayout(3) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (gridLayoutMode == 3) ElenioElectricBlue else ElenioNavyCard,
                        border = if (!isPro) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)) else null,
                        modifier = Modifier.testTag("grid_mode_3x3")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "3×3",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            if (!isPro) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Pro Feature",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x33000000)
                ) {
                    Text(
                        text = "Substream Rendah • Hemat Data",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = ElenioSecondaryText
                    )
                }
            }

            if (gridCameras.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada kamera untuk ditampilkan di Grid.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ElenioSecondaryText
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridLayoutMode),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("multi_camera_grid_view")
                ) {
                    items(gridCameras, key = { it.id }) { camera ->
                        Card(
                            onClick = { viewModel.openSingleCamera(camera) },
                            colors = CardDefaults.cardColors(containerColor = ElenioNavyCard),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("grid_cell_${camera.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .background(VideoPlayerBlack)
                            ) {
                                if (camera.previewDrawableRes != 0) {
                                    Image(
                                        painter = painterResource(id = camera.previewDrawableRes),
                                        contentDescription = camera.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = null,
                                            tint = ElenioSecondaryText,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                // Top live badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(6.dp)
                                ) {
                                    LiveStatusBadge(isOnline = camera.isOnline, fps = camera.fps)
                                }

                                // Bottom name overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomStart)
                                        .background(Color(0x99000000))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = camera.name,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = camera.substreamResolution,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontFamily = FontFamily.Monospace),
                                            color = ElenioSecondaryText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Upgrade slot card if Free user with more cameras available
                    if (!isPro && allCameras.size > 2) {
                        item {
                            Card(
                                onClick = { viewModel.navigateTo(AppScreen.UPGRADE_PRO) },
                                colors = CardDefaults.cardColors(containerColor = ElenioBlueContainer.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElenioElectricBlue.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Buka Hingga 8 Grid",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Upgrade Pro Rp59k",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = ElenioElectricBlue
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
}
