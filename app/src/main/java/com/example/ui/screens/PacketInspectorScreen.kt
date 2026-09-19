package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.PacketEntry
import com.example.model.PacketProtocol
import com.example.ui.theme.*

@Composable
fun PacketInspectorScreen(
    packets: List<PacketEntry>,
    isCaptureActive: Boolean,
    selectedFilter: PacketProtocol?,
    searchQuery: String,
    onToggleCapture: () -> Unit,
    onClearPackets: () -> Unit,
    onSelectFilter: (PacketProtocol?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectPacket: (PacketEntry) -> Unit
) {
    val filteredPackets = remember(packets, selectedFilter, searchQuery) {
        packets.filter { p ->
            val matchesFilter = selectedFilter == null || p.protocol == selectedFilter
            val matchesSearch = searchQuery.isEmpty() ||
                    p.sourceIp.contains(searchQuery, ignoreCase = true) ||
                    p.destIp.contains(searchQuery, ignoreCase = true) ||
                    p.info.contains(searchQuery, ignoreCase = true) ||
                    p.protocol.name.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    val totalBytes = remember(packets) { packets.sumOf { it.lengthBytes } }
    val suspiciousCount = remember(packets) { packets.count { it.isSuspicious } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Telemetry Summary Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                color = CyberSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("PACKET STREAM", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TextSecondary))
                    Text(
                        text = "${packets.size} frames",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = CyberCyan
                        )
                    )
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                color = CyberSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("THROUGHPUT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TextSecondary))
                    Text(
                        text = "${totalBytes / 1024} KB",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = CyberEmerald
                        )
                    )
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                color = CyberSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (suspiciousCount > 0) SeverityCritical else CyberCardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("ANOMALIES", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TextSecondary))
                    Text(
                        text = "$suspiciousCount alerts",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (suspiciousCount > 0) SeverityCritical else SeveritySafe
                        )
                    )
                }
            }
        }

        // Action Controls & Search
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Filter IP, Protocol or Port...", fontSize = 12.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("input_packet_search"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface,
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberCardBorder
                )
            )

            // Pause/Resume Capture Button
            FilledIconButton(
                onClick = onToggleCapture,
                shape = RoundedCornerShape(8.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isCaptureActive) CyberSurfaceVariant else CyberEmerald,
                    contentColor = if (isCaptureActive) TextPrimary else Color.Black
                ),
                modifier = Modifier.size(48.dp).testTag("btn_toggle_packet_capture")
            ) {
                Icon(
                    imageVector = if (isCaptureActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Toggle Live Feed"
                )
            }

            // Clear Button
            IconButton(
                onClick = onClearPackets,
                modifier = Modifier.size(48.dp).testTag("btn_clear_packets")
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Packets", tint = TextSecondary)
            }
        }

        // Protocol Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { onSelectFilter(null) },
                    label = { Text("All Protocols", style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberEmerald.copy(alpha = 0.2f),
                        selectedLabelColor = CyberEmerald
                    )
                )
            }
            items(PacketProtocol.values()) { proto ->
                FilterChip(
                    selected = selectedFilter == proto,
                    onClick = { onSelectFilter(if (selectedFilter == proto) null else proto) },
                    label = { Text(proto.name, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(proto.colorHex).copy(alpha = 0.25f),
                        selectedLabelColor = Color(proto.colorHex)
                    )
                )
            }
        }

        // Live Packet Stream Table
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filteredPackets, key = { it.id }) { packet ->
                PacketRowItem(
                    packet = packet,
                    onClick = { onSelectPacket(packet) }
                )
            }

            if (filteredPackets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isCaptureActive) "Listening for active network traffic..." else "Capture paused.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PacketRowItem(
    packet: PacketEntry,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                if (packet.isSuspicious) SeverityCritical.copy(alpha = 0.7f) else CyberCardBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .testTag("packet_row_${packet.id}"),
        color = CyberSurface
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(packet.protocol.colorHex))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = packet.protocol.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 9.sp
                            )
                        )
                    }
                    Text(
                        text = "${packet.sourceIp}:${packet.sourcePort} → ${packet.destIp}:${packet.destPort}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = packet.timestamp,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = packet.info,
                    fontSize = 11.sp,
                    color = if (packet.isSuspicious) SeverityCritical else TextSecondary,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${packet.lengthBytes}B",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}
