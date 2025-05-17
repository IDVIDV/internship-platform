package ru.internship.platform.repository.archive;

import ru.internship.platform.entity.archive.ArchiveUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchiveUserRepository extends JpaRepository<ArchiveUser, Integer> {
}
