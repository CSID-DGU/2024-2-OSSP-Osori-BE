package dongguk.osori;

import dongguk.osori.domain.quest.entity.Quest;
import dongguk.osori.domain.quest.entity.Stamp;
import dongguk.osori.domain.quest.repository.QuestRepository;
import dongguk.osori.domain.quest.repository.StampRepository;
import dongguk.osori.global.scheduler.QuestAndStampScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@SpringBootTest
class QuestAndStampSchedulerTest {

    @MockBean
    private QuestRepository questRepository;

    @MockBean
    private StampRepository stampRepository;

    @Test
    void 스탬프_데이터가_있는_경우_초기화() {
        // Given
        Stamp stamp1 = mock(Stamp.class);
        Stamp stamp2 = mock(Stamp.class);

        when(stampRepository.findAll()).thenReturn(Arrays.asList(stamp1, stamp2));

        QuestAndStampScheduler scheduler = new QuestAndStampScheduler(stampRepository, questRepository);

        // When
        scheduler.resetWeeklyStamps();

        // Then
        verify(stamp1, times(1)).resetStampCount();
        verify(stamp2, times(1)).resetStampCount();
        verify(stampRepository, times(1)).saveAll(anyList());
    }

    @Test
    void 퀘스트_데이터가_없는_경우_초기화() {
        // Given
        when(questRepository.findAll()).thenReturn(Collections.emptyList());

        QuestAndStampScheduler scheduler = new QuestAndStampScheduler(stampRepository, questRepository);

        // When
        scheduler.resetDailyQuests();

        // Then
        verify(questRepository, times(1)).findAll();
        verify(questRepository, times(1)).saveAll(anyList());
    }

    @Test
    void 퀘스트_데이터가_있는_경우_초기화() {
        // Given
        Quest quest1 = mock(Quest.class);
        Quest quest2 = mock(Quest.class);

        when(questRepository.findAll()).thenReturn(Arrays.asList(quest1, quest2));

        QuestAndStampScheduler scheduler = new QuestAndStampScheduler(stampRepository, questRepository);

        // When
        scheduler.resetDailyQuests();

        // Then
        verify(quest1, times(1)).resetDailyStatus();
        verify(quest2, times(1)).resetDailyStatus();
        verify(questRepository, times(1)).saveAll(anyList());
    }

    @Test
    void 스탬프_데이터가_없는_경우_초기화() {
        // Given
        when(stampRepository.findAll()).thenReturn(Collections.emptyList());

        QuestAndStampScheduler scheduler = new QuestAndStampScheduler(stampRepository, questRepository);

        // When
        scheduler.resetWeeklyStamps();

        // Then
        verify(stampRepository, times(1)).findAll();
        verify(stampRepository, times(1)).saveAll(anyList());
    }

    @Test
    void 스탬프가_최대값을_넘지_않음() {
        // Given
        Stamp stamp = mock(Stamp.class);
        when(stamp.isFullyCompleted()).thenReturn(true);
        when(stampRepository.findAll()).thenReturn(Collections.singletonList(stamp));

        QuestAndStampScheduler scheduler = new QuestAndStampScheduler(stampRepository, questRepository);

        // When
        scheduler.resetWeeklyStamps();

        // Then
        verify(stamp, never()).incrementStamp();
    }
}
