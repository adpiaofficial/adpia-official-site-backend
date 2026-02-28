package org.adpia.official.domain.history.repository;

import java.util.List;

import org.adpia.official.domain.history.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HistoryRepository extends JpaRepository<History, Long> {

	List<History> findByYearBetweenOrderByYearDescMonthAscSortOrderAsc(int startYear, int endYear);

	@Query("select distinct (h.year / 10) * 10 from History h order by (h.year / 10) * 10 asc")
	List<Integer> findDistinctDecades();

}