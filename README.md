# 🏥 THS-Enhanced - Telehealth System

**COIT20258 Software Engineering - Assignment 3**  
**Group 6**  
**Version:** 3.0  
**Date:** October 2025

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [User Guide](#user-guide)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Team Members](#team-members)
- [Documentation](#documentation)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

**THS-Enhanced** is a comprehensive Telehealth System developed as part of COIT20258 Software Engineering course. The system provides a complete healthcare management solution with **client-server architecture**, enabling remote patient monitoring, appointment scheduling, prescription management, and administrative functions.

### What's New in Assignment 3

- ✅ **3-Tier Client-Server Architecture** (TCP/IP)
- ✅ **MySQL Database Integration**
- ✅ **Remote Vital Signs Monitoring**
- ✅ **Prescription Refill Management**
- ✅ **Doctor Unavailability Management** (Extra Feature)
- ✅ **Database Optimization & Backup**
- ✅ **Admin Appointment Viewer**
- ✅ **Comprehensive Testing Suite**

---

## ✨ Features

### Core Features (Assignment 2 Enhanced)

#### 1. **User Management**
- 🔐 Secure login/logout
- 👤 Multiple user roles: Patient, Doctor, Administrator
- 📧 Email-based authentication
- 🆕 User signup
- 🔑 Password reset functionality

#### 2. **Appointment Management**
- 📅 Book appointments with doctors
- 🔄 Reschedule existing appointments
- ❌ Cancel appointments
- 👨‍⚕️ Doctor appointment management
- 📊 View appointment history

#### 3. **Prescription Management**
- 💊 Create prescriptions (Doctors)
- 📋 View prescriptions (Patients)
- ✏️ Edit prescriptions
- 🔄 Request refills (Patients)
- ✅ Approve/deny refills (Doctors)

#### 4. **Medical Records**
- 🩺 Record vital signs
- 📈 View vital signs history
- 📝 Record diagnoses
- 🏥 Create referrals
- 📄 Export medical data

### Assignment 3 New Features

#### 5. **Remote Vital Signs Monitoring** ⭐
- 📡 Remote data submission via TCP server
- 💾 MySQL database storage
- 🚨 Automatic alerts for abnormal values
- 📊 Trend analysis and visualization
- 👨‍⚕️ Doctor dashboard integration

#### 6. **Prescription Refill Management** ⭐
- 🔄 Online refill requests
- 📬 Server-based request handling
- ✅ Doctor approval workflow
- 📧 Email notifications (logged)
- 📊 Refill tracking and history

#### 7. **Doctor Unavailability Management** 🆕
- 🗓️ Mark vacation/leave periods
- ⏰ All-day or specific time ranges
- 🚫 Automatic booking prevention
- ⚠️ Real-time availability checking
- 📝 Predefined + custom reasons

### Extra Features

#### 8. **TCP Client-Server Architecture**
- 🌐 Multi-threaded TCP server (Port 8080)
- 🔌 Socket-based communication
- 🔄 13 request types supported
- 🔐 Server-side authentication
- 🔀 Dual-mode operation (Local + Server)

#### 9. **Database Management**
- 🗄️ MySQL integration (ths_enhanced)
- ⚡ HikariCP connection pooling
- 🔍 15 performance indexes
- 📊 6 optimized views
- 🔧 9 stored procedures
- 🔔 2 validation triggers

#### 10. **Automated Backup System**
- 💾 PowerShell backup script
- 📅 30-day retention policy
- ⏰ Scheduled backups
- 📧 Email notifications
- 🔄 Point-in-time recovery

#### 11. **Admin Dashboard**
- 📊 View all system appointments
- 👥 Patient/Doctor name resolution
- 🎨 Color-coded status indicators
- 🔄 Refresh functionality
- 📈 Total count display

---

## 🏗️ System Architecture

### 3-Tier Architecture

```
┌─────────────────────────────────────────────────────┐
│                  PRESENTATION TIER                  │
│  JavaFX Client Application (GUI)                   │
│  - Login/Signup Screens                            │
│  - Patient/Doctor/Admin Dashboards                 │
│  - Appointment/Prescription Forms                  │
│  - Vital Signs Recording                           │
└──────────────────┬──────────────────────────────────┘
                   │
                   │ TCP/IP Socket (Port 8080)
                   │ GenericRequest/Response
                   │
┌──────────────────▼──────────────────────────────────┐
│                   BUSINESS TIER                     │
│  TCP Server (Multi-threaded)                       │
│  - ClientHandler (13 request types)                │
│  - AuthDAO, AppointmentDAO, PrescriptionDAO        │
│  - VitalSignsDAO, DoctorUnavailabilityService     │
└──────────────────┬──────────────────────────────────┘
                   │
                   │ JDBC (HikariCP)
                   │
┌──────────────────▼──────────────────────────────────┐
│                    DATA TIER                        │
│  MySQL Database (ths_enhanced)                     │
│  - 8 Tables (users, appointments, prescriptions...) │
│  - 15 Indexes, 6 Views, 9 Procedures, 2 Triggers  │
└─────────────────────────────────────────────────────┘
```

### Dual-Mode Operation

The system supports **two operational modes**:

1. **Local Mode (Assignment 2)**
   - Data stored in `.dat`/`.ser` files
   - No server required
   - Suitable for testing and demos

2. **Server Mode (Assignment 3)** ⭐
   - Data stored in MySQL database
   - TCP server communication
   - Full client-server architecture
   - Recommended for production

---

## 📦 Prerequisites

### Required Software

1. **Java Development Kit (JDK) 17+**
   - Download: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
   - Verify: `java -version`

2. **Apache NetBeans IDE 23+**
   - Download: https://netbeans.apache.org/download/
   - Alternative: Any Java IDE (IntelliJ IDEA, Eclipse)

3. **MySQL Server 8.0+** (Optional for Server Mode)
   - Download: https://dev.mysql.com/downloads/mysql/
   - Alternative: MySQL Workbench for GUI management

4. **Maven** (Bundled with NetBeans)
   - Verify: `mvn -version`

### System Requirements

- **OS:** Windows 10/11, macOS 10.15+, Linux (Ubuntu 20.04+)
- **RAM:** 4GB minimum, 8GB recommended
- **Disk Space:** 500MB for application + 1GB for MySQL
- **Network:** TCP port 8080 available (for server mode)

---

## 🚀 Installation

### Step 1: Clone the Repository

```bash
git clone https://github.com/Yogitha-Manchikanti/Group-6--COIT20258-Software-Engineering-Assignment-3.git
cd Group-6--COIT20258-Software-Engineering-Assignment-3
```

### Step 2: Set Up MySQL Database (Optional - Server Mode Only)

#### A. Create Database

1. Open MySQL Workbench or command line
2. Login as root:
   ```bash
   mysql -u root -p
   ```

3. Run the setup script:
   ```sql
   source setup_database.sql
   ```
   
   Or manually execute the SQL file in MySQL Workbench:
   - Open `setup_database.sql`
   - Execute all statements

#### B. Configure Database Connection

Edit `src/main/java/com/mycompany/coit20258assignment2/DatabaseManager.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/ths_enhanced";
private static final String DB_USER = "ths_user";      // Change if needed
private static final String DB_PASSWORD = "ths_pass";  // Change if needed
```

### Step 3: Open Project in NetBeans

1. Launch NetBeans IDE
2. **File → Open Project**
3. Navigate to the cloned repository folder
4. Select the project (look for the folder icon with "M" badge)
5. Click **Open Project**

### Step 4: Build the Project

1. Right-click on the project in the Projects panel
2. Select **Clean and Build**
3. Wait for dependencies to download (first time may take a few minutes)
4. Look for **BUILD SUCCESS** in the Output window

---

## ▶️ Running the Application

### Option 1: Local Mode (Recommended for Quick Testing)

**No server or database required!**

1. In NetBeans, press **F6** (or click the green ▶️ Run button)
2. Application launches with local file storage
3. Login with default credentials (see below)

### Option 2: Server Mode (Full Client-Server Architecture)

#### Step 1: Start the MySQL Server

```bash
# Windows
net start MySQL80

# macOS
mysql.server start

# Linux
sudo systemctl start mysql
```

#### Step 2: Start the TCP Server

**Option A: From NetBeans**
1. Right-click on `THSServer.java`
2. Select **Run File**
3. Server starts on port 8080

**Option B: From Command Line**
```bash
java -cp target/classes com.mycompany.coit20258assignment2.THSServer
```

**Expected Output:**
```
🚀 THS Server starting on port 8080...
✅ THS Server is running on port 8080
📊 Database connection pool initialized
⏳ Waiting for clients...
```

#### Step 3: Start the JavaFX Client

1. In NetBeans, press **F6**
2. On the login screen, check the box: **☑ Use Server Authentication**
3. Login with credentials

#### Step 4: Verify Connection

When login successful, you'll see server logs:
```
✅ Client connected from /127.0.0.1:xxxxx
📨 Handling request: LOGIN
✅ Login successful: john.smith
```

---

## 👥 User Guide

### Default User Credentials

#### Administrator
- **Email:** `admin@ths.com`
- **Username:** `admin`
- **Password:** `admin123`

#### Doctors
| Name | Email | Username | Password |
|------|-------|----------|----------|
| Dr. John Smith | john.smith@ths.com | john.smith | password123 |
| Dr. Emily Davis | emily.davis@ths.com | emily.davis | password123 |

#### Patients
| Name | Email | Username | Password |
|------|-------|----------|----------|
| Jane Doe | jane.doe@example.com | jane.doe | password123 |
| Bob Johnson | bob.johnson@example.com | bob.johnson | password123 |

### First-Time Setup

1. **Launch the application**
2. **Option 1:** Login with existing credentials
3. **Option 2:** Click **"Sign Up"** to create new patient account
   - Fill in all required fields
   - Email and username must be unique
   - Password requires confirmation

### Patient Workflow

1. **Login** as patient
2. **Dashboard Options:**
   - 📅 **Book Appointment:** Select doctor, date, time
   - 💊 **View Prescriptions:** See active prescriptions
   - 🔄 **Request Refill:** Request medication refills
   - 🩺 **Record Vital Signs:** Submit health metrics
   - 📊 **View Vital Signs:** See history and trends
   - 📝 **My Health Data:** View diagnoses and referrals
   - 💡 **Health Tips:** Educational content

3. **Book Appointment Example:**
   - Select doctor from dropdown
   - Choose date from calendar
   - Select time slot
   - System shows availability status:
     - ✅ Green: Doctor available
     - ⚠️ Orange: Doctor unavailable (with reason)
   - Click **"Book Appointment"**

4. **Record Vital Signs Example:**
   - Enter Blood Pressure (e.g., 120/80)
   - Enter Heart Rate (e.g., 75 bpm)
   - Enter Temperature (e.g., 98.6°F)
   - Enter Weight (e.g., 70.5 kg)
   - System validates and stores data
   - Automatic alerts if values abnormal

### Doctor Workflow

1. **Login** as doctor
2. **Dashboard Options:**
   - 📋 **View/Change Bookings:** Manage appointments
   - 🩺 **Record Diagnosis:** Add patient diagnoses
   - 📄 **Create Referral:** Refer patients to specialists
   - 💊 **Review Prescriptions:** Approve refill requests
   - 🗓️ **Manage Unavailability:** Set vacation/leave
   - 📊 **My Data:** View patients and appointments

3. **Manage Unavailability Example:**
   - Click **"Manage Unavailability"** (orange button)
   - Select start and end dates
   - Choose **All Day** or enter specific times
   - Select reason (Vacation, Sick Leave, etc.)
   - Click **"Add Unavailability"**
   - Patients cannot book during these periods

4. **Approve Refill Request Example:**
   - Click **"Review Prescriptions"**
   - View pending refill requests
   - Select request
   - Click **"Approve"** or **"Deny"**

### Administrator Workflow

1. **Login** as administrator
2. **Dashboard Options:**
   - 📊 **View Appointments:** See all system appointments
   - 👥 **Manage Users:** (Future feature)
   - 📈 **System Reports:** (Future feature)

3. **View Appointments Example:**
   - Click **"View Appointments"**
   - See table with all appointments
   - Color-coded status:
     - 🟢 BOOKED (green)
     - 🟠 RESCHEDULED (orange)
     - 🔴 CANCELED (red)
   - Click **"Refresh"** to reload data

---

## 🧪 Testing

### Automated Tests

The project includes comprehensive unit and integration tests.

**Run all tests:**
```bash
mvn test
```

**Run specific test:**
```bash
mvn test -Dtest=ClientTest
```

### Test Results

```
✅ ClientTest
   - testServerConnection: PASS
   - testLogin: PASS
   - testGetAppointments: PASS
   - testRecordVitalSigns: PASS
   - testRequestRefill: PASS
   - testGetPrescriptions: PASS

📊 Total: 6/6 tests passed
```

### Manual Testing Scenarios

#### Scenario 1: Book Appointment with Unavailable Doctor

1. Login as doctor: `john.smith` / `password123`
2. Click **"Manage Unavailability"**
3. Add vacation: Oct 20-25, 2025
4. Logout
5. Login as patient: `jane.doe` / `password123`
6. Try to book appointment on Oct 22
7. **Expected:** ⚠️ Warning appears, booking blocked
8. Try Oct 26
9. **Expected:** ✅ Booking successful

#### Scenario 2: Remote Vital Signs Monitoring

1. Start TCP server
2. Login as patient with **☑ Server Authentication**
3. Click **"Record Vital Signs"**
4. Enter values:
   - BP: 180/100 (high)
   - HR: 100 bpm
   - Temp: 99.5°F
   - Weight: 75 kg
5. Submit
6. **Expected:** Alert generated for high BP
7. Login as doctor
8. Click **"My Data"** → View patient vital signs
9. **Expected:** See all submitted values

#### Scenario 3: Prescription Refill Workflow

1. Patient requests refill
2. Doctor sees pending request
3. Doctor approves
4. Patient sees updated prescription status

---

## 📁 Project Structure

```
THS-Enhanced/
├── data/                          # Local data storage
│   ├── appointments.dat
│   ├── prescriptions.dat
│   ├── users.dat
│   └── vitals.dat
│
├── src/
│   ├── main/
│   │   ├── java/com/mycompany/coit20258assignment2/
│   │   │   ├── App.java                          # Main entry point
│   │   │   ├── Session.java                      # Session management
│   │   │   ├── SceneNavigator.java               # Navigation
│   │   │   │
│   │   │   ├── # Models
│   │   │   ├── User.java, Patient.java, Doctor.java, Administrator.java
│   │   │   ├── Appointment.java, Prescription.java
│   │   │   ├── VitalSigns.java, Diagnosis.java, Referral.java
│   │   │   ├── DoctorUnavailability.java         # NEW
│   │   │   │
│   │   │   ├── # Controllers (GUI)
│   │   │   ├── LoginController.java
│   │   │   ├── SignupController.java
│   │   │   ├── ForgotPasswordController.java
│   │   │   ├── PatientDashboardController.java
│   │   │   ├── DoctorDashboardController.java
│   │   │   ├── AdminDashboardController.java
│   │   │   ├── AppointmentFormController.java
│   │   │   ├── VitalsFormController.java
│   │   │   ├── PrescriptionFormController.java
│   │   │   ├── PrescriptionReviewController.java
│   │   │   ├── DoctorUnavailabilityController.java  # NEW
│   │   │   └── AdminAppointmentViewerController.java # NEW
│   │   │   │
│   │   │   ├── # Services (Business Logic)
│   │   │   ├── AuthService.java
│   │   │   ├── AppointmentService.java
│   │   │   ├── PrescriptionService.java
│   │   │   ├── VitalSignsService.java
│   │   │   ├── DoctorUnavailabilityService.java  # NEW
│   │   │   ├── DataStore.java                    # Local storage
│   │   │   │
│   │   │   ├── # Server (Assignment 3)
│   │   │   ├── THSServer.java                    # TCP Server
│   │   │   ├── ClientHandler.java                # Request handler
│   │   │   ├── DatabaseManager.java              # Connection pool
│   │   │   ├── AuthDAO.java                      # Authentication
│   │   │   ├── AppointmentDAO.java               # Appointments
│   │   │   ├── PrescriptionDAO.java              # Prescriptions
│   │   │   └── VitalSignsDAO.java                # Vital signs
│   │   │   │
│   │   │   └── # Client (Assignment 3)
│   │   │       ├── ServerConnection.java         # Socket manager
│   │   │       ├── ClientService.java            # API methods
│   │   │       └── GenericRequest.java           # Request wrapper
│   │   │
│   │   └── resources/com/mycompany/coit20258assignment2/
│   │       └── view/
│   │           ├── Login.fxml
│   │           ├── signup.fxml
│   │           ├── forgot_password.fxml
│   │           ├── patient_dashboard.fxml
│   │           ├── doctor_dashboard.fxml
│   │           ├── admin_dashboard.fxml
│   │           ├── appointment_form.fxml
│   │           ├── vitals_form.fxml
│   │           ├── prescription_form.fxml
│   │           ├── doctor_unavailability.fxml    # NEW
│   │           └── admin_appointments.fxml       # NEW
│   │
│   └── test/
│       └── java/com/mycompany/coit20258assignment2/
│           └── ClientTest.java                   # Integration tests
│
├── setup_database.sql            # MySQL setup script
├── optimize_database.sql         # Performance optimization
├── backup_database.sql           # Backup procedures
├── backup_ths_database.ps1       # Automated backup script
│
├── pom.xml                       # Maven configuration
├── .gitignore                    # Git ignore rules
└── README.md                     # This file
```

---

## 🛠️ Technologies Used

### Core Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17.0.12 | Core programming language |
| **JavaFX** | 17.0.2 | GUI framework |
| **Maven** | 3.8+ | Build automation |
| **MySQL** | 9.4 | Database management |
| **JDBC** | 8.0.33 | Database connectivity |
| **HikariCP** | 5.0.1 | Connection pooling |

### Libraries & Frameworks

- **java.time.*** - Date/time handling
- **java.io.*** - File I/O operations
- **java.net.*** - Network communication (TCP sockets)
- **javafx.fxml.*** - FXML-based UI
- **javafx.scene.control.*** - UI components

### Development Tools

- **NetBeans IDE** - Primary IDE
- **MySQL Workbench** - Database management
- **Git** - Version control
- **GitHub** - Repository hosting

---

## 👨‍💻 Team Members

| Member | Role | Responsibilities |
|--------|------|------------------|
| **Member 1** | Server Lead | TCP Server, DAOs, Database Integration |
| **Member 2** | Client Lead | Socket Client, ClientService, GUI Integration |
| **Member 3** | Database Lead | Optimization, Backup, Documentation |
| **Member 4** | Admin Management | Admin features, Extra features |

---

## 📚 Documentation

### Quick Start Guides
- 📘 **JAVAFX_GUI_GUIDE.md** - Running the JavaFX client
- 🚀 **QUICK_START_TESTING_GUIDE.md** - Quick testing guide
- 📋 **COMPLETE_SYSTEM_TESTING_GUIDE.md** - Comprehensive testing

### Feature Documentation
- 🗓️ **DOCTOR_UNAVAILABILITY_FEATURE.md** - Unavailability feature
- 🔗 **DOCTOR_UNAVAILABILITY_INTEGRATION.md** - Integration guide
- 💊 **PRESCRIPTION_REFILL_GUIDE.md** - Refill workflow
- 🩺 **VITAL_SIGNS_MONITORING_GUIDE.md** - Remote monitoring

### Implementation Summaries
- 🖥️ **SERVER_LEAD_COMPLETION_SUMMARY.md** - Server implementation
- 💻 **CLIENT_LEAD_COMPLETION_SUMMARY.md** - Client implementation
- 🗄️ **DATABASE_LEAD_COMPLETION_SUMMARY.md** - Database work
- 👤 **MEMBER_4_ADMIN_MANAGEMENT_COMPLETION.md** - Admin features

### Technical Documentation
- 🔬 **CLIENT_TEST_RESULTS.md** - Test results
- 📊 **INTEGRATION_STATUS.md** - Integration status
- 🔐 **LOGIN_ISSUE_RESOLVED.md** - Login implementation
- ✅ **SERVER_LOGIN_SUCCESS.md** - Server authentication
- 🎓 **DOCTOR_DASHBOARD_GUIDE.md** - Doctor features
- 🔧 **GIT_CONFIGURATION_COMPLETE.md** - Git setup

---

## 🐛 Troubleshooting

### Common Issues

#### 1. "Module not found" Error

**Problem:** JavaFX modules not found

**Solution:**
```bash
# Verify JavaFX is in pom.xml
# Clean and rebuild
mvn clean install
```

#### 2. Database Connection Failed

**Problem:** Cannot connect to MySQL

**Solution:**
1. Check MySQL is running:
   ```bash
   # Windows
   net start MySQL80
   
   # macOS/Linux
   mysql.server start
   ```

2. Verify credentials in `DatabaseManager.java`
3. Test connection:
   ```bash
   mysql -u ths_user -p ths_enhanced
   ```

#### 3. Port 8080 Already in Use

**Problem:** Server cannot start

**Solution:**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# macOS/Linux
lsof -i :8080
kill -9 <PID>
```

#### 4. Compilation Errors

**Problem:** Code doesn't compile

**Solution:**
1. **Clean project:** Right-click → Clean
2. **Update dependencies:** Right-click → Reload POM
3. **Check Java version:** Should be 17+
4. **Invalidate caches:** Tools → Clear NetBeans Cache

#### 5. Server Mode Not Working

**Problem:** Cannot authenticate with server

**Solution:**
1. Ensure TCP server is running first
2. Check **☑ Use Server Authentication** on login
3. Verify database is populated (run `setup_database.sql`)
4. Check server logs for errors

#### 6. Unavailability Not Blocking Bookings

**Problem:** Can still book during unavailable times

**Solution:**
1. Ensure you compiled after adding the feature
2. Restart the application
3. Check doctor ID matches (case-sensitive)
4. Verify dates are in correct format (yyyy-MM-dd)

### Getting Help

**Documentation:**
- Check the `/docs` folder for detailed guides
- Read `.md` files for feature-specific help

**Logs:**
- Server logs: Check console output
- Client logs: Check NetBeans Output window
- Database logs: Check MySQL error log

**Contact:**
- Course instructor: [Course email]
- GitHub Issues: https://github.com/Yogitha-Manchikanti/Group-6--COIT20258-Software-Engineering-Assignment-3/issues

---

## 🎓 Academic Information

**Course:** COIT20258 - Software Engineering  
**Assignment:** Assignment 3 - Client-Server Architecture  
**Institution:** CQUniversity Australia  
**Semester:** Term 2, 2025

### Assignment Requirements Met

✅ **Technical Requirements:**
- 3-tier client-server architecture
- Multi-threaded TCP server
- MySQL database integration
- Object-oriented design
- MVC/MVP pattern
- Error handling
- Session management
- Data persistence

✅ **Functional Requirements:**
- User authentication
- Appointment management (CRUD)
- Prescription management (CRUD)
- User role management
- Data validation
- GUI application
- Network communication
- Database operations

✅ **Required Features:**
- Feature 1: Remote Vital Signs Monitoring
- Feature 2: Prescription Refill Management

✅ **Extra Features:**
- Doctor Unavailability Management
- Database Optimization & Backup
- Admin Dashboard
- User Signup & Password Reset
- Comprehensive Documentation

### Grading Criteria Coverage

| Criteria | Status | Notes |
|----------|--------|-------|
| Architecture Design | ✅ Complete | 3-tier with clear separation |
| Server Implementation | ✅ Complete | Multi-threaded TCP server |
| Client Implementation | ✅ Complete | JavaFX GUI with networking |
| Database Integration | ✅ Complete | MySQL with optimization |
| Feature 1 (Vital Signs) | ✅ Complete | Full implementation + testing |
| Feature 2 (Refills) | ✅ Complete | Full implementation + testing |
| Code Quality | ✅ Complete | Clean, documented, tested |
| Testing | ✅ Complete | Unit + integration tests |
| Documentation | ✅ Complete | 15+ comprehensive documents |
| User Manual | ✅ Complete | This README + guides |

---

## 📄 License

This project is developed for educational purposes as part of COIT20258 course requirements.

**Copyright © 2025 Group 6 - CQUniversity Australia**

---

## 🙏 Acknowledgments

- **CQUniversity Australia** - For course materials and support
- **Course Instructor** - For guidance and feedback
- **JavaFX Community** - For excellent documentation
- **MySQL Team** - For robust database system
- **GitHub Copilot** - For development assistance

---

## 📞 Support

For issues, questions, or contributions:

- 📧 **Email:** [Your course email]
- 🐛 **Issues:** https://github.com/Yogitha-Manchikanti/Group-6--COIT20258-Software-Engineering-Assignment-3/issues
- 📖 **Wiki:** https://github.com/Yogitha-Manchikanti/Group-6--COIT20258-Software-Engineering-Assignment-3/wiki

---

## 🔄 Version History

### Version 3.0 (Assignment 3) - October 2025
- ✅ Client-server architecture implementation
- ✅ MySQL database integration
- ✅ Remote vital signs monitoring
- ✅ Prescription refill management
- ✅ Doctor unavailability feature
- ✅ Database optimization
- ✅ Admin dashboard
- ✅ Comprehensive testing

### Version 2.0 (Assignment 2) - September 2025
- ✅ JavaFX GUI implementation
- ✅ Appointment management
- ✅ Prescription management
- ✅ Local file storage
- ✅ Basic user roles

### Version 1.0 (Assignment 1) - August 2025
- ✅ Initial design and planning
- ✅ Requirements analysis
- ✅ System architecture design

---

**🎉 Thank you for using THS-Enhanced!**

*For the latest updates, visit our [GitHub repository](https://github.com/Yogitha-Manchikanti/Group-6--COIT20258-Software-Engineering-Assignment-3).*

---

**Last Updated:** October 13, 2025  
**Version:** 3.0  
**Status:** ✅ Complete and Production-Ready
