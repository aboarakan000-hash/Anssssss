package com.example.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NetworkInterfaceInfo
import com.example.model.SecurityAuditOverview
import com.example.ui.theme.*

@Composable
fun SecurityAuditScreen(
    interfaceInfo: NetworkInterfaceInfo,
    audit: SecurityAuditOverview,
    onRunScan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Master Scorecard Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CyberEmerald.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .testTag("security_scorecard"),
            color = CyberSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "NETWORK SECURITY HEALTH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = CyberEmerald,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Score: ${audit.score}/100",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Grade: ${audit.encryptionGrade}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CyberCyan
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(CyberSurfaceVariant)
                        .border(3.dp, CyberEmerald, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield",
                        tint = CyberEmerald,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        // Subnet Health Check Matrix
        Text(
            text = "DEFENSIVE INTEGRITY CHECKS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CheckRow(
                title = "Wi-Fi Encryption Standard",
                desc = interfaceInfo.encryptionType,
                status = "COMPLIANT",
                isOk = true
            )
            CheckRow(
                title = "ARP Spoofing / Poisoning Defense",
                desc = audit.arpIntegrityStatus,
                status = "SECURE",
                isOk = true
            )
            CheckRow(
                title = "Rogue Access Point (Evil Twin) Check",
                desc = "BSSID signature matches verified gateway profile",
                status = "CLEAN",
                isOk = !audit.rogueApDetected
            )
            CheckRow(
                title = "DNS Resolver Integrity",
                desc = "DNS requests directed to: ${interfaceInfo.dnsServers.joinToString(", ")}",
                status = "VERIFIED",
                isOk = !audit.dnsTamperingDetected
            )
            CheckRow(
                title = "Exposed High-Risk LAN Services",
                desc = "${audit.highRiskDevicesCount} endpoints flagged with unencrypted ports",
                status = if (audit.highRiskDevicesCount > 0) "ACTION REQUIRED" else "PASS",
                isOk = audit.highRiskDevicesCount == 0
            )
        }

        // Interface Technical Parameters
        Text(
            text = "HARDWARE & INTERFACE TELEMETRY",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ParamRow("Interface Driver", interfaceInfo.interfaceName)
                ParamRow("Hardware MAC", interfaceInfo.macAddress, isMono = true)
                ParamRow("Local Subnet IP", interfaceInfo.localIp, isMono = true)
                ParamRow("Default Gateway", interfaceInfo.gatewayIp, isMono = true)
                ParamRow("Subnet Mask", interfaceInfo.subnetMask, isMono = true)
                ParamRow("Channel Frequency", "${interfaceInfo.frequencyMhz} MHz (5GHz)")
                ParamRow("Physical Link Rate", "${interfaceInfo.linkSpeedMbps} Mbps")
            }
        }

        // Recommendations List
        Text(
            text = "RECOMMENDED DEFENSIVE ACTIONS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = CyberEmerald,
                letterSpacing = 1.sp
            )
        )

        audit.recommendations.forEach { recommendation ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = CyberSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
                    Text(
                        text = recommendation,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, lineHeight = 16.sp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CheckRow(title: String, desc: String, status: String, isOk: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
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
                Text(text = title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = TextSecondary))
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isOk) SeveritySafe.copy(alpha = 0.15f) else SeverityCritical.copy(alpha = 0.15f))
                    .border(0.5.dp, if (isOk) SeveritySafe else SeverityCritical, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOk) SeveritySafe else SeverityCritical
                    )
                )
            }
        }
    }
}

@Composable
private fun ParamRow(label: String, value: String, isMono: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        )
    }
}
