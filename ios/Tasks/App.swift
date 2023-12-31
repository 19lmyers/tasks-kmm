import FirebaseCore
import FirebaseMessaging
import Foundation
import SwiftUI
import TasksShared

private var DEEP_LINK_HOST = "tasks.chara.dev"

private var PATH_VERIFY_EMAIL = "/verify"
private var QUERY_VERIFY_EMAIL_TOKEN = "token"

private var PATH_RESET_PASSWORD = "/reset"
private var QUERY_PASSWORD_RESET_TOKEN = "token"

private var PATH_JOIN_LIST = "/join"
private var QUERY_LIST_INVITE_TOKEN = "token"

private var PATH_VIEW_LIST = "/list"
private var QUERY_LIST_ID = "id"

private var PATH_VIEW_TASK = "/task"
private var QUERY_TASK_ID = "id"

var rootHolder: RootHolder = .init()

@main
struct TasksApp: App {
    @UIApplicationDelegateAdaptor private var appDelegate: AppDelegate

    init() {
        UserDefaults.standard.register(defaults: ["NSApplicationCrashOnExceptions": true])

        FirebaseApp.configure()

        EntryPointKt.setupKoin(firebaseWrapper: AppleFirebaseWrapper(), endpointUrl: Configuration.endpointUrl.absoluteString)

        EntryPointKt.setupLogger()
    }

    var body: some Scene {
        WindowGroup {
            ContentView(rootHolder: rootHolder)
                .ignoresSafeArea(.all)
                .onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
                    LifecycleRegistryExtKt.resume(rootHolder.lifecycle)
                }
                .onReceive(NotificationCenter.default.publisher(for: UIApplication.willResignActiveNotification)) { _ in
                    LifecycleRegistryExtKt.pause(rootHolder.lifecycle)
                }
                .onReceive(NotificationCenter.default.publisher(for: UIApplication.didEnterBackgroundNotification)) { _ in
                    LifecycleRegistryExtKt.stop(rootHolder.lifecycle)
                }
                .onReceive(NotificationCenter.default.publisher(for: UIApplication.willTerminateNotification)) { _ in
                    LifecycleRegistryExtKt.destroy(rootHolder.lifecycle)
                }
                .onOpenURL { url in
                    guard let components = NSURLComponents(url: url, resolvingAgainstBaseURL: true),
                          let path = components.path,
                          let params = components.queryItems
                    else {
                        return
                    }

                    switch path {
                    case PATH_VERIFY_EMAIL:
                        if let token = params.first(where: { $0.name == QUERY_VERIFY_EMAIL_TOKEN })?.value {
                            rootHolder.root.onDeepLink(deepLink: DeepLinkVerifyEmail(token: token))
                        }
                    case PATH_RESET_PASSWORD:
                        if let token = params.first(where: { $0.name == QUERY_PASSWORD_RESET_TOKEN })?.value {
                            rootHolder.root.onDeepLink(deepLink: DeepLinkResetPassword(token: token))
                        }
                    case PATH_JOIN_LIST:
                        if let token = params.first(where: { $0.name == QUERY_LIST_INVITE_TOKEN })?.value {
                            rootHolder.root.onDeepLink(deepLink: DeepLinkJoinList(token: token))
                        }
                    case PATH_VIEW_LIST:
                        if let id = params.first(where: { $0.name == QUERY_LIST_ID })?.value {
                            rootHolder.root.onDeepLink(deepLink: DeepLinkViewList(id: id))
                        }
                    case PATH_VIEW_TASK:
                        if let id = params.first(where: { $0.name == QUERY_TASK_ID })?.value {
                            rootHolder.root.onDeepLink(deepLink: DeepLinkViewTask(id: id))
                        }
                    default:
                        break
                    }
                }
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions _: [UIApplication.LaunchOptionsKey: Any]?) -> Bool
    {
        Messaging.messaging().delegate = self

        UNUserNotificationCenter.current().delegate = self

        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: { _, _ in }
        )

        application.registerForRemoteNotifications()

        initNotificationCategories()

        return true
    }

    func application(
        _: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        if let shortcutItem = options.shortcutItem {
            if shortcutItem.type == "CreateTaskAction" {
                rootHolder.root.onDeepLink(deepLink: DeepLinkCreateTask.shared)
            }
        }

        let configuration = UISceneConfiguration(
            name: connectingSceneSession.configuration.name,
            sessionRole: connectingSceneSession.role
        )
        configuration.delegateClass = SceneDelegate.self
        return configuration
    }

    func application(_: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }
}

class SceneDelegate: NSObject, UIWindowSceneDelegate {
    func windowScene(_: UIWindowScene, performActionFor shortcutItem: UIApplicationShortcutItem, completionHandler: @escaping (Bool) -> Void) {
        if shortcutItem.type == "CreateTaskAction" {
            rootHolder.root.onDeepLink(deepLink: DeepLinkCreateTask.shared)
        }

        completionHandler(true)
    }
}

class RootHolder: ObservableObject {
    let lifecycle: LifecycleRegistry

    var root: RootComponent

    init() {
        lifecycle = LifecycleRegistryKt.LifecycleRegistry()

        root = DefaultRootComponent(
            componentContext: DefaultComponentContext(lifecycle: lifecycle),
            deepLink: DeepLinkNone.shared
        )

        LifecycleRegistryExtKt.create(lifecycle)
    }

    deinit {
        // Destroy the root component before it is deallocated
        LifecycleRegistryExtKt.destroy(lifecycle)
    }
}
