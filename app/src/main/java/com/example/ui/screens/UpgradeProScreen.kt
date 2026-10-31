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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.BillingRepository
import com.example.data.repository.PurchaseState
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
fun UpgradeProScreen(
    viewModel: CctvViewModel,
    modifier: Modifier = Modifier
) {
    val isPro by viewModel.isProUser.collectAsState()
    val purchaseState by viewModel.billingRepository.purchaseState.collectAsState()

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
                        modifier = Modifier.testTag("upgrade_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Elenio Connect PRO",
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
            item {
                // Hero Plan Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = ElenioNavyCard),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ElenioElectricBlue),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ElenioElectricBlue.copy(alpha = 0.2f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ElenioElectricBlue,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isPro) "Paket PRO Aktif" else "Beli Sekali, Nikmati Selamanya",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Tanpa langganan bulanan. Beli satu kali untuk semua fitur premium di akun Google Play Anda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ElenioOffWhite.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ElenioBlueContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = BillingRepository.PRICE_FORMATTED,
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "/ Seumur Hidup",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF93C5FD)
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Comparison Feature Table
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
                        Text(
                            text = "Perbandingan Fitur:",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        ComparisonRow(
                            feature = "Maksimal Kamera Tersimpan",
                            freeVal = "2 Kamera",
                            proVal = "Hingga 8 Kamera",
                            isHighlight = true
                        )
                        ComparisonRow(
                            feature = "Bebas Iklan",
                            freeVal = "Iklan Banner",
                            proVal = "100% Bebas Iklan",
                            isHighlight = true
                        )
                        ComparisonRow(
                            feature = "Tata Letak Grid Monitoring",
                            freeVal = "1×1 & 2×2",
                            proVal = "1×1, 2×2, 3×3",
                            isHighlight = false
                        )
                        ComparisonRow(
                            feature = "Kamera Favorit / Pinning",
                            freeVal = "Tidak",
                            proVal = "Ya, Prioritas",
                            isHighlight = false
                        )
                        ComparisonRow(
                            feature = "ONVIF LAN Discovery",
                            freeVal = "Didukung",
                            proVal = "Didukung",
                            isHighlight = false
                        )
                        ComparisonRow(
                            feature = "Manual RTSP Transport",
                            freeVal = "TCP / UDP",
                            proVal = "TCP / UDP",
                            isHighlight = false
                        )
                    }
                }
            }

            item {
                // Purchase Status Notice or Action Button
                when (purchaseState) {
                    is PurchaseState.Processing -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = ElenioElectricBlue)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Menghubungkan ke Google Play Billing...", color = Color.White)
                            }
                        }
                    }
                    is PurchaseState.Success -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ElenioSuccess.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElenioSuccess),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ElenioSuccess)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Pembelian PRO Berhasil!",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Semua fitur PRO telah aktif di perangkat Anda.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF86EFAC)
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        if (!isPro) {
                            Button(
                                onClick = { viewModel.purchasePro() },
                                colors = ButtonDefaults.buttonColors(containerColor = ElenioElectricBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("buy_pro_button")
                            ) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Beli PRO Seumur Hidup — Rp59.000",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Restore Purchases Button
                OutlinedButton(
                    onClick = { viewModel.restorePurchase() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElenioNavyBorder),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("restore_purchases_btn")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pulihkan Pembelian (Restore Purchases)")
                }
            }
        }
    }
}

@Composable
private fun ComparisonRow(
    feature: String,
    freeVal: String,
    proVal: String,
    isHighlight: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            style = MaterialTheme.typography.bodySmall,
            color = if (isHighlight) Color.White else ElenioSecondaryText,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = freeVal,
            style = MaterialTheme.typography.bodySmall,
            color = ElenioSecondaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = proVal,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isHighlight) ElenioElectricBlue else Color.White
            ),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.1f)
        )
    }
}
