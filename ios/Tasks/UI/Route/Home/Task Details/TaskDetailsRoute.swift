//
//  TaskDetailsRoute.swift
//  Tasks
//
//  Created by Luke Myers on 4/14/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct TaskDetailsRoute: View {
    @Environment(\.presentationMode) var presentation

    var taskId: String
    @StateObject var viewModel = TaskDetailsViewModel()

    var onClose: () -> Void

    enum AlertType {
        case deleteTask, confirmExit
    }

    @State var showAlert: Bool = false
    @State var alertType: AlertType = .deleteTask

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        if !uiState.isLoading, uiState.task == nil {
            ProgressView().onAppear {
                onClose()
            }
        } else {
            let parentList = uiState.taskLists.first(where: { list in list.id == uiState.task!.listId })

            TaskDetailsScreen(state: uiState) { modified in
                if modified {
                    alertType = .confirmExit
                    showAlert = true
                } else {
                    onClose()
                }
            } onUpdate: { task in
                viewModel.updateTask(listId: task.listId, taskId: task.id, task: task)
            } onMove: { task, newListId in
                viewModel.moveTask(oldListId: task.listId, newListId: newListId, taskId: task.id, lastModified: DateKt.toInstant(Date.now))
            }
            .onAppear {
                viewModel.observeTask(taskId: taskId)
            }.onChange(of: taskId) { taskId in
                viewModel.observeTask(taskId: taskId)
            }
            .tint(parentList?.color?.ui ?? Color.accentColor)
            .toolbar {
                ToolbarItem(placement: .secondaryAction) {
                    Button(role: .destructive, action: {
                        alertType = .deleteTask
                        showAlert = true
                    }) {
                        Image(systemName: "trash")
                        Text("Delete task")
                    }
                }
            }
            .alert(isPresented: $showAlert) {
                if alertType == .deleteTask {
                    return Alert(
                        title: Text("Delete task?"),
                        message: Text("This task will be permanently deleted"),
                        primaryButton: .destructive(Text("Delete")) {
                            viewModel.deleteTask(listId: uiState.task!.listId, taskId: uiState.task!.id)
                            showAlert = false
                        },
                        secondaryButton: .cancel {
                            showAlert = false
                        }
                    )
                } else {
                    return Alert(
                        title: Text("Close without saving?"),
                        message: Text("Your changes to this task will not be saved"),
                        primaryButton: .destructive(Text("Close")) {
                            presentation.wrappedValue.dismiss()
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
