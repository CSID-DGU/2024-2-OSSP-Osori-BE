package dongguk.osori.domain.academicEvent.repository;

import dongguk.osori.domain.academicEvent.entity.AcademicEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AcademicEventRepository extends JpaRepository<AcademicEvent, Long> {
    List<AcademicEvent> findByStartDateBetween(LocalDate start, LocalDate end);
}
