package dongguk.osori.global.scheduler;

import dongguk.osori.domain.quest.entity.Quest;
import dongguk.osori.domain.quest.entity.Stamp;
import dongguk.osori.domain.quest.repository.QuestRepository;
import dongguk.osori.domain.quest.repository.StampRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestAndStampScheduler {

    private final StampRepository stampRepository;
    private final QuestRepository questRepository;

    // 매주 월요일 0시 스탬프 초기화
    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void resetWeeklyStamps() {
        log.info("스탬프 초기화를 시작합니다.");

        Iterable<Stamp> allStamps = stampRepository.findAll();
        for (Stamp stamp : allStamps) {
            stamp.resetStampCount(); // 초기화 메서드 호출
        }
        stampRepository.saveAll(allStamps);

        log.info("스탬프 초기화가 완료되었습니다.");
    }

    // 매일 0시 퀘스트 초기화
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void resetDailyQuests() {
        log.info("퀘스트 초기화를 시작합니다.");

        // 모든 퀘스트 상태 초기화
        Iterable<Quest> allQuests = questRepository.findAll();
        for (Quest quest : allQuests) {
            quest.resetDailyStatus();
        }
        questRepository.saveAll(allQuests);

        log.info("퀘스트 초기화가 완료되었습니다.");
    }


}
