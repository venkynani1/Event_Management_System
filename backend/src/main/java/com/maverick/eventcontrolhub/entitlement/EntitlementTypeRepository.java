package com.maverick.eventcontrolhub.entitlement;

import com.maverick.eventcontrolhub.common.EntitlementCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntitlementTypeRepository extends JpaRepository<EntitlementType, Long> {
    Optional<EntitlementType> findByCode(EntitlementCode code);
}
