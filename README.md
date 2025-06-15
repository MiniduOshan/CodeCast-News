# CodeCast News

**CodeCast News** is a modern Android application built with **Java** that delivers real-time news articles to users. With intuitive design, robust Firebase integration, and secure user authentication, it offers a seamless experience for browsing, reading, and managing news content. The app supports full **CRUD** operations (Create, Read, Update, Delete) on user data, powered by Firebase Realtime Database.

---

## ✨ Features

- **Splash Screen**  
  A welcoming splash screen to enhance the user experience upon launch.

- **User Authentication (Sign In & Sign Up)**  
  Secure sign-in and sign-up functionalities powered by **Firebase Authentication**.

- **News Feed**  
  Browse a list of news articles in a clean, engaging layout.

- **News Detail**  
  View comprehensive information about each article.

- **Search Functionality**
  Enable users to find news articles by keywords or phrases quickly.
  
- **Settings Screen**  
  Manage app preferences and configuration.

- **User Profile Management**  
  Users can edit personal information such as email, name, and phone number and securely change their passwords.

- **Developer Info Screen**  
  Displays application version details and developer information.

- **Realtime Data Updates**  
  News content and user data are updated in real-time using **Firebase Realtime Database**.

- **Interactive Dialogs**  
  Custom dialogue fragments include password changes, name/Profile PIcture/phone edits, and sign-out confirmations.

- **Responsive UI**  
  Adaptive layout ensures an optimal experience across various Android devices.

---


## 📁 Project Structure

```
codecastnews/
├── adapters/
│   └── NewsAdapter.java
├── dialogs/
│   ├── ChangePasswordDialogFragment.java
│   ├── EditEmailDialogFragment.java
│   ├── EditNameDialogFragment.java
│   ├── EditPhoneNumberDialogFragment.java
│   └── SignOutDialogFragment.java
├── models/
│   └── NewsArticle.java
├── DevInfoScreen.java
├── MainActivity.java
├── NewsDetailScreen.java
├── NewsScreen.java
├── SettingScreen.java
├── SignInScreen.java
├── SignUpScreen.java
└── UserProfile.java

res/
├── drawable/
├── layout/
│   ├── activity_dev_info_screen.xml
│   ├── activity_main.xml
│   ├── activity_news_detail.xml
│   ├── activity_news_screen.xml
│   ├── activity_setting_screen.xml
│   ├── activity_sign_in_screen.xml
│   ├── activity_sign_up_screen.xml
│   ├── activity_user_profile.xml
│   ├── dialog_change_password.xml
│   ├── dialog_edit_email.xml
│   ├── dialog_edit_name.xml
│   ├── dialog_edit_phone_number.xml
│   ├── dialog_sign_out.xml
│   └── item_news_small_card.xml
```

---

## 🚀 Getting Started

### ✅ Prerequisites

- **Android Studio** (latest version recommended)  
- **Java Development Kit (JDK)**  
- **Firebase Project**  
  - Realtime Database configured  
  - Authentication enabled  
  - `google-services.json` downloaded  

---

## 🛠️ Technologies Used

| Technology              | Purpose                                 |
|-------------------------|------------------------------------------|
| Java                    | Core programming language                |
| Android SDK             | App development framework                |
| Firebase Authentication | User login and session management        |
| Firebase Realtime DB    | News data storage and live updates       |
| Gradle                  | Build automation                         |

---


## Login Details(For Testing)
- **Email**: miniduoshan@gmail.com
- **Password**: minidu1234

Also, you can create a new account using Signup


## 👨‍💻 Developer Info

- **Name**: B.D.M.O.B. Wanigarathna  
- **Index Number**: 2022t01584  

---


