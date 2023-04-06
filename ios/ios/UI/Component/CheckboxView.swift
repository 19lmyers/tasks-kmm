//
//  CheckboxView.swift
//  ios
//
//  Created by Luke Myers on 4/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct CheckboxView: View {
    @State var isChecked: Bool = false
    
    var body: some View {
        Button(action: {
            isChecked = !isChecked
        }) {
            Image(systemName: isChecked ? "checkmark.square" : "square")
        }.buttonStyle(PlainButtonStyle())
    }
}

struct CheckboxView_Previews: PreviewProvider {
    static var previews: some View {
        CheckboxView()
    }
}
