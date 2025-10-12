# 🏥 DOCTOR DASHBOARD - COMPLETE GUIDE

**Date:** October 13, 2025  
**Status:** ✅ Ready to Test  
**Location:** Documented

---

## 📍 DOCTOR DASHBOARD LOCATION

### **Files:**

**Controller:**
```
src/main/java/com/mycompany/coit20258assignment2/DoctorDashboardController.java
```

**View (FXML):**
```
src/main/resources/com/mycompany/coit20258assignment2/view/doctor_dashboard.fxml
```

---

## 🎯 DOCTOR DASHBOARD FEATURES

### **What Doctors Can Do:**

1. **📅 Manage Appointments**
   - View all appointments
   - Update appointment status
   - Staff booking editor

2. **📋 Create Diagnosis**
   - Record patient diagnoses
   - Add medical notes
   - Save to database

3. **📝 Create Referral**
   - Refer patients to specialists
   - Add referral notes
   - Track referrals

4. **💊 Review Prescriptions**
   - View prescription requests
   - Approve/deny refills (NEW - Assignment 3)
   - Create new prescriptions

5. **🩺 View Vital Signs** (NEW - Assignment 3)
   - Monitor patient vital signs remotely
   - View trends and alerts
   - Access via server

6. **👤 View My Data**
   - View personal information
   - Update profile

7. **🚪 Logout**
   - End session securely

---

## 🧪 HOW TO ACCESS DOCTOR DASHBOARD

### **Method 1: Server Mode** (Recommended for Testing Assignment 3)

**Prerequisites:**
- ✅ Server running on port 8080
- ✅ Database connected

**Steps:**
1. Run JavaFX app (F6 in NetBeans)
2. ✅ **Check** "Use Server Authentication (Port 8080)"
3. Enter doctor credentials (see below)
4. Click Login

**Expected Result:**
- Connects to server
- Authenticates via MySQL
- Doctor Dashboard opens
- Welcome message: "Welcome, Dr. [Name]"

---

### **Method 2: Local Mode** (Fallback/Offline Testing)

**No server needed!**

**Steps:**
1. Run JavaFX app
2. ❌ **Uncheck** "Use Server Authentication"
3. Username: `doctor`
4. Password: *(leave empty or try "pass")*
5. Click Login

**Expected Result:**
- Uses local file authentication
- Doctor Dashboard opens
- Works completely offline

---

## 🔐 DOCTOR CREDENTIALS

### **Server Mode (MySQL Database):**

To find doctor usernames in database, run this SQL:

```sql
USE ths_enhanced;
SELECT user_id, username, name, user_type FROM users WHERE user_type = 'DOCTOR';
```

**Expected Output:**
```
user_id | username | name            | user_type
--------|----------|-----------------|----------
D1      | drjames  | Dr. James Smith | DOCTOR
D2      | dranita  | Dr. Anita Rao   | DOCTOR
D3      | drmarcus | Dr. Marcus Lee  | DOCTOR
```

**Try these:**
- Username: `drjames` (Dr. James Smith - Cardiologist)
- Username: `dranita` (Dr. Anita Rao - Pediatrician)
- Username: `drmarcus` (Dr. Marcus Lee - Dermatologist)

**Note:** Passwords are encrypted in database. Check `setup_database.sql` for original passwords, or use password recovery feature.

---

### **Local Mode (File-based):**

From `data/users.dat`:
- Username: `doctor`
- Password: *(empty)* or `pass`

---

## 🖥️ DOCTOR DASHBOARD LAYOUT

### **Top Bar:**
```
Welcome, Dr. [Name]                                    [Logout]
```

### **Action Tiles:**
```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  Manage         │  │  Create         │  │  Create         │  │  Review         │
│  Appointments   │  │  Diagnosis      │  │  Referral       │  │  Prescriptions  │
└─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘

┌─────────────────┐
│  View My Data   │
└─────────────────┘
```

