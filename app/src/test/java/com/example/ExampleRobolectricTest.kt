package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.NotificationEngine
import com.example.data.ReportGeneratorEngine
import com.example.data.WirelessAssessmentEngine
import com.example.model.NetworkInterfaceInfo
import com.example.model.NotificationType
import com.example.model.RiskSeverity
import com.example.model.SecurityAuditOverview
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Network Analyzer", appName)
  }

  @Test
  fun `test notification engine generates initial notifications`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val notificationEngine = NotificationEngine(context)
    val notifications = notificationEngine.getInitialNotifications()

    assertTrue(notifications.isNotEmpty())
    assertTrue(notifications.any { it.type == NotificationType.CRITICAL_VULNERABILITY })
    assertTrue(notifications.any { it.severity == RiskSeverity.CRITICAL })
  }

  @Test
  fun `test wireless assessment engine performs audit`() {
    val engine = WirelessAssessmentEngine()
    val iface = NetworkInterfaceInfo(ssid = "Test_WLAN", bssid = "AA:BB:CC:DD:EE:FF")
    val result = engine.performWirelessAudit(iface)

    assertEquals("Test_WLAN", result.ssid)
    assertTrue(result.securityScore > 80)
    assertTrue(result.isDeauthResistant)
    assertTrue(result.findings.isNotEmpty())
  }

  @Test
  fun `test report generator builds csv and printable report`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val reportEngine = ReportGeneratorEngine(context)
    val iface = NetworkInterfaceInfo(ssid = "Test_WLAN", gatewayIp = "192.168.1.1")
    val wirelessEngine = WirelessAssessmentEngine()
    val wirelessResult = wirelessEngine.performWirelessAudit(iface)

    val report = reportEngine.generateSecurityReport(
      interfaceInfo = iface,
      devices = emptyList(),
      wirelessAudit = wirelessResult,
      securityAudit = SecurityAuditOverview()
    )

    assertNotNull(report)
    assertTrue(report.csvContent.contains("Section,Record_ID,Target_Name"))
    assertTrue(report.printableTextContent.contains("CYBERSECURITY AUDIT & ASSESSMENT REPORT"))
    assertTrue(report.recommendations.isNotEmpty())
    assertTrue(report.recommendationsAr.isNotEmpty())
  }
}

