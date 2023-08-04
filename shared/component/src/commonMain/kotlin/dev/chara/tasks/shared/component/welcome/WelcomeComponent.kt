package dev.chara.tasks.shared.component.welcome

import com.arkivanov.decompose.ComponentContext

interface WelcomeComponent {
    fun onSignUp()
    fun onSignIn()
}

class DefaultWelcomeComponent(
    componentContext: ComponentContext,
    private val navigateToSignUp: () -> Unit,
    private val navigateToSignIn: () -> Unit
) : WelcomeComponent, ComponentContext by componentContext {
    override fun onSignUp() = navigateToSignUp()
    override fun onSignIn() = navigateToSignIn()
}