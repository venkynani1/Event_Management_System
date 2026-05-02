package com.maverick.eventcontrolhub.qr;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QrTokenService {
    public String generateToken() {
        return "MECH-" + UUID.randomUUID();
    }
}
