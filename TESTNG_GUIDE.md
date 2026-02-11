# TestNG Test Framework - Quick Reference Guide

## Project Overview

This is now a **TestNG-based Selenium automation project** (no Cucumber/BDD).

**Framework Stack:**
- Selenium WebDriver 4.15.0
- TestNG 7.10.2 (test framework)
- Maven 3.6+ (build tool)
- Java 11
- Log4j (logging)
- GSON (JSON configuration)

---

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn test -Dtest=LoginTest
mvn test -Dtest=HomePageTest
mvn test -Dtest=EmployeeListTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=LoginTest#testUserLoginWithValidCredentials
mvn test -Dtest=HomePageTest#testHomePageLoads
```

### Run with Parallel Execution (edit testng.xml)
Currently set to: `parallel="tests" thread-count="1"`
- Change to `thread-count="3"` for 3 parallel threads

### Skip Tests During Build
```bash
mvn clean install -DskipTests
```

---

## Project Structure

```
vibecode-java/
├── pom.xml                    # Maven dependencies & plugins
├── testng.xml                 # Test suite configuration
│
├── src/main/java/com/eaapp/tests/
│   ├── core/
│   │   ├── TestBase.java      # Base class with @BeforeMethod/@AfterMethod
│   │   ├── BasePage.java      # Common page object methods
│   │   └── WebDriverFactory.java  # Creates WebDriver instances
│   │
│   ├── pages/                 # Page Object Model classes
│   │   ├── LoginPage.java
│   │   ├── HomePage.java
│   │   ├── EmployeeListPage.java
│   │   ├── EmployeeCreatePage.java
│   │   ├── EmployeeEditPage.java
│   │   └── EmployeeDeletePage.java
│   │
│   ├── config/
│   │   └── TestConfiguration.java  # Loads application.json settings
│   │
│   └── utilities/
│       └── ScreenshotHelper.java   # Takes screenshots on failure
│
├── src/test/java/com/eaapp/tests/
│   ├── LoginTest.java         # Test class with 3 test methods
│   ├── HomePageTest.java      # Test class with 2 test methods
│   └── EmployeeListTest.java  # Test class with 3 test methods
│
├── src/test/resources/
│   ├── application.json       # Test configuration (URLs, credentials)
│   ├── log4j.properties       # Logging configuration
│   └── testng.xml             # (Copy of root testng.xml)
│
└── target/
    ├── surefire-reports/      # HTML test reports
    └── test-classes/          # Compiled test classes
```

---

## Creating New Tests

### Step 1: Create Test Class
```java
package com.eaapp.tests;

import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class YourNewTest {
    private WebDriver driver;
    private YourPage yourPage;

    @BeforeMethod
    public void setUp() {
        // Initialize driver and page object
    }

    @Test(description = "Test description here")
    public void testName() {
        // Test logic
    }

    @AfterMethod
    public void tearDown() {
        // Clean up (quit driver)
    }
}
```

### Step 2: Add to testng.xml
```xml
<test name="Your Test Suite">
    <classes>
        <class name="com.eaapp.tests.YourNewTest"/>
    </classes>
</test>
```

### Step 3: Run
```bash
mvn test -Dtest=YourNewTest
```

---

## TestNG Annotations Guide

| Annotation | Purpose | Example |
|-----------|---------|---------|
| `@BeforeMethod` | Runs before each test | Setup WebDriver |
| `@AfterMethod` | Runs after each test | Quit WebDriver |
| `@BeforeSuite` | Runs before all tests | Initialize test environment |
| `@AfterSuite` | Runs after all tests | Cleanup resources |
| `@Test` | Marks method as test | `@Test(description="...") public void testName()` |
| `@DataProvider` | Provide test data | `@DataProvider(name="data") public Object[][] data()` |
| `@Parameters` | XML parameters | `@Parameters({"param1"}) public void test(String p1)` |

---

## Common Assertions

```java
// Basic assertions
Assert.assertTrue(condition);
Assert.assertFalse(condition);
Assert.assertEquals(actual, expected);
Assert.assertNotEquals(actual, expected);
Assert.assertNull(object);
Assert.assertNotNull(object);

