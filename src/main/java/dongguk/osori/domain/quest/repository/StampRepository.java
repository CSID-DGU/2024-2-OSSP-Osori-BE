package dongguk.osori.domain.quest.repository;

import dongguk.osori.domain.quest.entity.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StampRepository extends JpaRepository<Stamp, Long> {
    Optional<Stamp> findByUser_UserIdAndWeekNumberAndYear(Long userId, int weekNumber, int year);
}

