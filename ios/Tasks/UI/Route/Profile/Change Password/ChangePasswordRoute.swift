//
//  ChangePasswordRoute.swift
//  Tasks
//
//  Created by Luke Myers on 4/18/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import MultiPlatformLibrary

struct ChangePasswordRoute: View {
    @Environment(\.presentationMode) var presentation

    @ObservedObject var viewModel = ChangePasswordViewModel()

    @State var currentAlert: PopupMessage? = nil

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        if uiState.passwordChanged {
            ProgressView().onAppear {
                presentation.wrappedValue.dismiss()
            }
        } else {
            ChangePasswordScreen(state: uiState) { currentPassword, newPassword in
                viewModel.changePassword(currentPassword: currentPassword, newPassword: newPassword)
            }
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
                    .navigationBarBackButtonHidden(uiState.isLoading)
        }
    }
}
