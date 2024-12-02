package dongguk.osori.domain.academicEvent.service;

import dongguk.osori.domain.academicEvent.entity.AcademicEvent;
import dongguk.osori.domain.academicEvent.repository.AcademicEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicEventService {

    private final AcademicEventRepository repository;

    public List<AcademicEvent> getEventsByMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        return repository.findByStartDateBetween(startOfMonth, endOfMonth);
    }
}
