import mokoMvvmFlowSwiftUI
import MultiPlatformLibrary
import SwiftUI

@main
struct iOSApp: App {
    init() {
        InitKt.doInitKoin(endpointUrl: Configuration.endpointUrl.absoluteString)

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
