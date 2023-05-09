import FirebaseCore
import FirebaseMessaging
import Foundation
import MultiPlatformLibrary
import SwiftUI

@main
struct TasksApp: App {
    @UIApplicationDelegateAdaptor private var appDelegate: AppDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(appDelegate)
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate, ObservableObject {
    enum LaunchAction: Equatable {
        case none, task(String, Bool)
    }

    @Published var launchAction: LaunchAction = .none

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions _: [UIApplication
                         .LaunchOptionsKey: Any]?) -> Bool
    {
        InitKt.doInitKoin(endpointUrl: Configuration.endpointUrl.absoluteString)

        UserDefaults.standard.register(defaults: ["NSApplicationCrashOnExceptions": true])
        FirebaseApp.configure()
        Messaging.messaging().delegate = self

        UNUserNotificationCenter.current().delegate = self

        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: { _, _ in }
        )

        application.registerForRemoteNotifications()

        initNotificationCategories()

        #if DEBUG
            InitKt.doInitNapierDebug()
        #else
            InitKt.doInitNapierRelease()
        #endif

        return true
    }
}
