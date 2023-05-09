//
//  Messaging.swift
//  Tasks
//
//  Created by Luke Myers on 5/8/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import FirebaseCore
import FirebaseMessaging
import MultiPlatformLibrary

private var DATA_MESSAGE_TYPE = "DATA_MESSAGE_TYPE"
private var DATA_TASK_ID = "DATA_TASK_ID"

private var MESSAGE_TYPE_REMINDER = "MESSAGE_TYPE_REMINDER"

private var REMINDER_CATEGORY_IDENTIFIER = "reminder"
private var COMPLETE_ACTION_IDENTIFIER = "reminder.complete"

func initNotificationCategories() {
    let completeAction = UNNotificationAction(identifier: COMPLETE_ACTION_IDENTIFIER, title: "Mark as complete", options: [])
    let reminderCategory = UNNotificationCategory(
        identifier: REMINDER_CATEGORY_IDENTIFIER,
        actions: [completeAction],
        intentIdentifiers: []
    )

    UNUserNotificationCenter.current().setNotificationCategories([reminderCategory])
}

extension AppDelegate: UNUserNotificationCenterDelegate {
    func userNotificationCenter(_: UNUserNotificationCenter, willPresent _: UNNotification) async -> UNNotificationPresentationOptions {
        [[.banner, .list, .sound]]
    }

    func userNotificationCenter(_: UNUserNotificationCenter, didReceive response: UNNotificationResponse) async {
        let userInfo = response.notification.request.content.userInfo

        if userInfo[DATA_MESSAGE_TYPE] as? String == MESSAGE_TYPE_REMINDER {
            guard let taskId = userInfo[DATA_TASK_ID] as? String else { return }
            AppState.shared.launchAction = .task(taskId, response.actionIdentifier == COMPLETE_ACTION_IDENTIFIER)
        }
    }
}

extension AppDelegate: MessagingDelegate {
    func messaging(_: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        if fcmToken != nil {
            let helper = MessagingHelper()
            helper.onNewToken(token: fcmToken!)
        }
    }
}
