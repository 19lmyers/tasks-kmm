//
//  TaskView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TasksShared

struct TaskView: View {
    var task: Task
    var parentList: TaskList?

    var onListSelected: (String) -> Void
    var onTaskSelected: (String) -> Void

    var onUpdate: (Task) -> Void

    var showIndexNumbers: Bool = false
    var indexNumber: Int = 0

    var body: some View {
        ZStack(alignment: .taskAlignmentGuide) {
            VStack(alignment: .leading) {
                HStack {
                    if showIndexNumbers {
                        Text("\(indexNumber)")
                            .font(.subheadline)
                    }

                    CheckboxView(isChecked: task.isCompleted) { isChecked in
                        onUpdate(task.edit()
                            .isCompleted(value: isChecked)
                            .lastModified(value: DateKt.toInstant(Date.now))
                            .build())
                    }

                    VStack(alignment: .leading) {
                        Text(task.label)
                            .lineLimit(5)
                            .multilineTextAlignment(.leading)
                            .truncationMode(.tail)

                        if task.details != nil {
                            Text(task.details!)
                                .lineLimit(2)
                                .font(.caption)
                                .multilineTextAlignment(.leading)
                                .truncationMode(.tail)
                        }
                    }
                    .alignmentGuide(.taskVerticalAlignment) { context in
                        context[.taskVerticalAlignment]
                    }
                }
                TaskChipsView(
                    task: task,
                    parentList: parentList,
                    onListSelected: onListSelected
                )
            }

            HStack {
                Spacer()

                StarView(isStarred: task.isStarred) { isStarred in
                    onUpdate(task.edit()
                        .isStarred(value: isStarred)
                        .lastModified(value: DateKt.toInstant(Date.now))
                        .build())
                }
            }
            .alignmentGuide(.taskVerticalAlignment) { context in
                context[.taskVerticalAlignment]
            }
        }
        .contentShape(Rectangle())
        .onTapGesture {
            onTaskSelected(task.id)
        }
    }
}

struct CreateTaskView: View {
    var onCreateTaskPressed: () -> Void

    var body: some View {
        Button(action: onCreateTaskPressed) {
            HStack {
                Image(systemName: "plus")
                Text("New task")
                    .multilineTextAlignment(.leading)
            }
        }
    }
}

struct TaskChipsView: View {
    var task: Task
    var parentList: TaskList?

    var onListSelected: (String) -> Void

    var body: some View {
        if parentList != nil || task.reminderDate != nil || task.dueDate != nil {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack {
                    if parentList != nil {
                        ListChipView(list: parentList!, onListSelected: onListSelected)
                    }
                    if task.reminderDate != nil {
                        ReminderChipView(reminderDate: task.reminderDate!.toDate())
                    }
                    if task.dueDate != nil {
                        DueDateChipView(dueDate: task.dueDate!.toDate())
                    }
                }
            }
        }
    }
}

extension VerticalAlignment {
    private enum TaskVerticalAlignment: AlignmentID {
        static func defaultValue(in context: ViewDimensions) -> CGFloat {
            context[VerticalAlignment.center]
        }
    }

    static let taskVerticalAlignment = VerticalAlignment(TaskVerticalAlignment.self)
}

extension HorizontalAlignment {
    private enum TaskHorizontalAlignment: AlignmentID {
        static func defaultValue(in context: ViewDimensions) -> CGFloat {
            context[HorizontalAlignment.leading]
        }
    }

    static let taskHorizontalAlignment = HorizontalAlignment(TaskHorizontalAlignment.self)
}

extension Alignment {
    static let taskAlignmentGuide = Alignment(horizontal: .taskHorizontalAlignment, vertical: .taskVerticalAlignment)
}

struct TaskView_Previews: PreviewProvider {
    static var previews: some View {
        TaskView(
            task: Task(
                id: "1",
                listId: "1",
                label: "Fix SwiftUI bindings",
                isCompleted: false,
                isStarred: false,
                details: "Sample description",
                reminderDate: nil,
                dueDate: nil,
                dateCreated: DateKt.toInstant(Date.now),
                lastModified: DateKt.toInstant(Date.now),
                ordinal: 1
            ),
            onListSelected: { _ in },
            onTaskSelected: { _ in },
            onUpdate: { _ in }
        )
        CreateTaskView {}
    }
}
