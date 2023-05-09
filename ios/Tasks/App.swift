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
        case none, list(String), task(String, Bool), reset(String)
    }

    @Published var launchAction: LaunchAction = .none

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions options: [UIApplication
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

        if let url = options?[.url] as? URL {
            if url.host() == DEEP_LINK_HOST {
                guard let components = URLComponents(string: url.absoluteString) else { return true }

                switch url.path() {
                case PATH_RESET_PASSWORD:
                    if let token = components.queryItems?.first(where: { $0.name == QUERY_PASSWORD_RESET_TOKEN })?.value {
                        launchAction = .reset(token)
                    }
                case PATH_VIEW_LIST:
                    if let id = components.queryItems?.first(where: { $0.name == QUERY_LIST_ID })?.value {
                        launchAction = .list(id)
                    }
                case PATH_VIEW_TASK:
                    if let id = components.queryItems?.first(where: { $0.name == QUERY_TASK_ID })?.value {
                        launchAction = .task(id, false)
                    }
                default:
                    break
                }
            }
        }

        return true
    }
}

private var DEEP_LINK_HOST = "tasks.chara.dev"

private var PATH_RESET_PASSWORD = "/reset"
private var QUERY_PASSWORD_RESET_TOKEN = "token"

private var PATH_VIEW_LIST = "/list"
private var QUERY_LIST_ID = "id"

private var PATH_VIEW_TASK = "/task"
private var QUERY_TASK_ID = "id"
