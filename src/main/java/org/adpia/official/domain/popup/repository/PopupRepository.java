package org.adpia.official.domain.popup.repository;

import org.adpia.official.domain.popup.Popup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PopupRepository extends JpaRepository<Popup, Long> {

	Optional<Popup> findTopByOrderByIdDesc();

	@Query("""
        select p from Popup p
        where p.active = true
          and (p.startAt is null or p.startAt <= :now)
          and (p.endAt is null or :now <= p.endAt)
        order by p.updatedAt desc
    """)
	Optional<Popup> findActive(LocalDateTime now);
}