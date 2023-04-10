//
//  ChipView.swift
//  Tasks
//
//  Created by Luke Myers on 4/10/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

struct ChipView<Content>: View where Content: View {
    var selectable: Bool = false
    @State var selected: Bool = false
        
    var onClick: (Bool) -> Void = { _ in }
    
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
                }
                .padding(.all, 10)
                .background(
                    RoundedRectangle(cornerRadius: 12).fill(.tint.opacity(0.25))
                )
                .lineLimit(1)
                .foregroundColor(.primary)
            } else {
                HStack {
                    content()
                }
                .padding(.all, 10)
                .background(
                    RoundedRectangle(cornerRadius: 12)
                        .strokeBorder(.primary)
                        .background(RoundedRectangle(cornerRadius: 12).fill(.background))
                )
                .lineLimit(1)
                .foregroundColor(.primary)
            }
        }
    }
}

struct ListChipView: View {
    var list: TaskList

    var onClick: (TaskList) -> Void

    var body: some View {
        ChipView(selectable: false, onClick: { _ in
            onClick(list)
        }) {
            HStack {
                Image(systemName: "checklist")
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
                            Image(systemName: "bell.fill")
                            Text(formatter.formatDateTime(value: DateKt.toInstant(date)))
                            if (selectable) {
                                Image(systemName: "xmark")
                            }
                        }.foregroundColor(.red)
                    } else {
                        Image(systemName: "bell.fill")
                        Text(formatter.formatDateTime(value: DateKt.toInstant(date)))
                        if (selectable) {
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
                Image(systemName: "bell.slash.fill")
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
                            Text(formatter.formatDate(value: DateKt.toInstant(date)))
                            if (selectable) {
                                Image(systemName: "xmark")
                            }
                        }.foregroundColor(.red)
                    } else {
                        Image(systemName: "calendar")
                        Text(formatter.formatDate(value: DateKt.toInstant(date)))
                        if (selectable) {
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
                Image(systemName: "calendar.badge.plus")
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
            onClick: { _ in }
        )
    }
}
