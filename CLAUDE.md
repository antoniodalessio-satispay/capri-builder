# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Capri Builder is a lightweight alternative to Lombok that provides a `@Builder` annotation for Java records. It uses annotation processing to generate builder classes at compile time.

## Commands

**Build the project:**
```bash
mvn clean compile
```

**Run tests:**
```bash
mvn test
```

**Run a specific test class:**
```bash
mvn test -Dtest=BuilderTest
```

**Package the library:**
```bash
mvn clean package
```

**Install to local repository:**
```bash
mvn clean install
```

## Architecture

### Core Components

1. **`@Builder` Annotation** (`annotation.com.satispay.builder.Builder`)
   - Annotation that can be applied to records
   - Configurable builder class name and static method name
   - Retention policy: SOURCE (only available during compilation)

2. **BuilderProcessor** (`processor.com.satispay.builder.BuilderProcessor`)
   - Annotation processor that generates builder classes
   - Uses JavaPoet for code generation
   - Generates a helper class with static builder method and nested builder class

3. **Generated Code Pattern**
   - For a record `Person`, generates `PersonBuilderHelper` class
   - Contains static `builder()` method that returns a `PersonBuilder` instance
   - Builder has fluent methods for each record component
   - Builder has `build()` method that constructs the record

### Dependencies

- **JavaPoet**: For generating Java source code
- **Google Auto Service**: For annotation processor registration
- **JUnit 5**: For testing
- **Google Compile Testing**: For testing annotation processors

## Usage

### Maven Dependency

To use Capri Builder in your Maven project, add the following dependency:

```xml
<dependency>
    <groupId>com.capri</groupId>
    <artifactId>capri-builder</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Code Example

```java
@Builder
public record Person(String name, int age, String email) {}

// Generated usage:
Person person = PersonBuilderHelper.builder()
    .name("John Doe")
    .age(30)
    .email("john@example.com")
    .build();
```

## Development Notes

- Requires Java 17+ (for record support)
- Annotation processor automatically registered via `META-INF/services`
- Generated classes are created in the same package as the annotated record
- The processor validates that `@Builder` is only applied to records
- Tests require compilation to generate builder classes before they can fully execute
- Generated classes follow the pattern `<RecordName>BuilderHelper` with nested `<RecordName>Builder`

### Maven Configuration Issue

Currently, the annotation processor doesn't generate builders for test classes in the same Maven project during test compilation. This is a common limitation where annotation processors can't process their own project's test classes during the same build cycle.

**Workaround**: Use the library after it's been compiled and installed:
1. `mvn clean install` to build and install the library locally
2. Create a separate project or module that depends on the installed library
3. The annotation processing will work correctly in consuming projects

**Example for external projects**:
```xml
<dependency>
    <groupId>com.capri</groupId>
    <artifactId>capri-builder</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```