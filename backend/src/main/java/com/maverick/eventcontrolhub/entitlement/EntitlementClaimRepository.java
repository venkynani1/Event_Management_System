package com.maverick.eventcontrolhub.entitlement;

import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntitlementClaimRepository extends JpaRepository<EntitlementClaim, Long> {
    long countByEventAndEntitlementType(Event event, EntitlementType entitlementType);

    long countByRegistrationAndEntitlementType(Registration registration, EntitlementType entitlementType);

    Optional<EntitlementClaim> findFirstByRegistrationAndEntitlementTypeOrderByClaimedAtDesc(
            Registration registration,
            EntitlementType entitlementType
    );

    List<EntitlementClaim> findByEventOrderByClaimedAtDesc(Event event);
}
