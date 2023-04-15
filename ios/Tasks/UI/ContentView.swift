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
    @ObservedObject var viewModel = BaseViewModel()

    var onCreateTaskPressed = {}

    @State var colorScheme: ColorScheme? = .none
    
    @State var showAuthenticationFlow = false

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        if showAuthenticationFlow {
            WelcomeView(
                navigateToHome: {
                    showAuthenticationFlow = false
                }
            )
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
