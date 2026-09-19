package com.example.model

enum class DeviceType(val label: String) {
    GATEWAY("Gateway / Router"),
    SMARTPHONE("Smartphone"),
    LAPTOP("Laptop / PC"),
    IOT_DEVICE("IoT Smart Device"),
    SERVER("Server / NAS"),
    PRINTER("Network Printer"),
    SECURITY_CAMERA("IP Camera"),
    UNKNOWN("Network Node")
}

enum class RiskSeverity(val label: String) {
    SAFE("Secure"),
    LOW("Low Risk"),
    MEDIUM("Medium Risk"),
    HIGH("High Risk"),
    CRITICAL("Critical Risk")
}

data class PortInfo(
    val port: Int,
    val serviceName: String,
    val protocol: String = "TCP",
    val isOpen: Boolean = true,
    val banner: String = "",
    val riskSeverity: RiskSeverity = RiskSeverity.SAFE,
    val securityNotes: String = ""
)

data class Vulnerability(
    val id: String,
    val title: String,
    val severity: RiskSeverity,
    val cveRef: String? = null,
    val description: String,
    val mitigation: String
)

data class NetworkDevice(
    val id: String,
    val ip: String,
    val macAddress: String,
    val vendor: String,
    val hostname: String,
    val deviceType: DeviceType,
    val isGateway: Boolean = false,
    val isLocalDevice: Boolean = false,
    val signalDbm: Int = -55,
    val latencyMs: Long = 4,
    val connectionType: String = "Wi-Fi 5GHz",
    val openPorts: List<PortInfo> = emptyList(),
    val vulnerabilities: List<Vulnerability> = emptyList(),
    val securityScore: Int = 90,
    val isBlocked: Boolean = false,
    val bandwidthUsageKbps: Int = 120,
    val firstSeen: String = "Just now",
    val lastActive: String = "Active now",
    val arpFlags: String = "0x2",
    val discoveryMethod: String = "ARP Table (/proc/net/arp)"
)

enum class LogSeverity {
    INFO, WARN, ERROR, SECURITY_ALERT
}

enum class LogCategory(val label: String) {
    NETWORK("Network"),
    AUTH("Authentication"),
    FIREWALL("Firewall"),
    DNS("DNS / Resolver"),
    KERNEL("System / Kernel"),
    ANOMALY("Threat Detection")
}

data class SystemLogEntry(
    val id: String,
    val timestamp: String,
    val severity: LogSeverity,
    val category: LogCategory,
    val tag: String,
    val message: String,
    val sourceIp: String? = null,
    val rawLog: String = ""
)

enum class PacketProtocol(val colorHex: Long) {
    TCP(0xFF00E5FF),
    UDP(0xFF7C4DFF),
    DNS(0xFFFFB300),
    TLS(0xFF00E676),
    HTTP(0xFF2979FF),
    ICMP(0xFFFF5252),
    ARP(0xFFE040FB)
}

data class PacketEntry(
    val id: Long,
    val timestamp: String,
    val protocol: PacketProtocol,
    val sourceIp: String,
    val sourcePort: Int,
    val destIp: String,
    val destPort: Int,
    val lengthBytes: Int,
    val flags: String = "ACK, PSH",
    val ttl: Int = 64,
    val info: String,
    val payloadHex: String,
    val payloadAscii: String,
    val isSuspicious: Boolean = false,
    val suspiciousReason: String? = null
)

data class NetworkTopologyNode(
    val deviceId: String,
    val ip: String,
    val label: String,
    val deviceType: DeviceType,
    val isGateway: Boolean,
    val isLocal: Boolean,
    val xRatio: Float,
    val yRatio: Float,
    val pingMs: Long,
    val openPortCount: Int,
    val securityScore: Int,
    val isAlert: Boolean = false
)