Each tile is a clickable button that navigates to that feature.

---

## 📊 TESTING SCENARIOS

### **Test 1: Access Doctor Dashboard**

**Objective:** Verify doctor can log in and see dashboard

**Steps:**
1. Start app
2. Use server authentication
3. Login as doctor
4. Verify dashboard loads

**Expected:**
- ✅ Dashboard displays
- ✅ Welcome message shows doctor name
- ✅ All action buttons visible
- ✅ Logout button works

---

### **Test 2: Manage Appointments**

**Objective:** Test appointment management

**Steps:**
1. Login as doctor
2. Click "Manage Appointments"
3. View appointment list
4. Update appointment status

**Expected:**
- ✅ Staff booking editor opens
- ✅ Appointments load from database (server mode)
- ✅ Can update status (pending → confirmed)
- ✅ Changes persist

---

### **Test 3: Create Diagnosis**

**Objective:** Test diagnosis creation

**Steps:**
1. Login as doctor
2. Click "Create Diagnosis"
3. Fill diagnosis form
4. Save diagnosis

**Expected:**
- ✅ Diagnosis form opens
- ✅ Can select patient
- ✅ Can enter diagnosis details
- ✅ Saves successfully

---

### **Test 4: Review Prescriptions** (NEW Feature)

**Objective:** Test prescription refill management

**Steps:**
1. Login as doctor
2. Click "Review Prescriptions"
3. View refill requests
4. Approve/deny refill

**Expected:**
- ✅ Prescription review screen opens
- ✅ Refill requests visible
- ✅ Can approve/deny
- ✅ Request sent to server
- ✅ Database updated

---

### **Test 5: View Patient Vital Signs** (NEW Feature)

**Objective:** Test remote vital signs monitoring

**Steps:**
1. Login as doctor
2. Navigate to patient records
3. View vital signs history
4. Check for alerts

**Expected:**
- ✅ Vital signs data loads from server
- ✅ Trend analysis displays
- ✅ Abnormal values flagged
- ✅ Real-time data from database

---

## 🔄 NAVIGATION FLOW

```
Login Screen
    ↓
[Login as Doctor]
    ↓
Doctor Dashboard
    ↓
Choose Action:
    ├── Manage Appointments → Staff Booking Editor
    ├── Create Diagnosis → Diagnosis Form
    ├── Create Referral → Referral Form
    ├── Review Prescriptions → Prescription Review
    ├── View My Data → My Data View
    └── Logout → Back to Login
```

---

## 🎨 USER INTERFACE

### **Dashboard Style:**
- Clean, professional medical theme
- Large, easy-to-click action tiles
- Clear labels and icons
- Consistent with patient/admin dashboards

### **Color Scheme:**
- Primary: Blue (medical/professional)
- Secondary: White/light gray
- Accent: Green (success), Red (urgent)

---

## 🆚 COMPARISON: LOCAL VS SERVER MODE

### **Local Mode:**
```
✅ Works offline
✅ No server needed
✅ Good for development
❌ Limited data
❌ No real-time features
❌ Single user only
```

### **Server Mode:**
```
✅ Real-time data
✅ Full database access
✅ Multi-user support
✅ NEW features available (vital signs, refills)
✅ Data persistence
✅ Better for demonstration
```

**Recommendation:** Use **Server Mode** for Assignment 3 demonstration to show full 3-tier architecture!

---

## 📝 TROUBLESHOOTING

### **Issue: "Invalid credentials"**

**Solution:**
1. Check if using server mode or local mode
2. **Server mode:** Verify database credentials (drjames, dranita, etc.)
3. **Local mode:** Use `doctor` with empty/simple password
4. Check server is running if in server mode

---

### **Issue: "Failed to load doctor_dashboard.fxml"**

**Solution:**
1. Check FXML syntax (should be OK - already validated)
2. Verify controller class exists
3. Check for Insets format errors (already fixed in admin dashboard)

