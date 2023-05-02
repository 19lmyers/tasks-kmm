//
//  ProfileRoute.swift
//  Tasks
//
//  Created by Luke Myers on 4/18/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct ProfileRoute: View {
    @Environment(\.presentationMode) var presentation

    @StateObject var viewModel = ProfileViewModel()

    var body: some View {
        let uiState = viewModel.state(\.uiState, equals: { $0 == $1 }, mapper: { $0 })

        if uiState.isLoading {
            ProgressView()
        } else {
            ProfileScreen(
                state: uiState,
                onChangePhoto: { data in
                    let image = UIImage(data: data)
                    if let compressed = image?.jpegData(compressionQuality: 0.25) {
                        viewModel.updateUserProfilePhoto(photo: DataKt.toByteArray(compressed))
                    }
                },
                onUpdateUserProfile: { profile in
                    viewModel.updateUserProfile(profile: profile)
                }
            )
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(action: {
                        presentation.wrappedValue.dismiss()
                    }) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
