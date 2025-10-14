-Dconfig=/path/to/my.properties

EnvironmentManager.fromString(Config.get("env")).getBaseUrl()

Notes on remote/grid readiness:

grid.url in config (e.g. http://localhost:4444/wd/hub or http://selenium-grid:4444) will switch to remote.

SauceLabs / BrowserStack: add desired capability entries (username, accessKey) via Config and DesiredCapabilities as necessary.

ThreadLocal ensures parallel tests don’t share drivers.

mvn -Dconfig=src/test/resources/config.properties -Dusers.file=src/test/resources/users.json test -Dsurefire.suiteXmlFiles=testng.xml

mvn test -Dbrowser=chrome -Dconfig=ci/config.properties -Dusers.file=ci/users.json
