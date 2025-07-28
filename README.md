# Kitchensink Application

A modernized reimplementation of the classic **Kitchensink** application using **Spring Boot**, **MongoDB**, and **Thymeleaf**. Originally built on **JBoss** and **JSF**, this version leverages a contemporary tech stack for improved performance, scalability, and maintainability.

This full-stack web application offers user registration and role-based access management via a secure and intuitive interface. It includes a dedicated **Admin Portal**, login/logout functionality, and user profile support.

---

## 📌 Table of Contents

1. [Prerequisites](#prerequisites)  
2. [Getting Started](#getting-started)  
3. [Features](#features)  
4. [API Endpoints](#api-endpoints)  
5. [Technology Stack](#technology-stack)
6. [Dummy Users](#dummy-users)

---

## ✅ Prerequisites

Make sure the following tools are installed in your development environment:

- **Java 21 JDK** – Required for compiling and running the application  
- **Maven** – Used for building and managing project dependencies  
- **MongoDB** – NoSQL database used for persistent storage  
- **MongoDB Compass** *(optional)* – GUI tool for inspecting MongoDB data  
- **Git** – To clone the repository  
- **Thymeleaf** – Templating engine for dynamic frontend rendering  
- **Spring Security** – For authentication and role-based access  

---

## 🚀 Getting Started

Follow these steps to run the project locally:

### 1. Clone the Repository

```bash
git clone git@github.com:heygauravjain/modfac-kitchensink.git
````

### 2. Build the Application

Navigate to the project directory and build the application using Maven:

```bash
mvn clean install
```

### 3. Configure Environment Variables

Create a file named `creds.env` in the `src/main/resources` directory. Include the following:

* MongoDB connection URI
* JWT secret key
* Any additional required environment variables

⚠️ Ensure the file is included in your run/debug configuration, but **excluded from version control**.

### 4. Run the Application

You can use your IDE or the following Maven command to run the app:

```bash
mvn spring-boot:run
```

### 5. Access the Application

Open your browser and navigate to:

```
http://localhost:8080/login
```

Log in using the Admin credentials.

### 6. Admin Home Page

After logging in, you will be redirected to the Admin dashboard where you can:

* Register new members
* Edit or delete existing users
* View detailed member information

---

## 🧩 Features

* Secure login/logout with JWT
* Admin portal for managing members
* Role-based access control (Admin/User)
* User profile management
* Clean, responsive UI using Thymeleaf

---

## 📡 API Endpoints

| Method   | Endpoint              | Description                     | Access |
| -------- | --------------------- | ------------------------------- | ------ |
| `GET`    | `/admin/members`      | Retrieve all registered members | ADMIN  |
| `GET`    | `/admin/members/{id}` | Retrieve member by ID           | ADMIN  |
| `POST`   | `/admin/members`      | Register a new member           | ADMIN  |
| `PUT`    | `/admin/members/{id}` | Update member information       | ADMIN  |
| `DELETE` | `/admin/members/{id}` | Delete a member by ID           | ADMIN  |
| `GET`    | `/user-profile`       | View profile of logged-in user  | USER   |

---

## 🛠️ Technology Stack

* **Backend**: Spring Boot, Spring Security, Spring Data MongoDB
* **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
* **Database**: MongoDB
* **Testing**: JUnit, Mockito
* **Build Tool**: Maven
* **Security**: JWT authentication, role-based authorization

## 👥 Dummy Users
#### 👩‍💼 Admin User
- **Email:** `admin@admin.com`  
- **Role:** `ADMIN`  
- **Password:** `admin123`

#### 👨‍💼 Regular User
- **Email:** `user@user.com`  
- **Role:** `USER`  
- **Password:** `user123`


> Feel free to contribute or raise issues via GitHub if you'd like to enhance the project!
