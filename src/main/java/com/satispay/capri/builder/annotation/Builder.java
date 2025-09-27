package com.satispay.capri.builder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates a builder class for the annotated record.
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * @Builder
 * public record Person(String name, int age, String email) {}
 * }
 * </pre>
 *
 * <p>This will generate a {@code PersonBuilder} class with methods to set each field
 * and a {@code build()} method to create the record instance.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Builder {
    /**
     * The name of the generated builder class. If not specified, defaults to
     * the record name + "Builder".
     */
    String builderClassName() default "";

    /**
     * The name of the static method that creates a new builder instance.
     * Defaults to "builder".
     */
    String builderMethodName() default "builder";
}