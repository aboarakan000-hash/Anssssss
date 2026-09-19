package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppNavTab
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(darkTheme = true) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CyberDarkBg,
        topBar = {
            CyberTopBar(
                interfaceInfo = uiState.interfaceInfo,
                isScanning = uiState.isScanning,
                scanProgress = uiState.scanProgress,
                scanStatusText = uiState.scanStatusText,
                unreadNotificationsCount = uiState.unreadNotificationsCount,
                onNotificationClick = { viewModel.toggleNotificationCenter(true) },
                onRefreshScan = { viewModel.runNetworkScan() }
            )
        },
        bottomBar = {
            CyberBottomNav(
                currentTab = uiState.activeTab,
                onTabSelected = { viewModel.setNavTab(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CyberDarkBg)
        ) {
            // In-App Notification Alert Banner (Slide-down for critical vulnerabilities & task completions)
            if (uiState.activeInAppBanner != null) {
                CyberAlertBanner(
                    notification = uiState.activeInAppBanner!!,
                    onDismiss = { viewModel.dismissInAppBanner() },
                    onActionClick = { viewModel.onNotificationClicked(uiState.activeInAppBanner!!) }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                AnimatedContent(
                    targetState = uiState.activeTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { targetTab ->
                    when (targetTab) {
                        AppNavTab.TOPOLOGY -> {
                            TopologyScreen(
                                nodes = uiState.topologyNodes,
                                selectedNode = uiState.selectedNode,
                                layoutType = uiState.topologyLayout,
                                onSelectNode = { viewModel.selectTopologyNode(it) },
                                onChangeLayout = { viewModel.setTopologyLayout(it) },
                                onRefreshScan = { viewModel.runNetworkScan() }
                            )
                        }
                        AppNavTab.DISCOVERY -> {
                            DiscoveryScreen(
                                devices = uiState.devices,
                                arpEntries = uiState.arpEntries,
                                isArpScanning = uiState.isArpScanning,
                                arpStatusMessage = uiState.arpScanStatusText,
                                onRunArpScan = { viewModel.runArpDeviceScan() },
                                onSelectDevice = { viewModel.selectDevice(it) }
                            )
                        }
                        AppNavTab.WIRELESS -> {
                            WirelessAuditScreen(
                                interfaceInfo = uiState.interfaceInfo,
                                auditResult = uiState.wirelessAssessment,
                                isAuditing = uiState.isWirelessAuditing,
                                onRunAudit = { viewModel.runWirelessAudit() }
                            )
                        }
                        AppNavTab.LOG_ANALYZER -> {
                            LogAnalyzerScreen(
                                logs = uiState.systemLogs,
                                selectedSeverity = uiState.logFilterSeverity,
                                searchQuery = uiState.logSearchQuery,
                                onSelectSeverity = { viewModel.setLogFilterSeverity(it) },
                                onSearchChange = { viewModel.setLogSearch(it) },
                                onClearLogs = { viewModel.clearLogs() },
                                packets = uiState.packets,
                                isPacketCaptureActive = uiState.isPacketCaptureActive,
                                selectedPacketFilter = uiState.packetFilterProtocol,
                                packetSearchQuery = uiState.packetSearchQuery,
                                onTogglePacketCapture = { viewModel.togglePacketCapture() },
                                onClearPackets = { viewModel.clearPackets() },
                                onSelectPacketFilter = { viewModel.setPacketFilter(it) },
                                onPacketSearchQueryChange = { viewModel.setPacketSearch(it) },
                                onSelectPacket = { viewModel.selectPacket(it) }
                            )
                        }
                        AppNavTab.REPORTS -> {
                            ReportsScreen(
                                report = uiState.securityReport,
                                isGenerating = uiState.isGeneratingReport,
                                onGenerateReport = { viewModel.generateSecurityReport() },
                                onExportCsv = { viewModel.exportReportCsv() },
                                onExportPrintable = { viewModel.exportReportPrintable() }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Notification Center Dialog
    if (uiState.isNotificationCenterOpen) {
        NotificationCenterDialog(
            notifications = uiState.notifications,
            onDismiss = { viewModel.toggleNotificationCenter(false) },
            onNotificationClick = { viewModel.onNotificationClicked(it) },
            onDeleteNotification = { viewModel.dismissNotification(it) },
            onClearAll = { viewModel.clearAllNotifications() }
        )
    }

    // Modal Details Sheet for selected device
    if (uiState.selectedDevice != null) {
        DeviceDetailSheet(
            device = uiState.selectedDevice!!,
            isProbingPort = uiState.isProbingPort,
            probeResult = uiState.probeResult,
            isPinging = uiState.isPinging,
            pingResults = uiState.pingResults,
            onProbePort = { ip, port -> viewModel.probePort(ip, port) },
            onRunPing = { ip -> viewModel.runPingTest(ip) },
            onDismiss = { viewModel.selectDevice(null) }
        )
    }

    // Modal Dialog for deep Packet Inspection
    if (uiState.selectedPacket != null) {
        PacketDetailDialog(
            packet = uiState.selectedPacket!!,
            onDismiss = { viewModel.selectPacket(null) }
        )
    }
}
