package com.sebxx66.yogurtmaker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sebxx66.yogurtmaker.model.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<Recipe> findByActiveTrueOrderByNameAsc();

    @Query("""
            SELECT r FROM Recipe r
            WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY r.name
            """)
    List<Recipe> searchByKeyword(@Param("keyword") String keyword);
}
