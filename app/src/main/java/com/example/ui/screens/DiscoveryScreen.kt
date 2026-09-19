package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ArpEntry
import com.example.model.DeviceType
import com.example.model.NetworkDevice
import com.example.model.RiskSeverity
import com.example.ui.components.RiskChip
import com.example.ui.theme.*

@Composable
fun DiscoveryScreen(
    devices: List<NetworkDevice>,
    arpEntries: List<ArpEntry> = emptyList(),
    isArpScanning: Boolean = false,
    arpStatusMessage: String = "",
    onRunArpScan: () -> Unit = {},
    onSelectDevice: (NetworkDevice) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf<DeviceType?>(null) }
    var showArpCacheDialog by remember { mutableStateOf(false) }

    val filteredDevices = remember(devices, searchQuery, selectedTypeFilter) {
        devices.filter { dev ->
            val matchesQuery = searchQuery.isEmpty() ||
                    dev.hostname.contains(searchQuery, ignoreCase = true) ||
                    dev.ip.contains(searchQuery, ignoreCase = true) ||
                    dev.macAddress.contains(searchQuery, ignoreCase = true) ||
                    dev.vendor.contains(searchQuery, ignoreCase = true)

            val matchesType = selectedTypeFilter == null || dev.deviceType == selectedTypeFilter

            matchesQuery && matchesType
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "arp_spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "arp_rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ARP Discovery Header Control Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = "ARP Service",
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "ARP Device Discovery Service",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "IP-to-MAC Resolution • Kernel ARP Table (/proc/net/arp)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberCyan,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // ARP Scan Action Button
                    Button(
                        onClick = onRunArpScan,
                        enabled = !isArpScanning,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan,
                            contentColor = CyberDarkBg,
                            disabledContainerColor = CyberSurfaceVariant,
                            disabledContentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_arp_scan")
                    ) {
                        if (isArpScanning) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Scanning",
                                modifier = Modifier
                                    .size(14.dp)
                                    .rotate(spinAngle)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scanning...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = "Scan ARP",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ARP Scan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // ARP Metrics & Live Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Discovered: ${devices.size} hosts",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "•",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "ARP Cache: ${arpEntries.size} entries",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    TextButton(
                        onClick = { showArpCacheDialog = true },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(13.dp), tint = CyberCyan)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("View Raw ARP", fontSize = 10.sp, color = CyberCyan)
                    }
                }

                if (isArpScanning && arpStatusMessage.isNotEmpty()) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = CyberCyan,
                        trackColor = CyberSurfaceVariant
                    )
                    Text(
                        text = arpStatusMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }

        // Search Input Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search IP, MAC, Hostname or Vendor...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = CyberCyan) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_device_search"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberSurface,
                unfocusedContainerColor = CyberSurface,
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = CyberCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        // Type Filter Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedTypeFilter == null,
                    onClick = { selectedTypeFilter = null },
                    label = { Text("All (${devices.size})", style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberEmerald.copy(alpha = 0.2f),
                        selectedLabelColor = CyberEmerald
                    )
                )
            }
            items(DeviceType.values()) { type ->
                val count = devices.count { it.deviceType == type }
                if (count > 0) {
                    FilterChip(
                        selected = selectedTypeFilter == type,
                        onClick = { selectedTypeFilter = if (selectedTypeFilter == type) null else type },
                        label = { Text("${type.label} ($count)", style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyan
                        )
                    )
                }
            }
        }

        // Discovered Devices List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredDevices, key = { it.id }) { device ->
                DeviceCard(
                    device = device,
                    onClick = { onSelectDevice(device) }
                )
            }

            if (filteredDevices.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArpScanning) "Probing local network via ARP..." else "No matching devices found.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }
                }
            }
        }
    }

    // Modal Dialog to display Raw /proc/net/arp Kernel Table
    if (showArpCacheDialog) {
        AlertDialog(
            onDismissRequest = { showArpCacheDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Dns, contentDescription = null, tint = CyberCyan)
                    Text(
                        text = "Kernel ARP Neighbor Cache",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Source: /proc/net/arp (Linux Kernel IPv4 Neighbor Table)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )

                    if (arpEntries.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = CyberDarkBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "ARP Cache is currently resolving entries. Tap 'ARP Scan' to broadcast network probes.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(arpEntries) { entry ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(6.dp),
                                    color = CyberDarkBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = entry.ip,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = CyberCyan
                                            )
                                            Text(
                                                text = "MAC: ${entry.macAddress}",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = entry.networkInterface,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                color = CyberEmerald
                                            )
                                            Text(
                                                text = "Flags: ${entry.flags}",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 9.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showArpCacheDialog = false }) {
                    Text("Close", color = CyberCyan)
                }
            },
            containerColor = CyberSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun DeviceCard(
    device: NetworkDevice,
    onClick: () -> Unit
) {
    val hasHighRisk = device.vulnerabilities.any { it.severity >= RiskSeverity.HIGH }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (hasHighRisk) SeverityCritical.copy(alpha = 0.6f) else CyberCardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .testTag("device_card_${device.id}"),
        color = CyberSurface
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Row: Icon, Hostname, IP, Security Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getDeviceIcon(device.deviceType),
                            contentDescription = device.deviceType.label,
                            tint = if (device.isGateway) CyberCyan else CyberEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = device.hostname,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = device.ip,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = CyberCyan
                            )
                            // ARP badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "ARP",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan
                                )
                            }
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    RiskChip(
                        when {
                            device.securityScore >= 80 -> RiskSeverity.SAFE
                            device.securityScore >= 60 -> RiskSeverity.MEDIUM
                            else -> RiskSeverity.CRITICAL
                        }
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${device.latencyMs}ms • ${device.signalDbm}dBm",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            // Middle Row: Vendor & MAC
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vendor: ${device.vendor}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "MAC: ${device.macAddress}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }

            // Bottom Row: Open Ports Badges
            if (device.openPorts.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ports:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    )
                    device.openPorts.take(4).forEach { port ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (port.riskSeverity) {
                                        RiskSeverity.CRITICAL -> SeverityCritical.copy(alpha = 0.2f)
                                        RiskSeverity.HIGH -> SeverityHigh.copy(alpha = 0.2f)
                                        else -> CyberSurfaceVariant
                                    }
                                )
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${port.port} ${port.serviceName.take(6)}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = when (port.riskSeverity) {
                                    RiskSeverity.CRITICAL -> SeverityCritical
                                    RiskSeverity.HIGH -> SeverityHigh
                                    else -> TextPrimary
                                }
                            )
                        }
                    }
                    if (device.openPorts.size > 4) {
                        Text(
                            text = "+${device.openPorts.size - 4}",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

