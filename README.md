# ErasMapp

## Domain description

ErasMapp is a mobile application designed to help Erasmus exchange students at FOI integrate smoothly into their new environment. The app aims to make their stay in Varaždin more organized, social, and enjoyable by providing all the essential information and tools they need in one place.

Through navigation, relevant news, and a communication interface, ErasMapp eases the process of adapting to a new academic and social setting.

Administrators and staff have access to dedicated tools that allow them to manage faculty buildings and rooms, post announcements, and communicate directly with students.

## My contributions

Label | Name | Short description
----- | ---- | -----------------
F01 | Login | The application should allow users to authenticate using a combination of e-mail and password or by using a Google account. The entered credentials should be validated to ensure authorized access. The system should maintain sessions to keep users logged in after app restarts. 
F05 | Student channels | The system should provide discussion channels where students can post questions and answer them.
F06 | News and info | The system should display official announcements and updates published by administrators. It should categorize news by topic.

## All features

Label | Name | Short description 
------ | ----- | -----------
F01 | Login | The application should allow users to authenticate using a combination of e-mail and password or by using a Google account. The entered credentials should be validated to ensure authorized access. The system should maintain sessions to keep users logged in after app restarts.
F02 | Faculty buildings locator | The system should display FOI buildings on an interactive map with their names and details.
F03 | Room locator | The system should enable users to search for specific rooms in a selected building, and then display directions to the selected room.
F04 | Nearest point of interest | The system should detect the user’s current location and display nearby facilities such as cafés, libraries, etc. It should allow filtering by type of location and provide the option to open the result in Google Maps.
F05 | Student channels | The system should provide discussion channels where students can post questions and answer them.
F06 | News and info | The system should display official announcements and updates published by administrators. It should categorize news by topic and support urgent messages.
F07 | Schedule | The system should allow users to view and modify their personal academic schedules. It should provide both daily and weekly views.
F08 | Event calendar | The system should present a calendar of upcoming faculty and city events relevant to exchange students. It should display event details, including name, date, time, and location. Administrators should be able to create and manage events through the administration interface.
F09 | Admin console | The system should provide authorized staff with administrative tools for managing buildings, rooms, events, and news content. It should implement role-based access control to restrict operations to permitted users.

## Technology and Tools

- Kotlin, Jetpack Compose
- Google Maps Platform
- Firebase

## Some screenshots of my contributions

### Login
<img width="1080" height="2400" alt="login_ligth" src="https://github.com/user-attachments/assets/8cab5062-921c-442a-9366-f1dde873bc3e" />
<img width="1080" height="2400" alt="login_dark" src="https://github.com/user-attachments/assets/ac284902-f5ae-4b3b-8333-7edf9785074f" />

### Main screen
<img width="1080" height="2400" alt="pocetna_light" src="https://github.com/user-attachments/assets/d2efd644-23d2-4129-8036-b498c306d1ea" />
<img width="1080" height="2400" alt="pocetna_dark" src="https://github.com/user-attachments/assets/3a26ff7f-5a09-48d2-b911-8bb0beb527c9" />

#### Student channels
<img width="1080" height="2400" alt="channels light" src="https://github.com/user-attachments/assets/f0ada020-3ce0-48eb-ab4d-88a53e55e227" />
<img width="1080" height="2400" alt="channels dark" src="https://github.com/user-attachments/assets/0414cfc8-7467-4e2b-b2b5-a6811277dfde" />

### A thread inside a selected channel
<img width="1080" height="2400" alt="channels ligth" src="https://github.com/user-attachments/assets/98c5a68f-4439-46a3-a2f2-36b3b7848e23" />
<img width="1080" height="2400" alt="threads dark" src="https://github.com/user-attachments/assets/4ec159ce-5e35-442d-8b97-c1f4f4778e8b" />

