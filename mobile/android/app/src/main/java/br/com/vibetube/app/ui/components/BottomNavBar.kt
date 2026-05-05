package br.com.vibetube.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import br.com.vibetube.app.ui.navigation.Routes
import br.com.vibetube.app.ui.theme.VibeRed
import br.com.vibetube.app.ui.theme.VibeSurface
import br.com.vibetube.app.ui.theme.VibeTextPrimary
import br.com.vibetube.app.ui.theme.VibeTextSecondary

private data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    /** Algumas rotas (Publicar, Atividade) abrem standby quando feature flag = false. */
    val standbyFeatureName: String? = null
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onItemClick: (route: String) -> Unit
) {
    val items = listOf(
        NavItem(Routes.FEED, "Início", Icons.Outlined.Home),
        NavItem(Routes.EXPLORE, "Explorar", Icons.Outlined.Explore),
        // Publicar abre standby (upload em stand-by)
        NavItem("standby/upload", "Publicar", Icons.Outlined.AddBox, "upload"),
        NavItem(Routes.ACTIVITY, "Atividade", Icons.Outlined.Notifications),
        NavItem(Routes.PROFILE, "Perfil", Icons.Outlined.AccountCircle)
    )

    NavigationBar(
        containerColor = VibeSurface,
        contentColor = VibeTextPrimary
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VibeRed,
                    selectedTextColor = VibeRed,
                    unselectedIconColor = VibeTextSecondary,
                    unselectedTextColor = VibeTextSecondary,
                    indicatorColor = VibeSurface
                )
            )
        }
    }
}
