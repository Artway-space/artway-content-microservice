package space.artway.artwaycontent.domain;

import lombok.*;
import space.artway.artwaycontent.service.ContentStatus;
import space.artway.artwaycontent.service.ContentType;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "content")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ContentEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentType contentType;

    @Column(name = "size")
    private Long size;

    @Column(name = "link")
    private String link;

    @Column(name = "author_id")
    private Long authorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ContentStatus status;

    @Size(max = 256)
    @Column(name = "check_sum")
    private String checkSum;

    @Column(name = "file_id")
    private String fileId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "content")
    private List<LikeEntity> likes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "content")
    private List<DislikeEntity> dislikes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "content")
    private List<ViewEntity> views;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    private Section section;

    @ManyToMany
    @JoinTable(
            name = "content_genres",
            joinColumns = @JoinColumn(name = "genre_name"),
            inverseJoinColumns = @JoinColumn(name = "content_id")
    )
    private List<Genre> genres;

}
