//
//  SettingsRoute.swift
//  Tasks
//
//  Created by Luke Myers on 4/14/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct SettingsRoute: View {
    @StateObject var viewModel = SettingsViewModel()

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        SettingsScreen(state: uiState) { theme in
            viewModel.setAppTheme(theme: theme)
        } setEnabledForBoardSection: { boardSection, enabled in
            viewModel.setEnabledForBoardSection(boardSection: boardSection, enabled: enabled)
        } updateList: { list in
            viewModel.updateList(taskList: list)
        }
    }
}
