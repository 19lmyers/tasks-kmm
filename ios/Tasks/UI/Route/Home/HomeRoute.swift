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
    @Environment(\.horizontalSizeClass) var horizontalSizeClass

    @ObservedObject var viewModel = HomeViewModel()

    var navigateToWelcome: () -> Void

    @State var listToModify: TaskList? = nil
    @State var taskToCreate: Task? = nil

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        if !uiState.isLoading && !uiState.isAuthenticated {
            ProgressView().onAppear {
                navigateToWelcome()
            }
        } else {
            NavigationSplitView {
                HomeScreen(
                        state: uiState,
                        onCreateListPressed: {
                            listToModify = TaskListKt.doNew(id: "", title: "")
                        },
                        onUpdateTask: { task in
                            viewModel.updateTask(task: task)
                        },
                        onRefresh: {
                            DispatchQueue.main.sync {
                                viewModel.refreshCache()
                            }
                        }
                )
                        .navigationDestination(for: DetailNavTarget.self) { navTarget in
                            switch (navTarget) {
                            case .listDetails (let listId):
                                ListDetailsRoute(
                                        listId: listId,
                                        onModifyList: { taskList in
                                            listToModify = taskList
                                        },
                                        onCreateTask: { task in
                                            taskToCreate = task
                                        }
                                )
                                .navigationDestination(for: DetailNavTarget.self) { navTarget in
                                    switch (navTarget) {
                                    case .taskDetails (let taskId):
                                        TaskDetailsRoute(taskId: taskId)
                                                .navigationTitle(Text("Edit task"))
                                                .navigationBarBackButtonHidden(true)
                                    default:
                                        EmptyView()
                                    }
                                }
                            case .taskDetails (let taskId):
                                TaskDetailsRoute(taskId: taskId)
                                        .navigationTitle(Text("Edit task"))
                                        .navigationBarBackButtonHidden(true)
                            case .profile:
                                ProfileRoute()
                                        .navigationTitle(Text("Edit profile"))
                                        .navigationBarBackButtonHidden(true)
                            case .settings:
                                SettingsRoute()
                                        .navigationTitle(Text("Settings"))
                            }
                        }
                        .navigationTitle(Text("Tasks"))
                        .toolbar {
                            if !uiState.allLists.isEmpty {
                                ToolbarItem(placement: .navigationBarTrailing) {
                                    Button(action: {
                                        taskToCreate = TaskKt.doNew(id: "", listId: uiState.allLists.first!.id, label: "")
                                    }) {
                                        Image(systemName: "plus")
                                    }
                                }
                            }
                            ToolbarItem(placement: .navigationBarTrailing) {
                                Menu(content: {
                                    NavigationLink(value: DetailNavTarget.profile) {
                                        Text("Edit profile")
                                        Image(systemName: "person")
                                    }

                                    NavigationLink(value: DetailNavTarget.settings) {
                                        Text("Settings")
                                        Image(systemName: "gearshape")
                                    }

                                    Button(action: {
                                        viewModel.logout()
                                    }) {
                                        Text("Sign out")
                                        Image(systemName: "rectangle.portrait.and.arrow.forward")
                                    }
                                }) {
                                    ProfileImageView(email: uiState.profile?.email, profilePhotoUri: uiState.profile?.profilePhotoUri)
                                            .frame(width: 24, height: 24)
                                }
                            }
                        }
            } detail: {
                NavigationStack {
                    Text("Select a task")
                            .navigationTitle(Text(""))
                }
            }
                    .sheet(item: $listToModify) { list in
                        ModifyListSheet(
                                title: list.id.isEmpty ? "New list" : "Edit list",
                                current: list,
                                onDismiss: {
                                    listToModify = nil
                                },
                                onSave: { list in
                                    if list.id.isEmpty {
                                        viewModel.createList(taskList: list)
                                    } else {
                                        viewModel.updateList(taskList: list)
                                    }
                                    listToModify = nil
                                }
                        )
                    }
                    .sheet(item: $taskToCreate) { task in
                        CreateTaskSheet(
                                taskLists: uiState.allLists,
                                current: task,
                                onDismiss: {
                                    taskToCreate = nil
                                },
                                onSave: { task in
                                    viewModel.createTask(listId: task.listId, task: task)
                                    taskToCreate = nil
                                }
                        )
                                .presentationDetents([.medium, .large])
                    }
        }
    }
}
