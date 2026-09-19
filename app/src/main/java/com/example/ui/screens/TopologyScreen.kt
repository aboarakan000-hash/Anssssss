package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeviceType
import com.example.model.NetworkTopologyNode
import com.example.ui.theme.*

@Composable
fun TopologyScreen(
    nodes: List<NetworkTopologyNode>,
    selectedNode: NetworkTopologyNode?,
    layoutType: String,
    onSelectNode: (NetworkTopologyNode?) -> Unit,
    onChangeLayout: (String) -> Unit,
    onRefreshScan: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "topology_anim")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_progress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Layout and Filter Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NETWORK TOPOLOGY MAP",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = CyberEmerald,
                    letterSpacing = 1.sp
                )
            )

            // Layout Selector Chips
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("STAR" to "Star", "TREE" to "Tree", "RING" to "Ring").forEach { (type, label) ->
                    val isSelected = layoutType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { onChangeLayout(type) },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyan,
                            containerColor = CyberSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = CyberCardBorder,
                            selectedBorderColor = CyberCyan
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
        }

        // Main Topology Canvas Box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
                .testTag("topology_canvas_container"),
            color = CyberSurface
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = constraints.maxWidth.toFloat()
                val canvasHeight = constraints.maxHeight.toFloat()

                val gatewayNode = nodes.find { it.isGateway } ?: nodes.firstOrNull()

                // Background Canvas for grid and connection lines
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw subtle cyber grid
                    val gridSpacing = 40.dp.toPx()
                    val gridCols = (size.width / gridSpacing).toInt()
                    val gridRows = (size.height / gridSpacing).toInt()
                    for (i in 0..gridCols) {
                        drawLine(
                            color = Color(0x0F00E5FF),
                            start = Offset(i * gridSpacing, 0f),
                            end = Offset(i * gridSpacing, size.height),
                            strokeWidth = 1f
                        )
                    }
                    for (j in 0..gridRows) {
                        drawLine(
                            color = Color(0x0F00E5FF),
                            start = Offset(0f, j * gridSpacing),
                            end = Offset(size.width, j * gridSpacing),
                            strokeWidth = 1f
                        )
                    }

                    // Draw connection links from gateway to nodes
                    if (gatewayNode != null) {
                        val gwCenter = Offset(gatewayNode.xRatio * size.width, gatewayNode.yRatio * size.height)

                        nodes.filter { it.deviceId != gatewayNode.deviceId }.forEach { node ->
                            val nodeCenter = Offset(node.xRatio * size.width, node.yRatio * size.height)

                            // Link line
                            drawLine(
                                color = if (node.isAlert) SeverityCritical.copy(alpha = 0.6f) else CyberCyan.copy(alpha = 0.35f),
                                start = gwCenter,
                                end = nodeCenter,
                                strokeWidth = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                            )

                            // Animated data packet particle
                            val packetX = gwCenter.x + (nodeCenter.x - gwCenter.x) * pulseProgress
                            val packetY = gwCenter.y + (nodeCenter.y - gwCenter.y) * pulseProgress
                            drawCircle(
                                color = if (node.isAlert) SeverityCritical else CyberEmerald,
                                radius = 4.dp.toPx(),
                                center = Offset(packetX, packetY)
                            )
                        }
                    }
                }

                // Interactive Nodes Layer
                nodes.forEach { node ->
                    val isSelected = selectedNode?.deviceId == node.deviceId
                    val nodeLeft = (node.xRatio * canvasWidth) - 40.dp.value
                    val nodeTop = (node.yRatio * canvasHeight) - 40.dp.value

                    Box(
                        modifier = Modifier
                            .offset(
                                x = (node.xRatio * (maxWidth.value - 80.dp.value)).dp,
                                y = (node.yRatio * (maxHeight.value - 90.dp.value)).dp
                            )
                            .size(80.dp, 90.dp)
                            .clickable { onSelectNode(node) }
                            .testTag("node_${node.deviceId}"),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            // Node Icon Box
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) CyberEmerald.copy(alpha = 0.25f)
                                        else if (node.isAlert) SeverityCritical.copy(alpha = 0.2f)
                                        else CyberSurfaceVariant
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) CyberEmerald
                                        else if (node.isAlert) SeverityCritical
                                        else if (node.isGateway) CyberCyan
                                        else CyberCardBorder,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getDeviceIcon(node.deviceType),
                                    contentDescription = node.label,
                                    tint = if (isSelected) CyberEmerald
                                    else if (node.isAlert) SeverityCritical
                                    else if (node.isGateway) CyberCyan
                                    else TextPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Node IP & Hostname Label
                            Text(
                                text = node.ip.substringAfterLast("."),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (isSelected) CyberEmerald else TextPrimary
                            )
                            Text(
                                text = node.label.take(10),
                                fontSize = 9.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Bottom Topology Summary Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricMini("NODES", "${nodes.size}", CyberCyan)
                    MetricMini("GATEWAY", nodes.find { it.isGateway }?.ip ?: "192.168.1.1", TextPrimary, isMono = true)
                    MetricMini(
                        "ALERTS",
                        "${nodes.count { it.isAlert }}",
                        if (nodes.any { it.isAlert }) SeverityCritical else SeveritySafe
                    )
                }

                Text(
                    text = "Tap node for details",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun MetricMini(label: String, value: String, color: Color, isMono: Boolean = false) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TextSecondary))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default,
                color = color
            )
        )
    }
}

fun getDeviceIcon(type: DeviceType): ImageVector = when (type) {
    DeviceType.GATEWAY -> Icons.Default.Router
    DeviceType.SMARTPHONE -> Icons.Default.Smartphone
    DeviceType.LAPTOP -> Icons.Default.Laptop
    DeviceType.SERVER -> Icons.Default.Dns
    DeviceType.PRINTER -> Icons.Default.Print
    DeviceType.IOT_DEVICE -> Icons.Default.Sensors
    DeviceType.SECURITY_CAMERA -> Icons.Default.Videocam
    else -> Icons.Default.Devices
}
