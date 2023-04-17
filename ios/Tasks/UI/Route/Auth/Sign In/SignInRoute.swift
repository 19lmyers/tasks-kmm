//
//  SignInRoute.swift
//  ios
//
//  Created by Luke Myers on 4/6/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct SignInRoute: View {
    @ObservedObject var viewModel = SignInViewModel()

    @State var currentAlert: PopupMessage? = nil

    var navigateToHome: () -> Void

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        if uiState.isAuthenticated {
            ProgressView().onAppear {
                navigateToHome()
            }
        } else {
            SignInScreen(
                    state: uiState,
                    onSignInClicked: { email, password in
                        viewModel.signIn(email: email, password: password)
                    },
                    validateEmail: { email in
                        viewModel.validateEmail(email: email)
                    }
            )
                    .navigationBarBackButtonHidden(uiState.isLoading)
                    .alert(item: $currentAlert) { message in
                        Alert(title: Text(message.text), message: nil)
                    }
                    .onAppear {
                        viewModel.messages.subscribe(
                                onCollect: { message in
                                    currentAlert = message
                                }
                        )
                    }
        }
    }
}
