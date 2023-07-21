//
//  ListView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TasksShared

struct ListsView: View {
    var taskLists: [TaskList]

    var onListSelected: (String) -> Void

    var onCreateListPressed: () -> Void

    var body: some View {
        Section("Lists") {
            ForEach(taskLists, id: \.id) { taskList in
                ListView(taskList: taskList, onListSelected: onListSelected)
                    .tint(taskList.color?.ui ?? Color.accentColor)
            }

            CreateListView(onCreateListPressed: onCreateListPressed)
        }
    }
}

struct ListView: View {
    var taskList: TaskList

    var onListSelected: (String) -> Void

    var body: some View {
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
        .frame(maxWidth: .infinity, alignment: .leading)
        .contentShape(Rectangle())
        .onTapGesture {
            onListSelected(taskList.id)
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
            ),
            onListSelected: { _ in }
        )
        CreateListView {}
    }
}
