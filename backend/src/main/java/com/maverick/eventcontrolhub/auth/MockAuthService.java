package com.maverick.eventcontrolhub.auth;

import com.maverick.eventcontrolhub.common.ApiException;
import com.maverick.eventcontrolhub.common.Role;
import com.maverick.eventcontrolhub.employee.Employee;
import com.maverick.eventcontrolhub.employee.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class MockAuthService {
    // Development-only mock authentication. Replace this service with the SSO principal resolver for production.
    public static final String MOCK_AUTH_HEADER = "X-Employee-Code";

    private final EmployeeRepository employeeRepository;

    public MockAuthService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee login(MockLoginRequest request) {
        if (request.employeeCode() != null && !request.employeeCode().isBlank()) {
            Employee employee = employeeRepository.findByEmployeeCode(request.employeeCode())
                    .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unknown employee code"));
            if (employee.getRole() != request.role()) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Employee does not have requested role");
            }
            return employee;
        }

        return employeeRepository.findFirstByRole(request.role())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "No mock user for role " + request.role()));
    }

    public Employee requireUser(String employeeCode) {
        if (employeeCode == null || employeeCode.isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing mock auth header " + MOCK_AUTH_HEADER);
        }
        return employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unknown mock user"));
    }

    public Employee requireRole(String employeeCode, Role role) {
        Employee employee = requireUser(employeeCode);
        if (employee.getRole() != role) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Requires role " + role);
        }
        return employee;
    }

    public Employee requireAnyRole(String employeeCode, Role... roles) {
        Employee employee = requireUser(employeeCode);
        for (Role role : roles) {
            if (employee.getRole() == role) {
                return employee;
            }
        }
        throw new ApiException(HttpStatus.FORBIDDEN, "User role is not allowed for this operation");
    }
}
