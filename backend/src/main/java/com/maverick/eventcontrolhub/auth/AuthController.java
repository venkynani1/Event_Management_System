package com.maverick.eventcontrolhub.auth;

import com.maverick.eventcontrolhub.employee.Employee;
import com.maverick.eventcontrolhub.employee.EmployeeDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final MockAuthService mockAuthService;

    public AuthController(MockAuthService mockAuthService) {
        this.mockAuthService = mockAuthService;
    }

    @PostMapping("/mock-login")
    public AuthResponse mockLogin(@Valid @RequestBody MockLoginRequest request) {
        Employee employee = mockAuthService.login(request);
        return new AuthResponse(EmployeeDto.from(employee), employee.getEmployeeCode(), MockAuthService.MOCK_AUTH_HEADER);
    }

    @GetMapping("/me")
    public EmployeeDto me(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode) {
        return EmployeeDto.from(mockAuthService.requireUser(employeeCode));
    }
}
