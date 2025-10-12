# 🚀 Quick Reference: Signup & Password Reset

**Last Updated:** October 13, 2025

---

## 🆕 New User Signup

### How to Create a New Account

1. **Launch Application** → Login screen appears
2. **Click "Sign Up"** button
3. **Fill Form:**
   - 📝 Full Name: `Your Full Name`
   - 📧 Email: `your.email@example.com` (must be unique)
   - 👤 Username: `yourusername` (must be unique)
   - 🔒 Password: `yourpassword`
   - ✅ Confirm Password: `yourpassword` (must match)

4. **Click "Sign Up"**

### Success Response
```
✅ Account created successfully. Please login with your credentials.
```

### Possible Errors
- ❌ "All fields are required" → Fill all fields
- ❌ "Passwords do not match" → Confirm password must match
- ❌ "Username already exists" → Choose different username
- ❌ "Email already registered" → Use different email
- ❌ "Server not available" → Start THSServer first

### What Happens Behind the Scenes
1. Client validates form input
2. Client sends SIGNUP request to TCP server
3. Server checks for duplicate username/email
4. Server generates unique patient ID (e.g., `pat042`)
5. Server creates user record in MySQL database
6. Server returns success/failure response
7. Client displays message

### Database Record Created
```sql
INSERT INTO users (id, name, email, username, password, user_type) 
VALUES ('pat042', 'Your Name', 'your.email@example.com', 'yourusername', 'yourpassword', 'PATIENT');
```

---

## 🔑 Password Reset

### How to Reset Forgotten Password

1. **Login Screen** → Click **"Forgot Password?"** link
2. **Enter Identifier:**
   - Option A: Enter your **username** (e.g., `jsmith`)
   - Option B: Enter your **email** (e.g., `john.smith@email.com`)
3. **Click "Reset Password"**

### Success Response
```
✅ Password reset successfully. Temporary password: reset123
```

### Error Response
- ❌ "Please enter your username or email" → Field is empty
- ❌ "User not found" → Username/email doesn't exist
- ❌ "Server not available" → Start THSServer first

### Login with New Password
1. Click **"Back to Login"**
2. Enter:
   - Username: `yourusername`
   - Password: `reset123`
3. Click "Login"

### ⚠️ Important Notes
- Temporary password is: **`reset123`**
- This password works for ALL users who reset
- Change your password after logging in (recommended)
- Password reset works with BOTH username and email

---

## 🖥️ Server Requirements

### Before Using These Features

**1. Start MySQL Server:**
```bash
# Windows
net start MySQL80

# macOS
mysql.server start

# Linux
sudo systemctl start mysql
```

**2. Start TCP Server:**
```bash
# From NetBeans: Right-click THSServer.java → Run File

# OR from command line:
java -cp target/classes com.mycompany.coit20258assignment2.server.THSServer
```

**3. Verify Server Running:**
```bash
# Check port 8080 is listening:
netstat -an | findstr 8080

# Expected output:
TCP    0.0.0.0:8080           0.0.0.0:0              LISTENING
```

**4. Start Client Application:**
```bash
# From NetBeans: Press F6

# OR run App.java
```

---

## 🧪 Testing Checklist

### Test Signup

- [ ] Open application
- [ ] Click "Sign Up"
- [ ] Fill all fields correctly
- [ ] Click "Sign Up"
- [ ] See success message
- [ ] Return to login
- [ ] Login with new credentials
- [ ] Verify login successful

### Test Duplicate Prevention

- [ ] Try signup with existing username
- [ ] See error: "Username already exists"
- [ ] Try signup with existing email
- [ ] See error: "Email already registered"

### Test Password Reset

- [ ] Click "Forgot Password?"
- [ ] Enter username of existing user
- [ ] Click "Reset Password"
- [ ] See success message with temporary password
- [ ] Return to login
- [ ] Login with: username + "reset123"
- [ ] Verify login successful

### Test Email-Based Reset

- [ ] Click "Forgot Password?"
- [ ] Enter email instead of username
- [ ] Click "Reset Password"
- [ ] See success message
- [ ] Login with: username + "reset123"
- [ ] Verify login successful

---

## 📊 Database Verification

