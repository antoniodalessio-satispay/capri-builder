package com.satispay.capri.builder;

import com.satispay.capri.builder.annotation.Builder;

/**
 * Example record demonstrating the @Builder annotation usage.
 */
@Builder
public record PersonExample(String name, int age, String email, boolean active) {
}