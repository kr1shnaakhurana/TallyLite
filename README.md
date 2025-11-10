# TallyLite - Offline Accounting Software

A Tally-like offline accounting software built with JavaFX and JSON storage.

## Features

✅ **Step 1 Complete:**
- Company Management (Create, Load, Delete)
- Home Screen with company selection
- Dashboard with module navigation
- Settings and logging system
- Modern dark-blue UI theme

✅ **Step 2 Complete:**
- Ledger Management System (CRUD operations)
- Ledger dialog with validation
- Duplicate name prevention
- TableView with formatted display

✅ **Step 3 Complete:**
- Voucher Entry System
- Complete form with validation
- Debit/Credit ledger selection
- Voucher viewing TableView
- Comprehensive field validation

✅ **Step 4 Complete:**
- Inventory Management System (CRUD operations)
- Inventory dialog with validation
- Stock valuation (quantity * rate)
- Integration with vouchers (auto-update stock on Sales/Purchase)

✅ **Step 5 Complete:**
- Reports Module (Trial Balance, P&L, Balance Sheet)
- Report calculations from ledgers and vouchers
- TableView display with formatted numbers
- PDF & Excel Export (fully implemented)

✅ **Step 6 Complete:**
- Backup & Restore functionality (ZIP-based)
- Theme switcher (dark/light)
- Keyboard shortcuts (Ctrl+N, Ctrl+V, Esc)
- Enhanced Settings screen

✅ **Step 7 Complete:**
- Final polish & dashboard integration
- Enhanced About dialog
- Auto-select recent company
- Professional UI enhancements

✅ **Dashboard UI Enhancements:**
- Professional statistics cards with real-time data
- Interactive hover effects and animations
- Quick action buttons for fast navigation
- Financial summary panel
- Smooth transitions and modern design

🎉 **Project Complete!** All 7 steps implemented and working with a professional, engaging dashboard.

## Tech Stack

- **JavaFX** 17+ (FXML + CSS)
- **Gson** 2.10.1 (JSON serialization)
- **Apache POI** 5.2.3 (Excel export)
- **iText** 7.2.3 (PDF export)
- **Apache Commons Compress** 1.24.0 (ZIP handling)

## Project Structure

```
TallyLite/
├── src/main/java/com/tallylite/
│   ├── model/              # Data models (Company, Ledger, Voucher, Inventory, Settings)
│   ├── manager/            # Business logic (CompanyManager, etc.)
│   ├── controller/         # JavaFX controllers
│   ├── util/               # Utilities (FileManager, Logger, SettingsManager)
│   └── TallyLiteApp.java   # Main application class
├── src/main/resources/com/tallylite/
│   ├── view/               # FXML files
│   └── style/              # CSS files
└── build.gradle            # Build configuration
```

## Data Storage

All data is stored locally in:
```
~/TallyLocal/
├── Companies/
│   └── [CompanyName]/
│       ├── company.json
│       ├── ledgers.json
│       ├── vouchers.json
│       └── inventory.json
├── Backups/
├── settings.json
└── logs.txt
```

## Building and Running

### Prerequisites
- Java 17 or higher
- No need to install Gradle - the project includes Gradle Wrapper

### Build
**Windows:**
```bash
gradlew.bat build
```

**Linux/Mac:**
```bash
./gradlew build
```

### Run
**Windows:**
```bash
gradlew.bat run
```

**Linux/Mac:**
```bash
./gradlew run
```

### Create JAR
**Windows:**
```bash
gradlew.bat jar
```

**Linux/Mac:**
```bash
./gradlew jar
```

The JAR will be created in `build/libs/TallyLite-1.0.0.jar`

### Note
If you encounter "Could not find or load main class org.gradle.wrapper.GradleWrapperMain", make sure `gradle/wrapper/gradle-wrapper.jar` exists. If missing, download it from:
https://github.com/gradle/gradle/raw/v7.6.0/gradle/wrapper/gradle-wrapper.jar

## Usage

1. **Start the application** - The home screen will appear
2. **Create a Company** - Click "Create Company" and enter details
3. **Load a Company** - Select a company from the list and click "Load Company"
4. **Navigate Modules** - Use the sidebar in the dashboard to access different modules

## Development Roadmap

Follow the 7-step roadmap provided to build the complete application:
- Each step builds upon the previous one
- Modules are designed to be modular and extensible
- All data persists in JSON format for easy debugging

## License

This project is open source and available for educational purposes.

