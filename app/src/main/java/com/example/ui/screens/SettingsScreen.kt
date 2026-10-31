package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElenioBlueContainer
import com.example.ui.theme.ElenioElectricBlue
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
fun SettingsScreen(
    viewModel: CctvViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isPro by viewModel.isProUser.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val keepScreenAwake by viewModel.keepScreenAwake.collectAsState()
    val autoReconnect by viewModel.autoReconnect.collectAsState()
    val isDemoEnabled by viewModel.isDemoEnabled.collectAsState()

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
                        modifier = Modifier.testTag("settings_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pengaturan & Akun",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        },
        containerColor = ElenioNavyPrimary,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            // Plan & Subscription Status Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (isPro) ElenioBlueContainer else ElenioNavyCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isPro) ElenioElectricBlue else ElenioNavyBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (isPro) ElenioElectricBlue else ElenioNavySurface,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (isPro) Color.White else ElenioElectricBlue
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isPro) "Elenio Connect PRO" else "Paket Gratis (Free)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = if (isPro) "Lisensi Seumur Hidup Aktif • Bebas Iklan" else "Maks. 2 Kamera • Iklan Terbatas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isPro) Color(0xFF93C5FD) else ElenioSecondaryText
                                )
                            }
                        }

                        if (!isPro) {
                            Surface(
                                onClick = { viewModel.navigateTo(AppScreen.UPGRADE_PRO) },
                                shape = RoundedCornerShape(8.dp),
                                color = ElenioElectricBlue
                            ) {
                                Text(
                                    text = "Upgrade",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Stream & Display Preferences
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
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Preferensi Monitoring",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )

                        // Keep screen awake toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Layar Tetap Menyala (Keep Awake)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = Color.White
                                )
                                Text(
                                    text = "Mencegah layar redup saat memantau live video",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElenioSecondaryText
                                )
                            }
                            Switch(
                                checked = keepScreenAwake,
                                onCheckedChange = { viewModel.setKeepScreenAwake(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = ElenioElectricBlue
                                ),
                                modifier = Modifier.testTag("keep_awake_switch")
                            )
                        }

                        // Auto reconnect toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Sambung Ulang Otomatis",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = Color.White
                                )
                                Text(
                                    text = "Mencoba menghubungkan kembali RTSP bila jaringan terputus",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElenioSecondaryText
                                )
                            }
                            Switch(
                                checked = autoReconnect,
                                onCheckedChange = { viewModel.setAutoReconnect(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = ElenioElectricBlue
                                ),
                                modifier = Modifier.testTag("auto_reconnect_switch")
                            )
                        }

                        // Language Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Bahasa Antarmuka",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = Color.White
                                )
                                Text(
                                    text = "Indonesia / English",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElenioSecondaryText
                                )
                            }
                            Row {
                                Surface(
                                    onClick = { viewModel.setLanguage("ID") },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (language == "ID") ElenioElectricBlue else ElenioNavySurface
                                ) {
                                    Text(
                                        text = "ID",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    onClick = { viewModel.setLanguage("EN") },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (language == "EN") ElenioElectricBlue else ElenioNavySurface
                                ) {
                                    Text(
                                        text = "EN",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Data & Demo Mode
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
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Data & Mode Demo",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Sertakan Sampel Kamera Demo",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = Color.White
                                )
                                Text(
                                    text = "Menampilkan simulasi feed RTSP untuk Rumah, Toko, & Gudang",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElenioSecondaryText
                                )
                            }
                            Switch(
                                checked = isDemoEnabled,
                                onCheckedChange = { viewModel.toggleDemoMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = ElenioElectricBlue
                                ),
                                modifier = Modifier.testTag("demo_mode_switch")
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ElenioNavySurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Penyimpanan Lokal Saja (Local-First)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Kredensial dan URL RTSP Anda disimpan secara aman hanya di perangkat ini melalui Room SQLite. Tidak ada rekaman kamera atau password yang diunggah ke server cloud.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElenioSecondaryText
                                )
                            }
                        }
                    }
                }
            }

            // Company, Privacy & About Card
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Tentang Elenio Connect",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )

                        Text(
                            text = "Versi Aplikasi: 1.0.0 (Build 2026.1)",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElenioOffWhite.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Pengembang: PT Elenio Digital Indonesia",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElenioOffWhite.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            onClick = {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://elenio.id/"))
                                context.startActivity(browserIntent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = ElenioBlueContainer.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElenioElectricBlue.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = ElenioElectricBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Kunjungi Website Resmi: https://elenio.id/",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
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
