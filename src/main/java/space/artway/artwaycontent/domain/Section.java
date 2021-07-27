package space.artway.artwaycontent.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "sections")
@NoArgsConstructor
@Getter
@Setter
//@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Section extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

//    @ManyToMany
//    private List<Genre> genres;

    @OneToOne
    private ContentEntity content;
}
