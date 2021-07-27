package space.artway.artwaycontent.service;

import com.google.common.collect.ImmutableList;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import space.artway.artwaycontent.domain.ContentEntity;
import space.artway.artwaycontent.domain.DislikeEntity;
import space.artway.artwaycontent.domain.LikeEntity;
import space.artway.artwaycontent.repository.ContentRepository;
import space.artway.artwaycontent.repository.DislikeRepository;
import space.artway.artwaycontent.repository.LikeRepository;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class NoteServiceTest {
    private final ContentRepository contentRepository = Mockito.mock(ContentRepository.class);
    private final LikeRepository likeRepository = Mockito.mock(LikeRepository.class);
    private final DislikeRepository dislikeRepository = Mockito.mock(DislikeRepository.class);
    private final NoteService noteService = new NoteService(contentRepository, likeRepository, dislikeRepository);

    private ContentEntity content;
    private DislikeEntity dislike;
    private LikeEntity like;

    @BeforeEach
    void init() {
        content = new ContentEntity();
        content.setCreatedAt(new Date());
        content.setLastModified(new Date());
        like = new LikeEntity();
        like.setContent(content);
        like.setId(1L);
        like.setUserId(2L);
        dislike = new DislikeEntity();
        dislike.setId(1L);
        dislike.setUserId(1L);
        dislike.setContent(content);

        content.setId(21L);
        content.setDislikes(ImmutableList.of(dislike));
        content.setLikes(ImmutableList.of(like));

    }

    @Test
    @DisplayName("Like content")
    void setLikeTest() throws NotFoundException {
        var userId = 3L;

        when(contentRepository.findContentEntityById(anyLong())).thenReturn(Optional.of(content));

        noteService.setLike(userId, content.getId());

        verify(likeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Try to like content which not found")
    void setLikeWhenContentNotFound() throws NotFoundException {
        var userId = 1L;

        when(contentRepository.findContentEntityById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> noteService.setLike(userId, content.getId()));
    }

    @Test
    @DisplayName("Like disliked content")
    void setLikeWhenDislikeIsSet() throws NotFoundException {
        var userId = 1L;
        dislike.setUserId(1L);

        when(contentRepository.findContentEntityById(anyLong())).thenReturn(Optional.of(content));

        noteService.setLike(userId, content.getId());

        verify(dislikeRepository, times(1)).delete(any());
        verify(likeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Unset Like")
    void setLikeWhenLikeIsAlreadySet() throws NotFoundException {
        var userId = 2L;

        when(contentRepository.findContentEntityById(anyLong())).thenReturn(Optional.of(content));

        noteService.setLike(userId, content.getId());

        verify(likeRepository, times(1)).delete(any());
    }


    @Test
    @DisplayName("Dislike content")
    void setDislikeTest() throws NotFoundException {
        var userId = 3L;

        when(contentRepository.findContentEntityById(anyLong())).thenReturn(Optional.of(content));

        noteService.setDislike(userId, content.getId());

        verify(dislikeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Try to dislike content which not found")
    void setDislikeWhenContentNotFound() throws NotFoundException {
        var userId = 1L;

        when(contentRepository.findContentEntityById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> noteService.setDislike(userId, content.getId()));
    }

    @Test
    @DisplayName("Dislike liked content")
    void setDislikeWhenLikeIsSet() throws NotFoundException {
        var userId = 1L;
        like.setUserId(1L);

        when(contentRepository.findContentEntityById(anyLong())).thenReturn(Optional.of(content));

        noteService.setDislike(userId, content.getId());

        verify(likeRepository, times(1)).delete(any());
        verify(dislikeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Unset Dislike")
    void setDislikeWhenDislikeIsAlreadySet() throws NotFoundException {
        var userId = 1L;

        when(contentRepository.findContentEntityById(anyLong())).thenReturn(Optional.of(content));

        noteService.setDislike(userId, content.getId());

        verify(dislikeRepository, times(1)).delete(any());
    }
}
