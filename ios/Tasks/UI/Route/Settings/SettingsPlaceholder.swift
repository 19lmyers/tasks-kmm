//
//  SettingsPlaceholder.swift
//  Tasks
//
//  Created by Luke Myers on 4/26/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct SettingsPlaceholder: View {
    var body: some View {
        SettingsScreen(state: uiState) { theme in
            viewModel.setAppTheme(theme: theme)
        } setEnabledForBoardSection: { boardSection, enabled in
            viewModel.setEnabledForBoardSection(boardSection: boardSection, enabled: enabled)
        } updateList: { list in
            viewModel.updateList(taskList: list)
        }
    }
}

struct SettingsPlaceholder_Previews: PreviewProvider {
    static var previews: some View {
        SettingsPlaceholder()
    }
}
