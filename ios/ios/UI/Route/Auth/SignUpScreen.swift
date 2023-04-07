//
//  SignUpScreen.swift
//  ios
//
//  Created by Luke Myers on 4/7/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct SignUpScreen: View {
    var state: SignUpUiState

    var onSignUpClicked: (String, String, String) -> Void

    var validateEmail: (String) -> Result<KotlinUnit, NSString>

    @State private var email: String = ""
    @State private var displayName: String = ""
    @State private var password: String = ""

    var body: some View {
        let emailResult = validateEmail(email)

        VStack {
            TextField("Email", text: $email, prompt: Text("Email"))
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .keyboardType(.emailAddress)
                .autocorrectionDisabled(true)
                .padding(.horizontal)
                .padding(.top)

            if (!email.isEmpty && emailResult.isErr()) {
                Text(emailResult.getError() as! String)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.leading)
                    .foregroundColor(.red)
            }
            
            TextField("Display Name", text: $displayName, prompt: Text("Display Name"))
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding(.horizontal)
                .padding(.top)

            SecureField("Password", text: $password, prompt: Text("Password"))
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()

            Spacer()
        }.safeAreaInset(edge: .bottom) {
            HStack {
                Spacer()

                Button(action: {
                    onSignUpClicked(email, displayName, password)
                }) {
                    Text("Sign Up")
                }
                .disabled(email.isEmpty || emailResult.isErr() || password.isEmpty || state.isLoading)
                .buttonStyle(BorderedProminentButtonStyle())
                .padding()
            }.background(.bar)
        }
        .navigationTitle("Sign up")
    }
}

struct SignUpScreen_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            SignUpScreen(
                state: SignUpUiState(
                    isLoading: false,
                    isAuthenticated: false
                ),
                onSignUpClicked: { _, _, _ in },
                validateEmail: { _ in
                    ResultKt.success(value: KotlinUnit()) as! Result<KotlinUnit, NSString>
                }
            )
        }
    }
}