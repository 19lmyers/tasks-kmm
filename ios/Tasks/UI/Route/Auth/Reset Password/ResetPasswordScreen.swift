//
//  ResetPasswordScreen.swift
//  Tasks
//
//  Created by Luke Myers on 6/9/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct ResetPasswordScreen: View {
    var state: ResetPasswordUiState

    var onResetClicked: (String) -> Void

    @State private var password: String = ""

    @State private var showPassword: Bool = false

    var body: some View {
        VStack {
            HStack {
                if showPassword {
                    TextField("Password", text: $password, prompt: Text("Password"))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .submitLabel(.done)
                        .padding()
                } else {
                    SecureField("Password", text: $password, prompt: Text("Password"))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .submitLabel(.done)
                        .padding()
                }

                Button(action: { showPassword.toggle() }) {
                    if showPassword {
                        Image(systemName: "eye.slash.fill")
                    } else {
                        Image(systemName: "eye")
                    }
                }
                .padding(.trailing)
            }

            Spacer()
        }
        .safeAreaInset(edge: .bottom) {
            HStack {
                Spacer()

                Button(action: {
                    onResetClicked(password)
                }) {
                    Text("Reset")
                }
                .disabled(password.isEmpty || state.isLoading)
                .buttonStyle(BorderedProminentButtonStyle())
                .padding()
            }
            .background(.bar)
        }
        .navigationTitle("Forgot password?")
    }
}

struct ResetPasswordScreen_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            ResetPasswordScreen(
                state: ResetPasswordUiState(isLoading: false, passwordReset: false),
                onResetClicked: { _ in }
            )
        }
    }
}
