//
//  ContentView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct ContentView: View {
    var onCreateTaskPressed = {}
    
    @State var showAuthenticationFlow = false

    var body: some View {
        NavigationView {
            if (showAuthenticationFlow) {
                WelcomeView(
                    navigateToHome: {
                        showAuthenticationFlow = false
                    }
                )
            } else {
                HomeRoute(
                    navigateToWelcome: {
                        showAuthenticationFlow = true
                    }
                )
            }
        }
    }
}