data class NetworkInterfaceInfo(
    val ssid: String = "Secure_Corp_WLAN",
    val bssid: String = "F4:F5:E8:A1:3B:90",
    val localIp: String = "192.168.1.105",
    val gatewayIp: String = "192.168.1.1",
    val subnetMask: String = "255.255.255.0",
    val dnsServers: List<String> = listOf("1.1.1.1", "8.8.8.8"),
    val interfaceName: String = "wlan0",
    val macAddress: String = "B8:27:EB:7A:1C:89",
    val encryptionType: String = "WPA3-SAE / WPA2-Enterprise",
    val signalDbm: Int = -52,
    val frequencyMhz: Int = 5240,
    val linkSpeedMbps: Int = 866,
    val isVpnActive: Boolean = false,
    val isRootGranted: Boolean = true
)

data class SecurityAuditOverview(
    val score: Int = 88,
    val encryptionGrade: String = "A- (Strong)",
    val totalDevices: Int = 8,
    val highRiskDevicesCount: Int = 1,
    val openPortsCount: Int = 14,
    val arpIntegrityStatus: String = "Verified Safe",
    val rogueApDetected: Boolean = false,
    val dnsTamperingDetected: Boolean = false,
    val recommendations: List<String> = listOf(
        "Disable legacy Telnet (Port 23) on IoT Smart Plug (192.168.1.42)",
        "Upgrade Router firmware to mitigate CVE-2024-38812 SMB exposure",
        "Enable 802.11w Protected Management Frames (PMF) on Wi-Fi AP",
        "Restrict SMB (Port 445) access from external WAN interfaces"
    )
)

enum class NotificationType {
    CRITICAL_VULNERABILITY,
    SCAN_COMPLETED,
    WIRELESS_AUDIT_COMPLETED,
    LOG_ANOMALY,
    SYSTEM_INFO
}

data class AppNotification(
    val id: String,
    val title: String,
    val titleAr: String,
    val message: String,
    val messageAr: String,
    val timestamp: String,
    val type: NotificationType,
    val severity: RiskSeverity = RiskSeverity.MEDIUM,
    val isRead: Boolean = false,
    val targetTab: String? = null
)

data class WirelessFinding(
    val id: String,
    val title: String,
    val titleAr: String,
    val category: String,
    val status: String, // "SECURE", "WARNING", "VULNERABLE"
    val severity: RiskSeverity,
    val details: String,
    val detailsAr: String,
    val recommendation: String,
    val recommendationAr: String
)

data class WirelessAssessmentResult(
    val ssid: String = "Secure_Corp_WLAN",
    val bssid: String = "F4:F5:E8:A1:3B:90",
    val encryptionStandard: String = "WPA3-SAE / WPA2-Enterprise",
    val cipherSuite: String = "AES-CCMP / GCMP-256",
    val wpsStatus: String = "Disabled (Safe)",
    val isWpsVulnerable: Boolean = false,
    val pmfStatus: String = "Required (802.11w Protected)",
    val isDeauthResistant: Boolean = true,
    val handshakeSniffingRisk: String = "Very Low (SAE Protected)",
    val evilTwinRisk: String = "None (Known BSSID)",
    val channel: Int = 48,
    val frequencyMhz: Int = 5240,
    val signalStrengthDbm: Int = -52,
    val channelCongestion: String = "Low (3 competing APs)",
    val securityScore: Int = 92,
    val findings: List<WirelessFinding> = emptyList(),
    val assessmentTimestamp: String = "Just now"
)

data class SecurityReport(
    val reportId: String,
    val generatedAt: String,
    val networkName: String,
    val gatewayIp: String,
    val totalScannedDevices: Int,
    val criticalThreatsCount: Int,
    val wirelessSecurityScore: Int,
    val overallHealthScore: Int,
    val executiveSummary: String,
    val executiveSummaryAr: String,
    val devicesSummary: List<NetworkDevice>,
    val vulnerabilitiesSummary: List<Vulnerability>,
    val wirelessAuditSummary: WirelessAssessmentResult,
    val recommendations: List<String>,
    val recommendationsAr: List<String>,
    val csvContent: String = "",
    val printableTextContent: String = ""
)

