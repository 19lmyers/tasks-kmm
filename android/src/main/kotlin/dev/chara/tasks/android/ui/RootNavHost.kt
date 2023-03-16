package dev.chara.tasks.android.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import dev.chara.tasks.android.ui.route.auth.forgot_password.ForgotPasswordRoute
import dev.chara.tasks.android.ui.route.auth.reset_password.ResetPasswordRoute
import dev.chara.tasks.android.ui.route.auth.sign_in.SignInRoute
import dev.chara.tasks.android.ui.route.auth.sign_up.SignUpRoute
import dev.chara.tasks.android.ui.route.home.HomeRoute
import dev.chara.tasks.android.ui.route.home.profile.change_password.ChangePasswordRoute
import dev.chara.tasks.android.ui.route.settings.SettingsRoute
import dev.chara.tasks.android.ui.route.welcome.WelcomeRoute
import dev.chara.tasks.android.ui.viewmodel.LocalViewModelStore
import dev.chara.tasks.android.ui.viewmodel.viewModel
import dev.chara.tasks.viewmodel.auth.forgot_password.ForgotPasswordViewModel
import dev.chara.tasks.viewmodel.auth.reset_password.ResetPasswordViewModel
import dev.chara.tasks.viewmodel.auth.sign_in.SignInViewModel
import dev.chara.tasks.viewmodel.auth.sign_up.SignUpViewModel
import dev.chara.tasks.viewmodel.home.HomeViewModel
import dev.chara.tasks.viewmodel.profile.change_password.ChangePasswordViewModel
import dev.chara.tasks.viewmodel.settings.SettingsViewModel
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.currentHostEntry
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.replaceAll

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootNavHost(navController: NavController<RootNavTarget>, windowSizeClass: WindowSizeClass) {
    val presenterStore = LocalViewModelStore.current

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
                presenterStore.viewModel(navHostEntry = currentHostEntry) { scope ->
                    HomeViewModel(scope)
                },
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
                presenterStore.viewModel(navHostEntry = currentHostEntry) { scope ->
                    SettingsViewModel(scope)
                },
                navigateUp = {
                    navController.pop()
                }
            )

            // TODO email change + verify

            RootNavTarget.ChangePassword -> ChangePasswordRoute(
                presenterStore.viewModel(navHostEntry = currentHostEntry) { scope ->
                    ChangePasswordViewModel(scope)
                },
                navigateToHome = {
                    navController.replaceAll(RootNavTarget.Home.Default)
                },
                navigateUp = {
                    navController.pop()
                }
            )

            RootNavTarget.SignIn -> SignInRoute(
                presenterStore.viewModel(navHostEntry = currentHostEntry) { scope ->
                    SignInViewModel(scope)
                },
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
                presenterStore.viewModel(navHostEntry = currentHostEntry) { scope ->
                    SignUpViewModel(scope)
                },
                navigateToHome = {
                    navController.replaceAll(RootNavTarget.Home.Default)
                },
                navigateUp = {
                    navController.pop()
                }
            )

            RootNavTarget.ForgotPassword -> ForgotPasswordRoute(
                presenterStore.viewModel(navHostEntry = currentHostEntry) { scope ->
                    ForgotPasswordViewModel(scope)
                },
                navigateUp = {
                    navController.pop()
                }
            )

            is RootNavTarget.ResetPassword -> ResetPasswordRoute(
                presenterStore.viewModel(navHostEntry = currentHostEntry) { scope ->
                    ResetPasswordViewModel(navTarget.resetToken, scope)
                },
                navigateToSignIn = {
                    navController.replaceAll(listOf(RootNavTarget.Welcome, RootNavTarget.SignIn))
                }
            )
        }
    }
}