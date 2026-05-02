package com.maverick.eventcontrolhub.auth;

import com.maverick.eventcontrolhub.common.Role;
import jakarta.validation.constraints.NotNull;

public record MockLoginRequest(
        @NotNull Role role,
        String employeeCode
) {
}
