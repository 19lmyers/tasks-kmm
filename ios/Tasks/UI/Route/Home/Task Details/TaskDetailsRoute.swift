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

    @StateObject var viewModel: TaskDetailsViewModel
    
    @State var showAlert = false

    init(taskId: String) {
        _viewModel = StateObject(wrappedValue: TaskDetailsViewModel(taskId: taskId))
    }

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        if uiState.firstLoad {
            ProgressView()
        } else {
            if uiState.task == nil {
                ProgressView().onAppear {
                    presentation.wrappedValue.dismiss()
                }
            } else {
                let parentList = uiState.taskLists.first(where: { list in list.id == uiState.task!.listId })

                TaskDetailsScreen(state: uiState) { task in
                    viewModel.updateTask(listId: task.listId, taskId: task.id, task: task)
                } onMove: { task, newListId in
                    viewModel.moveTask(oldListId: task.listId, newListId: newListId, taskId: task.id, lastModified: DateKt.toInstant(Date.now))
                }
                .tint(parentList!.color?.ui ?? Color.accentColor)
                .toolbar {
                    ToolbarItem(placement: .primaryAction) {
                        StarView(isStarred: uiState.task!.isStarred) { isStarred in
                            viewModel.updateTask(
                                listId: uiState.task!.listId,
                                taskId: uiState.task!.id,
                                task: uiState.task!.edit()
                                    .isStarred(value: isStarred)
                                    .lastModified(value: DateKt.toInstant(Date.now))
                                    .build()
                            )
                        }
                    }
                    
                    ToolbarItem(placement: .secondaryAction) {
                        Button(role: .destructive, action: {
                            showAlert = true
                        }) {
                            Image(systemName: "trash")
                            Text("Delete item")
                        }
                    }
                }
                .alert(isPresented: $showAlert) {
                    Alert(
                        title: Text("Delete item?"),
                        message: Text("This item will be permanently deleted"),
                        primaryButton: .destructive(Text("Delete")) {
                            viewModel.deleteTask(listId: uiState.task!.listId, taskId: uiState.task!.id)
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
