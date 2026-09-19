package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RiskSeverity
import com.example.model.SecurityReport
import com.example.ui.theme.*

@Composable
fun ReportsScreen(
    report: SecurityReport?,
    isGenerating: Boolean,
    onGenerateReport: () -> Unit,
    onExportCsv: () -> Unit,
    onExportPrintable: () -> Unit
) {
    var selectedViewMode by remember { mutableStateOf("SUMMARY") } // "SUMMARY", "CSV", "PRINTABLE"
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SECURITY AUDIT REPORTS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = CyberEmerald,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "تقارير تدقيق الأمان السيبراني والتصدير",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                )
            }

            Button(
                onClick = onGenerateReport,
                enabled = !isGenerating,
                modifier = Modifier
                    .height(36.dp)
                    .testTag("btn_generate_report"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberEmerald,
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (isGenerating) Icons.Default.HourglassEmpty else Icons.Default.Assessment,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isGenerating) "Generating..." else "Generate Report",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // View Mode Selector (Summary, CSV, Printable)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "SUMMARY" to "Executive Summary",
                "CSV" to "CSV Export",
                "PRINTABLE" to "PDF / Text Report"
            ).forEach { (mode, label) ->
                val isSelected = selectedViewMode == mode
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedViewMode = mode },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
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
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                )
            }
        }

        // Export Buttons Bar
        if (report != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onExportCsv,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("btn_export_csv"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onExportPrintable,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("btn_export_pdf"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberEmerald),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Report", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = {
                        val contentToCopy = if (selectedViewMode == "CSV") report.csvContent else report.printableTextContent
                        clipboardManager.setText(AnnotatedString(contentToCopy))
                        Toast.makeText(context, "Report copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Main Report Content Area
        if (report == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No report generated yet.", color = TextSecondary, fontSize = 13.sp)
                    Text("انقر على 'Generate Report' لإنشاء تقرير مفصل.", color = TextMuted, fontSize = 11.sp)
                }
            }
        } else {
            when (selectedViewMode) {
                "SUMMARY" -> ExecutiveSummaryView(report)
                "CSV" -> CsvDataPreviewView(report.csvContent)
                "PRINTABLE" -> PrintableDocumentView(report.printableTextContent)
            }
        }
    }
}

@Composable
private fun ExecutiveSummaryView(report: SecurityReport) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Scorecard Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, CyberEmerald.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
            color = CyberSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "OVERALL SECURITY HEALTH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberEmerald,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "${report.overallHealthScore} / 100",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Report ID: ${report.reportId}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = report.generatedAt,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (report.criticalThreatsCount > 0) SeverityCritical.copy(alpha = 0.15f) else SeveritySafe.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (report.criticalThreatsCount > 0) "${report.criticalThreatsCount} Critical Threats" else "Subnet Secure",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (report.criticalThreatsCount > 0) SeverityCritical else SeveritySafe
                            )
                        )
                    }
                }
            }
        }

        // Executive Summary Text
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CyberDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "EXECUTIVE SUMMARY / الملخص التنفيذي",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                )
                Text(
                    text = report.executiveSummary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                )
                Text(
                    text = report.executiveSummaryAr,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                )
            }
        }

        // Scanned Scope Matrix
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReportMiniMetric("TOTAL NODES", "${report.totalScannedDevices}", CyberCyan, Modifier.weight(1f))
            ReportMiniMetric("WIRELESS RATING", "${report.wirelessSecurityScore}/100", CyberEmerald, Modifier.weight(1f))
            ReportMiniMetric("VULNERABILITIES", "${report.vulnerabilitiesSummary.size}", if (report.vulnerabilitiesSummary.isNotEmpty()) SeverityHigh else SeveritySafe, Modifier.weight(1f))
        }

        // Remediation Roadmap
        Text(
            text = "REMEDIATION RECOMMENDATIONS / توصيات المعالجة",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = CyberEmerald,
                letterSpacing = 1.sp
            )
        )

        report.recommendations.forEachIndexed { idx, rec ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = CyberSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "${idx + 1}. $rec",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    )
                    Text(
                        text = report.recommendationsAr.getOrNull(idx) ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = TextSecondary)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CsvDataPreviewView(csv: String) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
        color = TerminalBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Text(
                text = "CSV OUTPUT (RFC 4180)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
            ) {
                Text(
                    text = csv,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TerminalGreen,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun PrintableDocumentView(reportText: String) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
        color = TerminalBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Text(
                text = "FORMATTED EXECUTIVE REPORT (PDF-PRINTABLE TEXT)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = CyberEmerald
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
            ) {
                Text(
                    text = reportText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TextPrimary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ReportMiniMetric(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = CyberSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TextSecondary))
            Text(
                value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = color
                )
            )
        }
    }
}
