package com.maverick.eventcontrolhub.config;

import com.maverick.eventcontrolhub.common.EventStatus;
import com.maverick.eventcontrolhub.common.Role;
import com.maverick.eventcontrolhub.employee.Employee;
import com.maverick.eventcontrolhub.employee.EmployeeRepository;
import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.event.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class SeedDataConfig {
    @Bean
    CommandLineRunner seedData(EmployeeRepository employeeRepository,
                               EventRepository eventRepository) {
        return args -> {
            if (employeeRepository.count() == 0) {
                employeeRepository.save(new Employee("HR001", "HR Admin", "hr.admin@maverick.local", "Human Resources", Role.HR_ADMIN));
                employeeRepository.save(new Employee("SCAN001", "Scanner Operator", "scanner@maverick.local", "Operations", Role.SCANNER_OPERATOR));
                employeeRepository.save(new Employee("EMP001", "Aarav Mehta", "aarav.mehta@maverick.local", "Engineering", Role.EMPLOYEE));
                employeeRepository.save(new Employee("EMP002", "Diya Sharma", "diya.sharma@maverick.local", "Finance", Role.EMPLOYEE));
                employeeRepository.save(new Employee("EMP003", "Kabir Rao", "kabir.rao@maverick.local", "Sales", Role.EMPLOYEE));
                employeeRepository.save(new Employee("EMP004", "Nisha Iyer", "nisha.iyer@maverick.local", "Marketing", Role.EMPLOYEE));
                employeeRepository.save(new Employee("EMP005", "Rohan Gupta", "rohan.gupta@maverick.local", "Product", Role.EMPLOYEE));
            }

            if (eventRepository.count() == 0) {
                LocalDateTime now = LocalDateTime.now();
                eventRepository.save(new Event(
                        "Quarterly Town Hall",
                        "Leadership updates, recognitions, food service, and employee goodies distribution.",
                        "Maverick HQ Auditorium",
                        now.plusDays(7).withHour(10).withMinute(0),
                        now.plusDays(7).withHour(13).withMinute(0),
                        now.minusDays(1),
                        now.plusDays(6),
                        true,
                        true,
                        true,
                        now.plusDays(7).withHour(12).withMinute(0),
                        true,
                        true,
                        true,
                        "reg-quarterly-town-hall",
                        "walkin-quarterly-town-hall",
                        150,
                        EventStatus.OPEN
                ));

                eventRepository.save(new Event(
                        "Wellness Day",
                        "Employee wellness sessions with entry validation and healthy meal distribution.",
                        "Campus Lawn",
                        now.plusDays(18).withHour(9).withMinute(30),
                        now.plusDays(18).withHour(15).withMinute(0),
                        now,
                        now.plusDays(15),
                        false,
                        false,
                        false,
                        null,
                        true,
                        true,
                        false,
                        "reg-wellness-day",
                        null,
                        80,
                        EventStatus.OPEN
                ));
            }
        };
    }
}
