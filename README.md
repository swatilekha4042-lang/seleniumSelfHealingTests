# EA Employee App - Java Selenium Test Automation Framework

This is a Java implementation of the Selenium C# test automation framework for the EA Employee Application. It's built using Cucumber BDD, Selenium WebDriver, and Maven.

## Project Structure

```
vibecode-java/
├── src/
│   ├── main/java/com/eaapp/tests/
│   │   ├── config/                  # Configuration classes
│   │   │   └── TestConfiguration.java
│   │   ├── core/                    # Core framework classes
│   │   │   ├── BasePage.java
│   │   │   ├── TestBase.java
│   │   │   └── WebDriverFactory.java
│   │   ├── pages/                   # Page Object Model classes
│   │   │   ├── LoginPage.java
│   │   │   ├── HomePage.java
│   │   │   ├── EmployeeListPage.java
│   │   │   ├── EmployeeCreatePage.java
│   │   │   ├── EmployeeEditPage.java
│   │   │   ├── EmployeeDeletePage.java
│   │   │   └── RegisterPage.java
│   │   └── utilities/               # Utility classes
│   │       └── ScreenshotHelper.java
│   └── test/
│       ├── java/com/eaapp/tests/
│       │   ├── hooks/               # Cucumber hooks
│       │   └── stepdefinitions/     # Step definitions
│       │       ├── LoginSteps.java
│       │       ├── HomePageSteps.java
│       │       ├── EmployeeListSteps.java
│       │       ├── RegistrationSteps.java
│       │       └── EmployeeCRUDSteps.java
│       └── resources/
│           ├── features/            # Cucumber feature files
│           ├── application.json     # Test configuration
│           ├── cucumber.properties
│           └── log4j.properties
├── pom.xml                          # Maven configuration
└── README.md
```

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- Chrome, Firefox, or Edge browser installed

## Dependencies

The project uses the following key dependencies:

- **Selenium WebDriver 4.15.0** - Web automation
- **Cucumber 7.14.0** - BDD framework
- **WebDriverManager 5.6.3** - Automatic driver management
- **TestNG / JUnit** - Test frameworks
- **Log4j** - Logging
- **GSON** - JSON parsing

## Setup

### 1. Clone/Extract the project
```bash
cd vibecode-java
```

### 2. Install dependencies
```bash
mvn clean install
```

### 3. Configure the application
Edit `src/test/resources/application.json` to set:
- Base URL of the application
- Browser type (Chrome, Firefox, Edge)
- Wait timeouts
- Screenshot settings
- Test data

## Running Tests

### Run all tests
```bash
mvn clean test
```

### Run specific test class
```bash
mvn test -Dtest=LoginTests
```

### Run with specific browser
```bash
mvn test -Dbrowser=Firefox
```

### Run Cucumber tests
```bash
mvn test -Dtest=CucumberRunnerTest
```

## Test Structure

### Page Object Model
All page classes inherit from `BasePage` which provides:
- Explicit waits for elements
- Common interaction methods (click, sendKeys, etc.)
- Navigation methods
- JavaScript execution
- Element visibility checks

### BasePage Methods

```java
// Wait methods
WebElement waitForElement(By locator)
WebElement waitForElementToBeClickable(By locator)
boolean waitForElementToDisappear(By locator)

// Action methods
void click(By locator)
void sendKeys(By locator, String text)
String getText(By locator)
String getAttribute(By locator, String attribute)
void selectDropdownByText(By locator, String text)
void scrollToElement(By locator)

// Navigation
void navigateTo(String url)
String getCurrentUrl()
String getPageTitle()
```

### Step Definitions
BDD step definitions are organized by feature:
- `LoginSteps.java` - User login scenarios
- `RegistrationSteps.java` - User registration
- `HomePageSteps.java` - Home page navigation
- `EmployeeListSteps.java` - Employee listing
- `EmployeeCRUDSteps.java` - Create, Edit, Delete employees

## Configuration

### application.json
```json
{
  "testSettings": {
    "baseUrl": "http://eaapp.somee.com",
    "browser": "Chrome",
    "implicitWaitSeconds": 10,
    "explicitWaitSeconds": 30,
    "pageLoadTimeoutSeconds": 60,
    "takeScreenshotOnFailure": true,
    "screenshotPath": "./screenshots",
    "headless": false,
    "maximizeWindow": true
  },
  "testData": {
    "validUsername": "testuser123",
    "validPassword": "Test@1234",
    "validEmail": "test@test.com",
    "searchEmployeeName": "Karthik"
  }
}
```

## Logging

Logs are configured in `log4j.properties`:
- Console output for all levels
- File output to `./logs/test-automation.log`
- Rotating file appender (max 5MB per file, 10 backups)

## Screenshots

Screenshots are automatically taken on test failure and saved to the configured `screenshotPath` directory with timestamps.

## Comparison with C# Version

Key differences when converting from C# to Java:

| C# | Java |
|---|---|
| NUnit | JUnit / TestNG |
| OpenQA.Selenium.* | org.openqa.selenium.* |
| WebDriverWait | WebDriverWait |
| IWebDriver | WebDriver |
| By | By |
| TestContext | JUnit @Rule or method parameters |
| appsettings.json | application.json + GSON |
| Properties with backing fields | Java properties with getters |
| `?.` null-conditional | null checks in methods |

## Best Practices

1. **Use Page Objects** - All UI interaction should go through page objects
2. **Explicit Waits** - Use explicit waits instead of Thread.sleep()
3. **Meaningful Assertions** - Use descriptive assertion messages
4. **Data-Driven Testing** - Use external data sources for test data
5. **Logging** - Use Log4j for proper logging
6. **Clean Code** - Follow Java naming conventions and best practices

## Troubleshooting

### Driver not found
- Ensure WebDriverManager is properly configured
- Check that browser is installed on the system

### Tests fail to find elements
- Verify selectors match the actual application
- Check that the application is running and accessible
- Review the screenshots in the `screenshots` folder

### Timeout errors
- Increase the `explicitWaitSeconds` in configuration
- Check the application performance
- Verify network connectivity

## Contributing

1. Follow the existing code structure
2. Use meaningful variable and method names
3. Add logging for debugging
4. Update this README if adding new features

## License

This project is provided as-is for testing purposes.
