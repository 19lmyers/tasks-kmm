//
//  HomeView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct HomeScreen: View {
    var state: HomeUiState

    var onCreateTaskPressed: () -> Void

    var onProfilePressed: () -> Void

    var showAddAction: Bool

    var body: some View {
        TabView {
            HomeNavigationStack(
                content: AnyView(BoardRoute()),
                onCreateTaskPressed: onCreateTaskPressed,
                onProfilePressed: onProfilePressed,
                showAddAction: showAddAction
            )
            .tabItem {
                Image(systemName: "list.dash.header.rectangle")
                Text("Board")
            }

            HomeNavigationStack(
                content: AnyView(ListsRoute()),
                onCreateTaskPressed: onCreateTaskPressed,
                onProfilePressed: onProfilePressed,
                showAddAction: showAddAction
            )
            .tabItem {
                Image(systemName: "checklist.checked")
                Text("Lists")
            }
        }
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
