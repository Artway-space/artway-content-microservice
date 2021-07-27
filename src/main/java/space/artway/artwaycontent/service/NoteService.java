package space.artway.artwaycontent.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.artway.artwaycontent.domain.ContentEntity;
import space.artway.artwaycontent.domain.DislikeEntity;
import space.artway.artwaycontent.domain.LikeEntity;
import space.artway.artwaycontent.exception.ExceptionsMessages;
import space.artway.artwaycontent.repository.ContentRepository;
import space.artway.artwaycontent.repository.DislikeRepository;
import space.artway.artwaycontent.repository.LikeRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final ContentRepository contentRepository;
    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;

    public void setLike(Long userId, Long contentId) throws NotFoundException {
        ContentEntity content = contentRepository.findContentEntityById(contentId)
                .orElseThrow(() -> new NotFoundException(ExceptionsMessages.CONTENT_NOT_FOUND_TEXT));

        var like = new LikeEntity();
        like.setContent(content);
        like.setUserId(userId);

        Optional<DislikeEntity> dislikeOptional = content.getDislikes().stream()
                .filter(Objects::nonNull)
                .filter(dislike -> userId.equals(dislike.getUserId()))
                .findFirst();

        if (dislikeOptional.isPresent()) {
            deleteDislikeAndSetLike(dislikeOptional.get(), like);
            return;
        }

        Optional<LikeEntity> likeOptional = content.getLikes().stream()
                .filter(Objects::nonNull)
                .filter(likeEntity -> userId.equals(likeEntity.getUserId()))
                .findFirst();

        if (likeOptional.isPresent()) {
            LikeEntity likeEntity = likeOptional.get();
            likeRepository.delete(likeEntity);
            return;
        }

        likeRepository.save(like);

    }

    public void setDislike(Long userId, Long contentId) throws NotFoundException {
        var content = contentRepository.findContentEntityById(contentId)
                .orElseThrow(() -> new NotFoundException(ExceptionsMessages.CONTENT_NOT_FOUND_TEXT));

        var dislike = new DislikeEntity();
        dislike.setUserId(userId);
        dislike.setContent(content);

        Optional<LikeEntity> likeOptional = content.getLikes().stream()
                .filter(Objects::nonNull)
                .filter(like -> userId.equals(like.getUserId()))
                .findFirst();

        if(likeOptional.isPresent()){
            deleteLikeAndSetDislike(likeOptional.get(), dislike);
            return;
        }

        Optional<DislikeEntity> dislikeOptional = content.getDislikes().stream()
                .filter(Objects::nonNull)
                .filter(dislikeEntity -> userId.equals(dislikeEntity.getUserId()))
                .findFirst();

        if(dislikeOptional.isPresent()){
            DislikeEntity dislikeEntity = dislikeOptional.get();
            dislikeRepository.delete(dislikeEntity);
            return;
        }
        dislikeRepository.save(dislike);


    }

    private void deleteDislikeAndSetLike(DislikeEntity dislike, LikeEntity like) {
        dislikeRepository.delete(dislike);
        likeRepository.save(like);
    }

    private void deleteLikeAndSetDislike(LikeEntity like, DislikeEntity dislike){
        likeRepository.delete(like);
        dislikeRepository.save(dislike);
    }
}
