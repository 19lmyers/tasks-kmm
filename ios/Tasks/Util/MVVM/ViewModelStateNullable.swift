//
//  ViewModelStateNullable.swift
//  mokoMvvmFlow
//
//  Created by mdubkov on 25.09.2022.
//

import MultiPlatformLibrary
import SwiftUI

extension ObservableObject where Self: ViewModel {
    func stateNullable<T, R>(
        _ flowKey: KeyPath<Self, CStateFlow<T>>,
        equals: @escaping (T?, T?) -> Bool,
        mapper: @escaping (T?) -> R?
    ) -> R? {
        let stateFlow: CStateFlow<T> = self[keyPath: flowKey]
        var lastValue: T? = stateFlow.value

        var disposable: DisposableHandle? = nil

        disposable = stateFlow.subscribe(onCollect: { [weak self] value in
            if !equals(lastValue, value) {
                lastValue = value
                self?.objectWillChange.send()
                disposable?.dispose()
            }
        })

        return mapper(stateFlow.value)
    }

    func stateNullable(_ flowKey: KeyPath<Self, CStateFlow<KotlinBoolean>>) -> Bool? {
        stateNullable(
            flowKey,
            equals: { $0?.boolValue == $1?.boolValue },
            mapper: { $0?.boolValue }
        )
    }

    func stateNullable(_ flowKey: KeyPath<Self, CStateFlow<KotlinDouble>>) -> Double? {
        stateNullable(
            flowKey,
            equals: { $0?.doubleValue == $1?.doubleValue },
            mapper: { $0?.doubleValue }
        )
    }

    func stateNullable(_ flowKey: KeyPath<Self, CStateFlow<KotlinFloat>>) -> Float? {
        stateNullable(
            flowKey,
            equals: { $0?.floatValue == $1?.floatValue },
            mapper: { $0?.floatValue }
        )
    }

    func stateNullable(_ flowKey: KeyPath<Self, CStateFlow<KotlinInt>>) -> Int? {
        stateNullable(
            flowKey,
            equals: { $0?.intValue == $1?.intValue },
            mapper: { $0?.intValue }
        )
    }

    func stateNullable(_ flowKey: KeyPath<Self, CStateFlow<KotlinLong>>) -> Int64? {
        stateNullable(
            flowKey,
            equals: { $0?.int64Value == $1?.int64Value },
            mapper: { $0?.int64Value }
        )
    }

    func stateNullable(_ flowKey: KeyPath<Self, CStateFlow<NSString>>) -> String? {
        stateNullable(
            flowKey,
            equals: { $0 == $1 },
            mapper: { $0 as? String }
        )
    }

    func stateNullable<T>(_ flowKey: KeyPath<Self, CStateFlow<NSArray>>) -> [T]? {
        state(
            flowKey,
            equals: { $0 === $1 },
            mapper: { $0 as? [T] }
        )
    }
}
