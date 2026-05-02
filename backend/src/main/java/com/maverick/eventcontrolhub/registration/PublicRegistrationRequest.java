package com.maverick.eventcontrolhub.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PublicRegistrationRequest(
        @NotBlank String employeeId,
        @NotBlank String employeeName,
        @Email @NotBlank String employeeEmail,
        @NotBlank String deviceToken
) {
}
