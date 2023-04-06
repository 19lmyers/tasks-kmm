//
//  ListView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import mokoMvvmFlowSwiftUI
import MultiPlatformLibrary
import SwiftUI

struct ListView: View {
    var taskList: TaskList

    var body: some View {
        HStack {
            Image(systemName: "checklist.checked")
            VStack(alignment: .leading) {
                Text(taskList.title)
                    .font(.title)
                    .multilineTextAlignment(.leading)

                if taskList.description_ != nil {
                    Spacer()
                        .frame(height: 8)

                    Text(taskList.description_!)
                        .font(.footnote)
                        .multilineTextAlignment(.leading)
                }
            }
            .padding(.horizontal, 16)
        }
        .padding()
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
                dateCreated: ConvertersKt.toInstant(NSDate.now),
                lastModified: ConvertersKt.toInstant(NSDate.now)
            )
        )
    }
}
