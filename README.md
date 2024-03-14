# Memo App

A simple to-do app.

## Tech Stack

- Kotlin
- Jetpack Compose
- Firebase
    - Firestore
    - Authentication
    - Storage
    - Crashlytics
    - Analytics
- Dagger Hilt

## Development

### Prerequisites

- GitHub Account
- [Git](https://git-scm.com/download/win)

1. Download `google-services.json` from
   Firebase's [Project Settings](https://console.firebase.google.com/project/memo-f7e6d/settings/general/android:io.github.jhdcruz.memo)
   bottom part of the page.
    - Place `google-services.json` in inside `app/`.

2. Setup `SHA-1` and `SHA-256` fingerprints in Firebase project settings for Google services:

    ```bash
    $ ./gradlew signingReport

    Variant: debug    # <-------- SHOULD BE DEBUG VARIANT
    Config: debug
    Store: C:\Users\Admin\.android\debug.keystore
    Alias: AndroidDebugKey
    MD5: somethin:something
    SHA1: somethin:something       # <---------- Needed for Google Cloud Platform API & Services Credentials
    SHA-256: somethin:something    # <---------- Needed for Firebase Project Settings
    Valid until: Saturday, January 10, 2099
    ```

> [!IMPORTANT]\
> Variant should be `debug`.

3. Add the `SHA-256` fingerprint in
   Firebase's [Project Settings](https://console.firebase.google.com/project/memo-f7e6d/settings/general/android:io.github.jhdcruz.memo):

4. Create [OAuth Client ID](https://console.cloud.google.com/apis/credentials/oauthclient?previousPage=%2Fapis%2Fcredentials%3Forgonly%3Dtrue%26project%3Dmemo-android-app%26supportedpurview%3DorganizationId&orgonly=true&project=memo-android-app&supportedpurview=organizationId)
Credential in Google Cloud Platform:

  ```bash
  Name: Memo Android [LastName]
  Package name: io.github.jhdcruz.memo
  SHA-1 certificate fingerprint: SHA1 from the gradle command above
  ```

5. Set up firebase environment in `local.properties`.

    ```properties
    # Backend for authentication
    gcp.web.client=CLIENT ID from "Memo Backend" OAuth 2.0 Client IDs
    gcp.web.secret=CLIENT SECRET form "Memo Backend" OAuth 2.0 Client IDs
    # Firebase Client SDK
    gcp.client.debug=CLIENT ID from your "Memo Android [LastName]" OAuth 2.0 Client IDs
    gcp.client.release=LEAVE BLANK
    ```

6. Run the app.

### Submitting changes

1. Fork [Memo](https://github.com/jhdcruz/Memo)
2. Clone it to your local system
3. Make a new branch
4. Make your changes
5. Push it back to your repo
6. Click the Compare & pull request button
7. Click Create pull request to open a new pull request

> [!TIP]\
> Read tutorial/walkthrough
> here: https://www.freecodecamp.org/news/how-to-make-your-first-pull-request-on-github-3/

> [!NOTE]\
> If [Credential Manager](https://miro.medium.com/v2/resize:fit:872/1*IAy_jgfGxzdAfrAW47e2TA.png)
> starts from login page, Firebase and GCP is configured correctly.

## License

This project is distributed under GNU General Public License v3.0. See [COPYING](./COPYING) for more
information.
