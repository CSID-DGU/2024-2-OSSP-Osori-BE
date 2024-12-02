package dongguk.osori.domain.academicEvent.controller;

import dongguk.osori.domain.academicEvent.entity.AcademicEvent;
import dongguk.osori.domain.academicEvent.service.AcademicEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academic-events")
@RequiredArgsConstructor
public class AcademicEventController {

    private final AcademicEventService service;

    @GetMapping("/{year}/{month}")
    public ResponseEntity<List<AcademicEvent>> getEventsByMonth(
            @PathVariable int year,
            @PathVariable int month) {
        return ResponseEntity.ok(service.getEventsByMonth(year, month));
    }
}
