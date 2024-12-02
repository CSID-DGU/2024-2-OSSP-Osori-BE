package dongguk.osori.domain.portfolio.service;

import dongguk.osori.domain.portfolio.dto.*;
import dongguk.osori.domain.portfolio.entity.*;
import dongguk.osori.domain.portfolio.repository.PortfolioRepository;
import dongguk.osori.domain.quest.entity.MissionType;
import dongguk.osori.domain.quest.service.QuestService;
import dongguk.osori.domain.user.entity.User;
import dongguk.osori.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final QuestService questService;

    @Transactional
    public PortfolioDetailDto createPortfolio(Long userId, PortfolioRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Experience experience = new Experience(
                requestDto.getExperience().getSituation(),
                requestDto.getExperience().getTask(),
                requestDto.getExperience().getAction(),
                requestDto.getExperience().getResult()
        );

        Pmi pmi = new Pmi(
                requestDto.getPmi().getPlus(),
                requestDto.getPmi().getMinus(),
                requestDto.getPmi().getInteresting()
        );

        Portfolio portfolio = new Portfolio(
                requestDto.getName(),
                LocalDateTime.now(),
                requestDto.getStartDate(),
                Set.copyOf(requestDto.getTags()),
                experience,
                pmi,
                user
        );

        requestDto.getPhotoUrls().forEach(portfolio::addPhotoUrl);

        portfolioRepository.save(portfolio);

        // 퀘스트 업데이트
        questService.updateMissionStatus(userId, MissionType.PORTFOLIO_WRITTEN);

        return mapToDetailDto(portfolio);
    }

    @Transactional(readOnly = true)
    public List<PortfolioBaseDto> getPortfolioList(Long userId) {
        return portfolioRepository.findAllPortfoliosWithDetailsByUserId(userId).stream()
                .map(this::mapToBaseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PortfolioDetailDto getPortfolioDetail(Long userId, Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findPortfolioWithDetails(portfolioId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found or access denied for ID: " + portfolioId));

        return mapToDetailDto(portfolio);
    }

    @Transactional
    public PortfolioDetailDto updatePortfolio(Long userId, Long portfolioId, PortfolioRequestDto requestDto) {
        Portfolio portfolio = portfolioRepository.findPortfolioWithDetails(portfolioId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found or access denied for ID: " + portfolioId));

        portfolio.update(
                requestDto.getName(),
                requestDto.getStartDate(),
                Set.copyOf(requestDto.getTags())
        );

        portfolio.getPhotoUrls().clear();
        requestDto.getPhotoUrls().forEach(portfolio::addPhotoUrl);

        portfolio.getExperience().update(
                requestDto.getExperience().getSituation(),
                requestDto.getExperience().getTask(),
                requestDto.getExperience().getAction(),
                requestDto.getExperience().getResult()
        );

        portfolio.getPmi().update(
                requestDto.getPmi().getPlus(),
                requestDto.getPmi().getMinus(),
                requestDto.getPmi().getInteresting()
        );

        return mapToDetailDto(portfolio);
    }

    @Transactional
    public void deletePortfolio(Long userId, Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findPortfolioWithDetails(portfolioId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found or access denied for ID: " + portfolioId));

        portfolioRepository.delete(portfolio);
    }

    private PortfolioBaseDto mapToBaseDto(Portfolio portfolio) {
        return new PortfolioBaseDto(
                portfolio.getPortfolioId(),
                portfolio.getName(),
                portfolio.getUpdatedAt(),
                portfolio.getStartDate(),
                List.copyOf(portfolio.getTags())
        );
    }

    private PortfolioDetailDto mapToDetailDto(Portfolio portfolio) {

        ExperienceDto experienceDto = portfolio.getExperience() != null
                ? new ExperienceDto(
                portfolio.getExperience().getSituation(),
                portfolio.getExperience().getTask(),
                portfolio.getExperience().getAction(),
                portfolio.getExperience().getResult()
        )
                : null;

        PmiDto pmiDto = portfolio.getPmi() != null
                ? new PmiDto(
                portfolio.getPmi().getPlus(),
                portfolio.getPmi().getMinus(),
                portfolio.getPmi().getInteresting()
        )
                : null;

        return new PortfolioDetailDto(
                mapToBaseDto(portfolio),
                experienceDto,
                pmiDto,
                List.copyOf(portfolio.getPhotoUrls())
        );
    }
}

