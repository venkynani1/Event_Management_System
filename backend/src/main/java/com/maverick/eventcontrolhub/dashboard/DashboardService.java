package com.maverick.eventcontrolhub.dashboard;

import com.maverick.eventcontrolhub.common.RegistrationSource;
import com.maverick.eventcontrolhub.common.RegistrationStatus;
import com.maverick.eventcontrolhub.common.Stage;
import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.event.EventRepository;
import com.maverick.eventcontrolhub.event.EventService;
import com.maverick.eventcontrolhub.qr.StageScanRepository;
import com.maverick.eventcontrolhub.registration.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final RegistrationRepository registrationRepository;
    private final StageScanRepository stageScanRepository;

    public DashboardService(EventRepository eventRepository,
                            EventService eventService,
                            RegistrationRepository registrationRepository,
                            StageScanRepository stageScanRepository) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.registrationRepository = registrationRepository;
        this.stageScanRepository = stageScanRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummary forEvent(Long eventId) {
        Event event = eventService.find(eventId);
        long registered = registrationRepository.countByEventAndStatusIn(
                event,
                List.of(RegistrationStatus.REGISTERED, RegistrationStatus.WALKIN)
        );
        long normalRegistrations = registrationRepository.countByEventAndSource(event, RegistrationSource.NORMAL);
        long walkIns = registrationRepository.countByEventAndSource(event, RegistrationSource.WALKIN);
        long checkedIn = stageScanRepository.countByEventAndStage(event, Stage.ENTRY);
        long food = event.isEnableFood() ? stageScanRepository.countByEventAndStage(event, Stage.FOOD) : 0;
        long goodies = event.isEnableGoodies() ? stageScanRepository.countByEventAndStage(event, Stage.GOODIES) : 0;
        return new DashboardSummary(
                eventRepository.count(),
                event.getId(),
                event.getTitle(),
                registered,
                normalRegistrations,
                checkedIn,
                food,
                goodies,
                walkIns,
                Math.max(registered - checkedIn, 0),
                event.isRegistrationOpen(),
                event.isWalkinOpen(),
                event.isEnableEntry(),
                event.isEnableFood(),
                event.isEnableGoodies()
        );
    }
}
