package dongguk.osori.domain.portfolio.entity;

import dongguk.osori.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioId;

    private String name;

    private LocalDate startDate;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void onPrePersistOrUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @ElementCollection
    @CollectionTable(name = "portfolio_tags", joinColumns = @JoinColumn(name = "portfolio_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "portfolio_photos", joinColumns = @JoinColumn(name = "portfolio_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();

    public Portfolio(String name, LocalDateTime updatedAt, LocalDate startDate, Set<String> tags, Experience experience, Pmi pmi, User user) {
        this.name = name;
        this.updatedAt = updatedAt;
        this.startDate = startDate;
        this.tags = tags;
        this.user = user;
        setExperience(experience);
        setPmi(pmi);
    }

    public void update(String name, LocalDate startDate, Set<String> tags) {
        this.name = name;
        this.startDate = startDate;
        this.tags = tags;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
        if (experience != null) {
            experience.setPortfolio(this);
        }
    }

    public void setPmi(Pmi pmi) {
        this.pmi = pmi;
        if (pmi != null) {
            pmi.setPortfolio(this);
        }
    }

    public void addPhotoUrl(String photoUrl) {
        this.photoUrls.add(photoUrl);
    }

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "portfolio")
    private Experience experience;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "portfolio")
    private Pmi pmi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
