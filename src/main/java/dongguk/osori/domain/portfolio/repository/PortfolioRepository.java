package dongguk.osori.domain.portfolio.repository;

import dongguk.osori.domain.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @EntityGraph(attributePaths = {"experience", "pmi"})
    @Query("SELECT p FROM Portfolio p WHERE p.portfolioId = :portfolioId AND p.user.userId = :userId")
    Optional<Portfolio> findPortfolioWithDetails(@Param("portfolioId") Long portfolioId, @Param("userId") Long userId);

    @EntityGraph(attributePaths = {"experience", "pmi"})
    @Query("SELECT p FROM Portfolio p WHERE p.user.userId = :userId")
    List<Portfolio> findAllPortfoliosWithDetailsByUserId(@Param("userId") Long userId);
}
