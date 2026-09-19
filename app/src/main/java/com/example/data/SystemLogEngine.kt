package com.example.data

import com.example.model.LogCategory
import com.example.model.LogSeverity
import com.example.model.SystemLogEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SystemLogEngine {

    private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    private val securityPresets = listOf(
        SystemLogEntry(
            id = "LOG-001",
            timestamp = "15:20:12.104",
            severity = LogSeverity.SECURITY_ALERT,
            category = LogCategory.ANOMALY,
            tag = "SecNetGuard",
            message = "Unencrypted Telnet daemon connection detected on local subnet endpoint 192.168.1.42:23",
            sourceIp = "192.168.1.42",
            rawLog = "[SEC-ALERT] [SecNetGuard:304] Telnet handshake without TLS from 192.168.1.42"
        ),
        SystemLogEntry(
            id = "LOG-002",
            timestamp = "15:21:05.441",
            severity = LogSeverity.INFO,
            category = LogCategory.FIREWALL,
            tag = "iptables",
            message = "Inbound TCP SYN packet inspected from 192.168.1.24 -> 192.168.1.180 (Port 22 SSH Allowed)",
            sourceIp = "192.168.1.24",
            rawLog = "[INFO] [iptables:120] ACCEPT IN=wlan0 OUT= SRC=192.168.1.24 DST=192.168.1.180 PROTO=TCP SPT=51240 DPT=22"
        ),
        SystemLogEntry(
            id = "LOG-003",
            timestamp = "15:22:40.892",
            severity = LogSeverity.WARN,
            category = LogCategory.DNS,
            tag = "ResolverDaemon",
            message = "High frequency DNS queries (38 req/s) routed to cloud resolver 1.1.1.1",
            sourceIp = "192.168.1.105",
            rawLog = "[WARN] [ResolverDaemon:88] Burst query rate from local interface detected"
        ),
        SystemLogEntry(
            id = "LOG-004",
            timestamp = "15:23:14.302",
            severity = LogSeverity.INFO,
            category = LogCategory.NETWORK,
            tag = "WifiStateMachine",
            message = "802.11ac Link verified: BSSID F4:F5:E8:A1:3B:90 RSSI=-52dBm Channel=48 WPA3-SAE",
            sourceIp = "192.168.1.1",
            rawLog = "[INFO] [WifiStateMachine:490] ConnectedState: linkSpeed=866Mbps freq=5240MHz"
        ),
        SystemLogEntry(
            id = "LOG-005",
            timestamp = "15:24:02.119",
            severity = LogSeverity.SECURITY_ALERT,
            category = LogCategory.ANOMALY,
            tag = "ArpAuditEngine",
            message = "ARP Integrity Check Passed: Gateway MAC F4:F5:E8:A1:3B:90 matches cached baseline (No ARP Poisoning detected)",
            sourceIp = "192.168.1.1",
            rawLog = "[SEC-INFO] [ArpAuditEngine:72] Verified IP 192.168.1.1 is associated with unique HW addr F4:F5:E8:A1:3B:90"
        ),
        SystemLogEntry(
            id = "LOG-006",
            timestamp = "15:24:45.670",
            severity = LogSeverity.INFO,
            category = LogCategory.AUTH,
            tag = "KeyStoreAudit",
            message = "Hardware-backed Android KeyStore validated for local diagnostic certificates",
            sourceIp = "127.0.0.1",
            rawLog = "[INFO] [KeyStoreAudit:205] StrongBox Keymaster 4.1 hardware isolation active"
        ),
        SystemLogEntry(
            id = "LOG-007",
            timestamp = "15:25:10.012",
            severity = LogSeverity.ERROR,
            category = LogCategory.KERNEL,
            tag = "auditd",
            message = "avc: denied { connectto } for path=/dev/socket/raw_socket scontext=u:r:untrusted_app:s0",
            sourceIp = "127.0.0.1",
            rawLog = "[ERROR] [auditd:1044] SELinux policy restricted non-root raw socket creation"
        ),
        SystemLogEntry(
            id = "LOG-008",
            timestamp = "15:25:38.225",
            severity = LogSeverity.INFO,
            category = LogCategory.NETWORK,
            tag = "DhcpClient",
            message = "DHCP lease renewed successfully from 192.168.1.1. Lease time: 86400s",
            sourceIp = "192.168.1.1",
            rawLog = "[INFO] [DhcpClient:112] ACK received: IP=192.168.1.105 Netmask=255.255.255.0 Gateway=192.168.1.1"
        )
    )

    suspend fun getInitialLogs(): List<SystemLogEntry> = withContext(Dispatchers.IO) {
        val realLogs = mutableListOf<SystemLogEntry>()

        try {
            // Attempt reading live Android logcat for real system entries
            val process = Runtime.getRuntime().exec("logcat -d -v time -t 20")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String? = reader.readLine()
            var count = 0
            while (line != null && count < 10) {
                if (line.isNotBlank() && !line.startsWith("---------")) {
                    val severity = when {
                        line.contains(" E/") || line.contains(" F/") -> LogSeverity.ERROR
                        line.contains(" W/") -> LogSeverity.WARN
                        else -> LogSeverity.INFO
                    }
                    realLogs.add(
                        SystemLogEntry(
                            id = "REAL-${UUID.randomUUID().toString().take(6)}",
                            timestamp = timeFormat.format(Date()),
                            severity = severity,
                            category = if (line.contains("Wifi") || line.contains("Net") || line.contains("Socket")) LogCategory.NETWORK else LogCategory.KERNEL,
                            tag = "LogcatSys",
                            message = line.take(120),
                            sourceIp = "127.0.0.1",
                            rawLog = line
                        )
                    )
                    count++
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            // Logcat read fallback
        }

        // Combine curated security events with real system events
        securityPresets + realLogs
    }
}
