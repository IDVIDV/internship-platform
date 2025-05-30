package ru.internship.platform.entity;

import ru.internship.platform.entity.status.UserInternshipStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_internship")
public class UserInternship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_internship_id")
    private Integer userInternshipId;
    @ManyToOne
    @JoinColumn(name = "internship_id", nullable = false)
    private Internship internship;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    private UserInternshipStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInternship that = (UserInternship) o;
        return Objects.equals(userInternshipId, that.userInternshipId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userInternshipId);
    }
}
