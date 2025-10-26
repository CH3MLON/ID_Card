# Digital ID Card Generator

A console-based Java application that generates digital ID cards from student data. Student ID numbers are auto-generated randomly. ID card templates and issue logs are stored in a MySQL database.

## Features

- Generate digital ID cards as PNG images (400×250px)
- Auto-generate unique student IDs (format: STU123456)
- Store student data in MySQL database
- Create professional-looking ID cards with student photos
- Full CRUD operations (Create, Read, Update, Delete)
- Track all ID card issuances with timestamps
- Simple menu-driven interface
- Minimalistic design without complex Java concepts

## Requirements

- Java Development Kit (JDK) 8 or higher
- MySQL 5.7 or higher
- MySQL Connector/J (JDBC driver)

## Project Structure

```
ID_Card/
├── src/
│   ├── Main.java              # Application entry point and menu controller
│   ├── Student.java           # Student data model (POJO)
│   ├── DatabaseManager.java   # Database operations using JDBC
│   ├── IDCardGenerator.java   # PNG ID card generation using Java2D
│   └── Utils.java             # Helper utility methods
├── lib/
│   └── mysql-connector-java-8.0.33.jar  # MySQL JDBC driver (download separately)
├── output/                    # Generated ID card PNG files (created at runtime)
├── db.properties              # Database configuration
├── schema.sql                 # Database setup script
└── README.md                  # This file
```

## Setup Instructions

### 1. Download MySQL Connector

Download MySQL Connector/J from:
- Official: https://dev.mysql.com/downloads/connector/j/
- Maven Repository: https://repo1.maven.org/maven2/mysql/mysql-connector-java/

Download version 8.0.33 or newer and place the JAR file in the `lib/` folder.

### 2. Setup MySQL Database

Run the provided SQL script to create the database and tables:

```bash
mysql -u root -p < schema.sql
```

This will create:
- Database: `id_card_system`
- Table: `students` (stores student information)
- Table: `issue_logs` (tracks ID card issuances)

### 3. Configure Database Connection

Edit `db.properties` with your MySQL credentials:

```properties
db.url=jdbc:mysql://localhost:3306/id_card_system
db.username=root
db.password=yourpassword
db.driver=com.mysql.cj.jdbc.Driver
```

**Note:** Replace `yourpassword` with your actual MySQL password.

### 4. Compile the Application

From the project root directory:

**Linux/Mac:**
```bash
javac -d . -cp .:lib/mysql-connector-java-8.0.33.jar src/*.java
```

**Windows:**
```cmd
javac -d . -cp .;lib\mysql-connector-java-8.0.33.jar src\*.java
```

### 5. Run the Application

**Linux/Mac:**
```bash
java -cp .:lib/mysql-connector-java-8.0.33.jar Main
```

**Windows:**
```cmd
java -cp .;lib\mysql-connector-java-8.0.33.jar Main
```

## Usage Guide

### Main Menu

When you run the application, you'll see the following menu:

```
================================
   DIGITAL ID CARD SYSTEM
================================
1. Generate New ID Card
2. View All Students
3. Search Student by ID
4. Update Student Information
5. Delete Student Record
6. View Issue Logs
7. Exit
================================
Enter your choice (1-7):
```

### Option 1: Generate New ID Card

1. Enter student name
2. Enter course/program name
3. Enter year of study
4. Enter path to student photo (e.g., `photos/student.jpg`)
5. System will:
   - Auto-generate a unique Student ID
   - Save student data to database
   - Generate PNG ID card image
   - Save card to `output/` folder
   - Log the issuance

**Example:**
```
Enter student name: John Doe
Enter course: Computer Science
Enter year: 2024
Enter photo path: photos/john.jpg

Generating ID card...
[SUCCESS] ID card generated successfully!
Student ID: STU432156
Card saved to: output/STU432156.png
```

### Option 2: View All Students

Displays a formatted table of all students in the database:

```
=== All Students ===
ID          | Name        | Course             | Year
-----------------------------------------------------------
STU432156   | John Doe    | Computer Science   | 2024
STU789012   | Jane Smith  | Data Science       | 2023

Total: 2 students
```

### Option 3: Search Student by ID

