# ImageGenGPT
Using Dalle 2 model to generate images via voice commands powered by google text to speech API
# ImageGenGPT

ImageGenGPT is an Android app that allows users to generate images based on voice commands using the DALL·E API.

## Features

- Capture voice commands to generate images.
- Query the DALL·E API with voice commands.
- Display the generated image in the app.

## Requirements

- Android device or emulator running Android 5.0 (API level 21) or above.
- DALL·E API key. Obtain it from the DALL·E API provider.

## Getting Started

1. Clone the repository:

```shell
git clone https://github.com/vkanyoze/ImageGenGPT.git


Open the project in Android Studio.

Replace YOUR_DALLE_API_KEY in MainActivity.kt with your DALL·E API key.

Build and run the app on your Android device or emulator.

Permissions
The app requires the following permissions:

INTERNET: Access to the internet to make API requests.
RECORD_AUDIO: Permission to capture voice input from the user.
Dependencies
The app uses the following dependencies:

Add OkHttp: HTTP client for making API requests.
Make sure to add the necessary dependencies to your project's build.gradle file.


implementation(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))

// define any required OkHttp artifacts without version
implementation("com.squareup.okhttp3:okhttp")
implementation("com.squareup.okhttp3:logging-interceptor")

Contributing
Contributions to the ImageGenGPT project are welcome! If you find any issues or would like to add new features, please submit a pull request.
