package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun LogAnalyzerScreen(
    logs: List<SystemLogEntry>,
    selectedSeverity: LogSeverity?,
    searchQuery: String,
    onSelectSeverity: (LogSeverity?) -> Unit,
    onSearchChange: (String) -> Unit,
    onClearLogs: () -> Unit,
    // Packet Inspection integration
    packets: List<PacketEntry> = emptyList(),
    isPacketCaptureActive: Boolean = true,
    selectedPacketFilter: PacketProtocol? = null,
    packetSearchQuery: String = "",
    onTogglePacketCapture: () -> Unit = {},
    onClearPackets: () -> Unit = {},
    onSelectPacketFilter: (PacketProtocol?) -> Unit = {},
    onPacketSearchQueryChange: (String) -> Unit = {},
    onSelectPacket: (PacketEntry) -> Unit = {}
) {
    var activeSubMode by remember { mutableStateOf("LOGS") } // "LOGS" or "PACKETS"

    val filteredLogs = remember(logs, selectedSeverity, searchQuery) {
        logs.filter { entry ->
            val matchesSeverity = selectedSeverity == null || entry.severity == selectedSeverity
            val matchesSearch = searchQuery.isEmpty() ||
                    entry.message.contains(searchQuery, ignoreCase = true) ||
                    entry.tag.contains(searchQuery, ignoreCase = true) ||
                    (entry.sourceIp?.contains(searchQuery, ignoreCase = true) ?: false)
            matchesSeverity && matchesSearch
        }
    }

    val securityAlertsCount = remember(logs) { logs.count { it.severity == LogSeverity.SECURITY_ALERT } }
    val errorCount = remember(logs) { logs.count { it.severity == LogSeverity.ERROR } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mode Switcher: Logs vs Packets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeSubMode == "LOGS",
                onClick = { activeSubMode = "LOGS" },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("System Logs (${logs.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberEmerald.copy(alpha = 0.2f),
                    selectedLabelColor = CyberEmerald,
                    containerColor = CyberSurface,
                    labelColor = TextSecondary
                ),
                modifier = Modifier.weight(1f).height(34.dp)
            )

            FilterChip(
                selected = activeSubMode == "PACKETS",
                onClick = { activeSubMode = "PACKETS" },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Packet Traffic (${packets.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                    selectedLabelColor = CyberCyan,
                    containerColor = CyberSurface,
                    labelColor = TextSecondary
                ),
                modifier = Modifier.weight(1f).height(34.dp)
            )
        }

        if (activeSubMode == "PACKETS") {
            PacketInspectorScreen(
                packets = packets,
                isCaptureActive = isPacketCaptureActive,
                selectedFilter = selectedPacketFilter,
                searchQuery = packetSearchQuery,
                onToggleCapture = onTogglePacketCapture,
                onClearPackets = onClearPackets,
                onSelectFilter = onSelectPacketFilter,
                onSearchQueryChange = onPacketSearchQueryChange,
                onSelectPacket = onSelectPacket
            )
        } else {
            // Threat Detection Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (securityAlertsCount > 0) SeverityCritical.copy(alpha = 0.12f) else CyberSurface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (securityAlertsCount > 0) SeverityCritical else CyberCardBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (securityAlertsCount > 0) Icons.Default.SecurityUpdateWarning else Icons.Default.VerifiedUser,
                        contentDescription = "Threat Monitor",
                        tint = if (securityAlertsCount > 0) SeverityCritical else CyberEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = if (securityAlertsCount > 0) "ANOMALY DETECTION ALERTS ($securityAlertsCount)" else "SYSTEM LOG INTEGRITY VERIFIED",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (securityAlertsCount > 0) SeverityCritical else CyberEmerald
                            )
                        )
                        Text(
                            text = if (securityAlertsCount > 0) "Immediate audit recommended for highlighted subnet nodes." else "No active intrusion or unauthorized socket activity detected.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Search & Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Filter log message or tag...", fontSize = 12.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("input_log_search"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface,
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberCardBorder
                )
            )

            IconButton(
                onClick = onClearLogs,
                modifier = Modifier.size(48.dp).testTag("btn_clear_logs")
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Logs", tint = TextSecondary)
            }
        }

        // Severity Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedSeverity == null,
                    onClick = { onSelectSeverity(null) },
                    label = { Text("All Events (${logs.size})", style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberEmerald.copy(alpha = 0.2f),
                        selectedLabelColor = CyberEmerald
                    )
                )
            }
            items(LogSeverity.values()) { sev ->
                val count = logs.count { it.severity == sev }
                val color = when (sev) {
                    LogSeverity.SECURITY_ALERT -> SeverityCritical
                    LogSeverity.ERROR -> SeverityHigh
                    LogSeverity.WARN -> SeverityMedium
                    LogSeverity.INFO -> CyberCyan
                }
                FilterChip(
                    selected = selectedSeverity == sev,
                    onClick = { onSelectSeverity(if (selectedSeverity == sev) null else sev) },
                    label = { Text("${sev.name} ($count)", style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color.copy(alpha = 0.25f),
                        selectedLabelColor = color
                    )
                )
            }
        }

        // Terminal Log Console Surface
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
            color = TerminalBg
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredLogs, key = { it.id }) { logEntry ->
                    LogTerminalRow(logEntry)
                }

                if (filteredLogs.isEmpty()) {
                    item {
                        Text(
                            text = "No log records match the selected filter.",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun LogTerminalRow(entry: SystemLogEntry) {
    val sevColor = when (entry.severity) {
        LogSeverity.SECURITY_ALERT -> SeverityCritical
        LogSeverity.ERROR -> SeverityHigh
        LogSeverity.WARN -> SeverityMedium
        LogSeverity.INFO -> TerminalGreen
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .testTag("log_entry_${entry.id}"),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "[${entry.severity.name.take(4)}]",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = sevColor
                )
                Text(
                    text = "${entry.tag}:",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = CyberCyan
                )
            }
            Text(
                text = entry.timestamp,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = TextMuted
            )
        }

        Text(
            text = entry.message,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = TextPrimary,
            lineHeight = 15.sp
        )
    }
}
