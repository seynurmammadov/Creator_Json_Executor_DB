package Models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "students")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private  String name;
    @NonNull
    private  String surname;
    @NonNull
    @ManyToMany( cascade = CascadeType.ALL)
    @JoinTable(name="teacher_students",
            joinColumns=@JoinColumn(name="student_id"),
            inverseJoinColumns=@JoinColumn(name="teacher_id"))
    private List<Teacher> teachers;
    @ManyToOne( cascade = CascadeType.ALL)
    @JoinColumn(name="grade_id")
    private Grade grade;

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", teachers=" + teachers +
                ", grade=" + grade +
                '}';
    }
}