---

### **Issue: Dashboard loads but buttons don't work**

**Solution:**
1. Check `@FXML` annotations in controller
2. Verify `onAction` attributes in FXML match method names
3. Check console for errors

---

## 🎯 ASSIGNMENT 3 DEMONSTRATION

### **What to Show:**

**1. Login Process:**
```
Show: Doctor logging in via server authentication
Explain: TCP connection to server, MySQL authentication
```

**2. Dashboard Overview:**
```
Show: Doctor dashboard with all features
Explain: Role-based access, different from patient/admin
```

**3. Appointment Management:**
```
Show: Viewing and updating appointments
Explain: Real-time data from database
```

**4. NEW Feature - Prescription Refills:**
```
Show: Review refill requests
Explain: NEW Assignment 3 feature, server-based
Action: Approve a refill request
Result: Database updated, patient notified
```

**5. NEW Feature - Vital Signs Monitoring:**
```
Show: Patient vital signs data
Explain: Remote monitoring capability (Assignment 3)
Action: View trend analysis
Result: Identify concerning patterns
```

---

## ✅ CHECKLIST FOR TESTING

### **Basic Functionality:**
- [ ] Doctor can login (server mode)
- [ ] Doctor can login (local mode)
- [ ] Dashboard displays correctly
- [ ] Welcome message shows doctor name
- [ ] All buttons visible and labeled

### **Feature Testing:**
- [ ] Manage Appointments works
- [ ] Create Diagnosis works
- [ ] Create Referral works
- [ ] Review Prescriptions works
- [ ] View My Data works
- [ ] Logout works

### **Server Integration:**
- [ ] Connects to TCP server
- [ ] Authenticates via database
- [ ] Data loads from MySQL
- [ ] Changes persist in database
- [ ] Real-time updates work

### **NEW Assignment 3 Features:**
- [ ] Prescription refill review works
- [ ] Vital signs monitoring accessible
- [ ] Trend analysis displays
- [ ] Alerts for abnormal values

---

## 🏆 SUCCESS CRITERIA

**Doctor Dashboard is working correctly when:**

✅ Doctor can log in with valid credentials  
✅ Dashboard loads with welcome message  
✅ All action buttons are functional  
✅ Can navigate to all features  
✅ Can create/view medical records  
✅ Can review prescription refills (NEW)  
✅ Can monitor patient vitals (NEW)  
✅ Can logout successfully  
✅ Server integration working (in server mode)  
✅ Local fallback working (in local mode)  

---

## 📚 RELATED FILES

### **Other Dashboards:**
- `AdminDashboardController.java` / `admin_dashboard.fxml`
- `PatientDashboardController.java` / `patient_dashboard.fxml`

### **Connected Features:**
- `StaffBookingEditorController.java` / `staff_booking_editor.fxml`
- `DiagnosisFormController.java` / `diagnosis_form.fxml`
- `ReferralFormController.java` / `referral_form.fxml`
- `PrescriptionReviewController.java` / `prescription_review.fxml`
- `MyDataViewController.java` / `my_data_view.fxml`

---

## 🚀 QUICK START

**Want to test doctor dashboard right now?**

**1. If server not running:**
```powershell
java -cp "target/classes;mysql-connector-j-8.0.33.jar" com.mycompany.coit20258assignment2.server.THSServer
```

**2. Run JavaFX app:**
```
Press F6 in NetBeans
```

**3. Login:**
```
☑ Use Server Authentication
Username: drjames (or dranita, drmarcus)
Password: (check database)
```

**4. Explore:**
```
Click each feature tile
Test appointment management
Try prescription review
View vital signs monitoring
```

---

**Doctor Dashboard is ready for your testing and demonstration!** 🎉

---

**Document Created:** October 13, 2025  
**Assignment:** COIT20258 Assignment 3 - Group 6  
**Status:** Doctor Dashboard - Ready to Test
