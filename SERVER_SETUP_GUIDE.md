# THS-Enhanced Server Setup Guide
## Server Lead Component Setup

### Prerequisites
1. **Java 17+** ✅ (Already installed)
2. **MySQL 8.0+** (Need to install)
3. **MySQL JDBC Driver** (Need to download)

### Setup Steps

#### 1. Install MySQL
- Download and install MySQL Community Server from: https://dev.mysql.com/downloads/mysql/
- During installation, set root password (remember this!)
- Start MySQL service

#### 2. Create Database
Open MySQL Command Line and run:
```sql
source setup_database.sql
```

OR manually:
```sql
mysql -u root -p
CREATE DATABASE ths_enhanced;
USE ths_enhanced;
source setup_database.sql;
```

#### 3. Download MySQL JDBC Driver
- Download from: https://dev.mysql.com/downloads/connector/j/
- Download the "Platform Independent" ZIP file
- Extract `mysql-connector-java-8.0.33.jar` to project root directory

#### 4. Update Database Connection
Edit `DatabaseManager.java` and update:
```java
private static final String DB_USER = "root"; // Your MySQL username
private static final String DB_PASSWORD = "your_password"; // Your MySQL password
```

#### 5. Compile Server Components
```cmd
# Note: Your JavaFX client components are preserved
# Server components can be compiled separately for testing

# Compile basic model classes (no JavaFX dependencies)
javac -encoding UTF-8 -d target/classes -sourcepath src/main/java src/main/java/com/mycompany/coit20258assignment2/User.java src/main/java/com/mycompany/coit20258assignment2/Patient.java src/main/java/com/mycompany/coit20258assignment2/Doctor.java src/main/java/com/mycompany/coit20258assignment2/Administrator.java src/main/java/com/mycompany/coit20258assignment2/Appointment.java src/main/java/com/mycompany/coit20258assignment2/Prescription.java src/main/java/com/mycompany/coit20258assignment2/*Status.java

# Compile communication classes
javac -encoding UTF-8 -d target/classes -cp target/classes -sourcepath src/main/java src/main/java/com/mycompany/coit20258assignment2/common/*.java

# Compile server classes (with MySQL driver)
javac -encoding UTF-8 -d target/classes -cp "target/classes;mysql-connector-j-8.0.33.jar" -sourcepath src/main/java src/main/java/com/mycompany/coit20258assignment2/server/*.java

# Compile DAO classes
javac -encoding UTF-8 -d target/classes -cp "target/classes;mysql-connector-j-8.0.33.jar" -sourcepath src/main/java src/main/java/com/mycompany/coit20258assignment2/server/dao/*.java
```

#### 6. Test Your Server
```cmd
# Test database connection and AuthDAO
java -cp "target/classes;mysql-connector-j-8.0.33.jar" com.mycompany.coit20258assignment2.server.ServerTest

# Run the actual server
java -cp "target/classes;mysql-connector-j-8.0.33.jar" com.mycompany.coit20258assignment2.server.THSServer
```

**Note:** Your original JavaFX client application is completely intact:
- All FXML files are preserved
- All JavaFX controllers are preserved  
- Original Assignment 2 functionality is intact
- Server components work alongside existing code

### Your Deliverables ✅

#### Components Created:
1. **Multi-threaded TCP Server** (`THSServer.java`)
   - Handles 100 concurrent clients
   - Port 8080 (configurable)
   - Graceful shutdown

2. **Database Layer**
   - `DatabaseManager.java` - Connection management
   - `AuthDAO.java` - User authentication
   - MySQL schema with all tables

3. **Communication Protocol**
   - `BaseRequest.java` & `BaseResponse.java`
   - `LoginRequest.java` & `LoginResponse.java`
   - `ClientHandler.java` - Request processing

4. **Testing Framework**
   - `ServerTest.java` - Component testing
   - Sample data in database

#### Next Steps for You:
1. **Additional DAOs**: Implement `AppointmentDAO.java`, `PrescriptionDAO.java`
2. **Enhanced Security**: Add encryption, session management
3. **Request Handlers**: Complete all request types in `ClientHandler.java`
4. **Performance**: Add connection pooling, caching

#### For Team Integration:
- **Client Lead** can now connect to your server via sockets on port 8080
- **Database Lead** can use your schema as foundation
- **Documentation Lead** can document your APIs

### Testing Your Work

#### Database Test:
```sql
-- Check if data loaded correctly
SELECT COUNT(*) FROM users;
SELECT * FROM users WHERE user_type = 'ADMINISTRATOR';
```

#### Server Test:
```cmd
java -cp "target/classes;mysql-connector-java-8.0.33.jar" com.mycompany.coit20258assignment2.server.ServerTest
```

#### Expected Output:
```
🧪 THS-Enhanced Server Testing Utility
=====================================

🔍 Testing Database Connection...
Database connection successful!
Connected to: jdbc:mysql://localhost:3306/ths_enhanced
✅ Database connection test passed

🔍 Testing Authentication DAO...
User exists check: true
✅ Authentication test passed: System Administrator
```

### Troubleshooting

#### Common Issues:
1. **MySQL not running**: Start MySQL service
2. **Access denied**: Check username/password in `DatabaseManager.java`
3. **ClassNotFoundException**: Ensure MySQL JDBC driver is in classpath
4. **Port already in use**: Change port in `THSServer.java`

#### Success Indicators:
- ✅ Database connects successfully
- ✅ Sample users authenticate
- ✅ Server starts on port 8080
- ✅ "🚀 THS-Enhanced Server starting..." message appears