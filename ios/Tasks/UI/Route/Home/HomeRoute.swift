//
//  HomeRoute.swift
//  ios
//
//  Created by Luke Myers on 4/6/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct HomeRoute: View {
    @ObservedObject var viewModel = HomeViewModel()
    
    var navigateToWelcome: () -> Void
    
    @State var showCreateTaskSheet = false
    
    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })
                
        if !uiState.firstLoad {
            if !uiState.isAuthenticated {
                ProgressView().onAppear {
                    navigateToWelcome()
                }
            } else {
                HomeScreen(
                    state: uiState,
                    onCreateTaskPressed: {
                        showCreateTaskSheet = true
                    },
                    onProfilePressed: {
                        // TODO: implement profile
                    },
                    showAddAction: !uiState.taskLists.isEmpty
                ).sheet(isPresented: $showCreateTaskSheet) {
                    CreateTaskSheet(
                        taskLists: uiState.taskLists,
                        current: TaskKt.doNew(id: "", listId: uiState.taskLists.first!.id, label: "")
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
                    .presentationDetents([.medium])
                }
            }
        } else {
            ProgressView()
        }
    }
}
