//
//  ChangePasswordScreen.swift
//  Tasks
//
//  Created by Luke Myers on 4/18/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct ChangePasswordScreen: View {
    var state: ChangePasswordUiState

    @State var currentPassword: String = ""
    @State var newPassword: String = ""

    @State private var showCurrentPassword: Bool = false
    @State private var showNewPassword: Bool = false

    var onChangePasswordClicked: (String, String) -> Void

    var body: some View {
        VStack {
            HStack {
                if showCurrentPassword {
                    TextField("Current Password", text: $currentPassword, prompt: Text("Current password"))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .submitLabel(.next)
                        .padding()
                } else {
                    SecureField("Current Password", text: $currentPassword, prompt: Text("Current password"))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .submitLabel(.next)
                        .padding()
                }

                Button(action: { showCurrentPassword.toggle() }) {
                    if showCurrentPassword {
                        Image(systemName: "eye.slash.fill")
                    } else {
                        Image(systemName: "eye")
                    }
                }
                .padding(.trailing)
            }

            HStack {
                if showNewPassword {
                    TextField("New Password", text: $newPassword, prompt: Text("New password"))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .submitLabel(.next)
                        .padding()
                } else {
                    SecureField("New Password", text: $newPassword, prompt: Text("New password"))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .submitLabel(.next)
                        .padding()
                }

                Button(action: { showNewPassword.toggle() }) {
                    if showNewPassword {
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
                    onChangePasswordClicked(currentPassword, newPassword)
                }) {
                    Text("Change")
                }
                .disabled(currentPassword.isEmpty || newPassword.isEmpty || currentPassword == newPassword || state.isLoading)
                .buttonStyle(BorderedProminentButtonStyle())
                .padding()
            }
            .background(.bar)
        }
    }
}

struct ChangePasswordScreen_Previews: PreviewProvider {
    static var previews: some View {
        ChangePasswordScreen(
            state: ChangePasswordUiState(
                isLoading: false,
                passwordChanged: false
            ),
            onChangePasswordClicked: { _, _ in }
        )
    }
}
