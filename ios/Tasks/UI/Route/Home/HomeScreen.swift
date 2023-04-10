//
//  HomeView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import MultiPlatformLibrary

struct HomeScreen: View {
    var state: HomeUiState
    
    var onCreateTaskPressed: () -> Void
    
    var onProfilePressed: () -> Void
    
    var showAddAction: Bool
    
    var body: some View {
        TabView {
            BoardRoute()
                .tabItem {
                    Image(systemName: "list.dash.header.rectangle")
                    Text("Board")
                }
            
            ListsRoute()
                .tabItem {
                    Image(systemName: "checklist.checked")
                    Text("Lists")
                }
        }.toolbar {
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
    }
}

struct HomeScreen_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            HomeScreen(
                state: HomeUiState(
                    isLoading: false,
                    firstLoad: false,
                    isAuthenticated: true,
                    profile: nil,
                    startScreen: StartScreen.board,
                    taskLists: []
                ),
                onCreateTaskPressed: {},
                onProfilePressed: {},
                showAddAction: true
            )
        }
    }
}
