package dev.chara.tasks.android.ui.route.welcome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WelcomeScreen(
    onSignInClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
) {
    Scaffold(
        topBar = { LargeTopAppBar(title = { Text("Welcome") }) },
        bottomBar = {
            BottomAppBar(modifier = Modifier.imePadding()) {
                TextButton(
                    modifier = Modifier
                        .padding(16.dp, 8.dp),
                    onClick = { onSignInClicked() }
                ) {
                    Text(text = "Sign In")
                }

                Spacer(Modifier.weight(1f, true))

                FilledTonalButton(
                    modifier = Modifier
                        .padding(16.dp, 8.dp),
                    onClick = { onSignUpClicked() }
                ) {
                    Text(text = "Sign Up")
                }
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            ) {
                Text("TODO put onboarding here", modifier = Modifier.align(Alignment.Center))
            }
        }
    )
}

@Preview
@Composable
private fun Preview_WelcomeScreen() {
    WelcomeScreen(onSignInClicked = {}, onSignUpClicked = {})
}
