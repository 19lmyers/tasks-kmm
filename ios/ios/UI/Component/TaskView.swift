//
//  TaskView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import MultiPlatformLibrary

struct TaskView: View {
    var task: Task
    
    var body: some View {
        VStack {
            HStack {
                CheckboxView(isChecked: task.isCompleted)
                VStack {
                    Text(task.label)
                        .lineLimit(5)
                        .truncationMode(Text.TruncationMode.tail)
                }
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
                reminderDate: NSDateKt.toInstant(NSDate.now),
                dueDate: NSDateKt.toInstant(NSDate.now),
                dateCreated: NSDateKt.toInstant(NSDate.now),
                lastModified: NSDateKt.toInstant(NSDate.now),
                ordinal: 1
            )
        )
    }
}