Search for a specific student by their Student ID:

```
Enter student ID to search: STU432156

=== Student Details ===
Student ID: STU432156
Name: John Doe
Course: Computer Science
Year: 2024
Photo Path: photos/john.jpg
ID Card Location: output/STU432156.png
```

### Option 4: Update Student Information

Update existing student information and regenerate their ID card:

1. Enter Student ID
2. View current information
3. Enter new values (press Enter to keep current)
4. System will update database and regenerate the ID card
5. Log the update as a new issuance

### Option 5: Delete Student Record

Delete a student record from the database:

1. Enter Student ID
2. Confirm deletion (Y/N)
3. System will delete student and associated issue logs (cascade)
4. Optionally delete the PNG file

### Option 6: View Issue Logs

View all ID card issuances with timestamps:

```
=== ID Card Issue Logs ===
Log ID | Student ID | Name        | Issued At
-------------------------------------------------------
15     | STU432156  | John Doe    | 2024-10-26 14:32:05
14     | STU789012  | Jane Smith  | 2024-10-26 13:15:22

Total: 2 logs
```

### Option 7: Exit

Closes database connections and exits the application.

## ID Card Design

Generated ID cards have the following specifications:

- **Size:** 400px × 250px
- **Format:** PNG image
- **Layout:**
  - Header: "STUDENT ID CARD" (centered, bold)
  - Photo: 100×120px on the left side
  - Student details on the right:
    - ID Number
    - Name
    - Course
    - Year
- **Colors:**
  - Background: White
  - Text: Black
  - Border: Black
- **Placeholder:** If photo not found, displays "NO PHOTO" placeholder

## Error Handling

The application handles various error scenarios gracefully:

- **Empty input:** Prompts user to enter valid data
- **Invalid photo path:** Offers to continue with placeholder
- **Database connection failure:** Displays error message without crashing
- **Duplicate Student ID:** Automatically generates new unique ID
- **Student not found:** Displays appropriate error message
- **File system errors:** Displays error but continues operation where possible

## Database Schema

### students table

| Column      | Type         | Description                    |
|-------------|--------------|--------------------------------|
| student_id  | VARCHAR(20)  | Primary key, format: STU123456 |
| name        | VARCHAR(100) | Student's full name            |
| course      | VARCHAR(100) | Course/program name            |
| year        | VARCHAR(20)  | Year of study                  |
| photo_path  | VARCHAR(255) | File path to student photo     |
| created_at  | TIMESTAMP    | Record creation timestamp      |

### issue_logs table

| Column      | Type         | Description                      |
|-------------|--------------|----------------------------------|
| log_id      | INT          | Primary key, auto-increment      |
| student_id  | VARCHAR(20)  | Foreign key to students table    |
| issued_at   | TIMESTAMP    | When card was issued/generated   |

## Troubleshooting

### ClassNotFoundException: MySQL driver not found

**Problem:** MySQL Connector JAR not in classpath

**Solution:**
- Ensure mysql-connector-java JAR is in `lib/` folder
- Verify the JAR filename matches the one in compile/run commands
- Update commands if using different version

### Database connection failed

**Problem:** Cannot connect to MySQL

**Solution:**
- Check MySQL server is running
- Verify credentials in `db.properties`
- Ensure database `id_card_system` exists (run schema.sql)

### Photo file not found

**Problem:** Provided photo path doesn't exist

**Solution:**
- Verify photo file path is correct
- Use absolute path or relative path from project root
- Choose to continue with placeholder when prompted

## Technical Details

**Java Concepts Used:**
- Classes and objects (basic OOP)
- Methods and variables
- Control flow (if/else, switch, loops)
- ArrayList for collections
- Exception handling (try-catch)
- File I/O (Properties, File, ImageIO)
- JDBC (Connection, PreparedStatement, ResultSet)
- Graphics2D for image generation

**Java Concepts NOT Used (by design):**
- Interfaces
- Abstract classes
- Generics (except basic ArrayList)
- Streams/Lambda expressions
- Multi-threading
- Design patterns
- Custom exceptions

This application is designed to be simple and accessible, avoiding complex Java concepts while providing full functionality.

## License

This project is provided as-is for educational and practical use.
