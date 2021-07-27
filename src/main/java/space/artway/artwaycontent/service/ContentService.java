package space.artway.artwaycontent.service;


import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaycontent.domain.*;
import space.artway.artwaycontent.exception.ExceptionsMessages;
import space.artway.artwaycontent.repository.ContentRepository;
import space.artway.artwaycontent.repository.GenreRepository;
import space.artway.artwaycontent.repository.SectionRepository;
import space.artway.artwaycontent.service.dto.ContentDto;
import space.artway.artwaycontent.service.mapper.ContentMapper;

import javax.swing.event.ListDataEvent;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final SectionRepository sectionRepository;
    private final GenreRepository genreRepository;
    private final ContentMapper mapper;

    public List<ContentDto> getAllAuthorContent(long authorId) {
        return contentRepository.findContentByAuthorId(authorId).stream()
                .flatMap(Collection::stream)
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
    }

    public ContentDto getContentByNameAndAuthorId(String contentName, long authorId) throws NotFoundException {
        Optional<ContentEntity> contentByNameAndAuthorId = contentRepository.findContentByNameAndAuthorId(contentName, authorId);
        if (contentByNameAndAuthorId.isPresent()) {
            return mapper.convertToDto(contentByNameAndAuthorId.get());
        }
        throw new NotFoundException(ExceptionsMessages.CONTENT_NOT_FOUND_TEXT);
    }

    public ContentDto saveContent(MultipartFile multipartFile, ContentDto contentDto) {
        Section section = sectionRepository.getByName(contentDto.getSection())
                .orElseThrow();

        ContentEntity contentEntity = mapper.convertToEntity(contentDto, multipartFile);
        contentEntity.setViews(Collections.emptyList());
        contentEntity.setLikes(Collections.emptyList());
        contentEntity.setDislikes(Collections.emptyList());
        contentEntity.setSection(section);
        contentEntity.setGenres(getGenresFromDto(contentDto.getGenres()));

        uploadContent(multipartFile, contentEntity);

        contentEntity.setLink(null);
        return mapper.convertToDto(contentRepository.save(contentEntity));
    }

    public List<ContentDto> getAllViewedByUserIdContent(Long userId) {
        var content = contentRepository.findContentEntitiesWatchedByUserId(userId)
                .orElse(Collections.emptyList());

        return content.stream()
                .sorted(compareContentByViews(userId).reversed())
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
    }

    private Comparator<ContentEntity> compareContentByViews(Long userId){
       return (content1, content2) -> compareViewDates().compare(getViewByUserId(content1.getViews(), userId),getViewByUserId( content2.getViews(), userId));
    }

    private Comparator<ViewEntity> compareViewDates(){
        return Comparator.comparing(BaseEntity::getCreatedAt);
    }

    private ViewEntity getViewByUserId(List<ViewEntity> views, Long userId) {
        long count = views.stream()
                .filter(view -> userId.equals(view.getUserId()))
                .count();

        return views.stream()
                .filter(view -> userId.equals(view.getUserId()))
                .skip(count - 1)
                .findFirst()
                .get();
    }

    private List<Genre> getGenresFromDto(Set<String> genresDto) {
        return genresDto.stream()
                .filter(Objects::nonNull)
                .map(genreRepository::getByName)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private String uploadContent(MultipartFile file, ContentEntity contentEntity) {

        String link = "";
        return link;
    }


}
