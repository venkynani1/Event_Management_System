package com.maverick.eventcontrolhub.entitlement;

import com.maverick.eventcontrolhub.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventEntitlementRepository extends JpaRepository<EventEntitlement, Long> {
    List<EventEntitlement> findByEvent(Event event);

    Optional<EventEntitlement> findByEventAndEntitlementType(Event event, EntitlementType entitlementType);

    void deleteByEvent(Event event);
}
