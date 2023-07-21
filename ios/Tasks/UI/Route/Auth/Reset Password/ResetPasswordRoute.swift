//
//  ResetPasswordRoute.swift
//  Tasks
//
//  Created by Luke Myers on 6/9/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TasksShared

struct ResetPasswordRoute: View {
    @Environment(\.presentationMode) var presentation

    @StateObject var viewModel = ResetPasswordViewModel()

    @State var showAlert: Bool = false

    var resetToken: String

    var navigateUp: () -> Void

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        ResetPasswordScreen(
            state: uiState,
            onResetClicked: { password in
                viewModel.resetPassword(resetToken: resetToken, newPassword: password)
            }
        ).onChange(of: uiState.passwordReset) { value in
            if value {
                showAlert = true
            }
        }
        .alert(isPresented: $showAlert) {
            Alert(
                title: Text("Password reset"),
                message: Text("You may now sign in."),
                dismissButton: .default(Text("OK"), action: {
                    presentation.wrappedValue.dismiss()
                    navigateUp()
                })
            )
        }
    }
}
