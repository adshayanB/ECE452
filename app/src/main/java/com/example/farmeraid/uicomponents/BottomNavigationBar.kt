import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.SecondaryColour
import com.example.farmeraid.ui.theme.TertiaryColour
import com.example.farmeraid.uicomponents.models.UiComponentModel

@Composable
fun BottomNavigationBar(appNavigator: AppNavigator) {
    val backStackEntry = appNavigator.navController!!.currentBackStackEntryAsState()
    val bottomNavItems = listOf(
        UiComponentModel.BottomNavItem(
            text = "Home",
            unselectedIcon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home,
            navigateToRoute = NavRoute.Home,
        ),
        UiComponentModel.BottomNavItem(
            text = "Farm",
            unselectedIcon = Icons.Outlined.Agriculture,
            selectedIcon = Icons.Filled.Agriculture,
            navigateToRoute = NavRoute.Farm,
        ),
        UiComponentModel.BottomNavItem(
            text = "Market",
            unselectedIcon = Icons.Outlined.Storefront,
            selectedIcon = Icons.Filled.Storefront,
            navigateToRoute = NavRoute.Market
        ),
        UiComponentModel.BottomNavItem(
                    text = "Charity",
            unselectedIcon = Icons.Outlined.VolunteerActivism,
            selectedIcon = Icons.Filled.VolunteerActivism,
            navigateToRoute = NavRoute.Charity,
        ),
        UiComponentModel.BottomNavItem(
            text = "Sign Out",
            unselectedIcon = Icons.Outlined.Person,
            selectedIcon = Icons.Filled.Person,
            navigateToRoute = NavRoute.SignOut
        ),
    )
    val notShownScreens: List<String?> = listOf(
        null,
        NavRoute.SignIn.route,
        NavRoute.SignUp.route,
        NavRoute.JoinFarm.route,
        NavRoute.CreateFarm.route,
        NavRoute.FarmCode.route,
        NavRoute.FarmSelection.route,
        NavRoute.LoadingScreen.route,
        NavRoute.SignOutScreen.route
    )

    AnimatedVisibility(
        visible = backStackEntry.value?.destination?.route !in notShownScreens,
        enter = slideInVertically { height -> height } + fadeIn(),
        exit = slideOutVertically { height -> height } + fadeOut()
    ) {
        NavigationBar(
            containerColor = TertiaryColour
        ) {
            bottomNavItems.forEach { item ->
                val selected =
                    item.navigateToRoute.route == backStackEntry.value?.destination?.route

                NavigationBarItem(
                    selected = selected,
                    onClick = { appNavigator.navigateToMode(item.navigateToRoute) },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = "${item.text} Icon",
                        )
                    },
                    label = {
                        Text(
                            text = item.text,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = PrimaryColour,
                        indicatorColor = SecondaryColour,
                        unselectedTextColor = PrimaryColour,
                        unselectedIconColor = PrimaryColour,
                    ),
                )
            }
        }
    }
}