package com.maverick.eventcontrolhub.employee;

import com.maverick.eventcontrolhub.common.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "employees", uniqueConstraints = {
        @UniqueConstraint(name = "uk_employee_code", columnNames = "employee_code"),
        @UniqueConstraint(name = "uk_employee_email", columnNames = "email")
})
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false)
    private String employeeCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    protected Employee() {
    }

    public Employee(String employeeCode, String name, String email, String department, Role role) {
        this.employeeCode = employeeCode;
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public Role getRole() {
        return role;
    }
}
