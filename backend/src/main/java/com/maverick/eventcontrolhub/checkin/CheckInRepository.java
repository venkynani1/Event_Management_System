package com.maverick.eventcontrolhub.checkin;

import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    boolean existsByRegistration(Registration registration);

    Optional<CheckIn> findByRegistration(Registration registration);

    List<CheckIn> findByEventOrderByCheckedInAtDesc(Event event);

    long countByEvent(Event event);
}
