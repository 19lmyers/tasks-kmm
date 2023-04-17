//
//  UserProfileSheet.swift
//  Tasks
//
//  Created by Luke Myers on 4/14/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct UserProfileSheet: View {
    var profile: Profile

    var onChangePhotoPressed: () -> Void
    var onChangeEmailPressed: () -> Void
    var onChangePasswordPressed: () -> Void

    var onUpdateUserProfile: (Profile) -> Void

    var body: some View {
        List {
            HStack {
                ProfileImageView(email: profile.email, profilePhotoUrl: profile.profilePhotoUri)
                        .frame(width: 48, height: 48)

                VStack(alignment: .leading) {
                    Text(profile.displayName)
                            .font(.title3)

                    Text(profile.email)
                            .font(.caption)
                }
                        .padding([.leading])

                Spacer()

                Button(action: onChangePhotoPressed) {
                    Image(systemName: "plus.circle")
                    // TODO: state based
                }

            }

            Section {
                Button(action: { /* TODO: */ }) {
                    HStack {
                        Image(systemName: "person")
                        VStack(alignment: .leading) {
                            Text(profile.displayName)
                            Text("Change name")
                                    .font(.caption)
                        }
                                .padding([.leading])
                    }
                }

                Button(action: onChangeEmailPressed) {
                    HStack {
                        Image(systemName: "at")
                        VStack(alignment: .leading) {
                            Text(profile.email)
                            Text("Change email")
                                    .font(.caption)
                        }
                                .padding([.leading])
                    }
                }

                Button(action: onChangePasswordPressed) {
                    HStack {
                        Image(systemName: "ellipsis.rectangle")

                        VStack(alignment: .leading) {
                            Text("Password")
                            Text("Change password")
                                    .font(.caption)
                        }
                                .padding([.leading])
                    }
                }
            }
        }
    }
}

struct UserProfileSheet_Previews: PreviewProvider {
    static var previews: some View {
        UserProfileSheet(
                profile: Profile(
                        id: "1",
                        email: "user@email.com",
                        displayName: "John Smith",
                        profilePhotoUri: nil
                ),
                onChangePhotoPressed: {},
                onChangeEmailPressed: {},
                onChangePasswordPressed: {},
                onUpdateUserProfile: { _ in }
        )
    }
}
