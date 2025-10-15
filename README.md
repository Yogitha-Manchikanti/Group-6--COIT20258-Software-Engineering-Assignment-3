# 🏥 THS-Enhanced - Telehealth System

**COIT20258 Software Engineering - Assignment 3**  
**Group 6**  


## 🎯 Overview

**THS-Enhanced** is a comprehensive Telehealth System developed as part of COIT20258 Software Engineering course. The system provides a complete healthcare management solution with **client-server architecture**, enabling remote patient monitoring, appointment scheduling, prescription management, and administrative functions.

### What's New in Assignment 3

- ✅ **3-Tier Client-Server Architecture** (TCP/IP)
- ✅ **MySQL Database Integration**
- ✅ **Remote Vital Signs Monitoring**
- ✅ **Prescription Refill Management**
- ✅ **Doctor Unavailability Management** (Extra Feature)
- ✅ **Password Encryption/Decryption** (Base64 Security)
- ✅ **Admin Appointment Viewer**
- ✅ **Comprehensive Testing Suite**

---

## ✨ Features

#### 1. **User Management**
- 🔐 Secure login/logout (server-based authentication)
- 👤 Multiple user roles: Patient, Doctor, Administrator
- 📧 Email-based authentication
- 🆕 **New User Signup** (patients can self-register)
- 🔑 **Password Reset** (temporary password reset via username/email)
- 💾 Database-backed user storage
- 🔒 Session management
- 🔐 **Password Encryption/Decryption** (Base64 encoding for data security)

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

#### 8. **TCP Client-Server Architecture**
- 🌐 Multi-threaded TCP server (Port 8080)
- 🔌 Socket-based communication
- 🔄 **15 request types supported:**
  - LOGIN, SIGNUP, RESET_PASSWORD
  - GET_APPOINTMENTS, CREATE_APPOINTMENT, UPDATE_APPOINTMENT, DELETE_APPOINTMENT
  - GET_PRESCRIPTIONS, CREATE_PRESCRIPTION, UPDATE_PRESCRIPTION, REQUEST_REFILL
  - RECORD_VITAL_SIGNS, GET_VITAL_SIGNS
  - GET_UNAVAILABILITIES, CREATE_UNAVAILABILITY, DELETE_UNAVAILABILITY
  - GET_REFERRALS, GET_USERS, PING
- 🔐 Server-side authentication & validation
- 🔀 Dual-mode operation (Local + Server)
- ⚡ Auto-connect on first request

#### 9. **Database Management**
- 🗄️ MySQL integration (ths_enhanced)
- ⚡ HikariCP connection pooling
- 🔍 15 performance indexes
- 📊 6 optimized views
- 🔧 9 stored procedures
- 🔔 2 validation triggers
- � Password encryption (Base64 encoding)
- � Comprehensive setup script

#### 10. **Admin Dashboard**
- 📊 View all system appointments
- 👥 Patient/Doctor name resolution
- 🎨 Color-coded status indicators
- 🔄 Refresh functionality
- 📈 Total count display


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

### Option 1: Running from NetBeans IDE (Recommended)

The recommended way to run the application is directly from NetBeans IDE for development and testing.

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
1. Right-click on `THSServer.java` in the project explorer
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

1. In NetBeans, press **F6** or click the **Run** button
2. The JavaFX client window will open automatically
3. On the login screen, check the box: **☑ Use Server Authentication**
4. Login with default credentials (see User Guide section below)

#### Step 4: Verify Connection

When login is successful, you'll see server logs in the NetBeans output window:
```
✅ New client connected: /127.0.0.1 (Active connections: 1)
📨 Processing request: LOGIN from Client-/127.0.0.1:xxxxx
✅ Login successful for: username
```

---

### Option 2: Using Executable JAR Files (Alternative)

For deployment or when NetBeans is not available, you can use the pre-built executable JAR files.

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

**Using the Server JAR:**
```bash
# Navigate to project directory
cd Group-6--COIT20258-Software-Engineering-Assignment-3

# Run the server JAR
java -jar target/THS-Enhanced-Server.jar
```

**Expected Output:**
```
🚀 THS-Enhanced Server initialized on port 8080
🔄 THS-Enhanced Server starting...
✅ Database connection successful!
📊 Connected to: jdbc:mysql://localhost:3306/ths_enhanced
✅ Server ready for client connections on port 8080
```

#### Step 3: Start the JavaFX Client

**Open a new terminal/command prompt and run:**
```bash
# Navigate to project directory (if not already there)
cd Group-6--COIT20258-Software-Engineering-Assignment-3

# Run the client JAR
java -jar target/THS-Enhanced-Client.jar
```

The JavaFX client window will open automatically.

#### Step 4: Login and Use the Application

1. On the login screen, check the box: **☑ Use Server Authentication**
2. Login with default credentials (see User Guide section below)
3. The client will automatically connect to the server

#### Step 5: Verify Connection

When login is successful, you'll see server logs in the server terminal:
```
✅ New client connected: /127.0.0.1 (Active connections: 1)
📨 Processing request: LOGIN from Client-/127.0.0.1:xxxxx
✅ Login successful for: username
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
2. **Option 1:** Login with existing credentials (see table below)
3. **Option 2:** Create new patient account
   - Click **"Sign Up"** button on login page
   - Fill in required fields:
     - Full Name
     - Email (must be unique)
     - Username (must be unique)
     - Password
     - Confirm Password
   - Click **"Sign Up"**
   - On success: Account created in database with auto-generated patient ID (pat###)
   - Return to login and use your new credentials

### Password Reset

If you forget your password:
1. Click **"Forgot Password?"** on login page
2. Enter your **username** or **email**
3. Click **"Reset Password"**
4. Your password will be reset to: **reset123**
5. Return to login and use: **username + reset123**
6. (Recommended: Change password after logging in)

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
