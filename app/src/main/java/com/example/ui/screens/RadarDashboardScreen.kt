package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.global.GlobalJurisdictionRepository
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBluePrimary
import com.example.ui.theme.CyberBlueSecondary
import com.example.ui.theme.SafeGreen
import com.example.ui.viewmodel.ScamShieldViewModel

data class ToolGridItem(
    val index: Int,
    val title: String,
    val category: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun RadarDashboardScreen(
    viewModel: ScamShieldViewModel,
    onNavigateToTool: (Int) -> Unit,
    onNavigateToSos: () -> Unit,
    onNavigateToDrills: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cloudStatus by viewModel.cloudSyncStatus.collectAsState()
    val selectedCountryCode by viewModel.selectedCountryCode.collectAsState()
    val currentJurisdiction by viewModel.currentJurisdiction.collectAsState()

    val countries = GlobalJurisdictionRepository.getAllCountries()

    val toolsList = listOf(
        ToolGridItem(0, "SMS Phishing Radar", "Text & Messages", Icons.Default.Sms, CyberBluePrimary),
        ToolGridItem(1, "UPI & QR Inspector", "Banking Fraud", Icons.Default.QrCodeScanner, CyberBlueSecondary),
        ToolGridItem(2, "Phishing URL Analyzer", "Domains & Links", Icons.Default.Language, Color(0xFF6366F1)),
        ToolGridItem(3, "Caller Risk Inspector", "Phone Numbers", Icons.Default.Call, Color(0xFF10B981)),
        ToolGridItem(4, "Malicious APK Shield", "Trojan Apps", Icons.Default.Android, Color(0xFFF59E0B)),
        ToolGridItem(5, "Digital Arrest Defender", "Police Extortion", Icons.Default.Gavel, AlertRed),
        ToolGridItem(6, "Job & Task Fraud Calc", "Telegram Scams", Icons.Default.Work, Color(0xFFEC4899)),
        ToolGridItem(7, "Crypto Ponzi Verifier", "Investment Fraud", Icons.Default.MoneyOff, Color(0xFF8B5CF6)),
        ToolGridItem(8, "Call Forward USSD", "Secret Divert", Icons.Default.LockReset, Color(0xFF14B8A6)),
        ToolGridItem(9, "Emergency 1930 Guide", "Fund Freeze Desk", Icons.Default.Security, AlertRed),
        ToolGridItem(10, "Email BEC & SPF Shield", "Header Phish", Icons.Default.Email, Color(0xFF0EA5E9)),
        ToolGridItem(11, "AI Voice Clone Defense", "Deepfake Audio", Icons.Default.Mic, Color(0xFFE11D48))
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("radar_dashboard_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Hero Cyber Command Center Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_cyber_radar_1792783931117),
                        contentDescription = "Global Cyber Fraud Command Radar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xEE0A0F1D)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SafeGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GLOBAL THREAT RADAR ACTIVE",
                                color = SafeGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Suraksha Shield Defense",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Connected to Global Threat Network (${currentJurisdiction.flagEmoji} ${currentJurisdiction.countryName})",
                            color = Color(0xFFCBD5E1),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Live Cloud Backend Sync Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBluePrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (cloudStatus.isSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                            contentDescription = "Cloud Status",
                            tint = if (cloudStatus.isOnline) SafeGreen else AlertRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (cloudStatus.isSyncing) "Syncing with Cloud Threat API..." else "Cloud Threat Network: Live",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${cloudStatus.totalCloudRecords} Indicators Synced • Ping: ${cloudStatus.latencyMs}ms",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.syncWithCloudBackend() },
                        shape = RoundedCornerShape(8.dp),
                        enabled = !cloudStatus.isSyncing
                    ) {
                        if (cloudStatus.isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Sync", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Country Selector Chips
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = "Country", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Active Country Jurisdiction:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    countries.forEach { country ->
                        FilterChip(
                            selected = selectedCountryCode == country.countryCode,
                            onClick = { viewModel.setCountry(country.countryCode) },
                            label = { Text("${country.flagEmoji} ${country.countryName}", fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // Emergency Quick Dial Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("emergency_quick_card"),
                colors = CardDefaults.cardColors(
                    containerColor = AlertRed.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Urgent Help",
                            tint = AlertRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Victim of Financial Fraud?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${currentJurisdiction.flagEmoji} ${currentJurisdiction.primaryEmergencyLabel}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${currentJurisdiction.primaryEmergencyNumber}")
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("call_1930_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Interactive Scam Drills Launch Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDrills() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(imageVector = Icons.Default.School, contentDescription = "Drills", tint = CyberBluePrimary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Interactive Scam Drills & Defense Quiz",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Test your resilience against AI clones, fake police, and UPI traps",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "Start →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberBluePrimary
                    )
                }
            }
        }

        // 12 Real-time Tools Grid Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "12 Anti-Fraud Verification Tools",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Real-Time Engines",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Render tools in pairs (rows of 2)
        val toolPairs = toolsList.chunked(2)
        items(toolPairs.size) { pairIdx ->
            val pair = toolPairs[pairIdx]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEach { tool ->
                    ToolCardItem(
                        tool = tool,
                        onClick = { onNavigateToTool(tool.index) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ToolCardItem(
    tool: ToolGridItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag("tool_card_${tool.index}")
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tool.accentColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = tool.title,
                    tint = tool.accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = tool.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = tool.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Launch Tool →",
                    style = MaterialTheme.typography.labelSmall,
                    color = tool.accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

