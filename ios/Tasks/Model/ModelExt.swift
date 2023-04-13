//
//  TaskListExt.swift
//  ios
//
//  Created by Luke Myers on 4/8/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

extension TaskList.Color: Identifiable {
    var ui: Color {
        switch self {
        case .red: return Color.red
        case .orange: return Color.orange
        case .yellow: return Color.yellow
        case .green: return Color.green
        case .blue: return Color.blue
        case .purple: return Color.purple
        case .pink: return Color.pink
        default: return Color.accentColor
        }
    }
}

extension TaskList.SortType {
    var icon: String {
        switch self {
        case .ordinal: return "list.number"
        case .label: return "character"
        case .dateCreated: return "calendar"
        case .upcoming: return "clock.fill"
        case .starred: return "star.fill"
        default: return "list"
        }
    }
}

extension TaskList.SortDirection {
    var icon: String {
        switch self {
        case .ascending: return "arrow.up"
        case .descending: return "arrow.down"
        default: return "arrow.up.arrow.down"
        }
    }
}
