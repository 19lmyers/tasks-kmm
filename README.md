# Tasks (KMM)

A functional and colorful task / to-do list app built with Kotlin Multiplatform Mobile (KMM)

_Looking for the [web app](https://github.com/19lmyers/tasks.chara.dev)
or [server backend](https://github.com/19lmyers/tasks-backend)?_

## Objectives

My main goal with this project was to teach myself Jetpack Compose.
I also wanted to go through the process of creating a KMM-compatible app, with the eventual goal of
using the same shared core on both iOS and Android devices.

## Libraries

I've previously built apps with Retrofit, Room, and Hilt, but I realized that a KMM app presents a
unique set of challenges, and would require libraries explicitly compatible with it.

For this project, I settled on:

- Shared
    - **SQLDelight** (caching data)
    - **Jetpack DataStore** (persisting user preferences)
    - **Koin** (dependency injection)
    - **Ktor** (REST API consumption)
    - **Firebase / Crashlytics**
- Android
    - **Jetpack Compose** (& friends)

Some brief notes:

- I use SQLDelight 2.0 (alpha) for its support of newer SQLite language features (I pull in a newer
  version as a dependency).
- For my specific use case, I found some open-source libraries (mainly Navigation Reimagined and
  Compose Reorderable) that were a better fit for my project or implemented important functionality
  that hasn't yet made its way into Compose proper.

## Architecture

My goal was to utilize Jetpack Compose for as much as possible, and lessen its reliance on the
Android platform's Activity and Fragment classes for managing UI state.

The ViewModels use the **Moko MVVM** and **KSwift** libraries to make using them in iOS simpler.
(On Android, they inherit from the AndroidX component.)

## Design

My goal with the app's design was to be functional and colorful, mirroring the features available in
Google Tasks and Microsoft To-Do while utilizing my interpretation of the Material You design
language. I'm proud of how it's turned out thus far 😄

## Features

Currently, the app includes:

- Multiple task lists
- Tasks with multiple fields
- List customization options
- Reminder push notifications
- A helpful dashboard for starred and upcoming tasks
- Cross-device sync and an account system

There are a few things I have yet to implement, mainly:

- List sharing
- Widgets (using Glance)
- Subtasks
- Recurring tasks

## Backend

The backend server was concurrently designed with the app, using many of the same libraries (mainly
SQLDelight and Ktor) as the client
code. [You can view the code here.](https://github.com/19lmyers/tasks-backend)

## Evolution

I built the app to be extensible where possible, but some cross-cutting refactors have been
unavoidable.
To avoid loss of test user data, I used SQLDelight migrations on both the client and the backend.

## Reflection

If I were to start this project over, there are two major things I'd do differently:

1. I'd use Source Control (Git) from the beginning and practice a more Agile development cycle.
2. I'd add unit and integration testing to the list of concepts to teach myself during the project.
