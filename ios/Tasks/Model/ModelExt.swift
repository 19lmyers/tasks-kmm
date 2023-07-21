//
//  TaskListExt.swift
//  ios
//
//  Created by Luke Myers on 4/8/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TasksShared

extension Theme {
    var colorScheme: ColorScheme? {
        switch self {
        case .light: return .light
        case .dark: return .dark
        case .systemDefault: return .none
        default: return .none
        }
    }
}

extension BoardSection.Type_ {
    var icon: String {
        switch self {
        case .overdue: return "clock"
        case .starred: return "star.fill"
        case .upcoming: return "calendar"
        default: return "checklist"
        }
    }
}

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

extension TaskList.Icon: Identifiable {
    var ui: String {
        switch self {
        case .backpack: return "backpack.fill"
        case .book: return "book.closed.fill"
        case .bookmark: return "bookmark.fill"
        case .brush: return "paintbrush.pointed.fill"
        case .cake: return "birthday.cake.fill"
        case .call: return "phone.fill"
        case .car: return "car.fill"
        case .celebration: return "party.popper.fill"
        case .clipboard: return "clipboard"
        case .flight: return "airplane.departure"
        case .foodBeverage: return "cup.and.saucer.fill"
        case .football: return "football.fill"
        case .forest: return "tree.fill"
        case .group: return "person.2.fill"
        case .handyman: return "wrench.and.screwdriver.fill"
        case .homeRepairService: return "latch.2.case.fill"
        case .lightBulb: return "lightbulb.fill"
        case .medicalServices: return "cross.case.fill"
        case .musicNote: return "music.note"
        case .person: return "person.fill"
        case .pets: return "pawprint.fill"
        case .piano: return "pianokeys"
        case .restaurant: return "fork.knife"
        case .scissors: return "scissors"
        case .shoppingCart: return "cart.fill"
        case .smile: return "face.smiling"
        case .work: return "case.fill"
        default: return "checklist"
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
