package space.artway.artwaycontent.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.artway.artwaycontent.domain.ContentEntity;
import space.artway.artwaycontent.domain.ViewEntity;
import space.artway.artwaycontent.exception.ExceptionsMessages;
import space.artway.artwaycontent.repository.ContentRepository;
import space.artway.artwaycontent.repository.ViewsRepository;
import space.artway.artwaycontent.service.dto.ViewDto;
import space.artway.artwaycontent.service.mapper.ViewMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewsService {
    private final ContentRepository contentRepository;
    private final ViewsRepository viewsRepository;
    private final ViewMapper viewMapper;

    public void addNewView(Long userId, Long contentId) throws NotFoundException {
        var content = contentRepository.findContentEntityById(contentId)
                .orElseThrow(() -> new NotFoundException(ExceptionsMessages.CONTENT_NOT_FOUND_TEXT));

        var view = new ViewEntity();
        view.setContent(content);
        view.setUserId(userId);

        viewsRepository.save(view);
    }

    public List<ViewDto> getUniqueViews(Long contentId) throws NotFoundException {
        var content = contentRepository.findContentEntityById(contentId)
                .orElseThrow(() -> new NotFoundException(ExceptionsMessages.CONTENT_NOT_FOUND_TEXT));

        List<ViewEntity> uniqueViewEntities = content.getViews().stream()
                .filter(distinctByKey(ViewEntity::getUserId))
                .collect(Collectors.toList());

        return viewMapper.convertToDto(uniqueViewEntities);
    }

    public List<ViewDto> getAllViewsByAuthorId(long authorId) throws NotFoundException {
        var content = contentRepository.findContentByAuthorId(authorId)
                .orElseThrow(() -> new NotFoundException(ExceptionsMessages.CONTENT_NOT_FOUND_TEXT));

        return content.stream()
                .filter(Objects::nonNull)
                .map(ContentEntity::getViews)
                .map(viewMapper::convertToDto)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
