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

    var onUpdateUserProfile: (Profile) -> Void

    init(state: ProfileUiState, onChangePhoto: @escaping (Data) -> Void, onUpdateUserProfile: @escaping (Profile) -> Void) {
        self.state = state

        _displayName = State(initialValue: state.profile!.displayName)

        self.onChangePhoto = onChangePhoto
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
                        .frame(width: 18, height: 18)
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
                            .frame(width: 18, height: 18)
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
                                .frame(width: 18, height: 18)
                            Text("Remove profile photo")
                        }
                    }
                }
            }

            Section("Authentication") {
                NavigationLink(destination: {
                    Text("TODO")
                            .navigationTitle("Change email")
                }) {
                    HStack {
                        Image(systemName: "at")
                            .frame(width: 18, height: 18)
                        Text("Change email")
                    }.foregroundColor(.accentColor)
                }

                NavigationLink(destination: {
                    ChangePasswordRoute()
                            .navigationTitle("Change password")
                }) {
                    HStack {
                        Image(systemName: "ellipsis.rectangle")
                            .frame(width: 18, height: 18)
                        Text("Change password")
                    }.foregroundColor(.accentColor)
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
                onUpdateUserProfile: { _ in }
        )
    }
}
