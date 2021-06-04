package Models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "subjects")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subject {
    @Id
    private Long id;
    @NonNull
    private String name;

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
