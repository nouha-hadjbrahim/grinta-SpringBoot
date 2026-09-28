# 🏆 GRINTA - Backend (Spring Boot)

The core backend repository for the **Grinta** Sports Management Platform. 

This repository contains a robust **Spring Boot REST API** that manages the database, processes complex business logic, handles security & authentications, and communicates with various custom Machine Learning microservices (Python) to deliver advanced analytical features.

## 🚀 Features & Modules

The platform is divided into highly modularized domains:

*   **👥 Authentication & Core:** Google/Facebook social logins, JWT-based security, role-based access control (Admin, User, Coach, Gym Manager, Field Manager), and customized email recovery flows.
*   **🏋️‍♂️ Coaching & Workouts:** Complete coach application and rating system, training program enrollment, calendar scheduling, video session (Google Meet) integration, and interactive workout performance dashboards.
*   **🏟️ Gyms & Bookings:** Dynamic pricing allocations, Sport Terrain reservations, Gym Manager dashboards (with mapped working hours and equipment tracking), Stripe & Cryptocurrency payment processing, and ticket QR code generation.
*   **🥗 Nutrition:** Comprehensive meal planning, macro tracking, health profile evaluations, and automated meal scanning powered by external Gemini integrations.
*   **🏆 Events & Community:** Tournaments, event registrations, interactive social feeds (Likes, comments, threaded replies), live notifications, and real-time chat (WebSocket/STOMP).
*   **🧠 Machine Learning Microservices (Python):** 
    *   Predictive AI matching the best applicants for coaching jobs.
    *   Face Recognition routines for the Workout module.
    *   AI Calorie Estimation and intelligent ingredient substitution models.

---

## 🏗️ Project Architecture

The Java codebase strictly adheres to the **Separation of Concerns** principle:
*   `/Controller`: Exposes the HTTP REST API endpoints.
*   `/Services`: Contains all core business logic and ML service integrations.
*   `/Repository`: Interfaces handling direct database interactions (Spring Data JPA).
*   `/Entities`: Database table representations.
*   `/DTOs`: Data Transfer Objects for safely passing payloads between the client and server.
*   `/Security`: JWT Filters, authentication routines, and CORS configuration.
*   `/Exception`: Global exception handling (`@ControllerAdvice`) mapping internal errors to clean HTTP responses.

---

## 🛠️ Getting Started

### 1. Prerequisites
- **Java 17+**
- **Maven**
- **MySQL** (Running on port `3300`)
- **Python 3.9+** (For the internal ML Services)

### 2. Database Setup
Ensure you have a local MySQL instance running on port `3300` and create a blank database named `sport`:
```sql
CREATE DATABASE sport;
```

### 3. Clone & Build the Spring Boot App
```bash
git clone https://github.com/Mustapha-who/Grinta-springboot.git
cd Grinta-springboot

# Resolve dependencies
mvn clean install
```

### 4. Running the Application

**Via IntelliJ IDEA:**
- Sync Maven projects.
- Locate the main application class.
- Click the green ▶ **Run** button.

**Via Terminal (VS Code):**
```bash
mvn spring-boot:run
```
*The primary server will start on `http://localhost:8000`.*

---

## 🤖 Running the Machine Learning Services

Grinta uses local Python microservices to run specific predictive models internally (located in `/coach_ml` and `/ml-services`).

**1. Setup the Python Environment:**
```bash
# Navigate to the specific ML directory (e.g., coach_ml)
cd coach_ml

# Create and activate a Virtual Environment
python -m venv venv
venv\Scripts\activate  # On Windows
# source venv/bin/activate # On Mac/Linux

# Install dependencies
pip install -r requirements.txt
```
