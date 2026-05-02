package com.maverick.eventcontrolhub.qr;

import com.maverick.eventcontrolhub.common.Stage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScanRequest(
        @NotBlank String qrToken,
        @NotNull Stage stage,
        String locationName
) {
}
