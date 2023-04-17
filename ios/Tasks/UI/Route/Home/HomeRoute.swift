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

    @State var path = NavigationPath()
    var settings: String = "Settings"

    @State var showProfileSheet = false

    @State var showCreateListSheet = false
    @State var showCreateTaskSheet = false

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        if !uiState.firstLoad {
            if !uiState.isAuthenticated {
                ProgressView().onAppear {
                    navigateToWelcome()
                }
            } else {
                NavigationStack(path: $path) {
                    HomeScreen(
                            state: uiState,
                            onCreateListPressed: {
                                showCreateListSheet = true
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
                            .navigationDestination(for: TaskList.self) { taskList in
                                ListDetailsRoute(listId: taskList.id)
                                        .navigationBarTitle(Text(taskList.title))
                            }
                            .navigationDestination(for: Task.self) { task in
                                TaskDetailsRoute(taskId: task.id)
                                        .navigationBarTitle(Text("Edit item"))
                            }
                            .navigationDestination(for: String.self) { pathSegment in
                                if pathSegment == settings {
                                    SettingsRoute()
                                            .navigationTitle("Settings")
                                }
                            }
                            .toolbar {
                                if !uiState.allLists.isEmpty {
                                    ToolbarItem(placement: .navigationBarTrailing) {
                                        Button(action: {
                                            showCreateTaskSheet = true
                                        }) {
                                            Image(systemName: "plus")
                                        }
                                    }
                                }
                                ToolbarItem(placement: .navigationBarTrailing) {
                                    Menu(content: {
                                        Button(action: {
                                            showProfileSheet = true
                                        }) {
                                            Text("Edit profile")
                                            Image(systemName: "person")
                                        }
                                        Button(action: {
                                            path.append(settings)
                                        }) {
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
                                        ProfileImageView(email: uiState.profile!.email, profilePhotoUrl: uiState.profile!.profilePhotoUri)
                                                .frame(width: 24, height: 24)
                                    }
                                }
                            }
                            .navigationTitle("Tasks")
                }
                        .sheet(isPresented: $showProfileSheet) {
                            UserProfileSheet(
                                    profile: uiState.profile!,
                                    onChangePhotoPressed: {
                                        // TODO
                                    },
                                    onChangeEmailPressed: {
                                        // TODO
                                    },
                                    onChangePasswordPressed: {
                                        // TODO
                                    },
                                    onUpdateUserProfile: { profile in
                                        viewModel.updateUserProfile(profile: profile)
                                    }
                            )
                                    .presentationDetents([.medium, .large])
                        }
                        .sheet(isPresented: $showCreateListSheet) {
                            ModifyListSheet(
                                    title: "Create list",
                                    current: TaskListKt.doNew(id: "", title: ""),
                                    onDismiss: {
                                        showCreateListSheet = false
                                    },
                                    onSave: { list in
                                        viewModel.createList(taskList: list)
                                        showCreateListSheet = false
                                    }
                            )
                        }
                        .sheet(isPresented: $showCreateTaskSheet) {
                            CreateTaskSheet(
                                    taskLists: uiState.allLists,
                                    current: TaskKt.doNew(id: "", listId: uiState.allLists.first!.id, label: "")
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
            }
        } else {
            ProgressView()
        }
    }
}
