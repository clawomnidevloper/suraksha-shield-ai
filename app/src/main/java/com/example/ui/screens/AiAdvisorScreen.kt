package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QuickSampleChips
import com.example.ui.theme.CyberAccentPurple
import com.example.ui.theme.CyberBluePrimary
import com.example.ui.viewmodel.ScamShieldViewModel

@Composable
fun AiAdvisorScreen(
    viewModel: ScamShieldViewModel,
    modifier: Modifier = Modifier
) {
    val queryText by viewModel.aiQueryText.collectAsState()
    val responseText by viewModel.aiAdvisorResponse.collectAsState()
    val isLoading by viewModel.aiAdvisorLoading.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    val scenarios = listOf(
        "Digital Arrest Threat" to "A caller claiming to be from Mumbai Crime Branch showed a police badge on Skype and says a parcel with 5 passports and MDMA drugs in my name was stopped by customs. They asked me to transfer ₹1,50,000 to an RBI secret verification account to avoid immediate arrest. Is this real?",
        "OLX QR Code Payment" to "I am selling a sofa on OLX. A buyer says he works in the Army and sent me a QR code to scan on Google Pay to receive ₹12,000 in advance. Should I scan it?",
        "Telegram Video Like Job" to "A HR recruiter on WhatsApp offered ₹150 for liking 3 YouTube videos. They paid me ₹150 on Google Pay! Now they want me to join a VIP Telegram group and deposit ₹5,000 to earn ₹8,000 in 30 minutes. Is this safe?",
        "Electricity Bill SMS" to "Received SMS: Electricity power will be disconnected at 9:30 PM today because bill update is pending. Call 9876543210. Is this genuine?"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_advisor_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberAccentPurple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "AI Forensic Advisor",
                        tint = CyberAccentPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AI Cyber Threat Forensic Advisor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Powered by Gemini & Advanced Threat Intelligence",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            QuickSampleChips(
                title = "Common Ambiguous Scenarios:",
                samples = scenarios,
                onSelect = { scenario ->
                    viewModel.aiQueryText.value = scenario
                    viewModel.runAiAdvisorQuery()
                }
            )
        }

        item {
            OutlinedTextField(
                value = queryText,
                onValueChange = { viewModel.aiQueryText.value = it },
                label = { Text("Describe the suspicious call, message, or situation:") },
                placeholder = { Text("Explain what happened, what the person said, or what they demanded...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .testTag("ai_advisor_input"),
                trailingIcon = {
                    IconButton(onClick = {
                        val clip = clipboardManager.getText()?.text
                        if (!clip.isNullOrBlank()) {
                            viewModel.aiQueryText.value = clip
                        }
                    }) {
                        Icon(imageVector = Icons.Default.ContentPaste, contentDescription = "Paste Clipboard")
                    }
                }
            )
        }

        item {
            Button(
                onClick = { viewModel.runAiAdvisorQuery() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("run_ai_advisor_button"),
                enabled = !isLoading && queryText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberAccentPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Investigating Threat Vectors...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Investigate")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Perform Deep AI Scam Forensic Diagnosis", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (responseText.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_advisor_result_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Report",
                                tint = CyberAccentPurple,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Forensic Threat Report",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = responseText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
