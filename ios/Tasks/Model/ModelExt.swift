//
//  TaskListExt.swift
//  ios
//
//  Created by Luke Myers on 4/8/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import MultiPlatformLibrary
import SwiftUI

extension TaskList.Color : Identifiable {    
    var ui: Color {
        switch self {
        case TaskList.Color.red: return Color.red
        case TaskList.Color.orange: return Color.orange
        case TaskList.Color.yellow: return Color.yellow
        case TaskList.Color.green: return Color.green
        case TaskList.Color.blue: return Color.blue
        case TaskList.Color.purple: return Color.purple
        case TaskList.Color.pink: return Color.pink
        default: return Color.accentColor
        }
    }
}
