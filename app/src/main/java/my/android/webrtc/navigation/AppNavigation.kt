package my.android.webrtc.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import my.android.webrtc.ui.screens.HomeScreen
import my.android.webrtc.ui.screens.map.MapScreen
import my.android.webrtc.ui.screens.profile.EditProfileScreen
import my.android.webrtc.ui.screens.profile.ProfileScreen
import my.android.webrtc.ui.viewmodel.MapViewModel


@Composable
fun AppNavigation(mapViewModel: MapViewModel){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavigationRoutes.ProfileScreen.name) {

        composable(NavigationRoutes.HomeScreen.name){
            HomeScreen()
        }

        composable(NavigationRoutes.ProfileScreen.name){
            ProfileScreen(
                mapViewModel = mapViewModel,
                onEditProfileClick = {navController.navigate(NavigationRoutes.EditProfileScreen.name)},
                onMapClick = {navController.navigate(NavigationRoutes.MapScreen.name)},
            )
        }

        composable(NavigationRoutes.MapScreen.name){
            MapScreen(
                mapViewModel = mapViewModel,
                onConfirm = {navController.navigate(NavigationRoutes.ProfileScreen.name)})
        }

        composable(NavigationRoutes.EditProfileScreen.name){
            EditProfileScreen(
                onBackClick = { navController.navigateUp()},
                onSaveClick = { navController.navigateUp() },
                onCancelClick = { navController.navigateUp() })
        }
    }
}