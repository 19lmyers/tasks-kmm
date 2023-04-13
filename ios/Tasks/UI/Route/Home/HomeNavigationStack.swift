//
//  HomeNavigation.swift
//  Tasks
//
//  Created by Luke Myers on 4/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct HomeNavigationStack: View {
    var content: AnyView
    
    var onCreateTaskPressed: () -> Void
    
    var onProfilePressed: () -> Void
        
    var showAddAction: Bool
    
    var body: some View {
        NavigationSplitView {
            content
            .navigationDestination(for: TaskList.self) { taskList in
                ListDetailsRoute(listId: taskList.id)
                    .navigationTitle(taskList.title)
                    .toolbar(.hidden, for: .tabBar)
            }
            .navigationDestination(for: Task.self) { task in
                Text(task.description())
                    .navigationTitle("Tasks")
                    .toolbar(.hidden, for: .tabBar)
            }
            .toolbar {
                if (showAddAction) {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: {
                            onCreateTaskPressed()
                        }) {
                            Image(systemName: "plus")
                        }
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        onProfilePressed()
                    }) {
                        Image(systemName: "person.crop.circle.fill")
                    }
                }
            }
            .navigationTitle("Tasks")
        } detail: {
            Text("Show something here")
        }
    }
}
