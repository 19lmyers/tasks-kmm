//
//  Board.swift
//  Tasks
//
//  Created by Luke Myers on 4/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct BoardSectionsView: View {
    var sections: [BoardSection]
    var pinnedLists: [PinnedList]
    var allLists: [TaskList]

    var onListSelected: (String) -> Void
    var onTaskSelected: (String) -> Void

    var onCreateListPressed: () -> Void
    var onUpdateTask: (Task) -> Void

    var body: some View {
        ForEach(sections, id: \.type.name) { section in
            BoardSectionView(section: section, allLists: allLists, onListSelected: onListSelected, onTaskSelected: onTaskSelected, onUpdateTask: onUpdateTask)
        }

        ForEach(pinnedLists, id: \.taskList.id) { pinnedList in
            PinnedListView(pinnedList: pinnedList, onListSelected: onListSelected, onTaskSelected: onTaskSelected, onUpdateTask: onUpdateTask)
        }

        ListsView(taskLists: allLists, onListSelected: onListSelected, onCreateListPressed: onCreateListPressed)
    }
}

struct BoardSectionView: View {
    var section: BoardSection
    var allLists: [TaskList]

    var onListSelected: (String) -> Void
    var onTaskSelected: (String) -> Void

    var onUpdateTask: (Task) -> Void

    var body: some View {
        Section(header: Text(section.type.title)) {
            ForEach(section.tasks) { task in
                let parentList = allLists.first(where: { each in each.id == task.listId })

                TaskView(task: task, parentList: parentList, onListSelected: onListSelected, onTaskSelected: onTaskSelected, onUpdate: onUpdateTask)
                    .tint(parentList?.color?.ui ?? Color.accentColor)
                    .id("\(section.type.name)/\(task.id)")
            }
        }
    }
}

struct PinnedListView: View {
    var pinnedList: PinnedList

    var onListSelected: (String) -> Void
    var onTaskSelected: (String) -> Void

    var onUpdateTask: (Task) -> Void

    var body: some View {
        Section(header: Text(pinnedList.taskList.title)) {
            ForEach(Array(pinnedList.topTasks.enumerated()), id: \.element.id) { index, task in
                TaskView(task: task, onListSelected: onListSelected, onTaskSelected: onTaskSelected, onUpdate: onUpdateTask, showIndexNumbers: pinnedList.taskList.showIndexNumbers, indexNumber: index + 1)
                    .id("\(pinnedList.taskList.id)/\(task.id)")
            }

            let count = pinnedList.totalTaskCount - Int32(pinnedList.topTasks.count)

            if count > 0 {
                ViewMoreView(pinnedList: pinnedList, count: Int(count))
                    .contentShape(Rectangle())
                    .onTapGesture {
                        onListSelected(pinnedList.taskList.id)
                    }
            }
        }
        .tint(pinnedList.taskList.color?.ui ?? Color.accentColor)
    }
}

struct ViewMoreView: View {
    var pinnedList: PinnedList
    var count: Int

    var body: some View {
        Text("View \(count) more...")
            .foregroundStyle(.tint)
    }
}
