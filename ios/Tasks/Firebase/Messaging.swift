//
//  Messaging.swift
//  Tasks
//
//  Created by Luke Myers on 5/8/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import FirebaseCore
import FirebaseMessaging
import TasksShared

private var DATA_MESSAGE_TYPE = "DATA_MESSAGE_TYPE"
private var DATA_TASK_ID = "DATA_TASK_ID"
private var DATA_TASK_CATEGORY = "DATA_TASK_CATEGORY"

private var MESSAGE_TYPE_REMINDER = "MESSAGE_TYPE_REMINDER"
private var MESSAGE_TYPE_ACTION = "MESSAGE_TYPE_ACTION"

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
    func userNotificationCenter(_: UNUserNotificationCenter, willPresent notification: UNNotification) async -> UNNotificationPresentationOptions {
        let userInfo = notification.request.content.userInfo

        let messageType = userInfo[DATA_MESSAGE_TYPE] as? String

        if messageType == MESSAGE_TYPE_REMINDER {
            return [[.banner, .list, .sound]]
        }

        if messageType == MESSAGE_TYPE_ACTION {
            rootHolder.root.refreshCache()
            return [[.list]]
        }

        return [[.banner, .list, .sound]]
    }

    func userNotificationCenter(_: UNUserNotificationCenter, didReceive response: UNNotificationResponse) async {
        let userInfo = response.notification.request.content.userInfo

        let messageType = userInfo[DATA_MESSAGE_TYPE] as? String

        if messageType == MESSAGE_TYPE_REMINDER {
            guard let taskId = userInfo[DATA_TASK_ID] as? String else { return }
            if response.actionIdentifier == COMPLETE_ACTION_IDENTIFIER {
                rootHolder.root.markTaskAsCompleted(id: taskId)
            } else {
                rootHolder.root.onDeepLink(deepLink: DeepLinkViewTask(id: taskId))
            }
        }
    }
}

extension AppDelegate: MessagingDelegate {
    func messaging(_: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        if fcmToken != nil {
            rootHolder.root.linkFCMToken(token: fcmToken!)
        }
    }
}
