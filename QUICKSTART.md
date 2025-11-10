# TallyLite - Quick Start Guide

## Prerequisites

- **Java 17 or higher** (Check with `java -version`)
- **Gradle 7.0+** (or use the included Gradle wrapper)

## Building the Project

### Using Gradle Wrapper (Recommended)

**Windows:**
```bash
gradlew.bat build
```

**Linux/Mac:**
```bash
chmod +x gradlew
./gradlew build
```

### Running the Application

**Windows:**
```bash
gradlew.bat run
```

**Linux/Mac:**
```bash
./gradlew run
```

## Creating a Runnable JAR

**Windows:**
```bash
gradlew.bat jar
```

**Linux/Mac:**
```bash
./gradlew jar
```

The JAR file will be created at: `build/libs/TallyLite-1.0.0.jar`

Run it with:
```bash
java -jar build/libs/TallyLite-1.0.0.jar
```

## First Steps

1. **Start the application** - You'll see the home screen
2. **Create a Company:**
   - Click "Create Company"
   - Enter company name (e.g., "Demo Company")
   - Enter financial year (e.g., "2024-25")
   - Click "Create"
3. **Load the Company:**
   - Select the company from the list
   - Click "Load Company"
   - You'll be taken to the Dashboard

## Project Structure

```
TallyLite/
├── src/main/java/com/tallylite/
│   ├── model/          # Data models
│   ├── manager/        # Business logic
│   ├── controller/     # JavaFX controllers
│   ├── util/           # Utilities
│   └── TallyLiteApp.java
├── src/main/resources/
│   ├── com/tallylite/view/    # FXML files
│   └── com/tallylite/style/   # CSS files
└── build.gradle
```

## Data Location

All your data is stored locally in:
- **Windows:** `C:\Users\[YourUsername]\TallyLocal\`
- **Linux/Mac:** `~/TallyLocal/`

This folder contains:
- `Companies/` - All company data
- `Backups/` - Backup files
- `settings.json` - Application settings
- `logs.txt` - Application logs

## Troubleshooting

### Java Version Issues
Make sure you have Java 17 or higher:
```bash
java -version
```

### Build Errors
Try cleaning and rebuilding:
```bash
gradlew clean build
```

### JavaFX Runtime Issues
If you get JavaFX errors, make sure you're using Java 17+ and the build includes JavaFX modules.

## Next Steps

After Step 1 is complete, you can proceed with:
- **Step 2:** Ledger Management
- **Step 3:** Voucher Entry
- **Step 4:** Inventory Management
- **Step 5:** Reports
- **Step 6:** Themes & Backup
- **Step 7:** Final Polish

## Development

The project uses:
- **JavaFX** for UI
- **Gson** for JSON handling
- **Gradle** for build management

All modules are designed to be modular and can be extended independently.

