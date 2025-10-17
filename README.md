# ErasMapp

## Project team

First and last name | E-mail address (FOI) | JMBAG | Github username | Seminar group
------------  | ------------------- | ----- | --------------------- | ----------------
Luka Kanjir | lkanjir23@student.foi.hr | 0016164491 | lkanjir23 | G01
Nikola Kihas | nkihas23@student.foi.hr | 0016165847 | nkihas23 | G01
Sofiane Saou | ssaou25@student.foi.hr | 0016179480 | 444sofiane | -

## Domain description

ErasMapp is a mobile application designed to help Erasmus exchange students at FOI integrate smoothly into their new environment. The app aims to make their stay in Varaždin more organized, social, and enjoyable by providing all the essential information and tools they need in one place.

Through navigation, relevant news, and a communication interface, ErasMapp eases the process of adapting to a new academic and social setting.

Administrators and staff have access to dedicated tools that allow them to manage faculty buildings and rooms, post announcements, and communicate directly with students.

## Project specification

Label | Name | Short description | Responsible team member
------ | ----- | ----------- | -------------------
F01 | Login | The application should allow users to authenticate using a combination of e-mail and password or by using a Google account. The entered credentials should be validated to ensure authorized access. The system should maintain sessions to keep users logged in after app restarts. | Luka Kanjir
F02 | Faculty buildings locator | The system should display FOI buildings on an interactive map with their names and details. | Sofiane Saou
F03 | Room locator | The system should enable users to search for specific rooms in a selected building, and then display directions to the selected room.| Sofiane Saou
F04 | Nearest point of interest | The system should detect the user’s current location and display nearby facilities such as cafés, libraries, etc. It should allow filtering by type of location and provide the option to open the result in Google Maps. |Sofiane Saou
F05 | Student channels | The system should provide discussion channels where students can post questions and answer them. | Luka Kanjir
F06 | News and info | The system should display official announcements and updates published by administrators. It should categorize news by topic and support push notifications for urgent messages. | Luka Kanjir
F07 | Schedule | The system should allow users to view and modify their personal academic schedules. It should provide both daily and weekly views. | Nikola Kihas
F08 | Event calendar | The system should present a calendar of upcoming faculty and city events relevant to exchange students. It should display event details, including name, date, time, and location. Administrators should be able to create and manage events through the administration interface. | Nikola Kihas
F09 | Admin console | The system should provide authorized staff with administrative tools for managing buildings, rooms, events, and news content. It should implement role-based access control to restrict operations to permitted users. | Nikola Kihas

## Technology and Tools
#### Technologies
- Kotlin
- Android SDK
- Jetpack Compose
- Google Maps Platform
- Firebase

#### Tools
 - Android Studio
 - git and Github

## Database and server
 - Cloud Firestore
 - Realtime Database (Firebase)
 - Firebase Cloud Functions
