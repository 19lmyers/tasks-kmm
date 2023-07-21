//
//  ModifyList.swift
//  ios
//
//  Created by Luke Myers on 4/8/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TasksShared

struct ModifyListSheet: View {
    var title: String

    var current: TaskList

    var onDismiss: () -> Void
    var onSave: (TaskList) -> Void

    @State var listTitle: String

    @State var listColor: TaskList.Color?
    @State var listIcon: TaskList.Icon?
    @State var description: String?

    @State var isPinned: Bool
    @State var showIndexNumbers: Bool

    init(title: String, current: TaskList, onDismiss: @escaping () -> Void, onSave: @escaping (TaskList) -> Void) {
        self.title = title

        self.current = current

        self.onDismiss = onDismiss
        self.onSave = onSave

        _listTitle = State(initialValue: current.title)

        _listColor = State(initialValue: current.color)
        _listIcon = State(initialValue: current.icon)
        _description = State(initialValue: current.description_)

        _isPinned = State(initialValue: current.isPinned)
        _showIndexNumbers = State(initialValue: current.showIndexNumbers)
    }

    var body: some View {
        NavigationStack {
            List {
                Section {
                    TextField("Title", text: $listTitle, prompt: Text("Enter list title (required)"))
                    TextField(
                        "Description",
                        text: Binding($description, replacingNilWith: ""),
                        prompt: Text("Add description"),
                        axis: .vertical
                    )
                }

                Section("Appearance") {
                    let columns = [GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible())]

                    DisclosureGroup(
                        content: {
                            ScrollView {
                                LazyVGrid(columns: columns, alignment: .center) {
                                    Button(action: {
                                        listIcon = nil
                                    }) {
                                        IconSwatch(
                                            icon: "checklist",
                                            outline: Color.primary.opacity(0.25),
                                            selection: Color.accentColor,
                                            selected: listIcon == nil
                                        ) {
                                            listIcon = nil
                                        }
                                    }
                                    .padding(.all, 4)

                                    ForEach(TaskListKt.icons()) { icon in
                                        IconSwatch(
                                            icon: icon.ui,
                                            outline: Color.primary.opacity(0.25),
                                            selection: Color.accentColor,
                                            selected: listIcon == icon
                                        ) {
                                            listIcon = icon
                                        }
                                        .padding(.all, 4)
                                    }
                                }
                            }
                        },
                        label: {
                            HStack {
                                Text("Icon")
                                Spacer()
                                Image(systemName: listIcon?.ui ?? "checklist")
                            }
                        }
                    )

                    ScrollView(.horizontal) {
                        HStack {
                            ColorSwatch(
                                color: Color.gray.opacity(0.25),
                                outline: Color.primary.opacity(0.25),
                                selection: Color.gray,
                                selected: listColor == nil
                            ) {
                                listColor = nil
                            }
                            .padding(.all, 4)

                            ForEach(TaskListKt.colors()) { color in
                                ColorSwatch(
                                    color: color.ui.opacity(0.25),
                                    outline: Color.primary.opacity(0.25),
                                    selection: color.ui,
                                    selected: listColor == color
                                ) {
                                    listColor = color
                                }
                            }
                        }
                        .padding(.all, 4)
                    }
                }

                Section("List options") {
                    HStack {
                        Image(systemName: "pin.fill")

                        Toggle(isOn: $isPinned) {
                            Text("Pin to dashboard")
                        }
                    }

                    HStack {
                        Image(systemName: "list.number")

                        Toggle(isOn: $showIndexNumbers) {
                            Text("Show list numbers")
                        }
                    }
                }
            }
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        onDismiss()
                    }
                }

                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        onSave(
                            current.edit()
                                .title(value: listTitle)
                                .color(value: listColor)
                                .icon(value: listIcon)
                                .description(value: description)
                                .isPinned(value: isPinned)
                                .showIndexNumbers(value: showIndexNumbers)
                                .build()
                        )
                    }
                    .disabled(listTitle.isEmpty)
                }
            }
            .tint(listColor?.ui ?? Color.accentColor)
            .navigationTitle(title)
        }
    }
}

struct IconSwatch: View {
    var icon: String
    var outline: Color
    var selection: Color

    var selected: Bool

    var onSelection: () -> Void

    var body: some View {
        Button(action: onSelection) {
            ZStack {
                let outlineColor = selected ? selection : outline

                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .stroke(outlineColor, lineWidth: 2)
                    .frame(width: 48, height: 48)

                Image(systemName: icon).tint(outlineColor)
            }
        }
    }
}

struct ColorSwatch: View {
    var color: Color
    var outline: Color
    var selection: Color

    var selected: Bool

    var onSelection: () -> Void

    var body: some View {
        Button(action: onSelection) {
            ZStack {
                let outlineColor = selected ? selection : outline

                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .fill(color)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12, style: .continuous)
                            .stroke(outlineColor, lineWidth: 2)
                    )
                    .frame(width: 48, height: 48)

                if selected {
                    Image(systemName: "checkmark").tint(selection)
                }
            }
        }
    }
}

struct ModifyList_Previews: PreviewProvider {
    static var previews: some View {
        ModifyListSheet(
            title: "Edit list",
            current: TaskListKt.doNew(id: "", title: ""),
            onDismiss: {},
            onSave: { _ in }
        )
    }
}
