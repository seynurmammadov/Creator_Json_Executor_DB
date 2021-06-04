package Models;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "grades")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    @Id
    private Long id;
    @NonNull
    private  String name;

    @OneToMany(mappedBy = "grade", fetch = FetchType.LAZY)
    private List<Student> students = new ArrayList<>();

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", students=" + students +
                '}';
    }
}
