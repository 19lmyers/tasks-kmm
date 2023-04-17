//
//  TaskDetailsScreen.swift
//  Tasks
//
//  Created by Luke Myers on 4/14/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct TaskDetailsScreen: View {
    var state: TaskDetailsUiState

    var onUpdate: (Task) -> Void
    var onMove: (Task, String) -> Void

    @State var modified: Bool = false

    @State var listId: String

    @State var label: String

    @State var details: String?

    @State var reminderDate: Date?
    @State var dueDate: Date?

    init(state: TaskDetailsUiState, onUpdate: @escaping (Task) -> Void, onMove: @escaping (Task, String) -> Void) {
        self.state = state
        self.onUpdate = onUpdate
        self.onMove = onMove

        _listId = State(initialValue: state.task!.listId)

        _label = State(initialValue: state.task!.label)

        _details = State(initialValue: state.task!.details)

        _reminderDate = State(initialValue: state.task!.reminderDate?.toDate())
        _dueDate = State(initialValue: state.task!.dueDate?.toDate())
    }

    var body: some View {
        List {
            Section {
                HStack {
                    CheckboxView(isChecked: state.task!.isCompleted) { isChecked in
                        onUpdate(state.task!.edit()
                                .isCompleted(value: isChecked)
                                .lastModified(value: DateKt.toInstant(Date.now))
                                .build()
                        )
                    }
                    TextField("Label", text: $label, prompt: Text("Enter label (required)"))
                }

                if state.taskLists.count > 1 {
                    Picker(
                            selection: $listId,
                            content: {
                                ForEach(state.taskLists, id: \.id) { list in
                                    Text(list.title)
                                }
                            },
                            label: {
                                Text("List")
                            }
                    )
                }
            }

            Section {
                HStack {
                    Image(systemName: "text.justify.left")
                    TextField("Details", text: Binding($details, replacingNilWith: ""), prompt: Text("Add details"), axis: .vertical)
                }
            }

            Section {
                HStack {
                    Image(systemName: "bell")

                    if reminderDate == nil {
                        Button(action: {
                            reminderDate = Date.now
                        }) {
                            Text("Remind me")
                        }
                    } else {
                        DatePicker(
                                "Remind me",
                                selection: Binding(
                                        $reminderDate,
                                        replacingNilWith: Date.distantFuture
                                ),
                                in: Date.now...Date.distantFuture
                        )
                                .labelsHidden()

                        Spacer()

                        Button(action: {
                            reminderDate = nil
                        }) {
                            Image(systemName: "xmark")
                        }
                    }
                }

                HStack {
                    Image(systemName: "calendar")

                    if dueDate == nil {
                        Button(action: {
                            dueDate = Date.now
                        }) {
                            Text("Set due date")
                        }
                    } else {
                        DatePicker(
                                "Set due date",
                                selection: Binding(
                                        $dueDate,
                                        replacingNilWith: Date.distantFuture
                                ),
                                in: Date.now...Date.distantFuture,
                                displayedComponents: [.date]
                        )
                                .labelsHidden()

                        Spacer()

                        Button(action: {
                            dueDate = nil
                        }) {
                            Image(systemName: "xmark")
                        }
                    }
                }
            }
        }
                .onChange(of: listId) { id in
                    onMove(state.task!, id)
                }
                .onChange(of: label) { _ in
                    modified = true
                }
                .onChange(of: details) { _ in
                    modified = true
                }
                .onChange(of: reminderDate) { _ in
                    modified = true
                }
                .onChange(of: dueDate) { _ in
                    modified = true
                }
                .onDisappear {
                    if modified {
                        onUpdate(
                                state.task!.edit()
                                        .label(value: label)
                                        .details(value: details)
                                        .reminderDate(value: DateKt.toInstantOrNull(reminderDate))
                                        .dueDate(value: DateKt.toInstantOrNull(dueDate))
                                        .lastModified(value: DateKt.toInstant(Date.now))
                                        .build()
                        )
                        modified = false
                    }
                }
    }
}

struct TaskDetailsScreen_Previews: PreviewProvider {
    static var previews: some View {
        TaskDetailsScreen(
                state: TaskDetailsUiState(
                        isLoading: false,
                        firstLoad: false,
                        task: TaskKt.doNew(id: "1", listId: "1", label: "My task"),
                        taskLists: [
                            TaskListKt.doNew(id: "1", title: "My list")
                        ]
                ),
                onUpdate: { _ in },
                onMove: { _, _ in }
        )
    }
}
