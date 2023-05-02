//
//  ForgotPasswordScreen.swift
//  Tasks
//
//  Created by Luke Myers on 4/19/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct ForgotPasswordScreen: View {
    var state: ForgotPasswordUiState

    var onResetClicked: (String) -> Void

    var validateEmail: (String) -> Result<KotlinUnit, NSString>

    @State private var email: String = ""

    var body: some View {
        let emailResult = validateEmail(email)

        VStack {
            TextField("Email", text: $email, prompt: Text("Email"))
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .keyboardType(.emailAddress)
                .autocorrectionDisabled(true)
                .textInputAutocapitalization(.never)
                .submitLabel(.next)
                .padding(.horizontal)
                .padding(.top)

            if !email.isEmpty, emailResult.isErr() {
                Text(emailResult.getError() as! String)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.leading)
                    .foregroundColor(.red)
            }

            Spacer()
        }
        .safeAreaInset(edge: .bottom) {
            HStack {
                Spacer()

                Button(action: {
                    onResetClicked(email)
                }) {
                    Text("Confirm")
                }
                .disabled(email.isEmpty || emailResult.isErr() || state.isLoading)
                .buttonStyle(BorderedProminentButtonStyle())
                .padding()
            }
            .background(.bar)
        }
        .navigationTitle("Forgot password?")
    }
}

struct ForgotPasswordScreen_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            ForgotPasswordScreen(
                state: ForgotPasswordUiState(isLoading: false, passwordResetLinkSent: false),
                onResetClicked: { _ in },
                validateEmail: { _ in ResultKt.ok(value: KotlinUnit()) as! Result<KotlinUnit, NSString> }
            )
        }
    }
}
