# Messaging App

Hi! My name's Jake Marquardt and this is an android application where users can chat with their friends. I'm making it as a side project for my resume/portfolio so I don't have any plans to put it on the Google Play Store at the moment, but I will provide the APK here once I have the minimum functionality that I'd like to have in my first version.

(Note: Instructions in this document on how to perform certain functions within the app may be outdated at times as I'm making rapid changes)

# Table of Contents
- [Installing the application](#installing-the-application)
- [Creating an account](#creating-an-account)
- [Contacts](#contacts)
    - [Adding a new contact](#adding-a-new-contact)
    - [Accepting a contact request](#accepting-a-contact-request)
    - [Deleting a contact](#deleting-a-contact)
- [Chats](#chats)
    - [Creating a chat room](#creating-a-chat-room)
    - [Adding or removing users from a chat room](#adding-or-removing-users-from-a-chat-room)
    - [Deleting a chat room](#deleting-a-chat-room)
- [Coming soon<sup>tm](#coming-soontm)
- [API Info](#api-info)

# Installing the application

Download the APK: coming soon<sup>tm</sup>

To install the APK on your device, follow the instruction found here:<br>
https://www.wikihow.com/Install-APK-Files-from-a-PC-on-Android

All jokes aside, I have plans for a home page and a weather page and I'm debating on scrapping them as I'm not sure what purpose they really serve. Once I decide and implement a couple more things, I'll put the APK up for download.<br>
<br>

# Creating an account

- Open application
- Tap "Register"
- Fill out registration form according the the following constraints:

  - First and last name can only contain letters and must be between 1 and 20 characters in length
  - Username can only contain letters and numbers and must be between 1 and 30 characters in length
  - Password must:

    - Contain at least 1 uppercase letter
    - Contain at least 1 number
    - Contain at least 1 special character: !@#$%^&*()-_=+[]{};:'",.<>/?`~\\|
    - Be between 8 and 50 characters in length
- Tap "Register"

# Contacts

### Adding a new contact

- Go to Contacts page
- Tap options menu in action bar (top right of screen)
- Tap "Add new contact"
- Enter the username/email/first or last name of the user you wish to add as a contact in the text field
- Tap the search button
- Find the user you want to add from the search results
- Tap the '+' icon on the user
- Click Yes/OK on the pop-up to confirm that you want to add the user
- Wait for them to accept your request!

### Accepting a contact request

- Go to Contacts page
- Go to requests tab
- Click the '&#10004;' icon on the user whose request you wish to accept
- Click Yes/OK on the pop-up to confirm that you want to add the user

### Deleting a contact

- Go to Contacts page
- Find contact you wish to delete
- Tap options menu on contact
- Tap "Delete contact"
- Click Yes/OK on the pop-up to confrim that you want to delete the contact

# Chats

### Creating a chat room

- Go to Chats page
- Tap options menu in action bar (top right of screen)
- Tap "Add chat room"
- Enter desired name of chat room in the text field provided in the pop-up and tap Add

### Adding or removing users from a chat room

- Go to Chats page
- Tap options menu of chat room you wish to add/remove user from
- To add user:
  - Tap "Add user to chat room"
  - Enter user's username or email in the text field provided in the pop-up and tap Add
- To remove user:
  - Tap "Remove user from chat room"
  - Enter user's username or email in the text field provided in the pop-up and tap Remove

### Deleting a chat room

- Go to Chats page
- Tap options menu of chat room you wish to delete
- Tap "Delete chat room"
- Tap Yes/OK on the pop-up to confirm you wish to delete the chat room

# Coming soon<sup>tm

- Version 1.0 APK
- Email verification
- Forgot/change password
- Ability to stay signed in after exiting the application
- Ability to transfer ownership of chat rooms

# API Info

I created a collection of API endpoints for the back-end of this app using NodeJS/Express. For the source code and/or documentation of these endpoints, go to: <br>
https://github.com/JakeAMarq/MessagingAppServer
