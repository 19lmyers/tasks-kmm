import MultiPlatformLibrary
import SwiftUI

@main
struct iOSApp: App {
    init() {
        InitHelpersKt.doInitKoin(endpointUrl: Configuration.endpointUrl.absoluteString)

        #if DEBUG
        InitHelpersKt.doInitNapierDebug()
        #else
        InitHelpersKt.doInitNapierRelease()
        #endif
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
