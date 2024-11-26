package dongguk.osori.domain.portfolio.controller;

import dongguk.osori.domain.portfolio.dto.PortfolioBaseDto;
import dongguk.osori.domain.portfolio.dto.PortfolioDetailDto;
import dongguk.osori.domain.portfolio.dto.PortfolioRequestDto;
import dongguk.osori.domain.portfolio.service.PortfolioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final HttpSession httpSession;

    @PostMapping
    public ResponseEntity<PortfolioDetailDto> createPortfolio(@RequestBody PortfolioRequestDto requestDto) {
        Long userId = (Long) httpSession.getAttribute("userId");
        return ResponseEntity.ok(portfolioService.createPortfolio(userId, requestDto));
    }

    @GetMapping
    public ResponseEntity<List<PortfolioBaseDto>> getPortfolioList() {
        Long userId = (Long) httpSession.getAttribute("userId");
        return ResponseEntity.ok(portfolioService.getPortfolioList(userId));
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDetailDto> getPortfolioDetail(@PathVariable("portfolioId") Long portfolioId) {
        Long userId = (Long) httpSession.getAttribute("userId");
        return ResponseEntity.ok(portfolioService.getPortfolioDetail(userId, portfolioId));
    }


    @PutMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDetailDto> updatePortfolio(
            @PathVariable("portfolioId") Long portfolioId,
            @RequestBody PortfolioRequestDto requestDto
    ) {
        Long userId = (Long) httpSession.getAttribute("userId");
        return ResponseEntity.ok(portfolioService.updatePortfolio(userId, portfolioId, requestDto));
    }


    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable("portfolioId") Long portfolioId) {
        Long userId = (Long) httpSession.getAttribute("userId");
        portfolioService.deletePortfolio(userId, portfolioId);
        return ResponseEntity.noContent().build();
    }
}
