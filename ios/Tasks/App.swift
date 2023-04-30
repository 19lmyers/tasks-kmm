import MultiPlatformLibrary
import SwiftUI

@main
struct TasksApp: App {
    init() {
        InitKt.doInitKoin(endpointUrl: Configuration.endpointUrl.absoluteString)

        UserDefaults.standard.register(defaults: ["NSApplicationCrashOnExceptions" : true])
        InitKt.doInitFirebase()

        #if DEBUG
        InitKt.doInitNapierDebug()
        #else
        InitKt.doInitNapierRelease()
        #endif
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
