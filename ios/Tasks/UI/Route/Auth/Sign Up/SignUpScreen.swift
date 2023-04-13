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

            if !email.isEmpty && emailResult.isErr() {
                Text(emailResult.getError() as! String)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.leading)
                    .foregroundColor(.red)
            }

            TextField("Display Name", text: $displayName, prompt: Text("Display Name"))
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding(.horizontal)
                .submitLabel(.next)
                .focused($field, equals: .displayName)
                .padding(.top)

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
                }.padding(.trailing)
            }

            Spacer()
        }.safeAreaInset(edge: .bottom) {
            HStack {
                Spacer()

                Button(action: {
                    onSignUpClicked(email, displayName, password)
                }) {
                    Text("Sign Up")
                }
                .disabled(email.isEmpty || emailResult.isErr() || displayName.isEmpty || password.isEmpty || state.isLoading)
                .buttonStyle(BorderedProminentButtonStyle())
                .padding()
            }.background(.bar)
        }
        .onAppear {
            field = .email
        }
        .onSubmit {
            switch field {
            case .email:
                field = .displayName
            case .displayName:
                field = .password
            default:
                if (!email.isEmpty && emailResult.isOk() && !displayName.isEmpty && !password.isEmpty && !state.isLoading) {
                    field = nil
                    onSignUpClicked(email, displayName, password)
                }
            }
        }
        .navigationTitle("Sign up")
    }

    enum Field {
        case email, displayName, password
    }
}

struct SignUpScreen_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
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
