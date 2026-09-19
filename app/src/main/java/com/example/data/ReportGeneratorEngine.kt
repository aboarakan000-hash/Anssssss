package com.example.data

import android.content.Context
import android.content.Intent
import com.example.model.*
import java.text.SimpleDateFormat
import java.util.*

class ReportGeneratorEngine(private val context: Context) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun generateSecurityReport(
        interfaceInfo: NetworkInterfaceInfo,
        devices: List<NetworkDevice>,
        wirelessAudit: WirelessAssessmentResult,
        securityAudit: SecurityAuditOverview
    ): SecurityReport {
        val now = dateFormat.format(Date())
        val allVulnerabilities = devices.flatMap { it.vulnerabilities }
        val criticalCount = allVulnerabilities.count { it.severity >= RiskSeverity.HIGH }

        val overallScore = ((securityAudit.score * 0.5) + (wirelessAudit.securityScore * 0.5)).toInt()

        val recommendationsEn = listOf(
            "Disable legacy Telnet (Port 23) and unencrypted web servers on smart devices.",
            "Enforce WPA3-Personal or WPA2-Enterprise with 802.11w Protected Management Frames (PMF).",
            "Update router firmware to patch SMBv1 and RPC remote exposure vectors.",
            "Segment IoT peripherals into an isolated guest VLAN separated from primary workstations.",
            "Review firewall rules to block unsolicited inbound WAN packets to Port 445 and 8080."
        )

        val recommendationsAr = listOf(
            "تعطيل خدمة Telnet القديمة (منفذ 23) وخوادم الويب غير المشفرة على أجهزة إنترنت الأشياء.",
            "فرض معيار WPA3 أو WPA2-Enterprise مع تفعيل حماية إطارات الإدارة 802.11w لمنع هجمات الفصل.",
            "تحديث البرامج الثابتة للموجه (الراوتر) لسد ثغرات بروتوكول مشاركة الملفات SMB.",
            "عزل الأجهزة الذكية والأجهزة الطرفية في شبكة افتراضية معزولة (Guest VLAN) عن الأجهزة الرئيسية.",
            "مراجعة قواعد جدار الحماية وحظر الاتصالات الواردة غير المصرح بها عبر المنافذ الحساسة."
        )

        val csvContent = buildCsvExport(interfaceInfo, devices, allVulnerabilities, wirelessAudit)
        val textReport = buildPrintableTextReport(
            now,
            interfaceInfo,
            devices,
            allVulnerabilities,
            wirelessAudit,
            overallScore,
            recommendationsEn,
            recommendationsAr
        )

        return SecurityReport(
            reportId = "SEC-RPT-${System.currentTimeMillis() % 1000000}",
            generatedAt = now,
            networkName = interfaceInfo.ssid,
            gatewayIp = interfaceInfo.gatewayIp,
            totalScannedDevices = devices.size,
            criticalThreatsCount = criticalCount,
            wirelessSecurityScore = wirelessAudit.securityScore,
            overallHealthScore = overallScore,
            executiveSummary = "Security audit conducted on ${interfaceInfo.ssid} (${devices.size} endpoints). Overall posture rated at $overallScore/100. $criticalCount high/critical risk points identified requiring mitigation.",
            executiveSummaryAr = "تم إجراء تدقيق أمني شامل على شبكة ${interfaceInfo.ssid} (${devices.size} أجهزة متصلة). تصنيف الأمان العام: $overallScore/100. تم رصد $criticalCount نقاط ضعف حرجة تتطلب المعالجة.",
            devicesSummary = devices,
            vulnerabilitiesSummary = allVulnerabilities,
            wirelessAuditSummary = wirelessAudit,
            recommendations = recommendationsEn,
            recommendationsAr = recommendationsAr,
            csvContent = csvContent,
            printableTextContent = textReport
        )
    }

    private fun buildCsvExport(
        iface: NetworkInterfaceInfo,
        devices: List<NetworkDevice>,
        vulnerabilities: List<Vulnerability>,
        wireless: WirelessAssessmentResult
    ): String {
        val sb = StringBuilder()
        sb.append("Section,Record_ID,Target_Name,IP_Address,MAC_Address,Risk_Severity,Details,Recommendation\n")

        // Network Profile
        sb.append("NETWORK_PROFILE,NET-01,${escapeCsv(iface.ssid)},${iface.localIp},${iface.macAddress},INFO,Gateway: ${iface.gatewayIp} Encryption: ${iface.encryptionType},Keep firmware updated\n")

        // Wireless Assessment
        wireless.findings.forEach { wf ->
            sb.append("WIRELESS_AUDIT,${wf.id},${escapeCsv(wf.title)},${iface.gatewayIp},${iface.bssid},${wf.severity.name},${escapeCsv(wf.details)},${escapeCsv(wf.recommendation)}\n")
        }

        // Devices
        devices.forEach { dev ->
            val portsStr = dev.openPorts.joinToString(";") { "${it.port}/${it.serviceName}" }
            sb.append("DEVICE_INVENTORY,${dev.id},${escapeCsv(dev.hostname)},${dev.ip},${dev.macAddress},${if (dev.vulnerabilities.isNotEmpty()) "VULNERABLE" else "SECURE"},Type: ${dev.deviceType.label}; ARP: ${dev.discoveryMethod}; Ports: [${escapeCsv(portsStr)}],Audit open ports\n")
        }

        // Vulnerabilities
        vulnerabilities.forEach { vuln ->
            sb.append("VULNERABILITY,${vuln.id},${escapeCsv(vuln.title)},N/A,N/A,${vuln.severity.name},${escapeCsv(vuln.description)},${escapeCsv(vuln.mitigation)}\n")
        }

        return sb.toString()
    }

    private fun buildPrintableTextReport(
        date: String,
        iface: NetworkInterfaceInfo,
        devices: List<NetworkDevice>,
        vulnerabilities: List<Vulnerability>,
        wireless: WirelessAssessmentResult,
        overallScore: Int,
        recsEn: List<String>,
        recsAr: List<String>
    ): String {
        return buildString {
            appendLine("================================================================================")
            appendLine("                      CYBERSECURITY AUDIT & ASSESSMENT REPORT                   ")
            appendLine("                       تقرير تدقيق وتقييم الأمان السيبراني                       ")
            appendLine("================================================================================")
            appendLine("Generated At / تاريخ التقرير : $date")
            appendLine("Target SSID / اسم الشبكة     : ${iface.ssid}")
            appendLine("BSSID / عنوان نقطة الوصول    : ${iface.bssid}")
            appendLine("Default Gateway / البوابة     : ${iface.gatewayIp}")
            appendLine("Local Host IP / عنوان الجهاز : ${iface.localIp} (${iface.interfaceName})")
            appendLine("Overall Posture / تقييم الأمان: $overallScore / 100")
            appendLine("--------------------------------------------------------------------------------")
            appendLine()
            appendLine("1. EXECUTIVE SUMMARY / الملخص التنفيذي")
            appendLine("--------------------------------------------------------------------------------")
            appendLine("The audit examined ${devices.size} connected endpoints across subnet 192.168.1.0/24.")
            appendLine("Wireless encryption posture is running on ${wireless.encryptionStandard} with score ${wireless.securityScore}/100.")
            appendLine("Detected ${vulnerabilities.size} vulnerability entries (${vulnerabilities.count { it.severity >= RiskSeverity.HIGH }} critical/high).")
            appendLine()
            appendLine("2. CONNECTED ENDPOINT INVENTORY / جرد الأجهزة المتصلة")
            appendLine("--------------------------------------------------------------------------------")
            devices.forEachIndexed { idx, dev ->
                appendLine("[$idx] ${dev.hostname} | ${dev.ip} | MAC: ${dev.macAddress}")
                appendLine("    Vendor: ${dev.vendor} | Type: ${dev.deviceType.label} | Resolution: ${dev.discoveryMethod}")
                appendLine("    Latency: ${dev.latencyMs}ms | Signal: ${dev.signalDbm}dBm | Security Score: ${dev.securityScore}/100")
                if (dev.openPorts.isNotEmpty()) {
                    val portList = dev.openPorts.joinToString(", ") { "${it.port}/${it.serviceName} (${it.riskSeverity})" }
                    appendLine("    Open Ports: $portList")
                }
                if (dev.vulnerabilities.isNotEmpty()) {
                    dev.vulnerabilities.forEach { v ->
                        appendLine("    * [${v.severity}] ${v.title} (${v.cveRef ?: "Internal"})")
                        appendLine("      Mitigation: ${v.mitigation}")
                    }
                }
                appendLine()
            }
            appendLine("3. WIRELESS & RF SECURITY ASSESSMENT / الفحص اللاسلكي")
            appendLine("--------------------------------------------------------------------------------")
            appendLine("SSID: ${wireless.ssid} | Frequency: ${wireless.frequencyMhz} MHz (Channel ${wireless.channel})")
            appendLine("Encryption: ${wireless.encryptionStandard} | Cipher: ${wireless.cipherSuite}")
            appendLine("WPS Status: ${wireless.wpsStatus}")
            appendLine("PMF (802.11w) Deauth Protection: ${wireless.pmfStatus}")
            appendLine("Rogue AP / Evil Twin Verification: ${wireless.evilTwinRisk}")
            appendLine("Findings:")
            wireless.findings.forEach { wf ->
                appendLine("  - [${wf.status}] ${wf.title}")
                appendLine("    Details: ${wf.details}")
                appendLine("    Recommendation: ${wf.recommendation}")
            }
            appendLine()
            appendLine("4. REMEDIATION ROADMAP / خارطة طريق المعالجة والتوصيات")
            appendLine("--------------------------------------------------------------------------------")
            recsEn.forEachIndexed { i, rec ->
                appendLine("${i + 1}. $rec")
                appendLine("   العربية: ${recsAr.getOrNull(i) ?: ""}")
            }
            appendLine()
            appendLine("================================================================================")
            appendLine("                      END OF SECURITY ASSESSMENT REPORT                         ")
            appendLine("================================================================================")
        }
    }

    fun shareContent(subject: String, content: String, mimeType: String = "text/plain") {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, content)
            type = mimeType
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(sendIntent, "Export Security Report")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun escapeCsv(text: String): String {
        return if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            "\"" + text.replace("\"", "\"\"") + "\""
        } else {
            text
        }
    }
}
