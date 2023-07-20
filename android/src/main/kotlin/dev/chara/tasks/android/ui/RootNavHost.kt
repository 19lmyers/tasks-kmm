package dev.chara.tasks.android.ui

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import dev.chara.tasks.android.ui.route.auth.forgot_password.ForgotPasswordRoute
import dev.chara.tasks.android.ui.route.auth.reset_password.ResetPasswordRoute
import dev.chara.tasks.android.ui.route.auth.sign_in.SignInRoute
import dev.chara.tasks.android.ui.route.auth.sign_up.SignUpRoute
import dev.chara.tasks.android.ui.route.auth.verify_email.VerifyEmailRoute
import dev.chara.tasks.android.ui.route.home.HomeRoute
import dev.chara.tasks.android.ui.route.profile.ProfileRoute
import dev.chara.tasks.android.ui.route.profile.change_email.ChangeEmailRoute
import dev.chara.tasks.android.ui.route.profile.change_password.ChangePasswordRoute
import dev.chara.tasks.android.ui.route.settings.SettingsRoute
import dev.chara.tasks.android.ui.route.welcome.WelcomeRoute
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.replaceAll

@Composable
fun RootNavHost(
    navController: NavController<NavTarget>,
    windowSizeClass: WindowSizeClass,
    showCreateTaskSheet: Boolean
) {
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
                navigateToProfile = {
                    navController.navigate(NavTarget.Profile)
                },
                navigateToSettings = {
                    navController.navigate(NavTarget.Settings)
                },
                initCreateTaskSheet = showCreateTaskSheet
            )

            NavTarget.Profile -> ProfileRoute(
                navigateUp = {
                    navController.pop()
                },
                navigateToChangeEmail = {
                    navController.navigate(NavTarget.ChangeEmail)
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

            NavTarget.ChangeEmail -> ChangeEmailRoute(
                navigateToHome = {
                    navController.replaceAll(NavTarget.Home.Default)
                },
                navigateUp = {
                    navController.pop()
                }
            )

            NavTarget.ChangePassword -> ChangePasswordRoute(
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

            is NavTarget.VerifyEmail -> VerifyEmailRoute(
                navTarget.verifyToken,
                navigateToHome = {
                    navController.replaceAll(NavTarget.Home.Default)
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