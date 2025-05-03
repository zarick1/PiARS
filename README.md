# Shopping List

# Overview
Shopping List is an Android application developed in Java using the Android SDK, designed for creating and managing personal and shared shopping lists. Users can register, log in, create lists, add tasks (items), mark tasks as completed, and synchronize data with a remote MongoDB/Express server. The app leverages SQLite for offline storage, HTTP for server communication, and includes JNI for potential native code integration. It features a user-friendly interface with fragments, background services, and notifications.

# Features

User Authentication: Register and log in with username, email, and password, validated both locally (SQLite) and via server (REST API).
List Management: Create, view, and delete shopping lists, with options for private or shared lists.
Task Management: Add, mark as completed (with strikethrough), and delete tasks within lists.
Local Storage: Store users, lists, and tasks in a SQLite database for offline functionality.
Server Synchronization: Automatically sync local data with a MongoDB/Express server every 30 seconds, with notifications.
Notifications: Display synchronization status via Android notifications.
List Sharing: View shared lists and their tasks, with real-time updates via server.
UI Navigation: Use fragments for seamless transitions between login, registration, list creation, and task management.
Native Integration: Optional JNI support for native C/C++ code (e.g., increment function).
Responsive Design: Optimized for various Android devices.

# Technologies

Java & Android SDK: Core language and framework for Android development.
SQLite: Local database for offline storage of users, lists, and tasks.
HTTP (REST API): Communication with a MongoDB/Express backend for data synchronization.
JSON: Data format for server communication.
JNI (Java Native Interface): Optional integration with native C/C++ code.
Android Fragments: UI components for login, registration, and list/task views.
Android Service: Background service for periodic data synchronization.
Notifications: Android Notification API for synchronization feedback.
Javadoc: Comprehensive code documentation for maintainability.

# Usage

Register/Login:
Open the app and select "Register" to create a new account with username, email, and password.
Select "Login" to access the app with existing credentials.


Create Lists:
From the welcome screen, click "New List" to create a list.
Enter a name and choose whether the list is shared (via radio buttons).
Save to add the list locally and (if shared) to the server.


Manage Lists:
View all accessible lists ("See My Lists" for personal, "See Shared Lists" for server-shared lists).
Long-press a list to delete it (if you are the creator).
Click a list to view its tasks.


Manage Tasks:
In a list view, add tasks by entering a name and clicking "Add".
Check tasks to mark them as completed (strikethrough text).
Long-press a task to delete it.
For shared lists, click "Refresh" to sync tasks from the server.


Synchronization:
The app syncs local data with the server every 30 seconds (via MyService).
Notifications confirm synchronization status.


Logout:
Click the "Home" button in any activity to return to the login screen.


