//
//  ListView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct ListsView: View {
    var taskLists: [TaskList]

    var onCreateListPressed: () -> Void

    var body: some View {
        Section("Lists") {
            ForEach(taskLists, id: \.id) { taskList in
                ListView(taskList: taskList)
                    .tint(taskList.color?.ui ?? Color.accentColor)
            }

            CreateListView(onCreateListPressed: onCreateListPressed)
        }
    }
}

struct ListView: View {
    var taskList: TaskList

    var body: some View {
        NavigationLink(value: DetailNavTarget.listDetails(taskList.id)) {
            HStack {
                Image(systemName: taskList.icon?.ui ?? "checklist")
                    .frame(width: 18, height: 18)
                    .foregroundStyle(.tint)

                VStack(alignment: .leading) {
                    Text(taskList.title)
                        .font(.body)
                        .multilineTextAlignment(.leading)

                    if taskList.description_ != nil {
                        Text(taskList.description_!)
                            .font(.caption)
                            .multilineTextAlignment(.leading)
                    }
                }
            }
            .listRowInsets(
                EdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 0)
            )
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
            .foregroundStyle(.tint)
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
