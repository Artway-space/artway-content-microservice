package space.artway.artwaycontent.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Genre extends BaseEntity {
    @Id
    @Column(name = "name")
    private String name;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "section_id")
//    private Section section;

    @ManyToMany(mappedBy = "genres")
    private List<ContentEntity> contents;

}
