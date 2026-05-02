package com.maverick.eventcontrolhub.entitlement;

import com.maverick.eventcontrolhub.common.EntitlementCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EntitlementSettingDto(
        @NotNull EntitlementCode code,
        @Min(0) int totalQuantity,
        @Min(1) int perPersonLimit
) {
    public static EntitlementSettingDto from(EventEntitlement entitlement) {
        return new EntitlementSettingDto(
                entitlement.getEntitlementType().getCode(),
                entitlement.getTotalQuantity(),
                entitlement.getPerPersonLimit()
        );
    }
}
