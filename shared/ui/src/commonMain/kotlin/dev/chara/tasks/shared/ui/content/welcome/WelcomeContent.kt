package dev.chara.tasks.shared.ui.content.welcome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
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
import androidx.compose.ui.unit.dp
import dev.chara.tasks.shared.component.welcome.WelcomeComponent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WelcomeContent(component: WelcomeComponent) {
    Scaffold(
        topBar = { LargeTopAppBar(title = { Text("Welcome to Tasks") }) },
        bottomBar = {
            BottomAppBar(modifier = Modifier.padding(WindowInsets.ime.asPaddingValues())) {
                TextButton(
                    modifier = Modifier.padding(16.dp, 8.dp),
                    onClick = { component.onSignIn() }
                ) {
                    Text(text = "Sign in")
                }

                Spacer(Modifier.weight(1f, true))

                FilledTonalButton(
                    modifier = Modifier.padding(16.dp, 8.dp),
                    onClick = { component.onSignUp() }
                ) {
                    Text(text = "Sign up")
                }
            }
        },
        content = { innerPadding ->
            Box(
                modifier =
                    Modifier.fillMaxSize().padding(innerPadding).consumeWindowInsets(innerPadding),
            ) {
                Text("TODO put onboarding here", modifier = Modifier.align(Alignment.Center))
            }
        }
    )
}
