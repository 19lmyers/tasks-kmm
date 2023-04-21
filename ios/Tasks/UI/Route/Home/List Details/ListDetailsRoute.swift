//
//  ListDetailsRoute.swift
//  Tasks
//
//  Created by Luke Myers on 4/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct ListDetailsRoute: View {
    @Environment(\.presentationMode) var presentation

    @StateObject var viewModel: ListDetailsViewModel

    @State var showModifyListSheet = false
    @State var showCreateTaskSheet = false

    enum AlertType {
        case deleteList, clearTasks
    }

    @State var showAlert: Bool = false
    @State var alertType: AlertType = .deleteList

    init(listId: String) {
        _viewModel = StateObject(wrappedValue: ListDetailsViewModel(listId: listId))
    }

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        if uiState.firstLoad {
            ProgressView()
        } else {
            if uiState.selectedList == nil {
                ProgressView().onAppear {
                    presentation.wrappedValue.dismiss()
                }
            } else {
                ListDetailsScreen(
                        state: uiState,
                        onRefresh: {
                            DispatchQueue.main.sync {
                                viewModel.refreshCache()
                            }
                        },
                        onCreateTaskPressed: {
                            showCreateTaskSheet = true
                        },
                        onUpdateTask: { task in
                            viewModel.updateTask(listId: uiState.selectedList!.id, taskId: task.id, task: task)
                        },
                        onReorderTask: { id, from, to in
                            viewModel.reorderTask(
                                    listId: uiState.selectedList!.id,
                                    taskId: id,
                                    fromIndex: Int32(from),
                                    toIndex: Int32(to),
                                    lastModified: DateKt.toInstant(Date.now)
                            )
                        }
                )
                        .tint(uiState.selectedList!.color?.ui ?? Color.accentColor)
                        .navigationTitle(uiState.selectedList!.title)
                        .toolbar {
                            ToolbarItem(placement: .primaryAction) {
                                Button(action: {
                                    showModifyListSheet = true
                                }) {
                                    Image(systemName: "square.and.pencil")
                                }
                            }

                            ToolbarItem(placement: .secondaryAction) {
                                Button(action: {
                                    alertType = .clearTasks
                                    showAlert = true
                                }) {
                                    Image(systemName: "checkmark.circle")
                                    Text("Delete completed tasks")
                                }
                            }

                            ToolbarItem(placement: .secondaryAction) {
                                Button(role: .destructive, action: {
                                    alertType = .deleteList
                                    showAlert = true
                                }) {
                                    Image(systemName: "trash")
                                    Text("Delete list")
                                }
                            }

                            ToolbarItem(placement: .bottomBar) {
                                HStack {
                                    Menu {
                                        ForEach(TaskListKt.sortTypes(), id: \.self) { sortType in
                                            Button(action: {
                                                viewModel.updateList(
                                                        listId: uiState.selectedList!.id,
                                                        taskList: uiState.selectedList!.edit()
                                                                .sortType(value: sortType)
                                                                .build()
                                                )
                                            }) {
                                                Image(systemName: sortType.icon)
                                                Text(sortType.description())
                                            }
                                        }
                                    } label: {
                                        HStack {
                                            Image(systemName: uiState.selectedList!.sortType.icon)
                                            Text(uiState.selectedList!.sortType.description())
                                        }
                                    }

                                    if uiState.selectedList!.sortType != .ordinal {
                                        Button(action: {
                                            viewModel.updateList(
                                                    listId: uiState.selectedList!.id,
                                                    taskList: uiState.selectedList!.edit()
                                                            .sortDirection(value: uiState.selectedList!.sortDirection == .ascending ? .descending : .ascending)
                                                            .build()
                                            )
                                        }) {
                                            Image(systemName: uiState.selectedList!.sortDirection.icon)
                                            Text(uiState.selectedList!.sortDirection.description())
                                        }
                                    }
                                }
                            }
                        }
                        .sheet(isPresented: $showModifyListSheet) {
                            ModifyListSheet(
                                    title: "Edit list",
                                    current: uiState.selectedList!,
                                    onDismiss: {
                                        showModifyListSheet = false
                                    },
                                    onSave: { taskList in
                                        viewModel.updateList(listId: uiState.selectedList!.id, taskList: taskList)
                                        showModifyListSheet = false
                                    }
                            )
                                    .presentationDetents([.medium, .large])
                        }
                        .sheet(isPresented: $showCreateTaskSheet) {
                            CreateTaskSheet(
                                    taskLists: [uiState.selectedList!],
                                    current: TaskKt.doNew(id: "", listId: uiState.selectedList!.id, label: "")
                                            .edit()
                                            .lastModified(value: DateKt.toInstant(Date.now))
                                            .build(),
                                    onDismiss: {
                                        showCreateTaskSheet = false
                                    },
                                    onSave: { task in
                                        viewModel.createTask(listId: task.listId, task: task)
                                        showCreateTaskSheet = false
                                    }
                            )
                                    .presentationDetents([.medium, .large])
                        }
                        .alert(isPresented: $showAlert) {
                            if alertType == .deleteList {
                                return Alert(
                                        title: Text("Delete list?"),
                                        message: Text("All tasks in this list will be permanently deleted"),
                                        primaryButton: .destructive(Text("Delete")) {
                                            viewModel.deleteList(listId: uiState.selectedList!.id)
                                            showAlert = false
                                        },
                                        secondaryButton: .cancel {
                                            showAlert = false
                                        }
                                )
                            } else {
                                return Alert(
                                        title: Text("Delete all completed tasks?"),
                                        message: Text("Completed tasks will be permanently deleted from this list"),
                                        primaryButton: .destructive(Text("Delete")) {
                                            viewModel.clearCompletedTasks(listId: uiState.selectedList!.id)
                                            showAlert = false
                                        },
                                        secondaryButton: .cancel {
                                            showAlert = false
                                        }
                                )
                            }
                        }
            }
        }
    }
}
