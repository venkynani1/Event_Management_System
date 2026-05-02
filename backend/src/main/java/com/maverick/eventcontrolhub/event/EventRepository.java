package com.maverick.eventcontrolhub.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByRegistrationSlug(String registrationSlug);

    Optional<Event> findByWalkinSlug(String walkinSlug);
}
