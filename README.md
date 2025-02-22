# 📱 My Chat

**My Chat** is a modern Android chat application built using **MVVM**, **Hilt**, and **Data Binding**. Users can chat in real-time, send images, and manage their accounts securely with **Firebase Authentication**. Images are stored locally, while chat messages are stored in **Firestore**.  

## 🚀 Features
- 🔐 **Google Authentication** using Firebase  
- 💬 **Real-time Chat** using Firestore  
- 📸 **Send and Receive Images** (stored locally)  
- 🌎 **Remote Config** for dynamic app updates  
- 🔔 **Push Notifications** via Firebase Cloud Messaging (FCM)  
- 🏗️ **MVVM Architecture** with Hilt Dependency Injection  
- 🎨 **Material UI & Data Binding**  

## 🛠️ Tech Stack
| Tech | Description |
|------|------------|
| **Kotlin** | Primary language |
| **MVVM** | Architecture Pattern |
| **Hilt** | Dependency Injection |
| **Firestore** | Real-time chat storage |
| **Firebase Auth** | User authentication |
| **FCM** | Push notifications |
| **Volley** | Networking library |
| **Glide** | Image loading |
| **CameraX** | Image capturing |
| **Remote Config** | Dynamic app behavior |

 

## 🏗️ Installation

### 📂 Open in Android Studio
- Make sure you have the latest **Android SDK** and **Gradle** installed.


### 🔥 Setup Firebase
1. **Create a Firebase project** at [Firebase Console](https://console.firebase.google.com/)
2. **Add the `google-services.json` file** in the `app/` directory.

   
## 🔑 Add Fingerprint for Firebase Authentication  

To enable **Google Sign-In** and authentication, follow these steps:  

### 📝 Find Your SHA Fingerprints  
Run the following command to get your **SHA-1** and **SHA-256**:  
```sh
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android

📥 Add Your Own google-services.json
### ▶️ Run the App
```sh
./gradlew assembleDebug
