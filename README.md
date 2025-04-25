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

## Running the application

JavaFX dependencies are handled via Maven using the **JavaFX Maven plugin**, so no manual SDK setup is required unless you're running the app in an IDE.

**Note:** Always run commands from the project root directory (where the `pom.xml` file is located).

### ▶ Run with Maven (Recommended)

The application is configured to run directly from the terminal using Maven:
```bash
  mvn clean javafx:run
```
This command will build the project and launch the GUI automatically using the class se.pbt.ui.TraderJournalGui.

### Running the application in an IDE (Optional)

If you prefer to run the application manually in an IDE some extra setup is required due to how JavaFX is modularized.

Step 1: Download JavaFX SDK
Go to the Gluon [product page](https://gluonhq.com/products/javafx/)(it's free) to download JavaFX SDK 17, and extract it to a location of your choice, for example:
``` 
C:\Program Files\javafx\javafx-sdk-17
 ```

Step 2: Add VM options in your IDE's run configuration
In your IDE's run configuration for the main class se.pbt.ui.TraderJournalGui, add the following VM options:
``` 
--module-path "C:\your_chosen_path\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.fxml
 ```
☝️ Make sure to replace the path with your actual JavaFX SDK location.

### Lombok
This project uses Lombok, a library that automatically generates boilerplate code such as getters, setters, and constructors. 
Make sure Lombok support is enabled and that the necessary plugin is installed in your development environment.
