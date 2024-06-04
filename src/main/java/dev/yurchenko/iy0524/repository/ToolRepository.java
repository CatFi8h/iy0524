package dev.yurchenko.iy0524.repository;

import dev.yurchenko.iy0524.entites.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {
	@Query("select t from ToolEntity t " +
			       "left join fetch t.brand b " +
			       "left join fetch t.toolType tte " +
			       "where t.code = ?1")
	ToolEntity checkoutWith(String code);
}
