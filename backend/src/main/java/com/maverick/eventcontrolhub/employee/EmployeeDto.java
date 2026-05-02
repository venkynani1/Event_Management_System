package com.maverick.eventcontrolhub.employee;

import com.maverick.eventcontrolhub.common.Role;

public record EmployeeDto(
        Long id,
        String employeeCode,
        String name,
        String email,
        String department,
        Role role
) {
    public static EmployeeDto from(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getRole()
        );
    }
}
