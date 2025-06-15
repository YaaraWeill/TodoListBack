# Todo List Backend

A simple RESTful Todo List backend service built with Vert.x and Java.

## Prerequisites

- Java 11 or later
- Maven 3.9.x or later

## Installation

### For macOS Users

1. Install Homebrew (if not already installed):
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```

2. Install Java 11:
   ```bash
   brew install openjdk@11
   ```

3. Set up JAVA_HOME:
   ```bash
   echo 'export JAVA_HOME="/usr/local/opt/openjdk@11"' >> ~/.zshrc
   echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
   source ~/.zshrc
   ```

4. Install Maven:
   ```bash
   brew install maven
   ```

### For Windows Users

1. Download and install Java 11:
   - Go to [Oracle JDK Downloads](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html) or [AdoptOpenJDK](https://adoptium.net/temurin/releases/?version=11)
   - Download the Windows installer
   - Run the installer and follow the installation wizard

2. Set up JAVA_HOME (Run PowerShell as Administrator):
   ```powershell
   [System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Java\jdk-11', 'Machine')
   $env:Path = [System.Environment]::GetEnvironmentVariable('Path', 'Machine')
   [System.Environment]::SetEnvironmentVariable('Path', "$env:Path;$env:JAVA_HOME\bin", 'Machine')
   ```

3. Install Maven:
   - Download Maven from [Maven Downloads](https://maven.apache.org/download.cgi)
   - Extract the downloaded archive to a directory (e.g., `C:\Program Files\Apache\maven`)
   - Add Maven to your PATH (Run PowerShell as Administrator):
     ```powershell
     [System.Environment]::SetEnvironmentVariable('MAVEN_HOME', 'C:\Program Files\Apache\maven', 'Machine')
     $env:Path = [System.Environment]::GetEnvironmentVariable('Path', 'Machine')
     [System.Environment]::SetEnvironmentVariable('Path', "$env:Path;$env:MAVEN_HOME\bin", 'Machine')
     ```

### Verify Installation (Both macOS and Windows)

1. Open a new terminal/PowerShell window and verify Java:
   ```bash
   # macOS/Linux
   java -version
   
   # Windows PowerShell
   java -version
   ```

2. Verify Maven:
   ```bash
   # macOS/Linux
   mvn -version
   
   # Windows PowerShell
   mvn -version
   ```

## Building the Project

```bash
mvn clean package
```

## Running the Application

Run the application using one of these methods:

1. Using the JAR file:
   ```bash
   java -jar target/todolist-backend-1.0-SNAPSHOT.jar
   ```

2. Using Maven:
   ```bash
   mvn exec:java -Dexec.mainClass="com.todolist.Main"
   ```

The server will start on port 8080.

## API Endpoints

### Get all todos
```bash
GET http://localhost:8080/todos
```

### Get todo by ID
```bash
GET http://localhost:8080/todos/{id}
```

### Create new todo
```bash
POST http://localhost:8080/todos
Content-Type: application/json

{
    "title": "My Todo",
    "description": "Description of my todo",
    "completed": false
}
```

### Update todo
```bash
PUT http://localhost:8080/todos/{id}
Content-Type: application/json

{
    "title": "Updated Todo",
    "description": "Updated description",
    "completed": true
}
```

### Delete todo
```bash
DELETE http://localhost:8080/todos/{id}
```

## Data Storage

Todos are stored in a JSON file (`todos.json`) in the project root directory.

## Logging

Logs are written to:
- Console output
- `logs/todolist.log` file
- Daily rolling log files in the `logs` directory

## Troubleshooting

### Common Issues

1. Java Version Issues:
   - Make sure Java 11 is installed
   - Verify JAVA_HOME is set correctly:
     ```bash
     # macOS/Linux
     echo $JAVA_HOME
     
     # Windows PowerShell
     echo $env:JAVA_HOME
     ```
   - Check Java version:
     ```bash
     java -version
     ```

2. Maven Issues:
   - Verify Maven installation:
     ```bash
     mvn -version
     ```
   - Try cleaning the project:
     ```bash
     mvn clean
     ```
   - Check Maven settings:
     ```bash
     # macOS/Linux
     cat ~/.m2/settings.xml
     
     # Windows
     type %USERPROFILE%\.m2\settings.xml
     ```

3. Port Already in Use:
   - Check if port 8080 is in use:
     ```bash
     # macOS/Linux
     lsof -i :8080
     
     # Windows
     netstat -ano | findstr :8080
     ```
   - Change the port in `MainVerticle.java` if needed

4. File Permission Issues:
   - Make sure you have write permissions in the project directory
   - Check if the `logs` directory exists and is writable
   - Ensure you can create and modify `todos.json` 