//
//  CreateListView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct CreateListView: View {
    var body: some View {
        GroupBox {
            HStack {
                Image(systemName: "plus")
                VStack(alignment: .leading) {
                    Text("New list")
                        .font(.title)
                        .multilineTextAlignment(.leading)
                }
                .padding(.horizontal, 16)
            }
            .padding()
        }
    }
}

struct CreateListView_Previews: PreviewProvider {
    static var previews: some View {
        CreateListView()
    }
}
