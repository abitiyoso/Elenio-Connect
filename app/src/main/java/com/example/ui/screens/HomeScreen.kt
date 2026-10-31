package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CameraEntity
import com.example.ui.components.CameraCard
import com.example.ui.components.ElenioAdBanner
import com.example.ui.components.ElenioBottomNavigation
import com.example.ui.components.ElenioTopHeader
import com.example.ui.theme.ElenioBlueContainer
import com.example.ui.theme.ElenioElectricBlue
import com.example.ui.theme.ElenioNavyBorder
import com.example.ui.theme.ElenioNavyCard
import com.example.ui.theme.ElenioNavyPrimary
import com.example.ui.theme.ElenioNavySurface
import com.example.ui.theme.ElenioOffWhite
import com.example.ui.theme.ElenioSecondaryText
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CctvViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CctvViewModel,
    modifier: Modifier = Modifier
) {
    val filteredCameras by viewModel.filteredCameras.collectAsState()
    val allLocations by viewModel.allLocations.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isPro by viewModel.isProUser.collectAsState()
    val totalCameras by viewModel.allCameras.collectAsState()

    var isSearchVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ElenioTopHeader(
                isPro = isPro,
                onProClick = { viewModel.navigateTo(AppScreen.UPGRADE_PRO) },
                onSettingsClick = { viewModel.navigateTo(AppScreen.SETTINGS) }
            )
        },
        bottomBar = {
            ElenioBottomNavigation(
                selectedTab = 0,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> { /* Already home */ }
                        1 -> viewModel.navigateTo(AppScreen.LIVE_GRID)
                        2 -> viewModel.navigateTo(AppScreen.CAMERA_DETAILS)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (viewModel.checkCanAddCamera()) {
                        viewModel.navigateTo(AppScreen.ADD_CAMERA_METHOD)
                    }
                },
                containerColor = ElenioElectricBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_camera_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Kamera",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        containerColor = ElenioNavyPrimary,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search & Filter Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kamera Saya (${filteredCameras.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = if (isPro) "Paket PRO: Hingga 8 kamera" else "Paket Gratis: ${totalCameras.size}/2 kamera",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (!isPro && totalCameras.size >= 2) Color(0xFFF59E0B) else ElenioSecondaryText
                    )
                }

                Row {
                    IconButton(
                        onClick = { isSearchVisible = !isSearchVisible },
                        modifier = Modifier.testTag("toggle_search_btn")
                    ) {
                        Icon(
                            imageVector = if (isSearchVisible) Icons.Default.Clear else Icons.Default.Search,
                            contentDescription = "Cari Kamera",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.MANAGE_LOCATIONS) },
                        modifier = Modifier.testTag("manage_locations_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Kelola Lokasi",
                            tint = Color.White
                        )
                    }
                }
            }

            // Expandable Search Bar
            if (isSearchVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Cari nama kamera, IP, lokasi...", color = ElenioSecondaryText) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ElenioElectricBlue,
                        unfocusedBorderColor = ElenioNavyBorder,
                        focusedContainerColor = ElenioNavySurface,
                        unfocusedContainerColor = ElenioNavySurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("search_input_field")
                )
            }

            // Locations Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedLocation == "Semua",
                        onClick = { viewModel.setLocationFilter("Semua") },
                        label = { Text("Semua") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElenioElectricBlue,
                            selectedLabelColor = Color.White,
                            containerColor = ElenioNavyCard,
                            labelColor = ElenioOffWhite
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedLocation == "Semua",
                            borderColor = ElenioNavyBorder,
                            selectedBorderColor = ElenioElectricBlue
                        ),
                        modifier = Modifier.testTag("location_chip_all")
                    )
                }

                items(allLocations) { loc ->
                    FilterChip(
                        selected = selectedLocation == loc.name,
                        onClick = { viewModel.setLocationFilter(loc.name) },
                        label = { Text(loc.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElenioElectricBlue,
                            selectedLabelColor = Color.White,
                            containerColor = ElenioNavyCard,
                            labelColor = ElenioOffWhite
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedLocation == loc.name,
                            borderColor = ElenioNavyBorder,
                            selectedBorderColor = ElenioElectricBlue
                        ),
                        modifier = Modifier.testTag("location_chip_${loc.name}")
                    )
                }
            }

            // Main Cameras Content
            if (filteredCameras.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ElenioNavyCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = ElenioElectricBlue,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "Kamera Tidak Ditemukan" else "Belum Ada Kamera Terpasang",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank())
                                "Tidak ada kamera yang cocok dengan kata kunci '$searchQuery'."
                            else
                                "Hubungkan kamera IP, CCTV ONVIF, atau feed RTSP Anda untuk mulai memantau.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ElenioSecondaryText,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.ADD_CAMERA_METHOD) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElenioElectricBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("empty_state_add_camera_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tambah Kamera Pertama")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredCameras, key = { it.id }) { camera ->
                        CameraCard(
                            camera = camera,
                            isPro = isPro,
                            onClick = { viewModel.openSingleCamera(camera) },
                            onFavoriteToggle = { viewModel.toggleFavorite(camera) }
                        )
                    }

                    // Compact AdMob Banner for Free Users outside video stream
                    if (!isPro) {
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            ElenioAdBanner()
                        }
                    }
                }
            }
        }
    }
}
