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

    var onCreateListPressed: () -> Void
    var onUpdateTask: (Task) -> Void

    var onRefresh: @Sendable () async -> Void

    var body: some View {
        if state.firstLoad {
            ProgressView()
        } else {
            List {
                BoardSectionsView(
                    sections: state.boardSections,
                    pinnedLists: state.pinnedLists,
                    allLists: state.allLists,
                    onCreateListPressed: onCreateListPressed,
                    onUpdateTask: onUpdateTask
                )
            }
            .refreshable(action: onRefresh)
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
                    boardSections: [],
                    pinnedLists: [],
                    allLists: []
                ),
                onCreateListPressed: {},
                onUpdateTask: { _ in },
                onRefresh: {}
            )
        }
    }
}
