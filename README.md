# commons

![Java](https://img.shields.io/badge/Java-8+-orange)
![License](https://img.shields.io/badge/license-MIT-blue)
![Lightweight](https://img.shields.io/badge/dependencies-0-brightgreen)
![Maven Central](https://img.shields.io/maven-central/v/io.github.uncaughterrol/commons)

**commons** is a collection of lightweight, dependency-free Java utility libraries designed for everyday backend development.

Each module is **independently published** — pull only what you need.

---

# Modules

| Module                                   | Description                                                         | Version                                                                                            |
|------------------------------------------|---------------------------------------------------------------------|----------------------------------------------------------------------------------------------------|
| [commons-security](./commons-security)   | HMAC-SHA256 request signing and verification                        | ![Maven Central](https://img.shields.io/maven-central/v/io.github.uncaughterrol/commons-security)  |
| [commons-exception](./commons-exception) | Standardized API exceptions & factory                               | ![Maven Central](https://img.shields.io/maven-central/v/io.github.uncaughterrol/commons-exception) |
| [commons-model](./commons-model)         | API response wrappers & validation models                           | ![Maven Central](https://img.shields.io/maven-central/v/io.github.uncaughterrol/commons-model)     |
| [commons-utils](./commons-utils)         | Utility classes and helper functions shared across commons modules. | ![Maven Central](https://img.shields.io/maven-central/v/io.github.uncaughterrol/commons-utils)     |

> More modules coming soon.

---

# Installation

Pick only the modules you need.

## Maven
```xml
<dependency>
    <groupId>io.github.uncaughterrol</groupId>
    <artifactId>commons-security</artifactId>
    <version>0.4.0</version>
</dependency>

<dependency>
    <groupId>io.github.uncaughterrol</groupId>
    <artifactId>commons-exception</artifactId>
    <version>0.4.0</version>
</dependency>

<dependency>
    <groupId>io.github.uncaughterrol</groupId>
    <artifactId>commons-model</artifactId>
    <version>0.4.0</version>
</dependency>

<dependency>
    <groupId>io.github.uncaughterrol</groupId>
    <artifactId>commons-utils</artifactId>
    <version>0.4.0</version>
</dependency>
```

## Gradle
```gradle
implementation 'io.github.uncaughterrol:commons-security:0.4.0'
implementation 'io.github.uncaughterrol:commons-exception:0.4.0'
implementation 'io.github.uncaughterrol:commons-model:0.4.0'
implementation 'io.github.uncaughterrol:commons-utils:0.4.0'
```

---

# Module Details

## commons-security

HMAC-SHA256 request signing and verification.
```java
Map<String, String> payload = Map.of(
        "userId", "42",
        "action", "transfer"
);

// Sign
String signature = SignatureSigner.sign(payload, "my-secret-key");

// Verify
boolean valid = SignatureSigner.verify(payload, signature, "my-secret-key");
```

→ [Full documentation](./commons-security/README.md)


## commons-exception

Standardized API exceptions & factory.
```java
// 404 Not Found
throw ExceptionFactory.notFound("User", 42);

// 409 Conflict
throw ExceptionFactory.alreadyExists("User", "email", "john@example.com");

// 500 Internal Error
throw ExceptionFactory.internal("Connection failed", databaseError);
```

→ [Full documentation](./commons-exception/README.md)


## commons-model

API response wrappers and invalid parameter models.
```java
// Success response with payload
ApiResponse<UserDto> response = ApiResponse.success(
                "User Created",
                "User was created successfully",
                201,
                userDto
        );

// Error response without invalid params
ApiResponse<Void> response = ApiResponse.error(
        "Not Found",
        "User with id 42 does not exist",
        404
);

// Error response with invalid params (e.g. validation failure)
List<InvalidParam> invalidParams = List.of(
        new InvalidParam("email", "must be a valid email address"),
        new InvalidParam("age", "must be greater than 0")
);

ApiResponse<Void> response = ApiResponse.error(
        "Validation Failed",
        "Request contains invalid parameters",
        400,
        invalidParams
);
```

→ [Full documentation](./commons-model/README.md)

## commons-utils

Utility classes and helper functions shared across commons modules.

### Static Utility Usage (SmartStringUtils)
```java

import io.github.uncaughterrol.smartstring.SmartStringUtils;

public class Example {

    public static void main(String[] args) {

        String snake = SmartStringUtils.toSnakeCase("HelloWorldExample");
        System.out.println(snake);
        // hello_world_example

        String camel = SmartStringUtils.toCamelCase("hello_world");
        System.out.println(camel);
        // helloWorld

        String pascal = SmartStringUtils.toPascalCase("hello_world");
        System.out.println(pascal);
        // HelloWorld
    }
}

```

### Pluralization
```java

import io.github.uncaughterrol.smartstring.SmartStringUtils;

public class Example {

    public static void main(String[] args) {

        System.out.println(SmartStringUtils.toPlural("city"));
        // cities

        System.out.println(SmartStringUtils.toPlural("box"));
        // boxes

        System.out.println(SmartStringUtils.toPlural("dog"));
        // dogs
    }
}

```

### Singularization

```java
import io.github.uncaughterrol.smartstring.SmartStringUtils;

public class Example {

    public static void main(String[] args) {

        System.out.println(SmartStringUtils.toSingular("cities"));
        // city

        System.out.println(SmartStringUtils.toSingular("boxes"));
        // box
    }
}
```



→ [Full documentation](./commons-utils/README.md)

---

# License

MIT License