### Check New User Created
```sql
USE ths_enhanced;
SELECT * FROM users WHERE username = 'yourusername';
```

**Expected Output:**
```
+--------+-----------+------------------------+-------------+------------+-----------+--------------+
| id     | name      | email                  | username    | password   | user_type | specializ... |
+--------+-----------+------------------------+-------------+------------+-----------+--------------+
| pat042 | Your Name | your.email@example.com | yourusername| yourpasswd | PATIENT   | NULL         |
+--------+-----------+------------------------+-------------+------------+-----------+--------------+
```

### Check Password Reset
```sql
SELECT username, password, updated_at 
FROM users 
WHERE username = 'yourusername';
```

**After Reset:**
```
+-------------+----------+---------------------+
| username    | password | updated_at          |
+-------------+----------+---------------------+
| yourusername| reset123 | 2025-10-13 08:05:42 |
+-------------+----------+---------------------+
```

### Count All Users
```sql
SELECT user_type, COUNT(*) as count 
FROM users 
GROUP BY user_type;
```

**Expected Output:**
```
+---------------+-------+
| user_type     | count |
+---------------+-------+
| PATIENT       | 6     |
| DOCTOR        | 4     |
| ADMINISTRATOR | 1     |
+---------------+-------+
```

---

## 🔧 Troubleshooting

### Problem: "Server not available"

**Cause:** TCP server is not running

**Solution:**
1. Check if THSServer is running
2. Check port 8080 is not blocked
3. Verify MySQL is running
4. Check server logs for errors

### Problem: Database connection failed

**Cause:** MySQL server not running or wrong credentials

**Solution:**
1. Start MySQL: `net start MySQL80`
2. Test connection: `mysql -u root -p`
3. Verify database exists: `SHOW DATABASES;`
4. Check `DatabaseManager.java` credentials

### Problem: Duplicate key error

**Cause:** Username or email already exists

**Solution:**
1. Use different username
2. Use different email
3. Check existing users: `SELECT username, email FROM users;`

### Problem: Cannot compile

**Cause:** Code changes not compiled

**Solution:**
```bash
mvn clean compile
# OR in NetBeans: Right-click project → Clean and Build
```

---

## 📝 Default Test Accounts

### For Testing (Pre-created)

| Role | Username | Email | Password |
|------|----------|-------|----------|
| Admin | admin | admin@ths.com | admin123 |
| Doctor | drjohnson | sarah.johnson@ths.com | doctor123 |
| Doctor | drchen | michael.chen@ths.com | doctor123 |
| Patient | jsmith | john.smith@email.com | patient123 |
| Patient | mjohnson | mary.johnson@email.com | patient123 |

### For Password Reset Testing

1. **Reset jsmith's password:**
   - Enter: `jsmith` or `john.smith@email.com`
   - New password: `reset123`
   - Login: `jsmith` / `reset123`

2. **Reset admin's password:**
   - Enter: `admin` or `admin@ths.com`
   - New password: `reset123`
   - Login: `admin` / `reset123`

---

## 🎯 Quick Commands

### Start Everything
```bash
# 1. Start MySQL
net start MySQL80

# 2. Compile project
cd path\to\project
mvn clean compile

# 3. Start server (in terminal 1)
java -cp target/classes com.mycompany.coit20258assignment2.server.THSServer

# 4. Start client (in terminal 2 or from NetBeans)
```

### Stop Everything
```bash
# 1. Stop client: Close application window
# 2. Stop server: Ctrl+C in terminal
# 3. Stop MySQL: net stop MySQL80
```

### Check Status
```bash
# Check MySQL
mysql -u root -p -e "SELECT 'MySQL Running' as Status;"

# Check port 8080
netstat -an | findstr 8080

# Check database
mysql -u root -p ths_enhanced -e "SELECT COUNT(*) FROM users;"
```

---

## 📞 Need Help?

1. **Check server logs** - Look for error messages
2. **Check README.md** - Full documentation
3. **Check UPDATE_SUMMARY.md** - Detailed changes
4. **Verify database** - Run SQL queries above
5. **Check compilation** - Run `mvn clean compile`

---

**✅ Features Ready for Use!**

Both signup and password reset are fully functional and tested. The system uses server-based authentication with MySQL database storage.
