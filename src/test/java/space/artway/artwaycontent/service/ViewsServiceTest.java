package space.artway.artwaycontent.service;

import com.google.common.collect.ImmutableList;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import space.artway.artwaycontent.domain.ContentEntity;
import space.artway.artwaycontent.domain.ViewEntity;
import space.artway.artwaycontent.repository.ContentRepository;
import space.artway.artwaycontent.repository.ViewsRepository;
import space.artway.artwaycontent.service.dto.ViewDto;
import space.artway.artwaycontent.service.mapper.ViewMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ViewsServiceTest {
    private final ContentRepository contentRepository = Mockito.mock(ContentRepository.class);
    private final ViewsRepository viewsRepository = Mockito.mock(ViewsRepository.class);
    private final ViewMapper viewMapper = new ViewMapper();
    private final ViewsService viewsService = new ViewsService(contentRepository, viewsRepository, viewMapper);

    private Long contentId;
    private Long userId;

    @BeforeEach
    void setUp() {
        contentId = 21L;
        userId = 1L;
    }

    @Test
    @DisplayName("Add new view")
    void addNewView() throws NotFoundException {
        var content = new ContentEntity();

        when(contentRepository.findContentEntityById(anyLong())).thenReturn(Optional.of(content));

        viewsService.addNewView(userId, contentId);

        verify(viewsRepository, times(1)).save(any(ViewEntity.class));
    }


    @Test
    @DisplayName("Content not found")
    void addNewViewWhenContentNotFound() throws NotFoundException {
        when(contentRepository.findContentEntityById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> viewsService.addNewView(userId, contentId));
    }

    @Test
    @DisplayName("Get unique views of content")
    void getUniqueViews() throws NotFoundException {
        var content = new ContentEntity();
        content.setId(contentId);
        content.setViews(ImmutableList.of(createView(1L, content), createView(2L, content), createView(1L, content)));

        when(contentRepository.findContentEntityById(contentId)).thenReturn(Optional.of(content));

        List<ViewDto> result = viewsService.getUniqueViews(contentId);

        assertAll(
                () -> assertEquals(2, result.size())
        );
    }

    @Test
    @DisplayName("Get unique view of content when content not found")
    void getUniqueViewsWhenContentNotFound() throws NotFoundException {
        when(contentRepository.findContentEntityById(contentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> viewsService.getUniqueViews(contentId));
    }

    @Test
    @DisplayName("Get all author's content views")
    void getAllViewsByAuthorId() throws NotFoundException {
        var authorId = 12L;
        var content1 = new ContentEntity();
        content1.setId(contentId);
        content1.setAuthorId(authorId);
        content1.setViews(ImmutableList.of(createView(1L, content1), createView(2L, content1)));
        var content2 = new ContentEntity();
        content2.setId(22L);
        content2.setAuthorId(authorId);
        content2.setViews(ImmutableList.of(createView(3L, content2)));
        var content3 = new ContentEntity();
        content3.setId(23L);
        content3.setAuthorId(authorId);
        content3.setViews(Collections.emptyList());
        List<ContentEntity> content = ImmutableList.of(content1, content2, content3);

        when(contentRepository.findContentByAuthorIdAndStatusNotIn(anyLong(), anyCollection())).thenReturn(Optional.of(content));

        List<ViewDto> result = viewsService.getAllViewsByAuthorId(authorId);

        assertAll(
                () -> assertEquals(3, result.size())
        );
    }

    @Test
    @DisplayName("Get all views by author's content, but content not found")
    void getAllViewsByAuthorIdWhenContentNotFound() throws NotFoundException {
        when(contentRepository.findContentByAuthorIdAndStatusNotIn(anyLong(), anyCollection())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> viewsService.getAllViewsByAuthorId(12L));
    }

    private ViewEntity createView(Long userId, ContentEntity content) {
        var view = new ViewEntity();
        view.setUserId(userId);
        view.setContent(content);
        return view;
    }
}
