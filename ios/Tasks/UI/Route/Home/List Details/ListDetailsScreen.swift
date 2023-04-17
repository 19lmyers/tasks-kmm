//
//  ListDetailsScreen.swift
//  Tasks
//
//  Created by Luke Myers on 4/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct ListDetailsScreen: View {
    var state: ListDetailsUiState

    @State var showCompletedTasks: Bool = false

    var onRefresh: @Sendable () async -> Void

    var onCreateTaskPressed: () -> Void

    var onUpdateTask: (Task) -> Void

    var onReorderTask: (String, Int, Int) -> Void

    func reorder(from: IndexSet, to: Int) {
        let moved = state.currentTasks[from.first!]
        onReorderTask(moved.id, from.first!, to)
    }

    var body: some View {
        List {
            Section {
                ForEach(Array(state.currentTasks.enumerated()), id: \.element.id) { index, task in
                    TaskView(task: task, onUpdate: onUpdateTask, showIndexNumbers: state.selectedList!.showIndexNumbers, indexNumber: index + 1)
                }
                        .onMove(perform: state.selectedList!.sortType == .ordinal ? reorder : nil)

                CreateTaskView(onCreateTaskPressed: onCreateTaskPressed)
            }

            if !state.completedTasks.isEmpty {
                Section {
                    DisclosureGroup(
                            isExpanded: $showCompletedTasks,
                            content: {
                                ForEach(state.completedTasks) { task in
                                    TaskView(task: task, onUpdate: onUpdateTask)
                                            .id(task.id)
                                }
                            },
                            label: {
                                Text("Completed (\(state.completedTasks.count))")
                            }
                    )
                }
            }
        }
                .listStyle(.insetGrouped)
                .refreshable(action: onRefresh)
    }
}

struct ListDetailsScreen_Previews: PreviewProvider {
    static var previews: some View {
        ListDetailsScreen(
                state: ListDetailsUiState(
                        isLoading: false,
                        firstLoad: false,
                        selectedList: nil,
                        currentTasks: [],
                        completedTasks: []
                ),
                onRefresh: {},
                onCreateTaskPressed: {},
                onUpdateTask: { _ in },
                onReorderTask: { _, _, _ in }
        )
    }
}
