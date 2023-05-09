//
//  ContentView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct ContentView: View {
    @StateObject var appState = AppState.shared

    @StateObject var viewModel = BaseViewModel()

    @State var showAuthenticationFlow = false

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        switch appState.launchAction {
        case let .reset(resetToken):
            NavigationStack {
                Text(resetToken)
                /* ResetPasswordRoute(
                     navigateUp: {
                         AppState.shared.launchAction = .none
                     }
                 ) */
            }
            .preferredColorScheme(uiState.appTheme.colorScheme)
        default:
            if showAuthenticationFlow {
                NavigationStack {
                    WelcomeScreen(
                        navigateToHome: {
                            showAuthenticationFlow = false
                        }
                    )
                }
                .preferredColorScheme(uiState.appTheme.colorScheme)
            } else {
                HomeRoute(
                    navigateToWelcome: {
                        showAuthenticationFlow = true
                    }
                )
                .preferredColorScheme(uiState.appTheme.colorScheme)
            }
        }
    }
}
