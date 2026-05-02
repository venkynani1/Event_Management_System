package com.maverick.eventcontrolhub.employee;

import com.maverick.eventcontrolhub.common.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findFirstByRole(Role role);

    List<Employee> findByRole(Role role);
}
