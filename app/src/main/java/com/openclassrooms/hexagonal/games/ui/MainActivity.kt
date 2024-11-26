package com.openclassrooms.hexagonal.games.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.screen.ad.AddScreen
import com.openclassrooms.hexagonal.games.screen.auth.AuthenticationScreen
import com.openclassrooms.hexagonal.games.screen.detail.AddCommentScreen
import com.openclassrooms.hexagonal.games.screen.homefeed.HomefeedScreen
import com.openclassrooms.hexagonal.games.screen.management.AccountManagement
import com.openclassrooms.hexagonal.games.screen.management.AccountManagementViewModel
import com.openclassrooms.hexagonal.games.screen.settings.SettingsScreen
import com.openclassrooms.hexagonal.games.screen.detail.DetailScreen
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the application. This activity serves as the entry point and container for the navigation
 * fragment. It handles setting up the toolbar, navigation controller, and action bar behavior.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            HexagonalGamesTheme {
                HexagonalGamesNavHost(navHostController = navController)
            }
        }
    }

}

@Composable
fun HexagonalGamesNavHost(
    navHostController: NavHostController,
    viewModel: AccountManagementViewModel = hiltViewModel()
) {
    NavHost(
        navController = navHostController,
        //startDestination = Screen.Auth.route
        startDestination = Screen.Homefeed.route
    ) {

        composable(route = Screen.Homefeed.route) {
            HomefeedScreen(
                onPostClick = {
                    navHostController.navigate("${Screen.Detail.route}/${it.id}")                },
                onSettingsClick = {
                    navHostController.navigate(Screen.Settings.route)
                },
                onFABClick = {
                    navHostController.navigate(Screen.AddPost.route)
                },
                onMyAccountClick = {
                    navHostController.navigate(Screen.Management.route)
                }
            )
        }

        composable(
            route = "${Screen.Detail.route}/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            DetailScreen(postId = postId, onBackClick = { navHostController.navigateUp() }, onFABClick = {navHostController.navigate("${Screen.Comment.route}/$postId")})
        }
        composable(route = Screen.Management.route) {
            if (viewModel.isUserLoggedIn()) {
                AccountManagement(onBackClick = { navHostController.navigateUp() })
            } else {
                AuthenticationScreen(onLoginAction = { navHostController.navigate(Screen.Homefeed.route) }, onBackClick = {navHostController.navigateUp()})
            }
        }
        composable(
            route = "${Screen.Comment.route}/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            AddCommentScreen(postId = postId, onBackClick = { navHostController.navigateUp() })
        }
        composable(route = Screen.AddPost.route) {
            AddScreen(
                onBackClick = { navHostController.navigateUp() },
                onSaveClick = { navHostController.navigateUp() }
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navHostController.navigateUp() }
            )
        }
    }
}


