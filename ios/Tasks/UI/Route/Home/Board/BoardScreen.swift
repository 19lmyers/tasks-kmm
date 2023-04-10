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
                ForEach(state.boardSections, id: \.type) { section in
                    Section(header: Text(section.type.title)) {
                        ForEach(section.tasks, id: \.id) { task in
                            let parentList = state.allLists.first(where: { each in each.id == task.listId })

                            NavigationLink(destination: {
                                Text(task.description())
                                    .foregroundStyle(.tint)
                                    .tint(parentList?.color?.ui ?? Color.accentColor)
                            }) {
                                TaskView(task: task, onUpdate: onUpdateTask)
                                    .tint(parentList?.color?.ui ?? Color.accentColor)
                            }
                        }
                    }
                }

                ForEach(state.pinnedLists, id: \.taskList.id) { pinnedList in
                    Section(header: Text(pinnedList.taskList.title)) {
                        ForEach(pinnedList.topTasks, id: \.id) { task in
                            NavigationLink(destination: {
                                Text(task.description())
                                    .foregroundStyle(.tint)
                                    .tint(pinnedList.taskList.color?.ui ?? Color.accentColor)
                            }) {
                                TaskView(task: task, onUpdate: onUpdateTask)
                            }
                        }

                        let count = pinnedList.totalTaskCount - Int32(pinnedList.topTasks.count)

                        if count > 0 {
                            NavigationLink(destination: {
                                Text(pinnedList.taskList.description())
                                    .foregroundStyle(.tint)
                                    .tint(pinnedList.taskList.color?.ui ?? Color.accentColor)
                            }) {
                                Text("View \(count) more...")
                                    .foregroundStyle(.tint)
                            }
                        }
                    }.tint(pinnedList.taskList.color?.ui ?? Color.accentColor)
                }
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
