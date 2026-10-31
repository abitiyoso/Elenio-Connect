package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CameraEntity
import com.example.ui.components.ElenioBottomNavigation
import com.example.ui.components.ElenioTopHeader
import com.example.ui.theme.ElenioElectricBlue
import com.example.ui.theme.ElenioError
import com.example.ui.theme.ElenioNavyBorder
import com.example.ui.theme.ElenioNavyCard
import com.example.ui.theme.ElenioNavyPrimary
import com.example.ui.theme.ElenioNavySurface
import com.example.ui.theme.ElenioOffWhite
import com.example.ui.theme.ElenioSecondaryText
import com.example.ui.theme.ElenioSuccess
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CctvViewModel

@Composable
fun CameraDetailsScreen(
    viewModel: CctvViewModel,
    modifier: Modifier = Modifier
) {
    val activeCamera by viewModel.activeCamera.collectAsState()
    val allCameras by viewModel.allCameras.collectAsState()
    val isPro by viewModel.isProUser.collectAsState()

    var showDeleteConfirm by remember { mutableStateOf<CameraEntity?>(null) }

    // If an active camera is selected, edit its properties; otherwise show device management list
    var editingName by remember(activeCamera) { mutableStateOf(activeCamera?.name ?: "") }
    var editingLocation by remember(activeCamera) { mutableStateOf(activeCamera?.locationName ?: "") }
    var editingUrl by remember(activeCamera) { mutableStateOf(activeCamera?.rtspUrl ?: "") }

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
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
                            modifier = Modifier.testTag("details_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (activeCamera != null) "Detail & Pengaturan Kamera" else "Manajemen Perangkat",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    if (activeCamera != null) {
                        IconButton(
                            onClick = {
                                activeCamera?.let { cam ->
                                    val updated = cam.copy(
                                        name = editingName,
                                        locationName = editingLocation,
                                        rtspUrl = editingUrl
                                    )
                                    viewModel.updateCameraSettings(updated)
                                }
                            },
                            modifier = Modifier.testTag("save_camera_settings_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Simpan Perubahan",
                                tint = ElenioElectricBlue
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            ElenioBottomNavigation(
                selectedTab = 2,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> viewModel.navigateTo(AppScreen.CAMERAS_HOME)
                        1 -> viewModel.navigateTo(AppScreen.LIVE_GRID)
                        2 -> { /* Already on devices */ }
                    }
                }
            )
        },
        containerColor = ElenioNavyPrimary,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeCamera != null) {
                val cam = activeCamera!!

                item {
                    // Technical Specifications Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ElenioNavyCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Diagnostik & Info Endpoint",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (cam.isOnline) ElenioSuccess.copy(alpha = 0.2f) else ElenioError.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (cam.isOnline) "ONLINE" else "OFFLINE",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (cam.isOnline) ElenioSuccess else ElenioError,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            InfoRow("Pabrikan / Vendor", cam.manufacturer)
                            InfoRow("Model Perangkat", cam.model)
                            InfoRow("Alamat IP & Port", "${cam.ipAddress}:${cam.port}")
                            InfoRow("Transport", "${cam.transport} (RTSP Stream)")
                            InfoRow("Resolusi Stream", cam.resolution)
                            InfoRow("Substream", cam.substreamResolution)
                            InfoRow("Video Codec", cam.codec)
                            InfoRow("Target FPS", "${cam.fps} fps")
                        }
                    }
                }

                item {
                    // Edit Fields Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ElenioNavyCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Ubah Parameter Kamera",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            OutlinedTextField(
                                value = editingName,
                                onValueChange = { editingName = it },
                                label = { Text("Nama Kamera") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = ElenioElectricBlue,
                                    unfocusedBorderColor = ElenioNavyBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("edit_camera_name")
                            )

                            OutlinedTextField(
                                value = editingLocation,
                                onValueChange = { editingLocation = it },
                                label = { Text("Lokasi") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = ElenioElectricBlue,
                                    unfocusedBorderColor = ElenioNavyBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("edit_camera_location")
                            )

                            OutlinedTextField(
                                value = editingUrl,
                                onValueChange = { editingUrl = it },
                                label = { Text("RTSP Stream URL") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = ElenioElectricBlue,
                                    unfocusedBorderColor = ElenioNavyBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("edit_camera_rtsp")
                            )

                            Button(
                                onClick = {
                                    val updated = cam.copy(
                                        name = editingName,
                                        locationName = editingLocation,
                                        rtspUrl = editingUrl
                                    )
                                    viewModel.updateCameraSettings(updated)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElenioElectricBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simpan Konfigurasi")
                            }
                        }
                    }
                }

                item {
                    // Dangerous Actions: Delete
                    OutlinedButton(
                        onClick = { showDeleteConfirm = cam },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElenioError),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElenioError),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delete_camera_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hapus Kamera Ini")
                    }
                }
            } else {
                // List of all registered devices
                item {
                    Text(
                        text = "Daftar Kamera Terdaftar (${allCameras.size}):",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                items(allCameras) { cam ->
                    Card(
                        onClick = { viewModel.openCameraDetails(cam) },
                        colors = CardDefaults.cardColors(containerColor = ElenioNavyCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manage_device_${cam.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cam.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = "${cam.locationName} • ${cam.manufacturer} (${cam.ipAddress})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElenioSecondaryText
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { showDeleteConfirm = cam },
                                    modifier = Modifier.testTag("delete_device_icon_${cam.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = ElenioError
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (viewModel.checkCanAddCamera()) {
                                viewModel.navigateTo(AppScreen.ADD_CAMERA_METHOD)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElenioElectricBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manage_add_camera_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tambah Kamera Baru")
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteConfirm?.let { camToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Hapus Kamera?", color = Color.White) },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus kamera '${camToDelete.name}'? Pengaturan RTSP lokal akan dihapus dari perangkat.",
                    color = ElenioOffWhite
                )
            },
            containerColor = ElenioNavySurface,
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCamera(camToDelete)
                        showDeleteConfirm = null
                    }
                ) {
                    Text("Hapus", color = ElenioError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("Batal", color = ElenioOffWhite)
                }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = ElenioSecondaryText)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = Color.White)
    }
}
