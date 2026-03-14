# Repository Guidelines

- Charset is UTF-8
- Line Separator is currently CRLF, but the new files you create should be LF as I am planning to migrate into LINUX/macOS.

## GitHub Issue/PR/Comment

- Always write in Korean
- For PR, follow the template `./.gihub/pull_request.template.md`

## Test Generation

1. Test Scope: `domain/*-domain` modules and `application/*-application` modules only.
2. Test Fixture: For domain instance generation, Fixture must be used as in `domain/*-domain/src/testFixtures`. Test fixtures must be extending `BaseFixtures` to use `Faker` to generate random inputs for every test instead of using a fixed input.
3. Test Fixture Rule: Each domain fixture should have meaningful methods for test cases. For example as below:
   ### Example
   - Happy case: `createAnnouncement(title, contents...)` -> `createValidAnnouncement()`. random title and contents should be generated in the fixture.
   - Fail case: `createAnnouncement(title, contents...)` -> `createAnnouncementWithTitle(title)`. random contents should be generated in the fixture, and the title, test target, should be passed.
   - Mulitple manipulation: `createAnnouncementsWithUsers(userId1, userId2...)`. Reuse other fixture methods within the fixture and the other values for multiple test cases should be passed.
4. Application Service Test: Use `@SpringBootTest`, and `@Transactional` should be used to automatically rollback. Input data should be prepared in `@BeforeEach`.
5. Test Execution: Run `./gradlew :bootstrap:bootstrap-gateway:flywayMigrate` first to build test DB if new DDL is applied, and then run the tests.
6. Other Testing Rule: Always explicitly include //given, //when, and //then although they can be merged into one. Always use `ThrowingCallable` lambda for //when in the fail cases and include that lambda in //then. Leave it blank if there is no given, when or then.
7. Test Method Naming: Always name the test methods in Korean. Not fixture, but only the test methods in \*Test.java file.

## New Module

Rules for new modules may differ by case. You have to consider the below first, but you have to make sure to suggest new settings if your creation setting does not fully fit to the simple rules.

1. Domain module: Domain module should be remained as a pure domain module without any external dependency except for testing. Dependency to other domain module is not allowed except for a special case. Do always add a dependency of `common` module.
2. Infra module: All external or technical I/O happens here. No unit testing needed.
3. Application module: Facade of the business logics and ports.
4. Bootstrap module: In Doc, all the error examples should be indicated as `@ExampleObject` for every exception that can be happened in dependent usecases and domains, but not in infra module. HTTP status codes for exception types and example contents for docs should be in `bootstrap:bootstrap-common`.

## Coding Style & Naming Conventions

### Baseline

Google Java Style in Checkstyle (`config/checkstyle/checkstyle.xml`)

### Others

- Blank line at the end of file
- Blank line after class header and before class end. Only one blank line for empty class.

  #### Example

  ```java
  // non-empty class example below
  class NonEmpty {

   {fields, methods places}

  }

  // empty class example below
  class Empty {

  }
  ```

- UpperCamelCase for class name, lowerCamelCase for field name and method name, and `UPPER_SNAKE_CASE` for constant name
- Avoid tab characters (`FileTabCharacter`)
- Lombok is available, but prefer explicit constructors when behavior matters. Otherwise, use Lombok annotations.
- Keep package names aligned with module boundaries, e.g., `com.ddudu.application.user.*`.
