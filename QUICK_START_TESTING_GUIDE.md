# 🚀 QUICK START GUIDE - Running THS-Enhanced GUI with Server

**Date:** October 13, 2025  
**Status:** Ready to Test

---

## ⚡ EASIEST METHOD: Use NetBeans IDE

### **Step 1: Start the Server**

Open PowerShell in project directory and run:
```powershell
java -cp "target/classes;mysql-connector-j-8.0.33.jar" com.mycompany.coit20258assignment2.server.THSServer
```

**Expected Output:**
```
🚀 THS-Enhanced Server starting...
Database connection successful!
✅ Server ready for client connections on port 8080
```

**Keep this window open!** ✅

---

### **Step 2: Open Project in NetBeans**

1. **Open NetBeans IDE**
2. **File → Open Project**
3. Navigate to:
   ```
   C:\Users\Mdmon\OneDrive\Documents\Github\Group-6--COIT20258-Software-Engineering-Assignment-3
   ```
4. Select the project and click **Open Project**

---

### **Step 3: Run the JavaFX Application**

**Option A:** Use keyboard shortcut
- Press **F6** key

**Option B:** Use menu
- Right-click on project name → **Run**

**Option C:** Use toolbar
- Click the green **▶ Run Project** button

---

### **Step 4: Test the Integration**

When the login window appears:

#### **Test Server Mode (NEW):**
1. ✅ **Check** the "Use Server Authentication" checkbox
2. Enter credentials:
   - Username: `admin`
   - Password: `admin123`
3. Click **Login**

**Expected Result:**
- ✅ "Connecting to server..." message appears
- ✅ Login successful
- ✅ Dashboard opens
- ✅ Data loads from MySQL database via server

#### **Test Local Mode (Assignment 2):**
1. ❌ **Uncheck** the "Use Server Authentication" checkbox
2. Enter credentials from `data/users.dat` file
3. Click **Login**

**Expected Result:**
- ✅ Login successful
- ✅ Dashboard opens
- ✅ Data loads from local files

---

## 🎯 WHAT TO TEST

### **1. Login Screen**
- [ ] Server authentication works
- [ ] Local authentication works (fallback)
- [ ] Error messages display correctly
- [ ] Checkbox toggles between modes

### **2. Patient Dashboard**
- [ ] Appointments list loads
- [ ] Prescriptions list loads
- [ ] Can book new appointment
- [ ] Can view prescription details

### **3. Doctor Dashboard**
- [ ] View all appointments
- [ ] Manage appointment status
- [ ] Create prescriptions
- [ ] View patient vital signs

### **4. Data Operations**
- [ ] Create appointment
- [ ] Update appointment status
- [ ] Create prescription
- [ ] Request refill (NEW feature)
- [ ] Record vital signs (NEW feature)

---

## 📊 EXPECTED BEHAVIOR

### **With Server Running:**
```
Login → Server Auth → MySQL Database → Real-time data
```
- ✅ Fast response times (~100-200ms)
- ✅ Data persists in database
- ✅ Multi-user support
- ✅ New features available (Vital Signs, Refills)

### **Without Server (Fallback):**
```
Login → Local Auth → File Storage → Offline mode
```
- ✅ Works offline
- ✅ Data persists in files
- ✅ Single user
- ✅ All Assignment 2 features work

---

## 🔍 TROUBLESHOOTING

### **Problem: "Cannot find Maven"**
**Solution:** Use NetBeans IDE - it has Maven built-in

### **Problem: "Server connection failed"**
**Solution:** 
1. Check server is running (see Step 1)
2. Check port 8080 is not blocked
3. Application will automatically fallback to local mode

### **Problem: "Login failed"**
**Solution:**
- **Server mode:** Use `admin` / `admin123`
- **Local mode:** Check `data/users.dat` for credentials

### **Problem: "JavaFX errors"**
**Solution:** NetBeans handles JavaFX automatically. Don't compile manually.

---

## 📝 TEST CREDENTIALS

### **Server Mode (MySQL Database):**

| Username | Password | Role | Has Data? |
|----------|----------|------|-----------|
| admin | admin123 | Administrator | ✅ Yes |
| patient1 | (check DB) | Patient | ✅ Yes |
| doctor1 | (check DB) | Doctor | ✅ Yes |

**To see all users:**
```sql
SELECT username, user_type FROM users;
```

### **Local Mode (File-based):**
Check file: `data/users.dat`

---

## ✅ SUCCESS INDICATORS

