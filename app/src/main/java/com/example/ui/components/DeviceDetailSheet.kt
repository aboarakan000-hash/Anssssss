package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailSheet(
    device: NetworkDevice,
    isProbingPort: Boolean,
    probeResult: PortInfo?,
    isPinging: Boolean,
    pingResults: List<Long>,
    onProbePort: (String, Int) -> Unit,
    onRunPing: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var customPortInput by remember { mutableStateOf("80") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = CyberCardBorder) },
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberSurfaceVariant)
                            .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (device.deviceType) {
                                DeviceType.GATEWAY -> Icons.Default.Router
                                DeviceType.SMARTPHONE -> Icons.Default.Smartphone
                                DeviceType.LAPTOP -> Icons.Default.Laptop
                                DeviceType.SERVER -> Icons.Default.Dns
                                DeviceType.PRINTER -> Icons.Default.Print
                                DeviceType.IOT_DEVICE -> Icons.Default.Sensors
                                else -> Icons.Default.Devices
                            },
                            contentDescription = "Device Icon",
                            tint = CyberCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column {
                        Text(
                            text = device.hostname,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "${device.vendor} • ${device.deviceType.label}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary
                            )
                        )
                    }
                }

                // Security Score Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            when {
                                device.securityScore >= 80 -> SeveritySafe.copy(alpha = 0.15f)
                                device.securityScore >= 60 -> SeverityMedium.copy(alpha = 0.15f)
                                else -> SeverityCritical.copy(alpha = 0.15f)
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                device.securityScore >= 80 -> SeveritySafe
                                device.securityScore >= 60 -> SeverityMedium
                                else -> SeverityCritical
                            },
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Score: ${device.securityScore}/100",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when {
                                device.securityScore >= 80 -> SeveritySafe
                                device.securityScore >= 60 -> SeverityMedium
                                else -> SeverityCritical
                            }
                        )
                    )
                }
            }

            // Specs Grid
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CyberDarkBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpecRow("IPv4 Address", device.ip, isMonospace = true)
                    SpecRow("MAC Address (OUI)", device.macAddress, isMonospace = true)
                    SpecRow("ARP Resolution", device.discoveryMethod)
                    SpecRow("ARP Flags", "${device.arpFlags} (Neighbor Valid)")
                    SpecRow("Connection Link", device.connectionType)
                    SpecRow("Signal Strength", "${device.signalDbm} dBm")
                    SpecRow("Subnet RTT Latency", "${device.latencyMs} ms")
                    SpecRow("Telemetry Bandwidth", "${device.bandwidthUsageKbps} kbps")
                }
            }

            // Ping Diagnostic Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DIAGNOSTIC PING & JITTER",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            letterSpacing = 1.sp
                        )
                    )
                    OutlinedButton(
                        onClick = { onRunPing(device.ip) },
                        enabled = !isPinging,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_ping_test"),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isPinging) "Pinging..." else "Ping Host", style = MaterialTheme.typography.labelSmall)
                    }
                }

                if (pingResults.isNotEmpty() || isPinging) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = TerminalBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            pingResults.forEachIndexed { index, ms ->
                                Text(
                                    text = "64 bytes from ${device.ip}: icmp_seq=${index + 1} time=${ms}ms",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = TerminalGreen
                                )
                            }
                            if (isPinging) {
                                Text(
                                    text = "transmitting echo probes...",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // Open Ports Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "OPEN PORTS & ACTIVE SERVICES (${device.openPorts.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CyberEmerald,
                        letterSpacing = 1.sp
                    )
                )

                if (device.openPorts.isEmpty()) {
                    Text(
                        text = "No standard open ports discovered on this node.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                } else {
                    device.openPorts.forEach { portInfo ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = CyberSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when (portInfo.riskSeverity) {
                                    RiskSeverity.CRITICAL -> SeverityCritical
                                    RiskSeverity.HIGH -> SeverityHigh
                                    RiskSeverity.MEDIUM -> SeverityMedium
                                    else -> CyberCardBorder
                                }
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Port ${portInfo.port}/${portInfo.protocol}",
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = portInfo.serviceName,
                                            style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan)
                                        )
                                    }
                                    RiskChip(portInfo.riskSeverity)
                                }
                                if (portInfo.banner.isNotEmpty()) {
                                    Text(
                                        text = "Banner: ${portInfo.banner}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                if (portInfo.securityNotes.isNotEmpty()) {
                                    Text(
                                        text = portInfo.securityNotes,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = if (portInfo.riskSeverity >= RiskSeverity.HIGH) SeverityHigh else TextMuted
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Custom Port Probe
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = CyberDarkBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "MANUAL PORT AUDIT PROBE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customPortInput,
                            onValueChange = { if (it.length <= 5) customPortInput = it },
                            label = { Text("Port (1-65535)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_probe_port"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberCardBorder
                            )
                        )

                        Button(
                            onClick = {
                                val portNum = customPortInput.toIntOrNull() ?: 80
                                onProbePort(device.ip, portNum)
                            },
                            enabled = !isProbingPort && customPortInput.isNotEmpty(),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("btn_probe_port"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black)
                        ) {
                            Text(if (isProbingPort) "Probing..." else "Probe")
                        }
                    }

                    if (probeResult != null) {
                        Text(
                            text = "Result: Port ${probeResult.port} -> ${if (probeResult.isOpen) "OPEN (${probeResult.banner})" else "CLOSED"}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = if (probeResult.isOpen) CyberEmerald else SeverityCritical
                        )
                    }
                }
            }

            // Security Vulnerabilities & Mitigations
            if (device.vulnerabilities.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "VULNERABILITIES & DEFENSIVE MITIGATION",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SeverityCritical,
                            letterSpacing = 1.sp
                        )
                    )

                    device.vulnerabilities.forEach { vuln ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = CyberSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SeverityCritical.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = vuln.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SeverityCritical
                                        )
                                    )
                                    vuln.cveRef?.let {
                                        Text(
                                            text = it,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                                Text(
                                    text = vuln.description,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                                )
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(6.dp),
                                    color = CyberDarkBg
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = CyberEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Mitigation: ${vuln.mitigation}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 11.sp,
                                                color = CyberEmerald
                                            )
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
}

@Composable
private fun SpecRow(label: String, value: String, isMonospace: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        )
    }
}

@Composable
fun RiskChip(severity: RiskSeverity) {
    val (bgColor, textColor) = when (severity) {
        RiskSeverity.CRITICAL -> Pair(SeverityCritical.copy(alpha = 0.2f), SeverityCritical)
        RiskSeverity.HIGH -> Pair(SeverityHigh.copy(alpha = 0.2f), SeverityHigh)
        RiskSeverity.MEDIUM -> Pair(SeverityMedium.copy(alpha = 0.2f), SeverityMedium)
        RiskSeverity.LOW -> Pair(SeverityLow.copy(alpha = 0.2f), SeverityLow)
        RiskSeverity.SAFE -> Pair(SeveritySafe.copy(alpha = 0.2f), SeveritySafe)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = severity.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}
