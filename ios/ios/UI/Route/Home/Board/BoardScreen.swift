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

    var body: some View {
        if state.boardSections.isEmpty && state.pinnedLists.isEmpty {
            Text("No suggestions found")
        } else {
            List {
                ForEach(state.boardSections, id: \.type) { section in
                    Section(header: Text(section.type.title)) {
                        ForEach(section.tasks, id: \.id) { task in
                            TaskView(task: task)
                        }
                    }
                }

                ForEach(state.pinnedLists, id: \.taskList.id) { pinnedList in
                    Section(header: Text(pinnedList.taskList.title)) {
                        ForEach(pinnedList.topTasks, id: \.id) { task in
                            TaskView(task: task)
                        }

                        let count = pinnedList.totalTaskCount - Int32(pinnedList.topTasks.count)

                        if count > 0 {
                            HStack {
                                VStack {
                                    Text("View \(count) more...")
                                    // TODO: implement
                                }
                            }
                        }
                    }
                }
            }
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
                                dateCreated: NSDateKt.toInstant(NSDate.now),
                                lastModified: NSDateKt.toInstant(NSDate.now),
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
                            dateCreated: NSDateKt.toInstant(NSDate.now),
                            lastModified: NSDateKt.toInstant(NSDate.now)
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
                                dateCreated: NSDateKt.toInstant(NSDate.now),
                                lastModified: NSDateKt.toInstant(NSDate.now),
                                ordinal: 1
                            )
                        ],
                        totalTaskCount: 2
                    )
                ],
                allLists: []
            )
        )
    }
}
