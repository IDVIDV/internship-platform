package ru.internship.platform.repository.archive;

import ru.internship.platform.entity.archive.ArchiveUserInternship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchiveUserInternshipRepository extends JpaRepository<ArchiveUserInternship, Integer> {
}
