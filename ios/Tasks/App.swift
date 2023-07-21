import FirebaseCore
import FirebaseMessaging
import Foundation
import SwiftUI
import TasksShared

private var DEEP_LINK_HOST = "tasks.chara.dev"

private var PATH_RESET_PASSWORD = "/reset"
private var QUERY_PASSWORD_RESET_TOKEN = "token"

private var PATH_VIEW_LIST = "/list"
private var QUERY_LIST_ID = "id"

private var PATH_VIEW_TASK = "/task"
private var QUERY_TASK_ID = "id"

@main
struct TasksApp: App {
    @UIApplicationDelegateAdaptor private var appDelegate: AppDelegate

    @StateObject var appState = AppState.shared

    var body: some Scene {
        WindowGroup {
            ContentView().onOpenURL { url in
                guard let components = NSURLComponents(url: url, resolvingAgainstBaseURL: true),
                      let path = components.path,
                      let params = components.queryItems
                else {
                    return
                }

                switch path {
                case PATH_RESET_PASSWORD:
                    if let token = params.first(where: { $0.name == QUERY_PASSWORD_RESET_TOKEN })?.value {
                        AppState.shared.launchAction = .reset(token)
                    }
                case PATH_VIEW_LIST:
                    if let id = params.first(where: { $0.name == QUERY_LIST_ID })?.value {
                        AppState.shared.launchAction = .list(id)
                    }
                case PATH_VIEW_TASK:
                    if let id = params.first(where: { $0.name == QUERY_TASK_ID })?.value {
                        AppState.shared.launchAction = .task(id, false)
                    }
                default:
                    break
                }
            }
        }
    }
}

enum LaunchAction: Equatable {
    case none
    case list(String)
    case task(String, Bool)
    case reset(String)
}

class AppState: ObservableObject {
    static let shared = AppState()

    @Published var launchAction: LaunchAction = .none
}

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions _: [UIApplication
                         .LaunchOptionsKey: Any]?) -> Bool
    {
        InitKt.doInitKoin(endpointUrl: Configuration.endpointUrl.absoluteString)

        UserDefaults.standard.register(defaults: ["NSApplicationCrashOnExceptions": true])
        FirebaseApp.configure()
        Messaging.messaging().delegate = self

        Firebase.shared.link(platformWrapper: AppleFirebaseWrapper())

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

    func application(_: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }
}
