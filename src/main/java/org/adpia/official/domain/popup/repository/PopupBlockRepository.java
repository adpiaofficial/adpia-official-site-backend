package org.adpia.official.domain.popup.repository;

import org.adpia.official.domain.popup.PopupBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopupBlockRepository extends JpaRepository<PopupBlock, Long> {

	List<PopupBlock> findByPopupIdOrderBySortOrderAsc(Long popupId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete from PopupBlock pb where pb.popupId = :popupId")
	void deleteByPopupId(@Param("popupId") Long popupId);
}