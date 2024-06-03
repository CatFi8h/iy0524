package dev.yurchenko.iy0524.repository;

import dev.yurchenko.iy0524.entites.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {
	@Query("select t from ToolEntity t " +
			       "left join fetch BrandEntity b " +
			       "left join fetch ToolTypeEntity tte " +
			       "where t.code = ?")
	ToolEntity checkoutWith(String code);
}
