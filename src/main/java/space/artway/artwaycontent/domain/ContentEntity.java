package space.artway.artwaycontent.domain;

import lombok.*;
import space.artway.artwaycontent.service.ContentType;

import javax.persistence.*;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "content")
    private List<LikeEntity> likes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "content")
//    @JoinColumn(name = "dislike_id")
    private List<DislikeEntity> dislikes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "content")
//    @JoinColumn(name = "view_id")
    private List<ViewEntity> views;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sectionId")
    private Section section;

    @ManyToMany
    @JoinTable(
            name = "content_genres",
            joinColumns = @JoinColumn(name = "genre_name"),
            inverseJoinColumns = @JoinColumn(name = "content_id")
    )
    private List<Genre> genres;

}
