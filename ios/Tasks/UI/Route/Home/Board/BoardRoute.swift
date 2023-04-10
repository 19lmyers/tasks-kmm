//
//  BoardView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Combine
import mokoMvvmFlowSwiftUI
import MultiPlatformLibrary
import SwiftUI

struct BoardRoute: View {
    @ObservedObject var viewModel = BoardViewModel()
    
    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })
        BoardScreen(
            state: uiState,
            onRefresh: {
                DispatchQueue.main.sync {
                    viewModel.refreshCache()
                }
            },
            onUpdateTask: { task in
                viewModel.updateTask(task: task)
            }
        ).onAppear {
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
