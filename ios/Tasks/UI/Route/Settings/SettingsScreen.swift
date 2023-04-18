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
    var setEnabledForBoardSection: (BoardSection.Type_, Bool) -> Void
    var updateList: (TaskList) -> Void

    @State var appTheme: Theme

    init(
            state: SettingsUiState,
            setTheme: @escaping (Theme) -> Void,
            setEnabledForBoardSection: @escaping (BoardSection.Type_, Bool) -> Void,
            updateList: @escaping (TaskList) -> Void
    ) {
        self.state = state
        self.setTheme = setTheme
        self.setEnabledForBoardSection = setEnabledForBoardSection
        self.updateList = updateList

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

            Section("Dashboard Sections") {
                ForEach(BoardSectionKt.types(), id: \.self) { boardSection in
                    Toggle(isOn: Binding(get: { state.enabledBoardSections.contains(boardSection) }, set: { value in setEnabledForBoardSection(boardSection, value) })) {
                        HStack {
                            Image(systemName: boardSection.icon)
                            Text(boardSection.title)
                        }
                    }
                }
            }

            Section("Pinned Lists") {
                ForEach(state.taskLists, id: \.self) { taskList in
                    Toggle(isOn: Binding(get: { taskList.isPinned }, set: {
                        value in
                        updateList(taskList.edit()
                                .isPinned(value: value)
                                .lastModified(value: DateKt.toInstant(Date.now))
                                .build()
                        )
                    })) {
                        HStack {
                            Image(systemName: taskList.icon?.ui ?? "checklist")
                            Text(taskList.title)
                        }
                    }
                }
            }
        }
                .onChange(of: appTheme) { theme in
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
                        taskLists: [
                            TaskListKt.doNew(id: "", title: "My list")
                        ],
                        enabledBoardSections: []
                ),
                setTheme: { _ in },
                setEnabledForBoardSection: { _, _ in },
                updateList: { _ in }
        )
    }
}
