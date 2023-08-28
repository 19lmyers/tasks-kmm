//
//  Firebase.swift
//  Tasks
//
//  Created by Luke Myers on 7/20/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import FirebaseCore
import FirebaseCrashlytics
import FirebaseMessaging
import Foundation
import TasksShared

class AppleFirebaseWrapper: FirebaseWrapper {
    func getMessagingToken() async throws -> String? {
        Messaging.messaging().fcmToken
    }

    func log(msg: String) {
        Crashlytics.crashlytics().log(msg)
    }

    func recordException(t: KotlinThrowable) {
        CrashlyticsKt.recordException(throwable: t)
    }

    func setUserId(userId: String) {
        Crashlytics.crashlytics().setUserID(userId)
    }
}
