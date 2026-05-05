package br.com.vibetube.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.vibetube.app.features.activity.ActivityScreen
import br.com.vibetube.app.features.comments.CommentsScreen
import br.com.vibetube.app.features.comments.BloggerCommentWebViewScreen
import br.com.vibetube.app.features.explore.ExploreScreen
import br.com.vibetube.app.features.feed.FeedScreen
import br.com.vibetube.app.features.invite.InviteScreen
import br.com.vibetube.app.features.profile.ProfileScreen
import br.com.vibetube.app.features.standby.StandbyScreen
import br.com.vibetube.app.ui.components.BottomNavBar

@Composable
fun VibeTubeNavHost() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    // Rotas que NÃO mostram a bottom bar (telas modais/full screen)
    val routesWithoutBottomBar = remember {
        setOf(
            Routes.COMMENTS,
            Routes.COMMENT_WEB,
            Routes.STANDBY
        )
    }
    val showBottomBar = currentRoute !in routesWithoutBottomBar

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(Routes.FEED) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = Routes.FEED
            ) {
                composable(Routes.FEED) {
                    FeedScreen(
                        onOpenComments = { videoId ->
                            navController.navigate(Routes.comments(videoId))
                        },
                        onOpenStandby = { featureName ->
                            navController.navigate(Routes.standby(featureName))
                        }
                    )
                }
                composable(Routes.EXPLORE) {
                    ExploreScreen(
                        onVideoClick = { /* volta pro feed posicionando no item */ },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Routes.ACTIVITY) {
                    ActivityScreen(
                        onOpenStandby = { featureName ->
                            navController.navigate(Routes.standby(featureName))
                        },
                        onOpenInvite = { navController.navigate(Routes.INVITE) }
                    )
                }
                composable(Routes.PROFILE) {
                    ProfileScreen(
                        onOpenStandby = { featureName ->
                            navController.navigate(Routes.standby(featureName))
                        },
                        onOpenInvite = { navController.navigate(Routes.INVITE) }
                    )
                }
                composable(Routes.INVITE) {
                    InviteScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    route = Routes.COMMENTS,
                    arguments = listOf(navArgument(Routes.ARG_VIDEO_ID) { type = NavType.StringType })
                ) { entry ->
                    val encoded = entry.arguments?.getString(Routes.ARG_VIDEO_ID).orEmpty()
                    val videoId = Routes.decodeFromRoute(encoded)
                    CommentsScreen(
                        videoId = videoId,
                        onBack = { navController.popBackStack() },
                        onOpenWebComment = {
                            navController.navigate(Routes.commentWeb(videoId))
                        }
                    )
                }
                composable(
                    route = Routes.COMMENT_WEB,
                    arguments = listOf(navArgument(Routes.ARG_VIDEO_ID) { type = NavType.StringType })
                ) { entry ->
                    val encoded = entry.arguments?.getString(Routes.ARG_VIDEO_ID).orEmpty()
                    val videoId = Routes.decodeFromRoute(encoded)
                    BloggerCommentWebViewScreen(
                        videoId = videoId,
                        onClose = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Routes.STANDBY,
                    arguments = listOf(navArgument(Routes.ARG_FEATURE_NAME) { type = NavType.StringType })
                ) { entry ->
                    val featureName = entry.arguments?.getString(Routes.ARG_FEATURE_NAME).orEmpty()
                    StandbyScreen(
                        featureName = featureName,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

// Helper para evitar import inline
@Composable
private fun <T> remember(calculation: () -> T): T = androidx.compose.runtime.remember(calculation = calculation)
