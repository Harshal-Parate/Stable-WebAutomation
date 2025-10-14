# 🧪 Saucedemo Web Automation Framework

A modern, maintainable **Selenium + TestNG + Allure** automation framework for **Sauce Labs Demo (Swag Labs)**
application.

This framework supports:

- ✅ Local and Remote browser execution (Selenium Grid / Docker / BrowserStack)
- ✅ Environment-based configuration (QA, Stage, Prod)
- ✅ Data-driven testing using `@DataProvider`
- ✅ Automatic retry of failed tests (with Allure reporting)
- ✅ Parallel execution support
- ✅ Centralized configuration and user credentials
- ✅ CI/CD integration (Jenkins scheduled builds + Allure reporting)

---
🧩 Design Principles
 - DRY (Don’t Repeat Yourself)
 - KISS (Keep It Simple and Stable)
 - Single Responsibility: each class (page object, driver manager, config reader) does exactly one job
 - Thread Safety via ThreadLocal<WebDriver>
 - Configurable & Environment-Driven
 - --
🧠 How Tests Work
 - Each test:
 - Loads environment config through Config.java 
 - Initializes driver through DriverFactory 
 - Reads credentials using UsersLoader 
 - Executes page-object-based actions 
 - Reports results via Allure
 - --

 - mvn test -Dbrowser=chrome
 - grid.url=http://localhost:4444/wd/hub
 - docker run -d -p 4444:4444 -p 7900:7900 \
  --name selenium-grid \
 selenium/standalone-chrome:latest
 - Then open http://localhost:7900
 - (password = secret) to view live sessions.
 - allure serve target/allure-results



| Purpose                 | Command                                            |
| ----------------------- | -------------------------------------------------- |
| Clean & run tests       | `mvn clean test`                                   |
| Run on specific browser | `mvn test -Dbrowser=edge`                          |
| Run with grid           | `mvn test -Dgrid.url=http://localhost:4444/wd/hub` |
| Generate Allure report  | `mvn allure:report`                                |
| Serve Allure live       | `allure serve target/allure-results`               |
| Skip tests (build only) | `mvn clean install -DskipTests`                    |
