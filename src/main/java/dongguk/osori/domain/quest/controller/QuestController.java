package dongguk.osori.domain.quest.controller;

import dongguk.osori.domain.goal.dto.GoalResponseDto;
import dongguk.osori.domain.quest.dto.QuestAndStampStatusDto;
import dongguk.osori.domain.quest.dto.QuestStatusDto;
import dongguk.osori.domain.quest.service.QuestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;

    @GetMapping("/status")
    public ResponseEntity<QuestAndStampStatusDto> getQuestStatus(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build(); // Unauthorized 응답
        }

        QuestStatusDto questStatus = questService.getQuestStatus(userId, LocalDate.now());
        int stampCount = questService.getCurrentStampCount(userId); // 현재 스탬프 개수 가져오기
        boolean fullyCompleted = stampCount == 7; // 스탬프가 모두 채워졌는지 확인

        QuestAndStampStatusDto response = new QuestAndStampStatusDto(questStatus, stampCount, fullyCompleted);
        return ResponseEntity.ok(response);
    }
}

