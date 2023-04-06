//
//  ListsView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import MultiPlatformLibrary

struct ListsView: View {
    var taskLists: [TaskList]
    
    var body: some View {
        List {
            ForEach(taskLists, id: \.id) { taskList in
                Section {
                    ListView(taskList: taskList)
                }
            }
        }.listStyle(InsetGroupedListStyle())
    }
}

struct ListsView_Previews: PreviewProvider {
    static var previews: some View {
        ListsView(taskLists: [
            TaskList(
                id: "1",
                title: "Tasks",
                color: nil,
                icon: nil,
                description: "This is a list description",
                isPinned: false,
                showIndexNumbers: false,
                sortType: TaskList.SortType.ordinal,
                sortDirection: TaskList.SortDirection.ascending,
                dateCreated: ConvertersKt.toInstant(NSDate.now),
                lastModified: ConvertersKt.toInstant(NSDate.now)
            ),
            TaskList(
                id: "2",
                title: "Reminders",
                color: nil,
                icon: nil,
                description: "My Reminders :D",
                isPinned: false,
                showIndexNumbers: false,
                sortType: TaskList.SortType.ordinal,
                sortDirection: TaskList.SortDirection.ascending,
                dateCreated: ConvertersKt.toInstant(NSDate.now),
                lastModified: ConvertersKt.toInstant(NSDate.now)
            )
        ])
    }
}
