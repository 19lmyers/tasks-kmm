//
//  WelcomeView.swift
//  ios
//
//  Created by Luke Myers on 4/6/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct WelcomeScreen: View {
    var navigateToHome: () -> Void

    var body: some View {
        VStack {
            Spacer()

            Text("TODO put onboarding here")
            
            Spacer()
        }
                .safeAreaInset(edge: .bottom) {
                    HStack {
                        NavigationLink(destination: SignInRoute(navigateToHome: navigateToHome)) {
                            Text("Sign In")

                        }
                                .buttonStyle(DefaultButtonStyle())
                                .padding()

                        Spacer()

                        NavigationLink(destination: SignUpRoute(navigateToHome: navigateToHome)) {
                            Text("Sign Up")
                        }
                                .buttonStyle(BorderedProminentButtonStyle())
                                .padding()
                    }
                    .background(.bar)
                }
    }
}

struct WelcomeView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            WelcomeScreen(navigateToHome: {})
        }
    }
}