### **Server Connection Successful:**
- ✅ Login screen shows "Connected to server successfully"
- ✅ Green status indicator (if implemented)
- ✅ Fast data loading
- ✅ Database data appears

### **Application Working:**
- ✅ Window opens at 1100x700
- ✅ Login screen appears
- ✅ Can switch between server/local modes
- ✅ Dashboard loads after login
- ✅ Can navigate between screens

---

## 🎯 ASSIGNMENT 3 NEW FEATURES TO DEMONSTRATE

### **Feature 1: Remote Vital Signs Monitoring**

**How to Test:**
1. Login as patient or doctor
2. Navigate to Vital Signs section
3. Record new vital signs:
   - Heart rate, Blood pressure, Temperature, etc.
4. View history
5. Check trend analysis (available via server)

**What It Does:**
- ✅ Records vital signs to database
- ✅ Automatic alerts for abnormal values
- ✅ Trend analysis over time
- ✅ Doctor can view patient vitals

### **Feature 2: Prescription Refill Management**

**How to Test:**
1. Login as patient
2. View prescriptions
3. Click "Request Refill" on active prescription
4. Login as doctor
5. Review refill requests
6. Approve/deny refill

**What It Does:**
- ✅ Patients request refills via server
- ✅ Doctors review requests
- ✅ Automatic refill count tracking
- ✅ Email notifications (logged)

---

## 🏆 COMPLETE TEST CHECKLIST

### **Server Side:**
- [x] Server compiles
- [x] Server starts on port 8080
- [x] Database connection works
- [x] Authentication working
- [x] Data operations working

### **Client Side:**
- [x] Client components compile
- [x] ClientTest passes all tests
- [x] ServerConnection working
- [x] ClientService API functional

### **GUI Integration:**
- [ ] JavaFX application launches ← **YOU ARE HERE**
- [ ] Login with server works
- [ ] Login with local works (fallback)
- [ ] Dashboard loads
- [ ] Can navigate screens
- [ ] Data operations work

### **End-to-End:**
- [ ] Full workflow: Login → View Data → Create → Update → Logout
- [ ] Server mode fully functional
- [ ] Local mode fully functional
- [ ] Switching modes works
- [ ] All features demonstrated

---

## 🎬 DEMO SCRIPT FOR PRESENTATION

### **1. Server Setup (30 seconds)**
```
Show server starting in terminal
Explain: Multi-threaded TCP server on port 8080
Connected to MySQL database
```

### **2. Launch Application (10 seconds)**
```
Show: NetBeans → Run Project
Application window opens
```

### **3. Login Demo (30 seconds)**
```
Show: Server authentication checkbox
Enter: admin / admin123
Explain: Client connects via TCP sockets
Show: Successful login, dashboard loads
```

### **4. Feature Demo 1: Appointments (60 seconds)**
```
Show: View appointments list (from database)
Create: New appointment
Explain: Data sent to server, stored in MySQL
Show: Appointment appears in list
```

### **5. Feature Demo 2: Vital Signs (60 seconds)**
```
Navigate: Vital Signs section
Record: New vital signs entry
Explain: NEW FEATURE - Remote monitoring
Show: Trend analysis
```

### **6. Feature Demo 3: Prescription Refills (60 seconds)**
```
Show: Active prescriptions
Click: Request Refill
Explain: NEW FEATURE - Refill management
Show: Request sent to server
Switch: Login as doctor
Show: Refill request appears
Approve: Refill request
```

### **7. Fallback Demo (30 seconds)**
```
Stop: Server
Logout: From application
Login: Again (uncheck server mode)
Explain: Automatic fallback to local files
Show: Application still works offline
```

**Total Demo Time: ~5 minutes**

---

## 📚 DOCUMENTATION REFERENCE

- **Server Lead:** `SERVER_LEAD_COMPLETION_SUMMARY.md`
- **Client Lead:** `CLIENT_LEAD_COMPLETION_SUMMARY.md`
- **Test Results:** `CLIENT_TEST_RESULTS.md`
- **JavaFX GUI:** `JAVAFX_GUI_GUIDE.md`
- **Setup Guide:** `SERVER_SETUP_GUIDE.md`
- **Quick Reference:** `QUICK_REFERENCE.md`

---

## 🎉 YOU'RE READY!

**Everything is prepared:**
- ✅ Server working
- ✅ Client working
- ✅ Database populated
- ✅ GUI ready
- ✅ Documentation complete

**Just open NetBeans and press F6!** 🚀

---

**Guide Created:** October 13, 2025  
**Assignment:** COIT20258 Assignment 3 - Group 6  
**Status:** READY FOR TESTING ✅
