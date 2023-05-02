//
//  Profile.swift
//  Tasks
//
//  Created by Luke Myers on 4/14/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct ProfileImageView: View {
    var imageUrl: String

    init(email: String?, profilePhotoUri: String? = nil) {
        imageUrl = profilePhotoUri ?? GravatarKt.getGravatarUrl(email: email ?? "")
    }

    var body: some View {
        AsyncImage(url: URL(string: imageUrl)) { image in
            image.resizable()
        } placeholder: {
            Image(systemName: "person.crop.circle.fill").resizable()
        }
        .clipShape(Circle())
    }
}

struct Profile_Previews: PreviewProvider {
    static var previews: some View {
        ProfileImageView(email: "user@email.com")
    }
}
