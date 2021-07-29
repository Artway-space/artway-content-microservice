package space.artway.artwaycontent.service;

import com.google.common.collect.ImmutableList;
import javassist.NotFoundException;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaycontent.domain.ContentEntity;
import space.artway.artwaycontent.domain.LikeEntity;
import space.artway.artwaycontent.domain.Section;
import space.artway.artwaycontent.domain.ViewEntity;
import space.artway.artwaycontent.repository.ContentRepository;
import space.artway.artwaycontent.repository.GenreRepository;
import space.artway.artwaycontent.repository.SectionRepository;
import space.artway.artwaycontent.service.dto.ContentDto;
import space.artway.artwaycontent.service.mapper.ContentMapper;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ContentServiceTest {

    private final ContentRepository contentRepository = Mockito.mock(ContentRepository.class);
    private final SectionRepository sectionRepository = Mockito.mock(SectionRepository.class);
    private final GenreRepository genreRepository = Mockito.mock(GenreRepository.class);
    private final ContentMapper contentMapper = new ContentMapper();
    private final ContentService contentService = new ContentService(contentRepository, sectionRepository, genreRepository, contentMapper);

    @Test
    @DisplayName("Find Content By Author ID")
    void findContentByAuthorId() {
        ContentEntity contentEntity = new EasyRandom(new EasyRandomParameters()
                .collectionSizeRange(1, 3)
        ).nextObject(ContentEntity.class);

        when(contentRepository.findContentByAuthorId(1L)).thenReturn(Optional.of(ImmutableList.of(contentEntity)));

        List<ContentDto> result = contentService.getAllAuthorContent(1L);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(contentEntity.getName(), result.get(0).getName()),
                () -> assertEquals(contentEntity.getContentType().getCode(), result.get(0).getContentType()),
                () -> assertEquals(contentEntity.getLink(), result.get(0).getLink()),
                () -> assertEquals(contentEntity.getCreatedAt(), result.get(0).getCreateDate()),
                () -> assertEquals(contentEntity.getLikes().size(), result.get(0).getMetaData().getLikes()),
                () -> assertEquals(contentEntity.getDislikes().size(), result.get(0).getMetaData().getDislikes()),
                () -> assertEquals(contentEntity.getViews().size(), result.get(0).getMetaData().getViews())
        );
    }

    @Test
    @DisplayName("Author has not cotent")
    void findContentByAuthorId_contentNotFound() {
        when(contentRepository.findContentByAuthorId(anyLong())).thenReturn(Optional.empty());

        List<ContentDto> result = contentService.getAllAuthorContent(1L);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(Collections.emptyList(), result)
        );
    }

    @Test
    @DisplayName("Get content by Author and ContentName")
    void findContentByNameAndAuthorId() throws NotFoundException {
        ContentEntity contentEntity = new EasyRandom(new EasyRandomParameters()
                .collectionSizeRange(1, 3)
        ).nextObject(ContentEntity.class);

        when(contentRepository.findContentByNameAndAuthorId(anyString(), anyLong())).thenReturn(Optional.of(contentEntity));

        ContentDto result = contentService.getContentByNameAndAuthorId("Don't stop me now", 1L);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(contentEntity.getName(), result.getName()),
                () -> assertEquals(contentEntity.getContentType().getCode(), result.getContentType()),
                () -> assertEquals(contentEntity.getLink(), result.getLink()),
                () -> assertEquals(contentEntity.getCreatedAt(), result.getCreateDate()),
                () -> assertEquals(contentEntity.getLikes().size(), result.getMetaData().getLikes()),
                () -> assertEquals(contentEntity.getDislikes().size(), result.getMetaData().getDislikes()),
                () -> assertEquals(contentEntity.getViews().size(), result.getMetaData().getViews())
        );
    }

    @Test
    @DisplayName("Content not found by Author and ContentName")
    void findContentByNameAndAuthorId_contentNotFound() throws NotFoundException {
        when(contentRepository.findContentByNameAndAuthorId(anyString(), anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> contentService.getContentByNameAndAuthorId("Don't stop me now", 1L));
    }

    @Test
    @Disabled
    void saveContent() {
        var contentDto = new ContentDto();
        contentDto.setSection("Music");
        var section = new Section();
        section.setName("Music");
        MultipartFile multipartFile = new MockMultipartFile("test file", "whatch video without sms and registration", "video/mp4", new byte[1]);

        when(contentRepository.save(ArgumentMatchers.any(ContentEntity.class))).thenReturn(new ContentEntity());
        when(sectionRepository.getByName(anyString())).thenReturn(Optional.of(section));
        ContentDto result = contentService.saveContent(multipartFile, contentDto);

        assertAll(
                () -> assertEquals(multipartFile.getName(), result.getName()),
                () -> assertEquals(0L, result.getMetaData().getViews()),
                () -> assertEquals(0L, result.getMetaData().getLikes()),
                () -> assertEquals(0L, result.getMetaData().getDislikes())
        );
    }


    @Test
    @DisplayName("Get content viewed by user")
    void getAllViewedByUserIdContent() {
        var userId = 1L;
        var content1 = new ContentEntity();
        content1.setId(1L);
        content1.setName("1");
        content1.setDislikes(Collections.emptyList());
        content1.setLikes(Collections.emptyList());
        content1.setContentType(ContentType.MP4);
        content1.setViews(ImmutableList.of(createView(1L, content1, new Date(1627319488)), createView(2L, content1, new Date(1627319588))));
        var content2 = new ContentEntity();
        content2.setId(2L);
        content2.setName("2");
        content2.setDislikes(Collections.emptyList());
        content2.setLikes(Collections.emptyList());
        content2.setContentType(ContentType.MP4);
        content2.setViews(ImmutableList.of(createView(1L, content2, new Date(1627233088))));
        var content3 = new ContentEntity();
        content3.setId(3L);
        content3.setName("3");
        content3.setDislikes(Collections.emptyList());
        content3.setLikes(Collections.emptyList());
        content3.setContentType(ContentType.MP4);
        content3.setViews(ImmutableList.of(createView(1L, content3, new Date(1627405888))));

        when(contentRepository.findContentEntitiesWatchedByUserId(anyLong())).thenReturn(Optional.of(ImmutableList.of(content1, content2, content3)));

        List<ContentDto> result = contentService.getAllViewedByUserIdContent(userId);


        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(content3.getName(), result.get(0).getName()),
                () -> assertEquals(content1.getName(), result.get(1).getName()),
                () -> assertEquals(content2.getName(), result.get(2).getName())
        );
    }

    @Test
    @DisplayName("Get content viewed by user when no content history")
    void getAllViewedByUserIdContentWhenContentNotFound() {
        var userId = 1L;
        when(contentRepository.findContentEntitiesWatchedByUserId(anyLong())).thenReturn(Optional.empty());

        List<ContentDto> result = contentService.getAllViewedByUserIdContent(userId);

        assertAll(
                () -> assertEquals(Collections.emptyList(), result)
        );
    }

    @Test
    @DisplayName("Get content liked by user")
    void getAllLikedByUserIdContent() {
        var userId = 1L;
        var content1 = new ContentEntity();
        content1.setId(1L);
        content1.setName("1");
        content1.setDislikes(Collections.emptyList());
        content1.setViews(Collections.emptyList());
        content1.setContentType(ContentType.MP4);
        content1.setLikes(ImmutableList.of(createLike(1L, content1, new Date(1627319488)), createLike(2L, content1, new Date())));

        var content2 = new ContentEntity();
        content2.setId(1L);
        content2.setName("2");
        content2.setDislikes(Collections.emptyList());
        content2.setViews(Collections.emptyList());
        content2.setContentType(ContentType.MP4);
        content2.setLikes(ImmutableList.of(createLike(1L, content2, new Date(1627233088))));

        var content3 = new ContentEntity();
        content3.setId(3L);
        content3.setName("3");
        content3.setDislikes(Collections.emptyList());
        content3.setViews(Collections.emptyList());
        content3.setContentType(ContentType.MP4);
        content3.setLikes(ImmutableList.of(createLike(1L, content3, new Date(1627405888))));

        when(contentRepository.findContentEntitiesLikedByUserId(anyLong())).thenReturn(Optional.of(ImmutableList.of(content1, content2, content3)));

        List<ContentDto> result = contentService.getAllLikedByUserIdContent(userId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(content3.getName(), result.get(0).getName()),
                () -> assertEquals(content1.getName(), result.get(1).getName()),
                () -> assertEquals(content2.getName(), result.get(2).getName())
        );
    }

    @Test
    @DisplayName("Get content liked by user, but content not found ")
    void getAllLikedByUserIdContentWhenContentNotFound(){
        var userId = 1L;

        when(contentRepository.findContentEntitiesLikedByUserId(anyLong())).thenReturn(Optional.empty());

        List<ContentDto> result = contentService.getAllLikedByUserIdContent(userId);

        assertAll(
                () -> assertEquals(Collections.emptyList(), result)
        );
    }


    private ViewEntity createView(Long userId, ContentEntity content, Date date) {
        var view = new ViewEntity();
        view.setUserId(userId);
        view.setContent(content);
        view.setCreatedAt(date);
        return view;
    }

    private LikeEntity createLike(Long userId, ContentEntity content, Date date) {
        var like = new LikeEntity();
        like.setUserId(userId);
        like.setContent(content);
        like.setCreatedAt(date);
        return like;
    }
}