// String assertions
Assert.assertTrue(text.contains("expected"));
Assert.assertTrue(text.startsWith("prefix"));
Assert.assertEquals(text, "exact value");
```

---

## Test Configuration (application.json)

Located in: `src/test/resources/application.json`

```json
{
  "testSettings": {
    "baseUrl": "http://eaapp.somee.com",
    "browser": "Chrome",
    "implicitWaitSeconds": 10,
    "explicitWaitSeconds": 30,
    "pageLoadTimeoutSeconds": 60,
    "headless": false,
    "maximizeWindow": true,
    "noSandbox": true
  },
  "testData": {
    "validUsername": "testuser123",
    "validPassword": "Test@1234",
    "validEmail": "test@test.com",
    "searchEmployeeName": "Karthik"
  }
}
```

---

## Understanding the Execution Flow

```
1. Maven runs: mvn clean test
   ↓
2. Surefire reads testng.xml
   ↓
3. TestNG discovers @Test methods
   ↓
4. For each test method:
   - Execute @BeforeMethod
   - Execute @Test
   - Execute @AfterMethod
   ↓
5. Generate reports (HTML, XML)
   ↓
6. Display results
```

---

## Test Report Locations

**HTML Report:**
```
target/surefire-reports/index.html
```

**XML Report:**
```
target/surefire-reports/testng-results.xml
```

---

## Parallel Execution

Edit `testng.xml`:
```xml
<suite name="eaapp Test Suite" verbose="2" parallel="tests" thread-count="3">
    <!-- Tests will run in parallel with 3 threads -->
</suite>
```

Options:
- `parallel="tests"` - Parallel at test level
- `parallel="methods"` - Parallel at method level
- `parallel="classes"` - Parallel at class level
- `thread-count="N"` - Number of threads

---

## Useful Maven Commands

```bash
# Clean and compile
mvn clean compile

# Run tests silently (less output)
mvn clean test -q

# Run with verbose output
mvn clean test -X

# Skip test compilation
mvn clean test -DskipTestCompile

# Package without running tests
mvn clean package -DskipTests

# View dependency tree
mvn dependency:tree
```

---

## Troubleshooting

### Tests not running
- Check testng.xml syntax
- Verify test classes are in correct package
- Ensure `@Test` annotation is present

### Browser not opening
- Check `headless: false` in application.json
- Verify WebDriver is initialized in @BeforeMethod
- Check if Chrome/browser is installed

### Tests timeout
- Increase `explicitWaitSeconds` in application.json
- Check if application is running/accessible
- Verify network connectivity

### Port conflicts
- Change browser port in WebDriverFactory if needed
- Ensure no other tests are running on same resources

---

## Next Steps

1. **Add more tests** to LoginTest, HomePageTest, EmployeeListTest
2. **Create test data files** for data-driven tests
3. **Add retry logic** with `@Retry` annotation
4. **Implement screenshot capture** on failures
5. **Configure parallel execution** for faster runs
6. **Add listener classes** for custom reporting
7. **Integrate with CI/CD** (Jenkins, GitHub Actions, etc.)

---

## Tips & Best Practices

✅ **Do:**
- One assertion per test or related assertions
- Use descriptive test method names
- Keep tests independent (no test dependencies)
- Use page objects for UI interactions
- Add logging for debugging
- Commit testng.xml to version control

❌ **Don't:**
- Hard-code test data (use application.json)
- Create test dependencies (test A depends on test B)
- Use Thread.sleep() (use WebDriverWait instead)
- Skip assertions to make tests pass
- Log sensitive data (passwords, tokens)

---

For more info: https://testng.org
