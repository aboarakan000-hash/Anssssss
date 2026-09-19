package com.example.ui.components

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppNotification
import com.example.model.NotificationType
import com.example.model.RiskSeverity
import com.example.ui.theme.*

@Composable
fun CyberAlertBanner(
    notification: AppNotification,
    onDismiss: () -> Unit,
    onActionClick: () -> Unit
) {
    val isCritical = notification.severity >= RiskSeverity.HIGH
    val bannerColor = if (isCritical) SeverityCritical else CyberEmerald

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, bannerColor.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .testTag("in_app_notification_banner"),
        color = CyberSurface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(bannerColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.type) {
                        NotificationType.CRITICAL_VULNERABILITY -> Icons.Default.Warning
                        NotificationType.SCAN_COMPLETED -> Icons.Default.CheckCircle
                        NotificationType.WIRELESS_AUDIT_COMPLETED -> Icons.Default.WifiTethering
                        NotificationType.LOG_ANOMALY -> Icons.Default.SecurityUpdateWarning
                        NotificationType.SYSTEM_INFO -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = bannerColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onActionClick() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = bannerColor
                        )
                    )
                    Text(
                        text = notification.timestamp,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    )
                }
                Text(
                    text = notification.titleAr,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = TextSecondary
                    ),
                    maxLines = 2
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("btn_dismiss_alert_banner")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun NotificationCenterDialog(
    notifications: List<AppNotification>,
    onDismiss: () -> Unit,
    onNotificationClick: (AppNotification) -> Unit,
    onDeleteNotification: (String) -> Unit,
    onClearAll: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf<NotificationType?>(null) }

    val filtered = remember(notifications, selectedFilter) {
        if (selectedFilter == null) notifications
        else notifications.filter { it.type == selectedFilter }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, CyberCardBorder, RoundedCornerShape(18.dp))
                .testTag("notification_center_dialog"),
            color = CyberSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "CYBERSECURITY NOTIFICATIONS",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "مركز الإشعارات والتنبيهات الأمنية",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = CyberEmerald
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_close_notifications")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Filter chips & Clear All
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedFilter == null,
                                onClick = { selectedFilter = null },
                                label = { Text("All (${notifications.size})", style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberEmerald.copy(alpha = 0.2f),
                                    selectedLabelColor = CyberEmerald
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedFilter == NotificationType.CRITICAL_VULNERABILITY,
                                onClick = {
                                    selectedFilter = if (selectedFilter == NotificationType.CRITICAL_VULNERABILITY) null
                                    else NotificationType.CRITICAL_VULNERABILITY
                                },
                                label = { Text("Critical", style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SeverityCritical.copy(alpha = 0.2f),
                                    selectedLabelColor = SeverityCritical
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedFilter == NotificationType.SCAN_COMPLETED,
                                onClick = {
                                    selectedFilter = if (selectedFilter == NotificationType.SCAN_COMPLETED) null
                                    else NotificationType.SCAN_COMPLETED
                                },
                                label = { Text("Tasks", style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                    selectedLabelColor = CyberCyan
                                )
                            )
                        }
                    }

                    if (notifications.isNotEmpty()) {
                        TextButton(
                            onClick = onClearAll,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Clear All", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }

                // Notifications List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { item ->
                        NotificationItemRow(
                            notification = item,
                            onClick = { onNotificationClick(item) },
                            onDelete = { onDeleteNotification(item.id) }
                        )
                    }

                    if (filtered.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.NotificationsNone,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No notifications at this time", color = TextSecondary, fontSize = 12.sp)
                                    Text("لا توجد إشعارات حالياً", color = TextMuted, fontSize = 11.sp)
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
private fun NotificationItemRow(
    notification: AppNotification,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val isCritical = notification.severity >= RiskSeverity.HIGH
    val borderColor = if (isCritical) SeverityCritical.copy(alpha = 0.6f) else CyberCardBorder
    val iconColor = if (isCritical) SeverityCritical else CyberEmerald

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("notification_item_${notification.id}"),
        color = CyberDarkBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.type) {
                        NotificationType.CRITICAL_VULNERABILITY -> Icons.Default.Warning
                        NotificationType.SCAN_COMPLETED -> Icons.Default.CheckCircle
                        NotificationType.WIRELESS_AUDIT_COMPLETED -> Icons.Default.WifiTethering
                        NotificationType.LOG_ANOMALY -> Icons.Default.SecurityUpdateWarning
                        NotificationType.SYSTEM_INFO -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCritical) SeverityCritical else TextPrimary
                        )
                    )
                    Text(
                        text = notification.timestamp,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                }

                Text(
                    text = notification.titleAr,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = CyberEmerald
                    )
                )

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 14.sp
                    )
                )

                Text(
                    text = notification.messageAr,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = TextMuted,
                        lineHeight = 13.sp
                    )
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
