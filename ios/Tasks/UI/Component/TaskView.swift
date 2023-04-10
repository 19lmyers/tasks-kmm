//
//  TaskView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct TaskView: View {
    var task: Task

    var onUpdate: (Task) -> Void

    var body: some View {
        HStack {
            CheckboxView(isChecked: task.isCompleted) { isChecked in
                onUpdate(task.edit()
                    .isCompleted(value: isChecked)
                    .lastModified(value: DateKt.toInstant(Date.now))
                    .build())
            }

            VStack {
                Text(task.label)
                    .lineLimit(5)
                    .truncationMode(Text.TruncationMode.tail)
            }

            Spacer()

            StarView(isStarred: task.isStarred) { isStarred in
                onUpdate(task.edit()
                    .isStarred(value: isStarred)
                    .lastModified(value: DateKt.toInstant(Date.now))
                    .build())
            }
        }
    }
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
                details: nil,
                reminderDate: DateKt.toInstant(Date.now),
                dueDate: DateKt.toInstant(Date.now),
                dateCreated: DateKt.toInstant(Date.now),
                lastModified: DateKt.toInstant(Date.now),
                ordinal: 1
            ),
            onUpdate: { _ in }
        )
    }
}
