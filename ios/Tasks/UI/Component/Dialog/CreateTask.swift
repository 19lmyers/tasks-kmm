//
//  CreateTask.swift
//  Tasks
//
//  Created by Luke Myers on 4/10/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct CreateTaskSheet: View {
    var current: Task
    var taskLists: [TaskList]
    
    var onDismiss: () -> Void
    var onSave: (Task) -> Void
    
    @State var listId: String
    
    @State var label: String
    
    @State var reminderDate: Date?
    @State var dueDate: Date?
    
    @State var parentList: TaskList?
        
    init(taskLists: [TaskList], current: Task, onDismiss: @escaping () -> Void, onSave: @escaping (Task) -> Void) {
        self.current = current
        self.taskLists = taskLists
        
        self.onDismiss = onDismiss
        self.onSave = onSave
        
        _listId = State(initialValue: current.listId)
                
        _label = State(initialValue: current.label)
        
        _reminderDate = State(initialValue: current.reminderDate?.toDate())
        _dueDate = State(initialValue: current.dueDate?.toDate())
        
        _parentList = State(initialValue: taskLists.first(where: { list in list.id == current.listId }))
    }
    
    var body: some View {
        NavigationView {
            List {
                Section {
                    TextField("Label", text: $label, prompt: Text("Enter label (required)"))
                    
                    Picker(
                        selection: $listId,
                        content: {
                            ForEach(taskLists, id: \.id) { list in
                                Text(list.title)
                            }
                        },
                        label: {
                            Text("List")
                        }
                    )
                }
                
            }.toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        onDismiss()
                    }
                }
                
                ToolbarItem(placement: .confirmationAction) {
                    Button("Create") {
                        onSave(
                            current.edit()
                                .listId(value: listId)
                                .label(value: label)
                                .reminderDate(value: DateKt.toInstantOrNull(reminderDate))
                                .dueDate(value: DateKt.toInstantOrNull(dueDate))
                                .build()
                        )
                    }.disabled(label.isEmpty)
                }
            }
            .tint(parentList?.color?.ui ?? Color.accentColor)
            .navigationTitle("New item")
            .navigationBarTitleDisplayMode(.inline)
            .navigationViewStyle(.stack)
        }
        .onChange(of: listId) { id in
            parentList = taskLists.first(where: { list in list.id == id })
        }
    }
}

struct CreateTaskSheet_Previews: PreviewProvider {
    static var previews: some View {
        CreateTaskSheet(
            taskLists: [
                TaskListKt.doNew(id: "1", title: "Tasks")
                    .edit()
                    .color(value: TaskList.Color.green)
                    .build(),
                TaskListKt.doNew(id: "2", title: "Reminders")
                    .edit()
                    .color(value: TaskList.Color.orange)
                    .build()
            ],
            current: TaskKt.doNew(id: "10", listId: "1", label: ""),
            onDismiss: {},
            onSave: { _ in }
        )
    }
}
