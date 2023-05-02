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

    @State private var showPassword: Bool = false

    @FocusState private var field: Field?

    var body: some View {
        let emailResult = validateEmail(email)

        VStack {
            TextField("Email", text: $email, prompt: Text("Email"))
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .keyboardType(.emailAddress)
                .autocorrectionDisabled(true)
                .textInputAutocapitalization(.never)
                .submitLabel(.next)
                .focused($field, equals: .email)
                .padding(.horizontal)
                .padding(.top)

            if !email.isEmpty, emailResult.isErr() {
                Text(emailResult.getError() as! String)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.leading)
                    .foregroundColor(.red)
            }

            HStack {
                if showPassword {
                    TextField("Password", text: $password, prompt: Text("Password"))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .submitLabel(.done)
                        .focused($field, equals: .password)
                        .padding()
                } else {
                    SecureField("Password", text: $password, prompt: Text("Password"))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .submitLabel(.done)
                        .focused($field, equals: .password)
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
                NavigationLink(destination: {
                    ForgotPasswordRoute()
                        .navigationTitle("Forgot password?")
                }) {
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
            }
            .background(.bar)
        }
        .onAppear {
            field = .email
        }
        .onSubmit {
            switch field {
            case .email:
                field = .password
            default:
                if !email.isEmpty, emailResult.isOk(), !password.isEmpty, !state.isLoading {
                    field = nil
                    onSignInClicked(email, password)
                }
            }
        }
        .navigationTitle("Sign in")
    }

    enum Field {
        case email, password
    }
}

struct SignInScreen_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            SignInScreen(
                state: SignInUiState(
                    isLoading: false,
                    isAuthenticated: false
                ),
                onSignInClicked: { _, _ in },
                validateEmail: { _ in
                    ResultKt.ok(value: KotlinUnit()) as! Result<KotlinUnit, NSString>
                }
            )
        }
    }
}
