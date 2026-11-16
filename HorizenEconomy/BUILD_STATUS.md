# Build verification status

- Paper 26.3 API dependency and Java 25 target configured from current PaperMC developer documentation.
- Maven project XML parsed successfully.
- All plugin YAML files parsed successfully.
- Java source passed a compiler parser scan for syntax errors.
- SQLite schema DDL executed successfully in an in-memory SQLite smoke test.
- A basic order escrow SQL smoke test passed.
- JUnit tests are included for whole-rupee parsing and display formatting.

Not verified here:
- Full Maven compilation/package, because this execution environment has Java 21, no Maven installation, and no network access to obtain Java 25/Maven/Paper API dependencies.
- JUnit execution.
- Live Paper 26.3 server integration, GUI click behavior, permissions in a real permission manager, restart recovery, concurrent multi-player load, and Pterodactyl deployment.

Do not install a JAR until `mvn clean verify` succeeds on Java 25 and the resulting JAR has been tested on a copy of the target server.
