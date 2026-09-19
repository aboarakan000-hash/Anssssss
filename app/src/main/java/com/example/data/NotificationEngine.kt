package com.example.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.model.AppNotification
import com.example.model.NotificationType
import com.example.model.RiskSeverity
import java.text.SimpleDateFormat
import java.util.*

class NotificationEngine(private val context: Context) {

    private val channelId = "cyber_security_alerts"
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Cyber Security Alerts"
            val descriptionText = "Notifications for detected network vulnerabilities and scan task completions"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun getInitialNotifications(): List<AppNotification> {
        val now = timeFormat.format(Date())
        return listOf(
            AppNotification(
                id = UUID.randomUUID().toString(),
                title = "Critical Vulnerability Discovered",
                titleAr = "اكتشاف ثغرة أمنية حرجة",
                message = "Unencrypted Telnet (Port 23) active on IoT Smart Plug (192.168.1.42)",
                messageAr = "خدمة Telnet غير المشفرة (منفذ 23) نشطة على مقبس ذكي (192.168.1.42)",
                timestamp = now,
                type = NotificationType.CRITICAL_VULNERABILITY,
                severity = RiskSeverity.CRITICAL,
                targetTab = "DISCOVERY"
            ),
            AppNotification(
                id = UUID.randomUUID().toString(),
                title = "Subnet Discovery Sweep Completed",
                titleAr = "اكتمل فحص الشبكة المحلية",
                message = "Scanned 8 active nodes across 192.168.1.0/24 subnet. 1 critical anomaly flagged.",
                messageAr = "تم فحص 8 أجهزة نشطة في الشبكة 192.168.1.0/24. تم رصد تهديد أمني واحد.",
                timestamp = now,
                type = NotificationType.SCAN_COMPLETED,
                severity = RiskSeverity.MEDIUM,
                targetTab = "TOPOLOGY"
            ),
            AppNotification(
                id = UUID.randomUUID().toString(),
                title = "Wireless Security Audit Verified",
                titleAr = "تم إكمال التدقيق الأمني اللاسلكي",
                message = "WPA3-SAE encryption validated with 802.11w PMF protection against deauth.",
                messageAr = "تم تأكيد معيار تشفير WPA3-SAE وتفعيل حماية إطارات الإدارة 802.11w ضد هجمات الفصل.",
                timestamp = now,
                type = NotificationType.WIRELESS_AUDIT_COMPLETED,
                severity = RiskSeverity.SAFE,
                targetTab = "WIRELESS"
            )
        )
    }

    fun createNotification(
        title: String,
        titleAr: String,
        message: String,
        messageAr: String,
        type: NotificationType,
        severity: RiskSeverity,
        targetTab: String? = null
    ): AppNotification {
        val notification = AppNotification(
            id = UUID.randomUUID().toString(),
            title = title,
            titleAr = titleAr,
            message = message,
            messageAr = messageAr,
            timestamp = timeFormat.format(Date()),
            type = type,
            severity = severity,
            targetTab = targetTab
        )

        // Trigger Android System Notification
        postSystemNotification(notification)

        return notification
    }

    private fun postSystemNotification(notification: AppNotification) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification.title)
                .setContentText(notification.message)
                .setStyle(NotificationCompat.BigTextStyle().bigText("${notification.titleAr}\n${notification.message}"))
                .setPriority(
                    if (notification.severity == RiskSeverity.CRITICAL) NotificationCompat.PRIORITY_HIGH
                    else NotificationCompat.PRIORITY_DEFAULT
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val manager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                manager.notify(notification.id.hashCode(), builder.build())
            }
        } catch (_: Exception) {
            // Graceful fallback for simulator environments or restricted permissions
        }
    }
}
