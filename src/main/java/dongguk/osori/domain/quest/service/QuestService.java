package dongguk.osori.domain.quest.service;

import dongguk.osori.domain.quest.dto.QuestStatusDto;
import dongguk.osori.domain.quest.entity.MissionType;
import dongguk.osori.domain.quest.dto.MissionRequestDto;
import dongguk.osori.domain.quest.entity.Quest;
import dongguk.osori.domain.quest.entity.Stamp;
import dongguk.osori.domain.quest.repository.QuestRepository;
import dongguk.osori.domain.quest.repository.StampRepository;
import dongguk.osori.domain.user.entity.User;
import dongguk.osori.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final StampRepository stampRepository;
    private final UserRepository userRepository;

    @Transactional
    public void completeMission(Long userId, LocalDate date) {
        Quest quest = getOrCreateQuest(userId, date);

        // 모든 미션이 완료되었는지 확인
        if (!quest.isAllMissionsCompleted()) {
            throw new IllegalStateException("모든 미션이 완료되지 않았습니다.");
        }

        // 스탬프 증가
        incrementStamp(userId, date);
    }

    private void incrementStamp(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        int weekNumber = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        int year = date.getYear();

        Stamp stamp = stampRepository.findByUser_UserIdAndWeekNumberAndYear(userId, weekNumber, year)
                .orElseGet(() -> new Stamp(user, weekNumber, year));

        stamp.incrementStamp();
        stampRepository.save(stamp);
    }

    private Quest getOrCreateQuest(Long userId, LocalDate date) {
        return questRepository.findByUser_UserIdAndDate(userId, date)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
                    return questRepository.save(new Quest(user, date));
                });
    }

    @Transactional
    public void updateMissionStatus(Long userId, MissionType missionType) {
        Quest quest = getOrCreateQuest(userId, LocalDate.now());

        switch (missionType) {
            case ATTENDANCE_CHECKED:
                if (!quest.isAttended()) {
                    quest.markAttended();
                }
                break;
            case GOAL_WRITTEN:
                if (!quest.isGoalWritten()) {
                    quest.markGoalWritten();
                }
                break;
            case COMMENTED_ON_FRIEND_GOAL:
                if (!quest.isCommentedOnFriendGoal()) {
                    quest.markCommentedOnFriendGoal();
                }
                break;
            case PORTFOLIO_WRITTEN:
                if (!quest.isPortfolioWritten()) {
                    quest.markPortfolioWritten();
                }
                break;
            default:
                throw new IllegalArgumentException("잘못된 미션 타입입니다.");
        }

        questRepository.save(quest);

        // 모든 미션이 완료되었다면 스탬프 추가
        if (quest.isAllMissionsCompleted()) {
            completeMission(userId, LocalDate.now());
        }
    }

    @Transactional(readOnly = true)
    public QuestStatusDto getQuestStatus(Long userId, LocalDate date) {
        Quest quest = questRepository.findByUser_UserIdAndDate(userId, date)
                .orElse(new Quest(userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found")), date));

        return new QuestStatusDto(
                quest.isAttended(),
                quest.isGoalWritten(),
                quest.isCommentedOnFriendGoal(),
                quest.isPortfolioWritten()
        );
    }

    @Transactional(readOnly = true)
    public int getCurrentStampCount(Long userId) {
        LocalDate today = LocalDate.now();
        int weekNumber = today.get(WeekFields.ISO.weekOfWeekBasedYear());
        int year = today.getYear();

        return stampRepository.findByUser_UserIdAndWeekNumberAndYear(userId, weekNumber, year)
                .map(Stamp::getCount)
                .orElse(0);
    }

}


