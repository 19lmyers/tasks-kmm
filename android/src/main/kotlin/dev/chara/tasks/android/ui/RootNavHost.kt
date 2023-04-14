package dev.chara.tasks.android.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chara.tasks.android.ui.route.auth.forgot_password.ForgotPasswordRoute
import dev.chara.tasks.android.ui.route.auth.reset_password.ResetPasswordRoute
import dev.chara.tasks.android.ui.route.auth.sign_in.SignInRoute
import dev.chara.tasks.android.ui.route.auth.sign_up.SignUpRoute
import dev.chara.tasks.android.ui.route.home.HomeRoute
import dev.chara.tasks.android.ui.route.home.profile.change_password.ChangePasswordRoute
import dev.chara.tasks.android.ui.route.settings.SettingsRoute
import dev.chara.tasks.android.ui.route.welcome.WelcomeRoute
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.replaceAll

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootNavHost(navController: NavController<NavTarget>, windowSizeClass: WindowSizeClass) {
    val useDualPane = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
            && windowSizeClass.heightSizeClass > WindowHeightSizeClass.Compact

    NavBackHandler(navController)

    AnimatedNavHost(navController) { navTarget ->
        when (navTarget) {
            NavTarget.Welcome -> WelcomeRoute(
                navigateToSignIn = {
                    navController.navigate(NavTarget.SignIn)
                },
                navigateToSignUp = {
                    navController.navigate(NavTarget.SignUp)
                }
            )

            is NavTarget.Home -> HomeRoute(
                useDualPane = useDualPane,
                initialNavTarget = navTarget,
                navigateToWelcome = {
                    navController.replaceAll(NavTarget.Welcome)
                },
                navigateToSettings = {
                    navController.navigate(NavTarget.Settings)
                },
                navigateToChangeEmail = {
                    // TODO
                },
                navigateToChangePassword = {
                    navController.navigate(NavTarget.ChangePassword)
                }
            )

            NavTarget.Settings -> SettingsRoute(
                navigateUp = {
                    navController.pop()
                }
            )

            // TODO email change + verify

            NavTarget.ChangePassword -> ChangePasswordRoute(
                viewModel(),
                navigateToHome = {
                    navController.replaceAll(NavTarget.Home.Default)
                },
                navigateUp = {
                    navController.pop()
                }
            )

            NavTarget.SignIn -> SignInRoute(
                navigateToHome = {
                    navController.replaceAll(NavTarget.Home.Default)
                },
                navigateToForgotPassword = {
                    navController.navigate(NavTarget.ForgotPassword)
                },
                navigateUp = {
                    navController.pop()
                }
            )

            NavTarget.SignUp -> SignUpRoute(
                navigateToHome = {
                    navController.replaceAll(NavTarget.Home.Default)
                },
                navigateUp = {
                    navController.pop()
                }
            )

            NavTarget.ForgotPassword -> ForgotPasswordRoute(
                navigateUp = {
                    navController.pop()
                }
            )

            is NavTarget.ResetPassword -> ResetPasswordRoute(
                navTarget.resetToken,
                navigateToSignIn = {
                    navController.replaceAll(listOf(NavTarget.Welcome, NavTarget.SignIn))
                }
            )
        }
    }
}