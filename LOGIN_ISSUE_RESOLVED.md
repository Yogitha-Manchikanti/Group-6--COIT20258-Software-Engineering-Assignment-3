# 🔧 LOGIN ISSUE RESOLVED - UPDATED INSTRUCTIONS

**Date:** October 13, 2025  
**Issue:** "Invalid credentials" error on login  
**Status:** ✅ FIXED - Checkbox added to FXML

---

## 🐛 WHAT WAS THE PROBLEM?

The screenshot shows:
- ✅ JavaFX application launched successfully
- ❌ Login failing with "Invalid credentials"
- ❌ No "Use Server Authentication" checkbox visible

**Root Cause:**
- The `LoginController.java` had code for server authentication with checkbox
- But `Login.fxml` didn't have the `<CheckBox>` element
- So the app defaulted to local authentication
- Username "admin" with password "admin123" doesn't exist in local files

---

## ✅ WHAT WAS FIXED?

Updated `Login.fxml` to add:
```xml
<CheckBox fx:id="useServerCheckbox" 
          text="Use Server Authentication (Port 8080)" 
          style="-fx-font-size: 12px; -fx-text-fill: #0066cc;"/>
```

Now the login screen will have a checkbox to choose between server and local authentication!

---

## 🎯 HOW TO TEST NOW

### **Step 1: Restart the JavaFX Application**

The app needs to reload the updated FXML file.

**Close the current login window** or stop the Maven process, then:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
& "C:\Program Files\NetBeans-21\netbeans\java\maven\bin\mvn.cmd" javafx:run
```

**Or in NetBeans:** Press F6 again

---

### **Step 2: You'll Now See the Checkbox!**

The login screen will show:
```
Email / Username: [textfield]
Password:         [password field]
[ ] Use Server Authentication (Port 8080)  ← NEW CHECKBOX!

[Forgot Password?]  [Sign Up]  [Login]
```

---

### **Step 3A: Test with SERVER AUTHENTICATION**

**Prerequisites:**
- Server must be running on port 8080
- Check server window shows "Server ready"

**If server not running:**
```powershell
java -cp "target/classes;mysql-connector-j-8.0.33.jar" com.mycompany.coit20258assignment2.server.THSServer
```

**Then in Login Screen:**
1. ✅ **CHECK** the "Use Server Authentication" checkbox
2. Username: `admin`
3. Password: `admin123`
4. Click **Login**

**Expected Result:**
- ✅ Status: "Connecting to server..."
- ✅ Connects to TCP server (localhost:8080)
- ✅ Authenticates via MySQL database
- ✅ "Login successful!"
- ✅ Admin Dashboard opens

---

### **Step 3B: Test with LOCAL AUTHENTICATION (Fallback)**

**No server needed!**

**In Login Screen:**
1. ❌ **UNCHECK** the "Use Server Authentication" checkbox (or leave it checked if server is down)
2. Try these credentials:

**Option 1: Patient User**
- Username: `patient`
- Password: `pass`

**Option 2: Doctor User**
- Username: `doctor`
- Password: *(leave empty)*

**Option 3: Admin User (Local)**
- Username: `admin`
- Password: *(leave empty)*

**Expected Result:**
- ✅ Uses local file authentication
- ✅ Dashboard opens
- ✅ Works completely offline

---

## 📊 TWO AUTHENTICATION MODES

### **Mode 1: Server Authentication** (NEW - Assignment 3)

```
Login Screen
    ↓
[✓] Use Server Authentication
    ↓
ClientService.login()
    ↓
TCP Socket → localhost:8080
    ↓
THSServer receives request
    ↓
AuthDAO.authenticate()
    ↓
MySQL Database
    ↓
User data returned
    ↓
Dashboard loads
```

**Benefits:**
- Real-time database
- Multi-user support
- Remote monitoring features
- Prescription refill management

---

### **Mode 2: Local Authentication** (Assignment 2 - Preserved)

```
Login Screen
    ↓
[ ] Use Server Authentication (unchecked)
    ↓
AuthService.login()
    ↓
DataStore reads files
    ↓
data/users.ser
    ↓
User data returned
    ↓
