//
//  ListView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import mokoMvvmFlowSwiftUI
import MultiPlatformLibrary
import SwiftUI

struct ListView: View {
    var taskList: TaskList

    var body: some View {
        HStack {
            Image(systemName: "checklist")
                .foregroundStyle(.tint)

            VStack(alignment: .leading) {
                Text(taskList.title)
                    .multilineTextAlignment(.leading)

                if taskList.description_ != nil {
                    Text(taskList.description_!)
                        .font(.caption)
                        .multilineTextAlignment(.leading)
                }
            }
        }
    }
}

struct CreateListView: View {
    var onCreateListPressed: () -> Void

    var body: some View {
        Button(action: onCreateListPressed) {
            HStack {
                Image(systemName: "plus")
                Text("New list")
                    .multilineTextAlignment(.leading)
            }
        }
    }
}

struct ListView_Previews: PreviewProvider {
    static var previews: some View {
        ListView(
            taskList: TaskList(
                id: "1",
                title: "Tasks",
                color: nil,
                icon: nil,
                description: "This is a list description",
                isPinned: false,
                showIndexNumbers: false,
                sortType: TaskList.SortType.ordinal,
                sortDirection: TaskList.SortDirection.ascending,
                dateCreated: DateKt.toInstant(Date.now),
                lastModified: DateKt.toInstant(Date.now)
            )
        )
        CreateListView {}
    }
}
