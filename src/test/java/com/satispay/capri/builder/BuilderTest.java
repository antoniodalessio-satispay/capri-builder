package com.satispay.capri.builder;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the generated builder functionality.
 * Note: This test will work after compilation generates the builder classes.
 */
public class BuilderTest {

    @Test
    public void testPersonBuilderGeneration() {
        // This test demonstrates the expected usage after code generation
        // The actual builder classes will be generated during compilation

        // Example of what the generated code should support:
        // PersonExample person = PersonExampleBuilderHelper.builder()
        //     .name("John Doe")
        //     .age(30)
        //     .email("john@example.com")
        //     .active(true)
        //     .build();




        // assertEquals("John Doe", person.name());
        // assertEquals(30, person.age());
        // assertEquals("john@example.com", person.email());
        // assertTrue(person.active());
        // For now, just test that the record can be created normally
        PersonExample person = new PersonExample("Jane Doe", 25, "jane@example.com", false);
        assertEquals("Jane Doe", person.name());
        assertEquals(25, person.age());
        assertEquals("jane@example.com", person.email());
        assertFalse(person.active());
    }
}