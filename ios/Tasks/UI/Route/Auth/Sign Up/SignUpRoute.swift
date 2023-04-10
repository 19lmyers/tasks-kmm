//
//  SignUpRoute.swift
//  ios
//
//  Created by Luke Myers on 4/7/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct SignUpRoute: View {
    @ObservedObject var viewModel = SignUpViewModel()

    @State var currentAlert: PopupMessage? = nil

    var navigateToHome: () -> Void

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        if uiState.isAuthenticated {
            ProgressView().onAppear {
                navigateToHome()
            }
        } else {
            SignUpScreen(
                state: uiState,
                onSignUpClicked: { email, displayName, password in
                    viewModel.signUp(email: email, displayName: displayName, password: password)
                },
                validateEmail: { email in
                    viewModel.validateEmail(email: email)
                }
            )
            .navigationBarBackButtonHidden(uiState.isLoading)
            .alert(item: $currentAlert) { message in
                Alert(title: Text(message.text), message: nil)
            }.onAppear {
                viewModel.messages.subscribe(
                    onCollect: { message in
                        currentAlert = message
                    }
                )
            }
        }
    }
}
