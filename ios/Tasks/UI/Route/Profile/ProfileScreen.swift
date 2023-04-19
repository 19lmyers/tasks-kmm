//
//  UserProfileSheet.swift
//  Tasks
//
//  Created by Luke Myers on 4/14/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI
import PhotosUI

typealias UITask = SwiftUI.Task

struct ProfileScreen: View {

    var state: ProfileUiState

    @State var displayName: String
    @State var selectedPhoto: PhotosPickerItem?

    var onChangePhoto: (Data) -> Void
    
    var onChangeEmailPressed: () -> Void
    var onChangePasswordPressed: () -> Void

    var onUpdateUserProfile: (Profile) -> Void

    init(state: ProfileUiState, onChangePhoto: @escaping (Data) -> Void, onChangeEmailPressed: @escaping () -> Void, onChangePasswordPressed: @escaping () -> Void, onUpdateUserProfile: @escaping (Profile) -> Void) {
        self.state = state

        _displayName = State(initialValue: state.profile!.displayName)

        self.onChangePhoto = onChangePhoto
        self.onChangeEmailPressed = onChangeEmailPressed
        self.onChangePasswordPressed = onChangePasswordPressed
        self.onUpdateUserProfile = onUpdateUserProfile
    }

    var body: some View {
        List {
            Section("Preview") {
                HStack {
                    ProfileImageView(email: state.profile!.email, profilePhotoUri: state.profile!.profilePhotoUri)
                            .frame(width: 48, height: 48)

                    VStack(alignment: .leading) {
                        Text(displayName)
                                .font(.title3)

                        Text(state.profile!.email)
                                .font(.caption)
                    }
                            .padding([.leading])

                    Spacer()
                }
            }

            Section("Display name") {
                HStack {
                    Image(systemName: "person")
                    VStack(alignment: .leading) {
                        TextField("Display name", text: $displayName, prompt: Text("Enter display name (required)"))
                    }
                    if displayName != state.profile!.displayName {
                        Spacer()
                        Button(action: {
                            displayName = state.profile!.displayName
                        }) {
                            Image(systemName: "arrow.clockwise")
                        }
                    }
                }
            }

            Section("Profile Picture") {
                PhotosPicker(
                       selection: $selectedPhoto,
                       matching: .images,
                       photoLibrary: .shared()) {
                    HStack {
                        Image(systemName: "plus.circle")
                        if state.profile!.profilePhotoUri != nil {
                            Text("Change profile picture")
                        } else {
                            Text("Add profile picture")
                        }
                    }
                }

                if state.profile!.profilePhotoUri != nil {
                    Button(action: {
                        onUpdateUserProfile(state.profile!.edit()
                            .profilePhotoUri(value: nil)
                            .build())
                    }) {
                        HStack {
                            Image(systemName: "xmark.circle")
                            Text("Remove profile photo")
                        }
                    }
                }
            }

            Section("Authentication") {
                Button(action: onChangeEmailPressed) {
                    HStack {
                        Image(systemName: "at")
                        Text("Change email")
                    }
                }

                Button(action: onChangePasswordPressed) {
                    HStack {
                        Image(systemName: "ellipsis.rectangle")
                        Text("Change password")
                    }
                }
            }
        }
                .toolbar {
                    ToolbarItem(placement: .confirmationAction) {
                        Button(action: {
                            onUpdateUserProfile(state.profile!.edit()
                                    .displayName(value: displayName)
                                    .build())
                        }) {
                            Text("Save")
                        }
                                .disabled(displayName.isEmpty || displayName == state.profile!.displayName)
                    }
                }
                .onChange(of: selectedPhoto) { newPhoto in
                    UITask {
                        if let data = try? await newPhoto?.loadTransferable(type: Data.self) {
                            onChangePhoto(data)
                        }
                    }
                }
    }
}

struct ProfileScreen_Previews: PreviewProvider {
    static var previews: some View {
        ProfileScreen(
                state: ProfileUiState(
                        isLoading: false,
                        profile: Profile(
                                id: "1",
                                email: "user@email.com",
                                displayName: "John Smith",
                                profilePhotoUri: nil
                        )
                ),
                onChangePhoto: { _ in },
                onChangeEmailPressed: {},
                onChangePasswordPressed: {},
                onUpdateUserProfile: { _ in }
        )
    }
}
