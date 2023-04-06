//
//  HomeView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct HomeView: View {
    var body: some View {
        TabView {
            BoardRoute()
                .tabItem {
                    Image(systemName: "list.dash.header.rectangle")
                    Text("Board")
                }

            Text("Lists")
                .tabItem {
                    Image(systemName: "checklist.checked")
                    Text("Lists")
                }
        }
    }
}

struct HomeView_Previews: PreviewProvider {
    static var previews: some View {
        HomeView()
    }
}
