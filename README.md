# Capri Builder

<p align="center">
  <img width="800" height="800" alt="CapriLombok" src="https://github.com/user-attachments/assets/9889184f-c565-423c-bc62-169d144fc0b0">
</p>


A lightweight alternative to Lombok providing `@Builder` annotation for Java records

## Features

- ✨ Simple `@Builder` annotation for Java records
- 🚀 Zero runtime dependencies
- 📦 Compile-time code generation using annotation processing
- 🎯 Focused specifically on records (Java 17+)
- 🔧 Configurable builder class and method names

## Installation

Add the following dependency to your Maven project:

```xml
<dependency>
    <groupId>com.satispay.capri</groupId>
    <artifactId>capri-builder</artifactId>
    <version>1.0.2-SNAPSHOT</version>
</dependency>
```

## Usage

Simply annotate your record with `@Builder`:

```java
import com.satispay.capri.builder.annotation.Builder;

@Builder
public record Person(String name, int age, String email) {}
```

This generates a builder helper class that you can use:

```java
Person person = PersonBuilderHelper.builder()
    .name("John Doe")
    .age(30)
    .email("john@example.com")
    .build();
```

### Configuration

The `@Builder` annotation supports customization:

```java
@Builder(builderClassName = "PersonBuilder", builderMethodName = "newBuilder")
public record Person(String name, int age, String email) {}

// Usage:
Person person = PersonBuilderHelper.newBuilder()
    .name("Jane Doe")
    .age(25)
    .email("jane@example.com")
    .build();
```

## Requirements

- Java 17 or higher (for record support)
- Maven 3.6+ (for building from source)

## Building from Source

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Package the library
mvn clean package

# Install to local repository
mvn clean install
```

## How it Works

Capri Builder uses annotation processing to generate builder classes at compile time:

1. **Annotation Processing**: The `BuilderProcessor` scans for `@Builder` annotations on records
2. **Code Generation**: Uses JavaPoet to generate a helper class with a nested builder
3. **Pattern**: For a record `MyRecord`, generates `MyRecordBuilderHelper` containing `MyRecordBuilder`

The generated code follows this pattern:
- `MyRecordBuilderHelper.builder()` returns a new `MyRecordBuilder` instance
- Each record component gets a fluent setter method
- `build()` method constructs the final record instance
