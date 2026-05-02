package com.maverick.eventcontrolhub.registration;

import com.maverick.eventcontrolhub.common.RegistrationSource;
import com.maverick.eventcontrolhub.common.RegistrationStatus;
import com.maverick.eventcontrolhub.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    boolean existsByEventAndEmployeeIdIgnoreCase(Event event, String employeeId);

    boolean existsByEventAndEmployeeEmailIgnoreCase(Event event, String employeeEmail);

    boolean existsByEventAndDeviceToken(Event event, String deviceToken);

    Optional<Registration> findByEventAndDeviceToken(Event event, String deviceToken);

    Optional<Registration> findByQrToken(String qrToken);

    List<Registration> findByEventOrderByRegisteredAtDesc(Event event);

    long countByEventAndStatusIn(Event event, Collection<RegistrationStatus> statuses);

    long countByEventAndSource(Event event, RegistrationSource source);
}
