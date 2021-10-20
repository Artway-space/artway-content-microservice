package space.artway.artwaycontent.service;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import space.artway.artwaycontent.domain.ContentEntity;
import space.artway.artwaycontent.repository.ContentRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@SpringBootTest
@Profile("INTEGRATION")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContentServiceTestIT {

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentRepository contentRepository;

    ContentEntity contentEntity;

    @BeforeAll
    void setUp() {
       contentEntity = new EasyRandom(new EasyRandomParameters()
                .randomize(FieldPredicates.named("status"), () -> ContentStatus.IN_TRASH_BIN)
                .randomize(FieldPredicates.named("lastModified"), () -> Date.from(LocalDateTime.now().minusDays(31L).toInstant(ZoneOffset.UTC)))
                .excludeField(FieldPredicates.named("likes"))
                .excludeField(FieldPredicates.named("dislikes"))
                .excludeField(FieldPredicates.named("views"))
                .excludeField(FieldPredicates.named("section"))
                .excludeField(FieldPredicates.named("id"))
                .excludeField(FieldPredicates.named("genres"))
        ).nextObject(ContentEntity.class);

        contentRepository.save(contentEntity);
    }

    @Test
    void deleteContent() {
    //    Mockito.when(contentRepository.findContentEntitiesByStatus(eq(ContentStatus.IN_TRASH_BIN))).thenReturn(Optional.of(ImmutableList.of(contentEntity)));
        contentService.deleteContent();
    }


}
