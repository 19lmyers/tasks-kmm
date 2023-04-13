//
//  BoardView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct BoardScreen: View {
    var state: BoardUiState

    var onRefresh: @Sendable () async -> Void

    var onUpdateTask: (Task) -> Void

    var body: some View {
        if state.boardSections.isEmpty && state.pinnedLists.isEmpty {
            GeometryReader { geometry in
                ScrollView {
                    Text("No suggestions found")
                        .frame(width: geometry.size.width)
                        .frame(minHeight: geometry.size.height)
                }
                .background(Color(UIColor.systemGroupedBackground))
                .refreshable(action: onRefresh)
            }
        } else {
            List {
                BoardSectionsView(
                    sections: state.boardSections,
                    pinnedLists: state.pinnedLists,
                    allLists: state.allLists,
                    onUpdateTask: onUpdateTask
                )
            }.refreshable(action: onRefresh)
        }
    }
}

struct BoardScreen_Previews: PreviewProvider {
    static var previews: some View {
        BoardScreen(
            state: BoardUiState(
                isLoading: false,
                firstLoad: false,
                boardSections: [
                    BoardSection(
                        type: BoardSection.Type_.overdue,
                        tasks: [
                            Task(
                                id: "1",
                                listId: "2",
                                label: "Fix SwiftUI bindings",
                                isCompleted: false,
                                isStarred: false,
                                details: nil,
                                reminderDate: nil,
                                dueDate: nil,
                                dateCreated: DateKt.toInstant(Date.now),
                                lastModified: DateKt.toInstant(Date.now),
                                ordinal: 1
                            )
                        ]
                    )
                ],
                pinnedLists: [
                    PinnedList(
                        taskList: TaskList(
                            id: "1",
                            title: "Tasks",
                            color: nil,
                            icon: nil,
                            description: "This is a list description",
                            isPinned: false,
                            showIndexNumbers: false,
                            sortType: TaskList.SortType.ordinal,
                            sortDirection: TaskList.SortDirection.ascending,
                            dateCreated: DateKt.toInstant(Date.now),
                            lastModified: DateKt.toInstant(Date.now)
                        ),
                        topTasks: [
                            Task(
                                id: "2",
                                listId: "2",
                                label: "Make this look prettier",
                                isCompleted: false,
                                isStarred: false,
                                details: nil,
                                reminderDate: nil,
                                dueDate: nil,
                                dateCreated: DateKt.toInstant(Date.now),
                                lastModified: DateKt.toInstant(Date.now),
                                ordinal: 1
                            )
                        ],
                        totalTaskCount: 2
                    )
                ],
                allLists: []
            ),
            onRefresh: {},
            onUpdateTask: { _ in }
        )
    }
}
