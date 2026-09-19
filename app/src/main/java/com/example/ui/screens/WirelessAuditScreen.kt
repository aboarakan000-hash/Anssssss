package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NetworkInterfaceInfo
import com.example.model.RiskSeverity
import com.example.model.WirelessAssessmentResult
import com.example.model.WirelessFinding
import com.example.ui.theme.*

@Composable
fun WirelessAuditScreen(
    interfaceInfo: NetworkInterfaceInfo,
    auditResult: WirelessAssessmentResult,
    isAuditing: Boolean,
    onRunAudit: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radar_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Master Wireless Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CyberEmerald.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .testTag("wireless_audit_scorecard"),
            color = CyberSurface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "WIRELESS SECURITY ASSESSMENT",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = CyberEmerald,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "اختبار وتدقيق الأمان اللاسلكي",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        )
                        Text(
                            text = "${auditResult.ssid} • ${auditResult.bssid}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = CyberCyan
                        )
                    }

                    // Score Circle
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .scale(if (isAuditing) pulseScale else 1f)
                            .clip(CircleShape)
                            .background(CyberSurfaceVariant)
                            .border(3.dp, CyberEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${auditResult.securityScore}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "SCORE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberEmerald
                                )
                            )
                        }
                    }
                }

                // RF Telemetry Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricMiniBox("SIGNAL (RSSI)", "${auditResult.signalStrengthDbm} dBm", CyberCyan, Modifier.weight(1f))
                    MetricMiniBox("FREQUENCY", "${auditResult.frequencyMhz} MHz", TextPrimary, Modifier.weight(1f))
                    MetricMiniBox("CHANNEL", "Ch ${auditResult.channel}", CyberEmerald, Modifier.weight(1f))
                }

                // Run Audit Action Button
                Button(
                    onClick = onRunAudit,
                    enabled = !isAuditing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_run_wireless_audit"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = if (isAuditing) Icons.Default.HourglassEmpty else Icons.Default.Sensors,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAuditing) "Executing Penetration & Cipher Audits..." else "Run Wireless Security Audit / بدء الفحص اللاسلكي",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Defensive Posture Matrix
        Text(
            text = "PENETRATION RESILIENCE & CIPHER AUDIT",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SecurityCheckCard(
                title = "WPA3 / WPA2 Cipher Defense",
                titleAr = "قوة التشفير ومقاومة هجمات القواميس",
                status = "SECURE",
                isOk = true,
                desc = "Active: ${auditResult.cipherSuite}. Resistant to offline dictionary hash cracking."
            )

            SecurityCheckCard(
                title = "WPS PIN Exploit Audit (Pixie-Dust / Reaver)",
                titleAr = "تدقيق ثغرات إعداد الواي فاي السريع (WPS)",
                status = if (auditResult.isWpsVulnerable) "VULNERABLE" else "SECURE",
                isOk = !auditResult.isWpsVulnerable,
                desc = auditResult.wpsStatus
            )

            SecurityCheckCard(
                title = "Management Frame Protection (802.11w PMF)",
                titleAr = "الحماية من هجمات فصل العملاء (Deauth Flood)",
                status = if (auditResult.isDeauthResistant) "SECURE" else "VULNERABLE",
                isOk = auditResult.isDeauthResistant,
                desc = auditResult.pmfStatus
            )

            SecurityCheckCard(
                title = "Rogue AP & Evil Twin Detection",
                titleAr = "كشف نقاط الوصول المزيفة وشبيهة الشبكة",
                status = "CLEAN",
                isOk = true,
                desc = auditResult.evilTwinRisk
            )

            SecurityCheckCard(
                title = "4-Way Handshake Interception Resilience",
                titleAr = "مناعة مصافحة الاتصال ضد الاعتراض والتجسس",
                status = "PROTECTED",
                isOk = true,
                desc = auditResult.handshakeSniffingRisk
            )
        }

        // Detailed Findings List
        Text(
            text = "DETAILED AUDIT FINDINGS (${auditResult.findings.size})",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = CyberEmerald,
                letterSpacing = 1.sp
            )
        )

        auditResult.findings.forEach { finding ->
            FindingCard(finding)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MetricMiniBox(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = CyberDarkBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TextSecondary))
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = valueColor
                )
            )
        }
    }
}

@Composable
private fun SecurityCheckCard(
    title: String,
    titleAr: String,
    status: String,
    isOk: Boolean,
    desc: String
) {
    val statusColor = if (isOk) SeveritySafe else SeverityCritical

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CyberSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isOk) CyberCardBorder else SeverityCritical.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
                Text(
                    text = titleAr,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = CyberCyan)
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = TextSecondary)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .border(0.5.dp, statusColor, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                )
            }
        }
    }
}

@Composable
private fun FindingCard(finding: WirelessFinding) {
    val isSafe = finding.status == "SECURE"
    val isWarning = finding.status == "WARNING"
    val badgeColor = if (isSafe) SeveritySafe else if (isWarning) SeverityMedium else SeverityCritical

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CyberSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "[${finding.id}] ${finding.title}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(0.5.dp, badgeColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = finding.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = badgeColor
                        )
                    )
                }
            }

            Text(
                text = finding.titleAr,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = CyberEmerald
                )
            )

            Text(
                text = finding.details,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            )

            Text(
                text = finding.detailsAr,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 14.sp
                )
            )

            HorizontalDivider(color = CyberCardBorder, thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                )
                Column {
                    Text(
                        text = "Recommendation: ${finding.recommendation}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            color = CyberCyan
                        )
                    )
                    Text(
                        text = "التوصية: ${finding.recommendationAr}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    )
                }
            }
        }
    }
}
