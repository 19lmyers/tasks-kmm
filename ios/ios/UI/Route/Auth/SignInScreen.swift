//
//  SignInView.swift
//  ios
//
//  Created by Luke Myers on 4/6/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct SignInScreen: View {
    var state: SignInUiState

    var onSignInClicked: (String, String) -> Void

    var validateEmail: (String) -> Result<KotlinUnit, NSString>

    @State private var email: String = ""
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

            SecureField("Password", text: $password, prompt: Text("Password"))
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()

            Spacer()
        }.safeAreaInset(edge: .bottom) {
            HStack {
                NavigationLink(destination: Text("TODO")) {
                    Text("Forgot password?")
                }
                .disabled(state.isLoading)
                .buttonStyle(DefaultButtonStyle())
                .padding()
                
                Spacer()

                Button(action: {
                    onSignInClicked(email, password)
                }) {
                    Text("Sign In")
                }
                .disabled(email.isEmpty || emailResult.isErr() || password.isEmpty || state.isLoading)
                .buttonStyle(BorderedProminentButtonStyle())
                .padding()
            }.background(.bar)
        }
        .navigationTitle("Sign in")
    }
}

struct SignInScreen_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            SignInScreen(
                state: SignInUiState(
                    isLoading: false,
                    isAuthenticated: false
                ),
                onSignInClicked: { _, _ in },
                validateEmail: { _ in
                    ResultKt.success(value: KotlinUnit()) as! Result<KotlinUnit, NSString>
                }
            )
        }
    }
}
