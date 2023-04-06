//
//  ContentView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct ContentView: View {
    var onCreateTaskPressed = {}

    @State var showCreateTaskDialog = false

    var body: some View {
        NavigationView {
            HomeView()
                .navigationTitle("Tasks")
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: {
                            showCreateTaskDialog = true
                        }) {
                            Image(systemName: "plus")
                                .foregroundColor(.accentColor)
                        }.sheet(isPresented: $showCreateTaskDialog) {
                            Text("TODO show create task sheet")
                                .presentationDetents([.medium])
                        }
                    }
                }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
