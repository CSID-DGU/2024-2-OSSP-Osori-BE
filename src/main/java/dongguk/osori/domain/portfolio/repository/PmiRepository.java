package dongguk.osori.domain.portfolio.repository;

import dongguk.osori.domain.portfolio.entity.Pmi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PmiRepository extends JpaRepository<Pmi, Long> {
}