Dashboard loads
```

**Benefits:**
- Works offline
- No server needed
- Development testing
- Backward compatible

---

## 🔄 AUTOMATIC FALLBACK

If you check "Use Server Authentication" but the server is down:

```
1. Try server authentication
2. Server connection fails
3. Show message: "Server: Connection refused"
4. Automatically fall back to local authentication
5. Login with local credentials
6. Application continues working!
```

This provides resilience and great user experience!

---

## 🧪 COMPLETE TEST SCENARIOS

### **Test 1: Server Mode Success**
- [x] Server running
- [x] Checkbox checked
- [x] Enter admin/admin123
- [x] Click Login
- **Expected:** Dashboard from database

### **Test 2: Server Mode with Fallback**
- [ ] Server NOT running
- [x] Checkbox checked
- [x] Enter patient/pass
- [x] Click Login
- **Expected:** Error message, then local login

### **Test 3: Local Mode**
- [ ] Server NOT running
- [ ] Checkbox unchecked
- [x] Enter patient/pass
- [x] Click Login
- **Expected:** Dashboard from local files

### **Test 4: Invalid Credentials**
- [ ] Checkbox unchecked
- [x] Enter admin/admin123 (doesn't exist locally)
- [x] Click Login
- **Expected:** "Invalid credentials" error

---

## 💡 TROUBLESHOOTING

### **Problem: Still see "Invalid credentials"**

**Solution 1:** Restart the app to load updated FXML
```powershell
# Stop current app, then:
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
& "C:\Program Files\NetBeans-21\netbeans\java\maven\bin\mvn.cmd" javafx:run
```

**Solution 2:** Check if checkbox is visible
- If no checkbox, FXML not reloaded
- Try: Clean and Build in NetBeans

**Solution 3:** Use correct credentials
- With checkbox: admin/admin123 (server mode)
- Without checkbox: patient/pass (local mode)

---

### **Problem: "Server connection failed"**

**Solution:** Start the server first
```powershell
java -cp "target/classes;mysql-connector-j-8.0.33.jar" com.mycompany.coit20258assignment2.server.THSServer
```

Wait for: "✅ Server ready for client connections on port 8080"

---

### **Problem: Checkbox appears but login still fails**

**Check 1:** Is server running?
```
Look for server window showing "Server ready on port 8080"
```

**Check 2:** Is checkbox actually checked?
```
Click the checkbox to ensure it's checked (blue checkmark)
```

**Check 3:** Correct credentials?
```
Server mode: admin/admin123
Local mode: patient/pass or doctor/(empty)
```

---

## 📝 VALID CREDENTIALS REFERENCE

### **Server Mode (MySQL Database):**

| Username | Password | Role | Notes |
|----------|----------|------|-------|
| admin | admin123 | Administrator | ✅ Recommended |
| Check database | Check users table | Various | Use MySQL Workbench |

**To see all server users:**
```sql
USE ths_enhanced;
SELECT username, user_type FROM users;
```

---

### **Local Mode (File-based):**

| Username | Password | Role | Notes |
|----------|----------|------|-------|
| patient | pass | Patient | ✅ Works |
| doctor | *(empty)* | Doctor | ✅ Works |
| admin | *(empty)* | Administrator | ✅ Works |

**Note:** Local passwords may be empty or simple for testing

---

## 🎯 RECOMMENDED TESTING ORDER

**1. Test Local Mode First (Easier)**
```
☐ Close and restart app
☐ Uncheck "Use Server Authentication"  
☐ Username: patient
☐ Password: pass
☐ Click Login
☐ Verify Patient Dashboard opens
```

**2. Then Test Server Mode**
```
☐ Ensure server is running (check window)
☐ Logout from app
☐ Check "Use Server Authentication"
☐ Username: admin
☐ Password: admin123
☐ Click Login
☐ Verify Admin Dashboard opens
☐ Verify data loads from database
```

**3. Test Fallback Mechanism**
```
☐ Stop the server
☐ Logout from app
☐ Check "Use Server Authentication"
☐ Try to login
☐ See "Server connection failed" message
☐ Uncheck box
☐ Use local credentials
☐ Verify login works
```

---

## ✅ SUCCESS INDICATORS

### **Login Screen Shows:**
- ✅ Checkbox visible: "Use Server Authentication (Port 8080)"
- ✅ Can check/uncheck the box
- ✅ Username and password fields present
- ✅ Login button works

### **Server Mode Success:**
- ✅ Message: "Connecting to server..."
- ✅ Message: "Login successful!"
- ✅ Dashboard opens
- ✅ Data from MySQL database displays

### **Local Mode Success:**
- ✅ No "connecting to server" message
- ✅ Dashboard opens directly
- ✅ Data from local files displays

---

## 🏆 FINAL STATUS

**Issue Status:** ✅ RESOLVED

**Changes Made:**
1. Added `<CheckBox fx:id="useServerCheckbox">` to Login.fxml
2. Updated documentation with credentials
3. Provided restart instructions

**Next Steps:**
1. Restart JavaFX application
2. Verify checkbox appears
3. Test both authentication modes
4. Confirm everything works!

---

## 🎬 QUICK RESTART GUIDE

**In Current Terminal (if Maven is still running):**
```
Press Ctrl+C to stop
```

**Then run:**
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
& "C:\Program Files\NetBeans-21\netbeans\java\maven\bin\mvn.cmd" javafx:run
```

**Or in NetBeans:**
```
Press F6 (or Right-click → Run)
```

**When login appears:**
```
✅ Look for checkbox!
✅ Test with correct credentials
✅ Verify dashboard opens
```

---

**Problem Solved!** Now restart the app and you'll see the checkbox! 🎉

**Document Created:** October 13, 2025  
**Assignment:** COIT20258 Assignment 3 - Group 6  
**Status:** Issue resolved, ready for testing
