package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AiAdvisorScreen
import com.example.ui.screens.CommunityBlacklistScreen
import com.example.ui.screens.EmergencySosScreen
import com.example.ui.screens.RadarDashboardScreen
import com.example.ui.screens.ScamDrillsScreen
import com.example.ui.screens.VerificationLabScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberAccentPurple
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ScamShieldViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: ScamShieldViewModel = viewModel()) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentJurisdiction by viewModel.currentJurisdiction.collectAsState()
    val context = LocalContext.current

    // Handle system back navigation to return to Dashboard first
    BackHandler(enabled = selectedTab != 0) {
        viewModel.setTab(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .clickable { viewModel.setTab(4) } // Open Global SOS / Country Selector
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${currentJurisdiction.flagEmoji} ${currentJurisdiction.countryCode}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_scam_shield_1792783918021),
                            contentDescription = "Suraksha Shield Logo",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Suraksha Shield",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.setTab(5) }) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "AI Advisor",
                            tint = CyberAccentPurple,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${currentJurisdiction.primaryEmergencyNumber}")
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("app_bar_sos_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "SOS",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SOS", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = { Icon(imageVector = Icons.Default.Radar, contentDescription = "Radar") },
                    label = { Text("Radar", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_tab_radar")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = { Icon(imageVector = Icons.Default.Build, contentDescription = "12 Tools") },
                    label = { Text("12 Tools", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_tab_tools")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = { Icon(imageVector = Icons.Default.School, contentDescription = "Drills") },
                    label = { Text("Drills", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_tab_drills")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.setTab(3) },
                    icon = { Icon(imageVector = Icons.Default.Security, contentDescription = "Blacklist") },
                    label = { Text("Blacklist", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_tab_blacklist")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { viewModel.setTab(4) },
                    icon = { Icon(imageVector = Icons.Default.Public, contentDescription = "Global SOS") },
                    label = { Text("Global SOS", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_tab_sos")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> RadarDashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTool = { toolIdx -> viewModel.navigateToTool(toolIdx) },
                    onNavigateToSos = { viewModel.setTab(4) },
                    onNavigateToDrills = { viewModel.setTab(2) }
                )
                1 -> VerificationLabScreen(viewModel = viewModel)
                2 -> ScamDrillsScreen(viewModel = viewModel)
                3 -> CommunityBlacklistScreen(viewModel = viewModel)
                4 -> EmergencySosScreen(viewModel = viewModel)
                5 -> AiAdvisorScreen(viewModel = viewModel)
            }
        }
    }
}
