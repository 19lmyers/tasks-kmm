//
//  BoardView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import MultiPlatformLibrary
import mokoMvvmFlowSwiftUI
import Combine

struct BoardRoute: View {
    @StateObject var viewModel = BoardViewModel()
        
    var body: some View {
        BoardView(state: viewModel.state(\.uiState, equals: {$0 == $1 }, mapper: { $0 }))
    }
}

extension BoardUiState: ObservableObject {}
