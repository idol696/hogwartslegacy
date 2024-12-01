package ru.prostostudia.hogwartslegacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.stereotype.Repository;
import ru.prostostudia.hogwartslegacy.models.Avatar;

import java.util.Optional;

@Repository
@NonNullApi
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    Optional<Avatar> findByStudentId(long studentId);
}
