package dev.yurchenko.iy0524.repository;

import dev.yurchenko.iy0524.entites.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ToolRepository extends JpaRepository<ToolEntity, Long> {
}
