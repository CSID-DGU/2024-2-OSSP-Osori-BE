package dongguk.osori.domain.quest.repository;

import dongguk.osori.domain.quest.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Long> {
    Optional<Quest> findByUser_UserIdAndDate(Long userId, LocalDate date);

}

