package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.cctv.ConnectionTestResult
import com.example.data.model.DiscoveredDevice
import com.example.ui.theme.ElenioBlueContainer
import com.example.ui.theme.ElenioElectricBlue
import com.example.ui.theme.ElenioError
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
import com.example.ui.viewmodel.ScanState

@Composable
fun AddCameraScreen(
    viewModel: CctvViewModel,
    modifier: Modifier = Modifier
) {
    var selectedMethodTab by remember { mutableIntStateOf(0) } // 0: ONVIF Auto-Discovery, 1: Manual RTSP
    val scanState by viewModel.scanState.collectAsState()
    val discoveredDevices by viewModel.discoveredDevices.collectAsState()
    val selectedDevice by viewModel.selectedDiscoveredDevice.collectAsState()

    val wizardName by viewModel.wizardCameraName.collectAsState()
    val wizardUrl by viewModel.wizardRtspUrl.collectAsState()
    val wizardUser by viewModel.wizardUsername.collectAsState()
    val wizardPass by viewModel.wizardPassword.collectAsState()
    val wizardTransport by viewModel.wizardTransport.collectAsState()
    val wizardLocation by viewModel.wizardLocation.collectAsState()
    val allLocations by viewModel.allLocations.collectAsState()

    val isTesting by viewModel.isTestingConnection.collectAsState()
    val testResult by viewModel.testConnectionResult.collectAsState()

    var showPassword by remember { mutableStateOf(false) }

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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("add_camera_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tambah Kamera Baru",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
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
            // Method Tabs: ONVIF Discovery vs Manual RTSP
            TabRow(
                selectedTabIndex = selectedMethodTab,
                containerColor = ElenioNavySurface,
                contentColor = ElenioElectricBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedMethodTab]),
                        color = ElenioElectricBlue
                    )
                }
            ) {
                Tab(
                    selected = selectedMethodTab == 0,
                    onClick = { selectedMethodTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ONVIF Otomatis", fontWeight = FontWeight.SemiBold)
                        }
                    },
                    selectedContentColor = ElenioElectricBlue,
                    unselectedContentColor = ElenioSecondaryText,
                    modifier = Modifier.testTag("tab_onvif_discovery")
                )
                Tab(
                    selected = selectedMethodTab == 1,
                    onClick = { selectedMethodTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Manual RTSP", fontWeight = FontWeight.SemiBold)
                        }
                    },
                    selectedContentColor = ElenioElectricBlue,
                    unselectedContentColor = ElenioSecondaryText,
                    modifier = Modifier.testTag("tab_manual_rtsp")
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                if (selectedMethodTab == 0) {
                    // METHOD 1: ONVIF Auto Discovery
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ElenioNavyCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Deteksi Kamera di Jaringan Lokal (LAN)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Memindai WS-Discovery IP camera (Hikvision, Dahua, Tapo, Reolink, dll) di Wi-Fi yang sama.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElenioSecondaryText,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                val infiniteTransition = rememberInfiniteTransition(label = "scanRotate")
                                val rotation by infiniteTransition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 360f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(2000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Restart
                                    ),
                                    label = "rotation"
                                )

                                if (scanState == ScanState.SCANNING) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .rotate(rotation),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Radar,
                                            contentDescription = null,
                                            tint = ElenioElectricBlue,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Memindai multicast 239.255.255.250:3702...",
                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                        color = ElenioElectricBlue
                                    )
                                } else {
                                    Button(
                                        onClick = { viewModel.startOnvifScan() },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElenioElectricBlue),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("start_scan_btn")
                                    ) {
                                        Icon(imageVector = Icons.Default.Radar, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (discoveredDevices.isEmpty()) "Mulai Pemindaian LAN" else "Pindai Ulang LAN")
                                    }
                                }
                            }
                        }
                    }

                    if (discoveredDevices.isNotEmpty()) {
                        item {
                            Text(
                                text = "Kamera Terdeteksi (${discoveredDevices.size}):",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        items(discoveredDevices) { device ->
                            val isSelected = selectedDevice?.id == device.id
                            Card(
                                onClick = { viewModel.selectDiscoveredDevice(device) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) ElenioBlueContainer else ElenioNavyCard
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) ElenioElectricBlue else ElenioNavyBorder
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("discovered_device_${device.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = ElenioNavyPrimary,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Videocam,
                                                    contentDescription = null,
                                                    tint = ElenioElectricBlue
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = device.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                                color = Color.White
                                            )
                                            Text(
                                                text = "${device.manufacturer} • ${device.model}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ElenioOffWhite.copy(alpha = 0.8f)
                                            )
                                            Text(
                                                text = "IP: ${device.ipAddress} | Port: ${device.rtspPort}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 11.sp,
                                                    color = ElenioSecondaryText
                                                )
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Dipilih",
                                            tint = ElenioElectricBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Configuration & Credential Form (shown for Manual or when Discovered Device is selected)
                if (selectedMethodTab == 1 || selectedDevice != null) {
                    item {
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
                                    text = if (selectedMethodTab == 0) "Kredensial & Pengaturan Kamera" else "Konfigurasi RTSP Manual",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )

                                // Camera Name
                                OutlinedTextField(
                                    value = wizardName,
                                    onValueChange = { viewModel.updateWizardFields(name = it) },
                                    label = { Text("Nama Kamera (cth: Garasi Depan)") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = ElenioElectricBlue,
                                        unfocusedBorderColor = ElenioNavyBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("camera_name_input")
                                )

                                // RTSP URL
                                OutlinedTextField(
                                    value = wizardUrl,
                                    onValueChange = { viewModel.updateWizardFields(url = it) },
                                    label = { Text("RTSP Stream URL") },
                                    placeholder = { Text("rtsp://192.168.1.100:554/live/ch0") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = ElenioElectricBlue,
                                        unfocusedBorderColor = ElenioNavyBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("rtsp_url_input")
                                )

                                // Username & Password
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = wizardUser,
                                        onValueChange = { viewModel.updateWizardFields(user = it) },
                                        label = { Text("Username") },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = ElenioElectricBlue,
                                            unfocusedBorderColor = ElenioNavyBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("username_input")
                                    )

                                    OutlinedTextField(
                                        value = wizardPass,
                                        onValueChange = { viewModel.updateWizardFields(pass = it) },
                                        label = { Text("Password") },
                                        singleLine = true,
                                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                        trailingIcon = {
                                            IconButton(onClick = { showPassword = !showPassword }) {
                                                Icon(
                                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = null,
                                                    tint = ElenioSecondaryText
                                                )
                                            }
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = ElenioElectricBlue,
                                            unfocusedBorderColor = ElenioNavyBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("password_input")
                                    )
                                }

                                // Transport mode & Location assignment
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Transport Protokol:", style = MaterialTheme.typography.labelSmall, color = ElenioSecondaryText)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row {
                                            Surface(
                                                onClick = { viewModel.updateWizardFields(transport = "TCP") },
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (wizardTransport == "TCP") ElenioElectricBlue else ElenioNavyPrimary,
                                                border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                                                modifier = Modifier.testTag("transport_tcp_btn")
                                            ) {
                                                Text(
                                                    text = "TCP (Stabil)",
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                onClick = { viewModel.updateWizardFields(transport = "UDP") },
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (wizardTransport == "UDP") ElenioElectricBlue else ElenioNavyPrimary,
                                                border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                                                modifier = Modifier.testTag("transport_udp_btn")
                                            ) {
                                                Text(
                                                    text = "UDP",
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Penempatan Lokasi:", style = MaterialTheme.typography.labelSmall, color = ElenioSecondaryText)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = wizardLocation,
                                            onValueChange = { viewModel.updateWizardFields(location = it) },
                                            placeholder = { Text("Rumah / Toko") },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedBorderColor = ElenioElectricBlue,
                                                unfocusedBorderColor = ElenioNavyBorder
                                            ),
                                            modifier = Modifier.testTag("location_input")
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Test Connection Button
                                OutlinedButton(
                                    onClick = { viewModel.testConnection() },
                                    enabled = !isTesting && wizardUrl.isNotBlank(),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElenioElectricBlue),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ElenioElectricBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("test_connection_btn")
                                ) {
                                    if (isTesting) {
                                        CircularProgressIndicator(
                                            color = ElenioElectricBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Menguji Handshake RTSP...")
                                    } else {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Uji Sambungan & Pratinjau")
                                    }
                                }

                                // Test Connection Feedback Card
                                testResult?.let { res ->
                                    when (res) {
                                        is ConnectionTestResult.Success -> {
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFF062817)),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, ElenioSuccess),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.CheckCircle,
                                                            contentDescription = null,
                                                            tint = ElenioSuccess,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = "Koneksi Berhasil! Latensi: ${res.latencyMs}ms",
                                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                            color = Color.White
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = "Codec: ${res.codec} • Resolusi: ${res.resolution} @ ${res.fps}fps",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color(0xFF86EFAC)
                                                    )
                                                }
                                            }
                                        }
                                        is ConnectionTestResult.Failure -> {
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C0A0A)),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, ElenioError),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Error,
                                                            contentDescription = null,
                                                            tint = ElenioError,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = res.errorMessage,
                                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                            color = Color.White
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = "Solusi: ${res.troubleshootingTip}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color(0xFFFCA5A5)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Final Save Camera Button
                                Button(
                                    onClick = { viewModel.saveWizardCamera() },
                                    enabled = wizardUrl.isNotBlank() && wizardName.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElenioElectricBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("save_camera_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.Videocam, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Simpan & Mulai Memantau")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
