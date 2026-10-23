package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.simulator.ScamDrillRepository
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBluePrimary
import com.example.ui.theme.SafeGreen
import com.example.ui.viewmodel.ScamShieldViewModel

@Composable
fun ScamDrillsScreen(
    viewModel: ScamShieldViewModel,
    modifier: Modifier = Modifier
) {
    val currentIdx by viewModel.currentDrillIndex.collectAsState()
    val selectedOption by viewModel.drillSelectedOption.collectAsState()
    val score by viewModel.drillScore.collectAsState()
    val answersCount by viewModel.drillAnswersCount.collectAsState()

    val totalDrills = ScamDrillRepository.drills.size
    val drill = ScamDrillRepository.drills[currentIdx]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("scam_drills_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Drills",
                            tint = CyberBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Scam Defense Simulator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Live Interactive Cyber Resilience Drills",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberBluePrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Score: $score / 100",
                        fontWeight = FontWeight.Bold,
                        color = CyberBluePrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            // Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Scenario ${currentIdx + 1} of $totalDrills",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = drill.category,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (currentIdx + 1) / totalDrills.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CyberBluePrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        // Drill Scenario Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = drill.callerOrSender,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AlertRed.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${drill.urgencyLevel} URGENCY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = drill.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = drill.situationText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Options
        item {
            Text(
                text = "Choose your counter-action:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(drill.options.size) { optIdx ->
            val opt = drill.options[optIdx]
            val isSelected = selectedOption == optIdx
            val isAnswered = selectedOption != null

            val borderColor = when {
                !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
                opt.isCorrect -> SafeGreen
                isSelected && !opt.isCorrect -> AlertRed
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            val bgColor = when {
                !isAnswered -> MaterialTheme.colorScheme.surface
                opt.isCorrect -> SafeGreen.copy(alpha = 0.12f)
                isSelected && !opt.isCorrect -> AlertRed.copy(alpha = 0.12f)
                else -> MaterialTheme.colorScheme.surface
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                    .clickable(enabled = !isAnswered) {
                        viewModel.answerDrill(optIdx)
                    },
                colors = CardDefaults.cardColors(containerColor = bgColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${('A' + optIdx)}. ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = opt.text,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (isAnswered) {
                            if (opt.isCorrect) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Correct", tint = SafeGreen)
                            } else if (isSelected) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Wrong", tint = AlertRed)
                            }
                        }
                    }

                    if (isAnswered && (isSelected || opt.isCorrect)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = opt.explanation,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (opt.isCorrect) SafeGreen else AlertRed
                        )
                    }
                }
            }
        }

        if (selectedOption != null) {
            item {
                Button(
                    onClick = { viewModel.nextDrill() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (currentIdx < totalDrills - 1) "Next Scenario →" else "Complete Drill & Restart",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
