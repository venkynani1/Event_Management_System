package com.maverick.eventcontrolhub.auth;

import com.maverick.eventcontrolhub.employee.EmployeeDto;

public record AuthResponse(
        EmployeeDto user,
        String mockToken,
        String headerName
) {
}
