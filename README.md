# Spenderino

In the future, Spenderino (working title) is supposed to be a solution to facilitate spontaneous micro-donations to people in need.

With society moving into a cashless direction, spontaneously donating to people who are homeless or otherwise in need, becomes harder due to not having cash at hand.
This project contains source code for two mobile apps (iOS & Android) that feature the ability to receive and give donations digitally. 
A donator can scan a recipient's QR code and donate via Apple Pay, Google Pay or a Credit Card of their choice. 
The recipient can then generate a QR code which can be scanned by partnered brick and mortar stores that pay out a sum in cash to the recipient.

This codebase is part of my bachelor's thesis but I'm planning to grow this into a full fledged project backed by a non-profit organization in the future.



On a technical level this project explores Kotlin Multiplatform Mobile's (KMM) production readiness. It also presents a way to architect a KMM app in a way that only the UI code needs to be implemented in a platform specific (SwiftUI / Jetpack Compose) way. 
Everything up until (and including) the ViewModel layer is shared between the platforms and can therefore be tested easily with unit tests. 

There's a backend codebase located at https://github.com/pumapaul/spenderino-backend which you can run if you don't want to use the hosted backend while playing around.



## Getting started

* Clone the repository
* Import the project into Android Studio.
  We generally use the latest version, if not explicitly specified otherwise
* Provide a Stripe API key at `stripeKey ` in `gradle.properties`.
  You can build and use the apps without it but donating / payment will not function.
* If you want to use a backend other than the default one, change `BASE_URL` in `de.paulweber.spenderino.model.networking.Client.kt` in the `shared` module

### Android

There's no further configuration needed.

Build the `android` run configuration to a device or emulator of your choice.

### iOS

There's a bit more you need to do before you can run the iOS app. 

* All further configuration happens in the iOS directory
  `$ cd ios`
