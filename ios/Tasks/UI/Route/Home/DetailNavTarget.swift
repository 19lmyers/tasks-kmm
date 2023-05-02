//
//  HomeNavTarget.swift
//  Tasks
//
//  Created by Luke Myers on 4/25/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

enum DetailNavTarget: Hashable {
    case listDetails(String)
    case taskDetails(String)
    case profile, settings
}
