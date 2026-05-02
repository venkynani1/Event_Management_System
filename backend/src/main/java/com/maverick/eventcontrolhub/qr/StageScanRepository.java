package com.maverick.eventcontrolhub.qr;

import com.maverick.eventcontrolhub.common.Stage;
import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StageScanRepository extends JpaRepository<StageScan, Long> {
    boolean existsByRegistrationAndStage(Registration registration, Stage stage);

    Optional<StageScan> findByRegistrationAndStage(Registration registration, Stage stage);

    long countByEventAndStage(Event event, Stage stage);

    List<StageScan> findByEventOrderByScannedAtDesc(Event event);
}
