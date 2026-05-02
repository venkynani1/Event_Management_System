package com.maverick.eventcontrolhub.entitlement;

import com.maverick.eventcontrolhub.common.EntitlementCode;
import jakarta.persistence.*;

@Entity
@Table(name = "entitlement_types", uniqueConstraints = {
        @UniqueConstraint(name = "uk_entitlement_code", columnNames = "code")
})
public class EntitlementType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntitlementCode code;

    @Column(nullable = false)
    private String name;

    protected EntitlementType() {
    }

    public EntitlementType(EntitlementCode code, String name) {
        this.code = code;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public EntitlementCode getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
