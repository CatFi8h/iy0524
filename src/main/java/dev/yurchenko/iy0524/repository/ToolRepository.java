package dev.yurchenko.iy0524.repository;

import dev.yurchenko.iy0524.entites.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {
	@Query("from ToolEntity t " +
			       "join fetch t.brand " +
			       "join fetch t.toolType " +
			       "where t.code = :code")
	Optional<ToolEntity> getToolWithDetailsByCode(@Param("code") String code);
}
