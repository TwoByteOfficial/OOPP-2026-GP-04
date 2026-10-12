# UniMIS - Faculty Academic Management System

A Java-based desktop application developed for the **ICT2132 - Object Oriented Programming Practicum** at the **University of Ruhuna, Faculty of Technology**.

## 📌 Project Description

The Faculty Academic Management System is designed to manage academic activities within the Faculty of Technology.

The system provides different features based on the user's role.

## 👥 User Roles

* **Admin**
* **Lecturer**
* **Technical Officer**
* **Undergraduate**

## ⚙️ Main Features

* User Login & Authentication
* User Profile Management
* Course Management
* Course Materials
* Marks Management
* Eligibility & Grading
* Attendance Management
* Medical Record Management
* Notice Management
* Timetable Management
* SGPA / CGPA Calculation

## 🛠️ Technologies Used

* Java
* Java Swing
* FlatLaf
* MySQL
* JDBC
* IntelliJ IDEA

## 📁 Project Structure

```text
FacultyAcademicManagementSystem/
│
├── src/
│   ├── main/
│   ├── model/
│   ├── dao/
│   ├── gui/
│   ├── service/
│   └── util/
│
├── lib/
│   └── flatlaf.jar
│
├── database/
│   └── UniMIS1.sql
│
├── docs/
│
└── README.md
```

## 💻 Requirements

* JDK 17 or later
* MySQL Server
* IntelliJ IDEA
* FlatLaf 3.6.2

## 🚀 Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
```

### 2. Open the Project

Open the project using **IntelliJ IDEA**.

### 3. Add FlatLaf

Add the following JAR file to the project:

```text
lib/flatlaf.jar
```

### 4. Setup MySQL Database

Create the database and import:

```text
database/UniMIS1.sql
```

### 5. Configure Database Connection

Update the MySQL connection details in:

```text
src/util/DBConnection.java
```

### 6. Run the Application

Run:

```text
src/main/Main.java
```

## 🗄️ Database

The application uses **MySQL** as the database management system.

Database operations are handled using **JDBC**.

## 🎨 User Interface

The application uses **Java Swing** with **FlatLaf** to create a modern desktop interface.

## 📚 Course Information

**Course:** ICT2132 - Object Oriented Programming Practicum

**Degree:** Bachelor of Information and Communication Technology

**Faculty:** Faculty of Technology

**University:** University of Ruhuna

## 👨‍💻 Development Team

TG/2024/2067 - Dasindu Dilvan

TG/2024/2106 - Praveen Sandeepa

TG/2024/2107 - Dulsha Hemini

TG/2024/2120 - T.V.K. Sendiya

Bachelor of Information and Communication Technology

Faculty of Technology - University of Ruhuna

---

**ICT2132 - OOP Practicum Mini Project**

