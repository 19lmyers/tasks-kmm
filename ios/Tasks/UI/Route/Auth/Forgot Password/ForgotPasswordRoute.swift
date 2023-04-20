//
//  ForgotPasswordRoute.swift
//  Tasks
//
//  Created by Luke Myers on 4/19/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import MultiPlatformLibrary

struct ForgotPasswordRoute: View {
    @Environment(\.presentationMode) var presentation

    @ObservedObject var viewModel = ForgotPasswordViewModel()
    
    @State var showAlert: Bool = false
    
    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })
    
        ForgotPasswordScreen(
            state: uiState,
            onResetClicked: { email in
                viewModel.sendResetEmail(email: email)
            },
            validateEmail: { email in
                viewModel.validateEmail(email: email)
            }
        ).onChange(of: uiState.passwordResetLinkSent) { value in
            if (value) {
                showAlert = true
            }
        }
        .alert(isPresented: $showAlert) {
            Alert(
                title: Text("Password reset link sent"),
                message: Text("An email with a password reset link should be arriving shortly."),
                dismissButton: .default(Text("OK"), action: {
                    presentation.wrappedValue.dismiss()
                })
            )
        }
    }
}

