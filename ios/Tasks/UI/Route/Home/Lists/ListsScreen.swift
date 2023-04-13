//
//  ListsScreen.swift
//  ios
//
//  Created by Luke Myers on 4/7/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct ListsScreen: View {
    var state: ListsUiState

    var onRefresh: @Sendable () async -> Void

    var onCreateListPressed: () -> Void

    var body: some View {
        List {
            ListsView(taskLists: state.taskLists, onCreateListPressed: onCreateListPressed)
        }.refreshable(action: onRefresh)
    }
}

struct ListsScreen_Previews: PreviewProvider {
    static var previews: some View {
        ListsScreen(
            state: ListsUiState(
                isLoading: false,
                firstLoad: false,
                taskLists: [
                    TaskList(
                        id: "1",
                        title: "Tasks",
                        color: nil,
                        icon: nil,
                        description: "This is a list description",
                        isPinned: false,
                        showIndexNumbers: false,
                        sortType: TaskList.SortType.ordinal,
                        sortDirection: TaskList.SortDirection.ascending,
                        dateCreated: DateKt.toInstant(Date.now),
                        lastModified: DateKt.toInstant(Date.now)
                    )
                ]
            ),
            onRefresh: {},
            onCreateListPressed: {}
        )
    }
}
