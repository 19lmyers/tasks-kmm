package dev.chara.tasks.shared.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import dev.chara.tasks.shared.component.RootComponent
import dev.chara.tasks.shared.model.preference.Theme
import dev.chara.tasks.shared.ui.content.home.HomeContent
import dev.chara.tasks.shared.ui.content.link.ResetPasswordContent
import dev.chara.tasks.shared.ui.content.link.VerifyEmailContent
import dev.chara.tasks.shared.ui.content.profile.ChangeEmailContent
import dev.chara.tasks.shared.ui.content.profile.ChangePasswordContent
import dev.chara.tasks.shared.ui.content.profile.ProfileContent
import dev.chara.tasks.shared.ui.content.settings.SettingsContent
import dev.chara.tasks.shared.ui.content.welcome.ForgotPasswordContent
import dev.chara.tasks.shared.ui.content.welcome.SignInContent
import dev.chara.tasks.shared.ui.content.welcome.SignUpContent
import dev.chara.tasks.shared.ui.content.welcome.WelcomeContent
import dev.chara.tasks.shared.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun RootContent(component: RootComponent) {
    val windowSizeClass = calculateWindowSizeClass()

    val state = component.state.collectAsState()

    AppTheme(
        darkTheme =
            when (state.value.appTheme) {
                Theme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                Theme.LIGHT -> false
                Theme.DARK -> true
            },
        variant = state.value.appThemeVariant
    ) {
        Children(
            stack = component.childStack,
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            animation = stackAnimation(fade()),
        ) {
            when (val child = it.instance) {
                is RootComponent.Child.Welcome -> WelcomeContent(child.component)
                is RootComponent.Child.SignUp -> SignUpContent(child.component)
                is RootComponent.Child.SignIn -> SignInContent(child.component)
                is RootComponent.Child.ForgotPassword -> ForgotPasswordContent(child.component)
                is RootComponent.Child.Home -> HomeContent(child.component, windowSizeClass)
                is RootComponent.Child.Profile -> ProfileContent(child.component)
                is RootComponent.Child.Settings -> SettingsContent(child.component)
                is RootComponent.Child.ChangeEmail -> ChangeEmailContent(child.component)
                is RootComponent.Child.ChangePassword -> ChangePasswordContent(child.component)
                is RootComponent.Child.VerifyEmail -> VerifyEmailContent(child.component)
                is RootComponent.Child.ResetPassword -> ResetPasswordContent(child.component)
            }
        }
    }
}
