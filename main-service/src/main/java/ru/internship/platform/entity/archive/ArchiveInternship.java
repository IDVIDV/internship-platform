package ru.internship.platform.entity.archive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "internship", schema = "archive")
public class ArchiveInternship {
    @Id
    @Column(name = "internship_id")
    private Integer id;
    @Column(name = "internship_name")
    private String internshipName;
    private String description;
    @Column(name = "start_date")
    private Timestamp startDate;
    @Column(name = "sign_end_date")
    private Timestamp signEndDate;
    @OneToMany(mappedBy = "internship", fetch = FetchType.LAZY)
    private List<ArchiveUserInternship> userInternships;
    @OneToMany(mappedBy = "internship", fetch = FetchType.LAZY)
    private List<ArchivePerformance> performances;

}
