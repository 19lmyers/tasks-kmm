//
//  ContentView.swift
//  Tasks
//
//  Created by Luke Myers on 7/29/23.
//  Copyright © 2023 chara.dev. All rights reserved.
//

import FirebaseCrashlytics
import SwiftUI
import TasksShared
import UIKit

private var DEEP_LINK_HOST = "tasks.chara.dev"

private var PATH_RESET_PASSWORD = "/reset"
private var QUERY_PASSWORD_RESET_TOKEN = "token"

private var PATH_VIEW_LIST = "/list"
private var QUERY_LIST_ID = "id"

private var PATH_VIEW_TASK = "/task"
private var QUERY_TASK_ID = "id"

struct ComposeView: UIViewControllerRepresentable {
    var root: RootComponent

    func makeUIViewController(context _: Context) -> UIViewController {
        EntryPointKt.mainViewController(rootComponent: root)
    }

    func updateUIViewController(_: UIViewController, context _: Context) {
        // stub
    }
}

struct ContentView: View {
    @ObservedObject var rootHolder: RootHolder

    var body: some View {
        ComposeView(root: rootHolder.root)
            .ignoresSafeArea(.all)
    }
}
