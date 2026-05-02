package com.maverick.eventcontrolhub.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record WalkInRequest(
        @NotBlank String employeeCode,
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String department
) {
}
