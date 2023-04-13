//
//  ListsRoute.swift
//  ios
//
//  Created by Luke Myers on 4/7/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct ListsRoute: View {
    @ObservedObject var viewModel = ListsViewModel()

    @State var showCreateListSheet = false

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        ListsScreen(
            state: uiState,
            onRefresh: {
                DispatchQueue.main.sync {
                    viewModel.refreshCache()
                }
            },
            onCreateListPressed: { showCreateListSheet = true }
        ).sheet(isPresented: $showCreateListSheet) {
            ModifyListSheet(
                title: "New list",
                current: TaskListKt.doNew(id: "", title: ""),
                onDismiss: {
                    showCreateListSheet = false
                },
                onSave: { taskList in
                    viewModel.createList(taskList: taskList)
                    showCreateListSheet = false
                }
            ).presentationDetents([.medium, .large])
        }.onAppear {
            viewModel.messages.subscribe(
                onCollect: { message in
                    if let unwrapped = message {
                        print(unwrapped.description())
                    }
                }
            )
        }
    }
}
