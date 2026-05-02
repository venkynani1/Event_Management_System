package com.maverick.eventcontrolhub.entitlement;

import com.maverick.eventcontrolhub.common.EntitlementCode;

import java.time.LocalDateTime;

public record EntitlementClaimDto(
        Long id,
        Long eventId,
        Long registrationId,
        EntitlementCode code,
        LocalDateTime claimedAt,
        String counterName,
        String claimedBy
) {
    public static EntitlementClaimDto from(EntitlementClaim claim) {
        return new EntitlementClaimDto(
                claim.getId(),
                claim.getEvent().getId(),
                claim.getRegistration().getId(),
                claim.getEntitlementType().getCode(),
                claim.getClaimedAt(),
                claim.getCounterName(),
                claim.getClaimedBy().getName()
        );
    }
}
