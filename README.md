# 🎬 CinemaReserve

CinemaReserve is a *Java console-based movie ticket booking system* integrated with a MySQL database.

## 📌 Features
- User *Registration* and *Login*
- View available movies
- Book tickets (with seat availability check)
- View user bookings
- MySQL Database Integration via JDBC

## 🛠️ Tech Stack
- *Java* (Core Java, JDBC)
- *MySQL* (Database)
- *MySQL Connector/J* (JDBC Driver)
- *VS Code* or IntelliJ IDEA
- *Git & GitHub* for version control

## 📂 Project Structure

CinemaReserve/
 ├── src/
 │    ├── DBConnection.java
 │    └── Main.java
 ├── lib/
 │    └── mysql-connector-j-9.4.0.jar
 ├── db/
 │    └── cinema_db.sql
 └── README.md


## 🚀 How to Run
1. *Create the database* in MySQL:
sql
source db/cinema_db.sql;

2. Place the MySQL Connector .jar file inside the lib/ folder.
3. *Compile the code*:
bash
javac -cp "lib/mysql-connector-j-9.4.0.jar" src/*.java

4. *Run the program*:
bash
java -cp "src;lib/mysql-connector-j-9.4.0.jar" Main


## 👤 Author
- *Your Name*  
- [GitHub Profile](https://github.com/prathameshBR)