import Foundation

enum Configuration {
    // MARK: - Public API

    static var endpointUrl: URL {
        URL(string: string(for: "ENDPOINT_URL"))!
    }

    // MARK: - Helper Methods

    private static func string(for key: String) -> String {
        Bundle.main.infoDictionary?[key] as! String
    }
}
