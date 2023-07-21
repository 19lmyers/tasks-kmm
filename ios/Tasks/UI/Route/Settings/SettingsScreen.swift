//
//  SettingsScreen.swift
//  Tasks
//
//  Created by Luke Myers on 4/14/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TasksShared

struct SettingsScreen: View {
    var state: SettingsUiState

    var setTheme: (Theme) -> Void
    var setEnabledForBoardSection: (BoardSection.Type_, Bool) -> Void
    var updateList: (TaskList) -> Void

    var body: some View {
        if state.firstLoad {
            ProgressView()
        } else {
            SettingsForm(
                state: state,
                setTheme: setTheme,
                setEnabledForBoardSection: setEnabledForBoardSection,
                updateList: updateList
            )
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
                appThemeVariant: ThemeVariant.tonalSpot,
                taskLists: [
                    TaskListKt.doNew(id: "", title: "My list"),
                ],
                enabledBoardSections: []
            ),
            setTheme: { _ in },
            setEnabledForBoardSection: { _, _ in },
            updateList: { _ in }
        )
    }
}

struct SettingsForm: View {
    var state: SettingsUiState

    var setTheme: (Theme) -> Void
    var setEnabledForBoardSection: (BoardSection.Type_, Bool) -> Void
    var updateList: (TaskList) -> Void

    @State var appTheme: Theme = .systemDefault

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
                                .frame(width: 18, height: 18)
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
                                .frame(width: 18, height: 18)
                            Text(taskList.title)
                        }
                    }
                }
            }
        }
        .onAppear {
            appTheme = state.appTheme
        }
        .onChange(of: appTheme) { theme in
            setTheme(theme)
        }
    }
}
