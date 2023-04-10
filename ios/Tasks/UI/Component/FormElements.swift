//
//  StarView.swift
//  Tasks
//
//  Created by Luke Myers on 4/9/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct CheckboxView: View {
    @State var isChecked: Bool = false
    
    var onChange: (Bool) -> Void = { _ in }
    
    var body: some View {
        Button(action: {
            isChecked.toggle()
            onChange(isChecked)
        }) {
            Image(systemName: isChecked ? "checkmark.square" : "square").foregroundStyle(.tint)
        }.buttonStyle(PlainButtonStyle())
    }
}

struct StarView: View {
    @State var isStarred: Bool = false
    
    var onChange: (Bool) -> Void = { _ in }
    
    var body: some View {
        Button(action: {
            isStarred.toggle()
            onChange(isStarred)
        }) {
            Image(systemName: isStarred ? "star.fill" : "star").foregroundStyle(.tint)
        }.buttonStyle(PlainButtonStyle())
    }
}


struct FormElements_Previews: PreviewProvider {
    static var previews: some View {
        CheckboxView()
        StarView()
    }
}
