package ru.internship.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.internship.platform.entity.Commit;

import java.util.List;

@Repository
public interface CommitRepository extends JpaRepository<Commit, Integer> { }