//
//  SettingsScreen.swift
//  Tasks
//
//  Created by Luke Myers on 4/14/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct SettingsScreen: View {
    var state: SettingsUiState

    var setTheme: (Theme) -> Void

    @State var appTheme: Theme

    init(state: SettingsUiState, setTheme: @escaping (Theme) -> Void) {
        self.state = state
        self.setTheme = setTheme

        _appTheme = State(initialValue: state.appTheme)
    }

    var body: some View {
        List {
            Section("Appearance") {
                Picker("App theme", selection: $appTheme) {
                    ForEach(ThemeKt.values(), id: \.self) { theme in
                        Text(theme.description())
                    }
                }
            }
        }.onChange(of: appTheme) { theme in
            setTheme(theme)
        }
    }
}

struct SettingsScreen_Previews: PreviewProvider {
    static var previews: some View {
        SettingsScreen(
            state: SettingsUiState(
                isLoading: false,
                firstLoad: false,
                appTheme: .systemDefault,
                useVibrantColors: false,
                taskLists: [],
                enabledBoardSections: []
            ),
            setTheme: { _ in }
        )
    }
}
