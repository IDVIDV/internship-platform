package ru.internship.platform.repository.archive;

import ru.internship.platform.entity.archive.ArchiveInternship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchiveInternshipRepository extends JpaRepository<ArchiveInternship, Integer> {
}
