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
    @Environment(\.presentationMode) var presentation

    var state: TaskDetailsUiState

    var onUpdate: (Task) -> Void
    var onMove: (Task, String) -> Void

    @State var modified: Bool = false

    @State var listId: String = ""

    @State var label: String = ""

    @State var details: String? = nil

    @State var reminderDate: Date? = nil
    @State var dueDate: Date? = nil

    var body: some View {
        if state.firstLoad {
            ProgressView()
        } else {
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
                                in: Date.now ... Date.distantFuture
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
                                in: Date.now ... Date.distantFuture,
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
            .safeAreaInset(edge: .bottom) {
                HStack {
                    Spacer()

                    Button(action: {
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
                        presentation.wrappedValue.dismiss()
                    }) {
                        Text("Save")
                    }
                    .disabled(!modified || label.isEmpty)
                    .buttonStyle(BorderedProminentButtonStyle())
                    .padding()
                }
                .background(.bar)
            }
            .onAppear {
                listId = state.task!.listId

                label = state.task!.label

                details = state.task!.details

                reminderDate = state.task!.reminderDate?.toDate()
                dueDate = state.task!.dueDate?.toDate()
            }
            .onChange(of: state) { state in
                listId = state.task!.listId

                label = state.task!.label

                details = state.task!.details

                reminderDate = state.task!.reminderDate?.toDate()
                dueDate = state.task!.dueDate?.toDate()
            }
            .onChange(of: listId) { id in
                onUpdate(
                    state.task!.edit()
                        .label(value: label)
                        .details(value: details)
                        .reminderDate(value: DateKt.toInstantOrNull(reminderDate))
                        .dueDate(value: DateKt.toInstantOrNull(dueDate))
                        .lastModified(value: DateKt.toInstant(Date.now))
                        .build()
                )
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
        }
    }
}

struct TaskDetailsScreen_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            TaskDetailsScreen(
                state: TaskDetailsUiState(
                    isLoading: false,
                    firstLoad: false,
                    task: TaskKt.doNew(id: "1", listId: "1", label: "My task"),
                    taskLists: [
                        TaskListKt.doNew(id: "1", title: "My list"),
                    ]
                ),
                onUpdate: { _ in },
                onMove: { _, _ in }
            )
        }
    }
}
