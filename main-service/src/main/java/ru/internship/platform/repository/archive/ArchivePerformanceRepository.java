package ru.internship.platform.repository.archive;

import ru.internship.platform.entity.archive.ArchivePerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivePerformanceRepository extends JpaRepository<ArchivePerformance, Integer> {
}
