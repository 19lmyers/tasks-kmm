package dev.chara.tasks.android.ui.route.welcome

import androidx.compose.runtime.Composable

@Composable
fun WelcomeRoute(
    navigateToSignIn: () -> Unit,
    navigateToSignUp: () -> Unit
) {
    WelcomeScreen(
        onSignInClicked = navigateToSignIn,
        onSignUpClicked = navigateToSignUp
    )
}