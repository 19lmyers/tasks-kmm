# Tasks (KMM)

A functional and colorful task / to-do list app built with Kotlin Multiplatform Mobile (KMM)

_Looking for the [web app](https://github.com/19lmyers/tasks.chara.dev)
or [server backend](https://github.com/19lmyers/tasks-backend)?_

## Objectives

My main goal with this project was to teach myself Jetpack Compose.
I also wanted to go through the process of creating a KMM-compatible app, with the eventual goal of
using the same shared code on both iOS and Android devices.

## Libraries

I've previously built apps with Retrofit, Room, and Hilt, but I realized that a KMM app presents a
unique set of challenges, and would require libraries explicitly compatible with it.

For this project, I settled on:

- **SQLDelight** (caching data)
- **Jetpack DataStore** (persisting user preferences)
- **Koin** (dependency injection)
- **Ktor** (REST API consumption)
- **Firebase / Crashlytics**

New as of August 2023:

- **JetBrains Compose** (cross-platform declarative UI)
- **Decompose** (UI state tree model)

## Architecture

Recently, I've rewritten the app using JetBrains Compose Multiplatform! This means that 99% of app
UI *and business logic* is now shred between devices, and the app now sports the same colorful look
and feel on both platforms!

For UI state management, I decided to use Decompose and split the shared code into multiple modules:

- Shared
  - Model 
  - Database (SQLDelight generated database classes)
  - Data (Repository, data layer)
  - Domain (common client-side logic)
  - Component (w/ Decompose)
  - UI (w/ JetBrains Compose)
  - Ext (interfaces for app functionality required by other modules)
  - Framework (exports a framework for Xcode/SwiftUI to use)

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
- Widgets
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

## For developers

For code linting before each commit, change the project's git hooks directory with this command:

```shell
git config core.hooksPath hooks/
```

You can also copy the contents of /hooks to your project's git hooks folder.
