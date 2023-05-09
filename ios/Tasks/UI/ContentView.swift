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
    @EnvironmentObject var delegate: AppDelegate

    @StateObject var viewModel = BaseViewModel()

    @State var showAuthenticationFlow = false

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        switch delegate.launchAction {
        case let .reset(resetToken):
            NavigationStack {
                Text(resetToken)
                /* ResetPasswordRoute(
                     navigateToHome: {
                         .launchAction = .none
                         showAuthenticationFlow = false
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
