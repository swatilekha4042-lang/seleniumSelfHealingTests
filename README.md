# Selenium Auto-Heal

A Java Selenium automation project using Maven and TestNG. It includes a self-healing locator utility that can ask an LLM for alternate Selenium locators when an element lookup fails.

## Tech Stack

- Java 11
- Maven
- Selenium WebDriver
- TestNG
- WebDriverManager
- ExtentReports
- Ollama chat API integration

## Project Structure

```text
selenium-autoheal/
├── pom.xml
├── testng.xml
├── README.md
├── PERSISTENCE_CACHE_IMPLEMENTATION.md
└── src/
    ├── main/java/com/eaapp/tests/
    │   ├── config/      # test configuration reader
    │   ├── core/        # WebDriver setup and base classes
    │   ├── pages/       # page objects
    │   └── utilities/   # LLM, reports, screenshots, locator healing
    └── test/
        ├── java/com/eaapp/tests/   # TestNG tests
        └── resources/              # app, LLM, and logging config
```

## Setup

Install dependencies and compile:

```bash
mvn clean compile
```

Optional: set an Ollama API key for LLM-backed locator healing and LLM integration tests:

```bash
export OLLAMA_API_KEY="your-api-key"
```

## Configuration

- App and browser settings: `src/test/resources/application.json`
- LLM settings: `src/test/resources/llm-config.json`
- Test suite: `testng.xml`

## Run Tests

Run the default suite:

```bash
mvn test
```

Run a specific test class:

```bash
mvn test -Dtest=LoginTest
mvn test -Dtest=LLMIntegrationTest
```

## Key Files

- `LoginPage.java`: login page object
- `SelfHealingLocators.java`: locator retry, LLM healing, and cache logic
- `LLMClient.java`: Ollama chat API client
- `WebDriverFactory.java`: browser setup
- `ExtentManager.java`: Extent report setup

## Generated Output

Maven and test runs may create:

- `target/`
- `target/ExtentReport.html`
- `target/locator_cache.json`
- `target/surefire-reports/`

Delete `target/locator_cache.json` to force locator healing to run again.
