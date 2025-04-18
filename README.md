# Trader-Journal

**Trader-Journal** is a personal investment journal built in Java that helps you manually track and reflect on your trading decisions over time.

Each day, you can create a journal entry that contains detailed information about the assets you’ve bought or sold, along with notes and comments. 
The data is stored using JPA and an embedded H2 database, making it easy to save and revisit past entries.

Functionality for analyzing entries and identifying behavioral patterns and trends is currently under development. 
This will make it easier to spot habits, strengths, and areas for improvement in your trading over time.

## Tech Stack

- Java 17
- Maven
- JavaFX 17
- JPA (Hibernate)
- H2 (in-memory database)
- Lombok

### JavaFX Setup

This project uses **JavaFX 17** for its graphical interface. To run the GUI properly, you need to set up the JavaFX runtime on your local machine.

**GUI Main Class:** `se.pbt.ui.TraderJournalGui`  
This is the class that launches the graphical interface and should be used when running the application.

#### Step 1: Download JavaFX SDK
1. Go to the Gluon [product page](https://gluonhq.com/products/javafx/) (it´s free)
2. Download **JavaFX SDK 17** for your operating system.
3. Extract the SDK to a convenient location, e.g.:
```bash 
C:\Program Files\javafx\javafx-sdk-17\lib
 ```
☝️ Make sure to replace the path with your actual JavaFX SDK location.

#### Step 2: Configure your runtime environment
When running the application, make sure to provide the JavaFX module path and required modules as VM options.
```bash 
--module-path "C:\your_chosen_path\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.fxml
 ```
Where to set this:
* In an IDE: Add it to the VM options field of your run configuration.
* From the command line: Add it after java in your launch command:
```bash
java --module-path "C:\your_chosen_path\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.fxml -cp your-jar-file.jar your.MainClass
 ```
☝️ Again, make sure to replace the path with your actual JavaFX SDK location.

### Lombok
This project uses Lombok, a library that automatically generates boilerplate code such as getters, setters, and constructors. 
Make sure Lombok support is enabled and that the necessary plugin is installed in your development environment.
