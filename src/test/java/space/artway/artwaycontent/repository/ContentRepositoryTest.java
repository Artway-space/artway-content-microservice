package space.artway.artwaycontent.repository;

import com.google.common.collect.ImmutableList;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import space.artway.artwaycontent.domain.ContentEntity;
import space.artway.artwaycontent.domain.LikeEntity;
import space.artway.artwaycontent.domain.ViewEntity;
import space.artway.artwaycontent.service.ContentStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContentRepositoryTest {

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    ViewsRepository viewsRepository;

    @Autowired
    LikeRepository likeRepository;

    @BeforeAll
    public void setup() {
        var content1 = createContent(1L);
        createView(2L, content1);
        createView(5L, content1);
        createLike(5L, content1);

        var content2 = createContent(2L);
        createView(2L, content2);
        createView(3L, content2);

        var content3 = createContent(3L);
        createView(4L, content3);
        createView(5L, content3);
        createLike(5L, content3);

        var content4 = createContent(10L);
        content4.setStatus(ContentStatus.INACTIVE);
        contentRepository.save(content4);
        createView(7L, content4);
        createView(8L, content4);
        createLike(8L, content4);


        var content5 = createContent(10L);
        content5.setStatus(ContentStatus.ACTIVE);
        contentRepository.save(content5);
        createView(7L, content5);
        createView(8L, content5);
        createLike(8L, content5);
    }

    @Test
    public void testFindContentEntitiesWatchedByUserId() {
        Optional<List<ContentEntity>> contentEntitiesWatchedByUserId = contentRepository.findContentEntitiesWatchedByUserId(2L);
        List<ContentEntity> content = contentEntitiesWatchedByUserId.get();

        assertAll(
                () -> assertNotNull(content),
                () -> assertEquals(2, content.size()),
                () -> assertTrue(content.stream().noneMatch(c -> 3L == c.getId())),
                () -> assertTrue(content.stream().anyMatch(c -> 1L == c.getId())),
                () -> assertTrue(content.stream().anyMatch(c -> 2L == c.getId()))
        );
    }

    @Test
    public void testFindContentEntitiesLikedByUserId() {
        Optional<List<ContentEntity>> contentEntitiesLikedByUserId = contentRepository.findContentEntitiesLikedByUserId(5L);
        List<ContentEntity> content = contentEntitiesLikedByUserId.get();

        assertAll(
                () -> assertNotNull(content),
                () -> assertEquals(2, content.size()),
                () -> assertTrue(content.stream().noneMatch(c -> 2L == c.getId())),
                () -> assertTrue(content.stream().anyMatch(c -> 1L == c.getId())),
                () -> assertTrue(content.stream().anyMatch(c -> 3L == c.getId()))
        );
    }

    @Test
    public void testFindContentByAuthorIdAndStatusNotIn(){
        Optional<List<ContentEntity>> contentByAuthorIdAndStatusNotIn = contentRepository.findContentByAuthorIdAndStatusNotIn(10L, ImmutableList.of(ContentStatus.INACTIVE));
        List<ContentEntity> content = contentByAuthorIdAndStatusNotIn.get();

        assertAll(
                () -> assertNotNull(content),
                () -> assertEquals(5L, (long) content.get(0).getId())
        );
    }


    private void createView(Long userId, ContentEntity content) {
        var view = new ViewEntity();
        view.setContent(content);
        view.setUserId(userId);
        viewsRepository.save(view);
    }

    private void createLike(Long userId, ContentEntity content) {
        var like = new LikeEntity();
        like.setUserId(userId);
        like.setContent(content);
        likeRepository.save(like);
    }

    private ContentEntity createContent(Long authorId) {
        var content = new EasyRandom(new EasyRandomParameters()
                .randomize(FieldPredicates.named("authorId"), () -> authorId)
                .randomize(FieldPredicates.named("likes"), Collections::emptyList)
                .randomize(FieldPredicates.named("dislikes"), Collections::emptyList)
                .randomize(FieldPredicates.named("views"), Collections::emptyList)
                .excludeField(FieldPredicates.named("id"))
                .excludeField(FieldPredicates.named("sections"))
                .excludeField(FieldPredicates.named("section"))
                .excludeField(FieldPredicates.named("genres"))
        ).nextObject(ContentEntity.class);
        contentRepository.save(content);

        return content;
    }


}