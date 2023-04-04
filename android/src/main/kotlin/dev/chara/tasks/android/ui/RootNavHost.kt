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
fun RootNavHost(navController: NavController<RootNavTarget>, windowSizeClass: WindowSizeClass) {
    val useNavRail = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact

    val useEditDialog = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact
            && windowSizeClass.heightSizeClass > WindowHeightSizeClass.Compact

    val useDualPane = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
            && windowSizeClass.heightSizeClass > WindowHeightSizeClass.Compact

    NavBackHandler(navController)

    AnimatedNavHost(navController) { navTarget ->
        when (navTarget) {
            RootNavTarget.Welcome -> WelcomeRoute(
                navigateToSignIn = {
                    navController.navigate(RootNavTarget.SignIn)
                },
                navigateToSignUp = {
                    navController.navigate(RootNavTarget.SignUp)
                }
            )

            is RootNavTarget.Home -> HomeRoute(
                useNavRail = useNavRail,
                useEditDialog = useEditDialog,
                useDualPane = useDualPane,
                initialNavTarget = navTarget,
                navigateToWelcome = {
                    navController.replaceAll(RootNavTarget.Welcome)
                },
                navigateToSettings = {
                    navController.navigate(RootNavTarget.Settings)
                },
                navigateToChangeEmail = {
                    // TODO
                },
                navigateToChangePassword = {
                    navController.navigate(RootNavTarget.ChangePassword)
                }
            )

            RootNavTarget.Settings -> SettingsRoute(
                navigateUp = {
                    navController.pop()
                }
            )

            // TODO email change + verify

            RootNavTarget.ChangePassword -> ChangePasswordRoute(
                viewModel(),
                navigateToHome = {
                    navController.replaceAll(RootNavTarget.Home.Default)
                },
                navigateUp = {
                    navController.pop()
                }
            )

            RootNavTarget.SignIn -> SignInRoute(
                navigateToHome = {
                    navController.replaceAll(RootNavTarget.Home.Default)
                },
                navigateToForgotPassword = {
                    navController.navigate(RootNavTarget.ForgotPassword)
                },
                navigateUp = {
                    navController.pop()
                }
            )

            RootNavTarget.SignUp -> SignUpRoute(
                navigateToHome = {
                    navController.replaceAll(RootNavTarget.Home.Default)
                },
                navigateUp = {
                    navController.pop()
                }
            )

            RootNavTarget.ForgotPassword -> ForgotPasswordRoute(
                navigateUp = {
                    navController.pop()
                }
            )

            is RootNavTarget.ResetPassword -> ResetPasswordRoute(
                navTarget.resetToken,
                navigateToSignIn = {
                    navController.replaceAll(listOf(RootNavTarget.Welcome, RootNavTarget.SignIn))
                }
            )
        }
    }
}