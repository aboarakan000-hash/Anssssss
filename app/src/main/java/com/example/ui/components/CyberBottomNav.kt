package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppNavTab
import com.example.ui.theme.*

@Composable
fun CyberBottomNav(
    currentTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit
) {
    NavigationBar(
        windowInsets = WindowInsets.navigationBars,
        containerColor = CyberDarkBg,
        contentColor = TextPrimary,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            NavData(AppNavTab.TOPOLOGY, Icons.Filled.AccountTree, Icons.Outlined.AccountTree),
            NavData(AppNavTab.DISCOVERY, Icons.Filled.Devices, Icons.Outlined.Devices),
            NavData(AppNavTab.WIRELESS, Icons.Filled.WifiTethering, Icons.Outlined.WifiTethering),
            NavData(AppNavTab.LOG_ANALYZER, Icons.Filled.Terminal, Icons.Outlined.Terminal),
            NavData(AppNavTab.REPORTS, Icons.Filled.Assessment, Icons.Outlined.Assessment)
        )

        items.forEach { item ->
            val selected = currentTab == item.tab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.filledIcon else item.outlineIcon,
                        contentDescription = item.tab.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.tab.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyberEmerald,
                    unselectedIconColor = TextSecondary,
                    selectedTextColor = CyberEmerald,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = CyberSurfaceVariant
                ),
                modifier = Modifier.testTag(item.tab.testTag)
            )
        }
    }
}

private data class NavData(
    val tab: AppNavTab,
    val filledIcon: ImageVector,
    val outlineIcon: ImageVector
)
