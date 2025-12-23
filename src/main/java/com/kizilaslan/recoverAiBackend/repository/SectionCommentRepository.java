package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.Section;
import com.kizilaslan.recoverAiBackend.model.SectionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SectionCommentRepository extends JpaRepository<SectionComment, UUID> {

    @Query("SELECT s FROM SectionComment s " +
            "WHERE s.section = :section AND s.user.id = :userId")
    Optional<SectionComment> findBySection(@Param("section") Section section, @Param("userId") UUID userId);


    @Modifying
    @Query("DELETE FROM SectionComment s " +
            "WHERE s.section = :section AND s.user.id = :userId")
    void deleteBySectionAndUser(@Param("section") Section section, @Param("userId") UUID userId);

}
