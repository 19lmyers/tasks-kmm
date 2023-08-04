package dev.chara.tasks.shared.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dev.chara.tasks.shared.component.RootComponent.Child
import dev.chara.tasks.shared.component.home.DefaultHomeComponent
import dev.chara.tasks.shared.component.home.HomeComponent
import dev.chara.tasks.shared.component.link.reset_password.DefaultResetPasswordComponent
import dev.chara.tasks.shared.component.link.reset_password.ResetPasswordComponent
import dev.chara.tasks.shared.component.link.verify_email.DefaultVerifyEmailComponent
import dev.chara.tasks.shared.component.link.verify_email.VerifyEmailComponent
import dev.chara.tasks.shared.component.profile.DefaultProfileComponent
import dev.chara.tasks.shared.component.profile.ProfileComponent
import dev.chara.tasks.shared.component.profile.change_email.ChangeEmailComponent
import dev.chara.tasks.shared.component.profile.change_email.DefaultChangeEmailComponent
import dev.chara.tasks.shared.component.profile.change_password.ChangePasswordComponent
import dev.chara.tasks.shared.component.profile.change_password.DefaultChangePasswordComponent
import dev.chara.tasks.shared.component.settings.DefaultSettingsComponent
import dev.chara.tasks.shared.component.settings.SettingsComponent
import dev.chara.tasks.shared.component.util.coroutineScope
import dev.chara.tasks.shared.component.welcome.DefaultWelcomeComponent
import dev.chara.tasks.shared.component.welcome.WelcomeComponent
import dev.chara.tasks.shared.component.welcome.forgot_password.DefaultForgotPasswordComponent
import dev.chara.tasks.shared.component.welcome.forgot_password.ForgotPasswordComponent
import dev.chara.tasks.shared.component.welcome.sign_in.DefaultSignInComponent
import dev.chara.tasks.shared.component.welcome.sign_in.SignInComponent
import dev.chara.tasks.shared.component.welcome.sign_up.DefaultSignUpComponent
import dev.chara.tasks.shared.component.welcome.sign_up.SignUpComponent
import dev.chara.tasks.shared.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface RootComponent {
    val state: StateFlow<RootUiState>

    val childStack: Value<ChildStack<*, Child>>

    fun onDeepLink(deepLink: DeepLink)

    fun markTaskAsCompleted(id: String)

    fun linkFCMToken(token: String)

    sealed class Child {
        class Welcome(val component: WelcomeComponent) : Child()
        class SignUp(val component: SignUpComponent) : Child()
        class SignIn(val component: SignInComponent) : Child()
        class ForgotPassword(val component: ForgotPasswordComponent) : Child()

        class Home(val component: HomeComponent) : Child()
        class Profile(val component: ProfileComponent) : Child()
        class Settings(val component: SettingsComponent) : Child()
        class ChangeEmail(val component: ChangeEmailComponent) : Child()
        class ChangePassword(val component: ChangePasswordComponent) : Child()

        class VerifyEmail(val component: VerifyEmailComponent) : Child()
        class ResetPassword(val component: ResetPasswordComponent) : Child()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    deepLink: DeepLink = DeepLink.None,
) : RootComponent, KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope(Dispatchers.Default + SupervisorJob())

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(RootUiState())
    override val state = _state.asStateFlow()

    init {
        coroutineScope.launch {
            combine(
                repository.getAppTheme(),
                repository.getAppThemeVariant()
            ) { theme, variant ->
                RootUiState(
                    appTheme = theme,
                    appThemeVariant = variant
                )
            }.collect {
                _state.value = it
            }
        }
    }

    private val navigation = StackNavigation<Config>()

    private val stack = childStack(
        source = navigation,
        initialStack = { listOf(getStackFor(deepLink)) },
        childFactory = ::child,
        handleBackButton = true
    )

    override val childStack: Value<ChildStack<*, Child>> = stack

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            Config.Welcome -> Child.Welcome(
                DefaultWelcomeComponent(
                    componentContext,
                    navigateToSignUp = {
                        navigation.push(Config.SignUp)
                    },
                    navigateToSignIn = {
                        navigation.push(Config.SignIn)
                    }
                )
            )

            Config.SignUp -> Child.SignUp(
                DefaultSignUpComponent(
                    componentContext,
                    navigateUp = {
                        navigation.pop()
                    },
                    navigateToHome = {
                        navigation.replaceAll(Config.Home.Default)
                    }
                )
            )

            Config.SignIn -> Child.SignIn(
                DefaultSignInComponent(
                    componentContext,
                    navigateUp = {
                        navigation.pop()
                    },
                    navigateToForgotPassword = {
                        navigation.push(Config.ForgotPassword)
                    },
                    navigateToHome = {
                        navigation.replaceAll(Config.Home.Default)
                    }
                )
            )

            Config.ForgotPassword -> Child.ForgotPassword(
                DefaultForgotPasswordComponent(
                    componentContext,
                    navigateUp = {
                        navigation.pop()
                    }
                )
            )

            is Config.Home -> Child.Home(
                DefaultHomeComponent(
                    componentContext,
                    defaultListId = if (config is Config.Home.WithListDetails) config.id else null,
                    defaultTaskId = if (config is Config.Home.WithTaskDetails) config.id else null,
                    showCreateTaskSheet = config is Config.Home.WithCreateTask,
                    navigateToWelcome = {
                        navigation.replaceAll(Config.Welcome)
                    },
                    navigateToProfile = {
                        navigation.push(Config.Profile)
                    },
                    navigateToSettings = {
                        navigation.push(Config.Settings)
                    }
                )
            )

            Config.Profile -> Child.Profile(
                DefaultProfileComponent(
                    componentContext,
                    navigateUp = {
                        navigation.pop()
                    },
                    navigateToChangeEmail = {
                        navigation.push(Config.ChangeEmail)
                    },
                    navigateToChangePassword = {
                        navigation.push(Config.ChangePassword)
                    }
                )
            )

            Config.Settings -> Child.Settings(
                DefaultSettingsComponent(
                    componentContext,
                    navigateUp = {
                        navigation.pop()
                    }
                )
            )

            Config.ChangeEmail -> Child.ChangeEmail(
                DefaultChangeEmailComponent(
                    componentContext,
                    navigateUp = {
                        navigation.pop()
                    },
                )
            )

            Config.ChangePassword -> Child.ChangePassword(
                DefaultChangePasswordComponent(
                    componentContext,
                    navigateUp = {
                        navigation.pop()
                    },
                )
            )

            is Config.VerifyEmail -> Child.VerifyEmail(
                DefaultVerifyEmailComponent(
                    componentContext,
                    verifyToken = config.verifyToken,
                    navigateUp = {
                        navigation.pop()
                    },
                    navigateToHome = {
                        navigation.replaceAll(Config.Home.Default)
                    }
                )
            )

            is Config.ResetPassword -> Child.ResetPassword(
                DefaultResetPasswordComponent(
                    componentContext,
                    resetToken = config.resetToken,
                    navigateUp = {
                        navigation.pop()
                    },
                    navigateToSignIn = {
                        navigation.replaceAll(Config.Welcome, Config.SignIn)
                    }
                )
            )
        }

    override fun onDeepLink(deepLink: DeepLink) {
        navigation.replaceAll(getStackFor(deepLink))
    }

    override fun markTaskAsCompleted(id: String) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val task = repository.getTaskById(id).first() ?: return@withContext

                val result = repository.updateTask(
                    task.listId, task.id, task.copy(
                        isCompleted = true,
                        lastModified = Clock.System.now()
                    )
                )
                // TODO show snackbar
            }
        }
    }

    override fun linkFCMToken(token: String) {
        coroutineScope.launch {
            if (!repository.isUserAuthenticated().first()) return@launch

            repository.linkFCMToken(token)
        }
    }

    private companion object {
        private fun getStackFor(deepLink: DeepLink): Config =
            when (deepLink) {
                DeepLink.None -> Config.Home.Default

                is DeepLink.ViewList -> Config.Home.WithListDetails(deepLink.id)
                is DeepLink.ViewTask -> Config.Home.WithTaskDetails(deepLink.id)

                DeepLink.CreateTask -> Config.Home.WithCreateTask

                is DeepLink.VerifyEmail -> Config.VerifyEmail(deepLink.token)
                is DeepLink.ResetPassword -> Config.ResetPassword(deepLink.token)
            }
    }

    private sealed interface Config : Parcelable {
        @Parcelize
        object Welcome : Config

        @Parcelize
        object SignUp : Config

        @Parcelize
        object SignIn : Config

        @Parcelize
        object ForgotPassword : Config

        sealed interface Home : Config {

            @Parcelize
            object Default : Home

            @Parcelize
            data class WithListDetails(val id: String) : Home

            @Parcelize
            data class WithTaskDetails(val id: String) : Home

            @Parcelize
            object WithCreateTask : Home
        }

        @Parcelize
        object Profile : Config

        @Parcelize
        object Settings : Config

        @Parcelize
        object ChangeEmail : Config

        @Parcelize
        object ChangePassword : Config

        @Parcelize
        class VerifyEmail(val verifyToken: String) : Config

        @Parcelize
        class ResetPassword(val resetToken: String) : Config
    }

}
