//
//  SettingsRoute.swift
//  Tasks
//
//  Created by Luke Myers on 4/14/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import MultiPlatformLibrary

struct SettingsRoute: View {
    @ObservedObject var viewModel = SettingsViewModel()
    
    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })
        
        if !uiState.firstLoad {
            SettingsScreen(state: uiState) { theme in
                viewModel.setAppTheme(theme: theme)
            }
        } else {
            ProgressView()
        }
    }
}
