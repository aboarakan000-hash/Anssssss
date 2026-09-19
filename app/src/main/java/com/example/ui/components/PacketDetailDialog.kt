package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.window.Dialog
import com.example.model.PacketEntry
import com.example.ui.theme.*

@Composable
fun PacketDetailDialog(
    packet: PacketEntry,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp)),
            color = CyberSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(packet.protocol.colorHex))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = packet.protocol.name,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                        }
                        Text(
                            text = "Frame #${packet.id}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary
                            )
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_close_packet_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Anomaly Banner if present
                if (packet.isSuspicious && packet.suspiciousReason != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = SeverityCritical.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SeverityCritical)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = SeverityCritical, modifier = Modifier.size(20.dp))
                            Text(
                                text = packet.suspiciousReason,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SeverityCritical
                                )
                            )
                        }
                    }
                }

                // Flow & Header Details
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = CyberDarkBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HeaderLine("Timestamp", packet.timestamp)
                        HeaderLine("Source Endpoint", "${packet.sourceIp}:${packet.sourcePort}")
                        HeaderLine("Destination Endpoint", "${packet.destIp}:${packet.destPort}")
                        HeaderLine("Packet Length", "${packet.lengthBytes} bytes")
                        HeaderLine("TCP/IP Flags", packet.flags)
                        HeaderLine("Time To Live (TTL)", "${packet.ttl}")
                        HeaderLine("Protocol Info", packet.info)
                    }
                }

                // Raw Hex Dump
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "PAYLOAD HEX DUMP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            letterSpacing = 1.sp
                        )
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        color = TerminalBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
                    ) {
                        Text(
                            text = packet.payloadHex,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = CyberCyan,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // ASCII Dump
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "ASCII DECODED STRING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberEmerald,
                            letterSpacing = 1.sp
                        )
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        color = TerminalBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
                    ) {
                        Text(
                            text = packet.payloadAscii,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TerminalGreen,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        )
    }
}
