package dongguk.osori.domain.quest.repository;

import dongguk.osori.domain.quest.entity.Quest;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Long> {
    Optional<Quest> findByUser_UserIdAndDate(Long userId, LocalDate date);
}