* Install [Mint](https://github.com/yonaskolb/Mint) - a package manager for Swift command line tools
  `$ brew install mint`

* Run [XcodeGen](https://github.com/yonaskolb/XcodeGen) - to generate the `.xcodeproj` file. 
  We don't check the Xcode project file into git to avoid the usual tedious merge conflict management that comes up when using Xcode projects together with git. 
  It is recommended that you run this command after switching between git branches. 
  It's at least necessary to run it when files have been added, removed or moved by an outside source (like git).
  `$ mint run xcodegen` 
* Open `Spenderino.xcodeproj`

You are now able to run the `Spenderino` target on your iOS simulator.  

If you want to run the app on your device, you need to grab the provisioning profiles. For this we use [fastlane](https://fastlane.tools)'s Match:

* Install [Bundler](https://bundler.io) - for synchronization of the ruby gems we use in our project.
  `$ gem install bundler`
* Install our ruby gems
  `$ bundle install`
* Run match
  `$ bundle exec fastlane match development`
* Run XcodeGen
  `$ mint run xcodegen`
* Restart Xcode

You should be able to build to a device of your choice now. 



## Architecture

We generally adhere to the MVVM pattern with some small tweaks.

The ViewModel and Model layers are defined in Kotlin in the `shared` codebase. The UI code for Android is defined in the `android` module and iOS UI Code is defined in the `ios` subfolder as an Xcode project.

Here's a quick overview for the core concepts, if you want a deeper dive, please refer to the GitHub project's Wiki.

### View

We differentiate between **Screen**s and **View**s. 

* **Screen**: Generally a full screen view that can be a navigation target and is capable of navigating to another screen. 
* **View**: A screen may contain many views. For example a screen that displays a list of data that's loaded remotely might define several views for when the data is loading, an error occured or there is no data to display in addition to the actual view where it then displays a list of subviews.

We use the respective declarative UI frameworks SwiftUI and Jetpack Compose. Navigation is also done in a declarative way.

### ViewModel

Every UI screen uses an underlying ViewModel which generally has `sealed class` or `enum class` definitions for its corresponding **State**, possible **Route**s and its **Action**s.

* **State**: Contains everything the declarative UI layers need to display like boolean flags, Strings, a list of items or whether a ViewModel is loading remote data or encountered and error.
* **Route**: Represents the navigation state. If there's an alert to display, that should be defined as a route. If this ViewModel can navigate to another ViewModel, that should be defined as a route, etc.
* **Action**: Defines the possible actions the user can take on a screen. Any button tap should result in an action, typing text should be represented as an action, etc.

The ViewModel exposes state and route as observable fields for the UI layer to bind to.

When the ViewModel receives an action, this will usually result in a change of state or a route might get set.

### Model

We adhere to a version of the Repository pattern. Repositories are classes that can provide and operate on data classes that are necessary to drive the ViewModels. They are generally feature-bound, i.e. there's a Repository for everything regarding user management called `UserRepository` or a Repository that handles donations called `DonationRepository`.
Currently every repository holds one or more RemoteSources. A RemoteSource uses an HTTP client to communicate with our backend and serves the resulting data to the Repository which then exposes that via observable fields or methods to the ViewModels.

In the future there will also be LocalSources that might use underlying sqllite databases for caching.

### Dependency Injection

We use [Koin](https://insert-koin.io) as a service locator for our repositories, remote sources and other services.



## Quality Assurance

The project is configured with Github Actions for continuous integration. The workflow definitions can be found in `/.github/workflows` . 
We use unit tests in our shared module as well as linters for both platforms. 

### Unit Testing

* `gradle :shared:koverVerify` will run our test suite and verify that the minimum line coverage is adhered to. The current minimum is set to 75%.
* `gradle :shared:koverHtmlReport` will generate a code coverage report at `/shared/build/reports/kover/project-html/`. This helps in finding out where you're lacking with test coverage.

### Linting

* [detekt](https://detekt.dev) for Kotlin:
  `gradle :detekt` 
  This will check all kotlin files for coding style breaches. It will also auto-correct most common mistakes, so running it twice will result in far less issues (if there remain any at all).
  The configuration is located in `/detekt/detekt_config.yml`

* [SwiftLint](https://github.com/realm/SwiftLint) for Swift:
  `$ cd ios`

  `$ mint run swiftlint` 

  You can also run `$ mint run swiftlint --autocorrect` to automatically fix whitespacing and other issues.
  The configuration is located in `/ios/.swiftlint.yml`

### CI jobs

* detekt: 
  * runs `gradle :shared:koverVerify`
  * whenever any `*.kt` file has been changed
* SwiftLint: 
  * runs `$ mint run swiftlint`
  * whenever any `*.swift` file has been changed
* shared tests: 
  * runs `gradle :shared:koverVerify`
  * whenever any `*.kt` file in the `shared` module has been changed
* iOS tests: 
  * runs xcodebuild to verify that the iOS project is able to build
  * whenever any `*.kt` file in the `shared` module has been changed
  * or whenever any `*.swift` file has been changed
* Android tests:
  * runs `gradle :android:test` to verify that the Android app is able to build
  * whenever any `*.kt` file in the `shared` module has been changed
  * or whenever any `*.kt` file in the `android` module has been changed



## Dependencies

We use several libraries / tools other than the first party frameworks & libraries provided by Google, Jetbrains and Apple.

### Shared

* [Ktor](https://ktor.io) for http requests

* [Koin](https://insert-koin.io) for dependency injection / service location
* [Kermit](https://github.com/touchlab/Kermit) for logging
* [MockK](https://mockk.io) for mocking classes and objects in unit tests
* [BuildKonfig](https://github.com/yshrsmz/BuildKonfig) to facilitate environment variables in Kotlin Multiplatform code
* [MOKO KSwift](https://github.com/icerockdev/moko-kswift) for automatic code generation of swift enums for kotlin sealed classes
* [Stripe](https://stripe.com) as a payment provider

### Android

* [ZXing](https://github.com/zxing/zxing) for scanning QR codes
* [Compose Destinations](https://github.com/raamcosta/compose-destinations) to generate boilerplate code necessary for navigation

### iOS

* [CodeScanner](https://github.com/twostraws/CodeScanner) for scanning QR codes
* [PagerTabStripView](https://github.com/xmartlabs/PagerTabStripView) to mimic the Android style swiping tab view