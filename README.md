# Recallio

Recallio is a feature-rich cloud-based note-taking app for Android. It enables users to securely create, organize, and manage notes with real-time synchronization across devices using **Firebase Authentication** and **Firebase Realtime Database**.

---

## Features

- **Secure Authentication**
  - Firebase Authentication with email/password login  
  - OTP verification for secure sign-up and login  
  - Password change and reset functionality  

- **Cloud Sync**
  - Notes stored in Firebase Realtime Database  
  - Access and edit your notes from multiple devices seamlessly  

- **Advanced Note Management**
  - Add and edit **titles** and **bodies** of notes  
  - Attach **images** directly to notes  
  - Create **checklists** with interactive checkboxes  
  - Set **reminders** for important tasks  
  - Add **labels** to organize notes efficiently  

- **User-Friendly Design**
  - Clean and responsive UI built with Android best practices  
  - Persistent login (stay logged in after app restart)  
  - All changes synced automatically without manual refresh  

---

## Tech Stack

- **Frontend:** Android (Java)
- **Backend:** Firebase Authentication + Firebase Realtime Database
- **Other Integrations:** OTP Integration for safe login

---

## How It Works

1. **User Authentication**
   - Users sign up using email and password with OTP verification.  
   - Firebase manages authentication sessions, allowing persistent logins.  

2. **Creating and Managing Notes**
   - Notes are stored under the authenticated user’s UID in the Realtime Database.  
   - Users can add titles, content, labels, reminders, images, and checklists.  

3. **Real-Time Sync**
   - Any updates made on one device are instantly reflected on all devices logged into the same account.  

4. **Security**
   - Firebase Database Rules ensure that each user can only access their own notes.  

---

## Firebase Database Rules Example

```json
{
  "rules": {
    "notes": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    }
  }
}
````

---

## Getting Started

### Prerequisites

* Android Studio installed
* A Firebase project set up at [Firebase Console](https://console.firebase.google.com)
* Google services JSON (`google-services.json`) added to your Android project

### Installation

1. Clone this repository:

   ```bash
   git clone [https://github.com/your-username/recallio.git](https://github.com/Sasken-Internship-2025/Recallio.git)
   ```
2. Open the project in **Android Studio**.
3. Add your Firebase configuration file (`google-services.json`) in `app/`.
4. Sync Gradle and build the project.
5. Run on an emulator or connected device.

---

## Future Enhancements

* Dark mode support
* Offline-first note caching
* Rich text formatting for notes
* Push notifications for reminders

---

