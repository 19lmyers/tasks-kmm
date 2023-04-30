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
    
    var listId: String
    @StateObject var viewModel = ListDetailsViewModel()

    var onModifyList: (TaskList) -> Void
    var onCreateTask: (Task) -> Void

    enum AlertType {
        case deleteList, clearTasks
    }

    @State var showAlert: Bool = false
    @State var alertType: AlertType = .deleteList

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0!.isEqual($1) }, mapper: { $0 })

        if !uiState.isLoading && uiState.selectedList == nil {
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
                        onCreateTask(TaskKt.doNew(id: "", listId: uiState.selectedList!.id, label: ""))
                    },
                    onUpdateList: { taskList in
                        viewModel.updateList(listId: taskList.id, taskList: taskList)
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
            ).onAppear {
                viewModel.observeList(listId: listId)
            }.onChange(of: listId) { listId in
                viewModel.observeList(listId: listId)
            }
                
                    .tint(uiState.selectedList?.color?.ui ?? Color.accentColor)
                    .navigationTitle(uiState.selectedList?.title ?? "")
                    .toolbar {
                        ToolbarItem(placement: .primaryAction) {
                            Button(action: {
                                onModifyList(uiState.selectedList!)
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
