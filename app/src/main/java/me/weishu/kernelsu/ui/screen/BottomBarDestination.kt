package me.weishu.kernelsu.ui.screen

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.screen.destinations.DashboardScreenDestination
import me.weishu.kernelsu.ui.screen.destinations.HomeScreenDestination
import me.weishu.kernelsu.ui.screen.destinations.SuperUserScreenDestination
import me.weishu.kernelsu.ui.screen.destinations.ModuleScreenDestination

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    @StringRes val label: Int,
    val iconSelected: ImageVector,
    val iconNotSelected: ImageVector,
    val rootRequired: Boolean,
) {
    Home(
        direction = HomeScreenDestination,
        label = R.string.home,
        iconSelected = Icons.Filled.Home,
        iconNotSelected = Icons.Outlined.Home,
        rootRequired = false
    ),
    Dashboard(
        direction = DashboardScreenDestination,
        label = R.string.dashboard,
        iconSelected = Icons.Filled.Dashboard,
        iconNotSelected = Icons.Outlined.Dashboard,
        rootRequired = false
    ),
    SuperUser(
        direction = SuperUserScreenDestination,
        label = R.string.superuser,
        iconSelected = Icons.Filled.Security,
        iconNotSelected = Icons.Outlined.Security,
        rootRequired = false
    ),
    Module(
        direction = ModuleScreenDestination,
        label = R.string.module,
        iconSelected = Icons.Filled.Apps,
        iconNotSelected = Icons.Outlined.Apps,
        rootRequired = false
    )
}
