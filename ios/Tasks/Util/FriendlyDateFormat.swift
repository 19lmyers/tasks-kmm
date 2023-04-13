//
//  FriendlyDateFormat.swift
//  Tasks
//
//  Created by Luke Myers on 4/12/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

class FriendlyDateFormat {
    func formatDate(date: Date) -> String {
        let formatter = DateFormatter()

        formatter.dateStyle = .medium
        formatter.timeStyle = .none
        formatter.locale = Locale.current

        return formatter.string(from: date)
    }

    func formatDateTime(date: Date) -> String {
        let formatter = DateFormatter()

        formatter.dateStyle = Calendar.current.isDateInToday(date) ? .none : .medium
        
        formatter.timeStyle = .short
        formatter.locale = Locale.current

        return formatter.string(from: date)
    }
}
