//
//  ChipView.swift
//  Tasks
//
//  Created by Luke Myers on 4/10/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TasksShared

struct ChipView<Content>: View where Content: View {
    var selectable: Bool = false
    @State var selected: Bool = false

    var onClick: (Bool) -> Void = { _ in
    }

    @ViewBuilder var content: () -> Content

    var body: some View {
        Button(action: {
            if selectable {
                selected.toggle()
            }
            onClick(selected)
        }) {
            if selected {
                HStack {
                    content()
                        .font(.footnote)
                }
                .padding(.all, 8)
                .background(
                    RoundedRectangle(cornerRadius: 12).fill(.tint.opacity(0.25))
                )
                .lineLimit(1)
                .foregroundColor(.primary)
            } else {
                HStack {
                    content()
                        .font(.footnote)
                }
                .padding(.all, 8)
                .background(
                    RoundedRectangle(cornerRadius: 12)
                        .strokeBorder(.primary, lineWidth: 1)
                )
                .lineLimit(1)
                .foregroundColor(.primary)
            }
        }
    }
}

struct ListChipView: View {
    var list: TaskList

    var onListSelected: (String) -> Void

    var body: some View {
        ChipView(selectable: false, onClick: { _ in
            onListSelected(list.id)
        }) {
            HStack {
                Image(systemName: list.icon?.ui ?? "checklist")
                Text(list.title)
            }
        }
    }
}

struct ReminderChipView: View {
    var reminderDate: Date?

    var selectable: Bool = false
    var onClick: () -> Void = {}

    var formatter = FriendlyDateFormat()

    var body: some View {
        if let date = reminderDate {
            ChipView(
                selectable: false,
                selected: selectable,
                onClick: { _ in
                    onClick()
                }
            ) {
                if date < Date.now {
                    Group {
                        Image(systemName: "bell")
                        Text(formatter.formatDateTime(date: date))
                        if selectable {
                            Image(systemName: "xmark")
                        }
                    }
                    .foregroundColor(.red)
                } else {
                    Image(systemName: "bell")
                    Text(formatter.formatDateTime(date: date))
                    if selectable {
                        Image(systemName: "xmark")
                    }
                }
            }
        } else {
            ChipView(
                selectable: false,
                selected: false,
                onClick: { _ in
                    onClick()
                }
            ) {
                Image(systemName: "bell")
                Text("Remind me")
            }
        }
    }
}

struct DueDateChipView: View {
    var dueDate: Date?

    var selectable: Bool = false
    var onClick: () -> Void = {}

    var formatter = FriendlyDateFormat()

    var body: some View {
        if let date = dueDate {
            ChipView(
                selectable: false,
                selected: selectable,
                onClick: { _ in
                    onClick()
                }
            ) {
                if date < Date.now {
                    Group {
                        Image(systemName: "calendar")
                        Text(formatter.formatDate(date: date))
                        if selectable {
                            Image(systemName: "xmark")
                        }
                    }
                    .foregroundColor(.red)
                } else {
                    Image(systemName: "calendar")
                    Text(formatter.formatDate(date: date))
                    if selectable {
                        Image(systemName: "xmark")
                    }
                }
            }
        } else {
            ChipView(
                selectable: false,
                selected: false,
                onClick: { _ in
                    onClick()
                }
            ) {
                Image(systemName: "calendar")
                Text("Set due date")
            }
        }
    }
}

struct Chips_Previews: PreviewProvider {
    static var previews: some View {
        ChipView(selectable: true, selected: true) {
            Text("Hello, world!")
        }
        ListChipView(
            list: TaskListKt.doNew(id: "", title: "My long list name"),
            onListSelected: { _ in }
        )
    }
}
