package Models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "teachers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    @Id
    private Long id;
    @NonNull
    private  String name;
    @NonNull
    private  String surname;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="subject_id")
    private Subject subject;
    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", subject=" + subject +
                '}';
    }
}
