package space.artway.artwaycontent.service;


import com.google.common.collect.ImmutableList;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaycontent.client.StorageMsClient;
import space.artway.artwaycontent.domain.*;
import space.artway.artwaycontent.exception.ExceptionsMessages;
import space.artway.artwaycontent.repository.ContentRepository;
import space.artway.artwaycontent.repository.GenreRepository;
import space.artway.artwaycontent.repository.SectionRepository;
import space.artway.artwaycontent.service.dto.ContentDto;
import space.artway.artwaycontent.service.dto.FileDto;
import space.artway.artwaycontent.service.dto.ShortContentDto;
import space.artway.artwaycontent.service.mapper.ContentMapper;

import java.io.InputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {
    private static final long DAYS_TO_DELETE = 30L;
    private final ContentRepository contentRepository;
    private final SectionRepository sectionRepository;
    private final GenreRepository genreRepository;
    private final ContentMapper mapper;
    private final StorageMsClient client;

    public List<ContentDto> getAllAuthorContent(long authorId) {
        var statuses = ImmutableList.of(ContentStatus.DELETED, ContentStatus.INACTIVE, ContentStatus.IN_TRASH_BIN);
        return contentRepository.findContentByAuthorIdAndStatusNotIn(authorId, statuses).stream()
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
        contentEntity.setStatus(ContentStatus.ACTIVE);
        contentEntity.setCheckSum(getFileCheckSum(multipartFile));

        uploadContent(multipartFile, contentEntity);

        //contentEntity.setLink(null);
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

    public List<ContentDto> getAllLikedByUserIdContent(Long userId) {
        var content = contentRepository.findContentEntitiesLikedByUserId(userId)
                .orElse(Collections.emptyList());

        return content.stream()
                .sorted(compareContentByLikes(userId).reversed())
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
    }

    public ShortContentDto putContentInTrashBin(Long contentId) throws NotFoundException {
        var content = contentRepository.findContentEntityById(contentId)
                .orElseThrow(() -> new NotFoundException(ExceptionsMessages.CONTENT_NOT_FOUND_TEXT));
        content.setStatus(ContentStatus.IN_TRASH_BIN);
        ContentEntity deletedContent = contentRepository.save(content);
        return mapper.convertToShortedDto(deletedContent);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteContent() {
        var content = contentRepository.findContentEntitiesByStatus(ContentStatus.IN_TRASH_BIN)
                .orElse(Collections.emptyList());

        content.stream()
                .filter(c -> LocalDateTime.now().minusDays(DAYS_TO_DELETE).isBefore(c.getLastModified()))
                .forEach(this::deleteContent);
    }

    private Comparator<ContentEntity> compareContentByViews(Long userId) {
        return (content1, content2) -> compareDates().compare(getViewByUserId(content1.getViews(), userId), getViewByUserId(content2.getViews(), userId));
    }

    private Comparator<ContentEntity> compareContentByLikes(Long userId) {
        return (content1, content2) -> compareDates().compare(getLikeByUserId(content1.getLikes(), userId), getLikeByUserId(content2.getLikes(), userId));
    }

    private Comparator<BaseEntity> compareDates() {
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

    private LikeEntity getLikeByUserId(List<LikeEntity> likes, Long userId) {
        long count = likes.stream()
                .filter(like -> userId.equals(like.getUserId()))
                .count();

        return likes.stream()
                .filter(like -> userId.equals(like.getUserId()))
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

    @SneakyThrows
    private String getFileCheckSum(MultipartFile multipartFile) {
        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
        InputStream is = multipartFile.getInputStream();

        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        while ((bytesCount = is.read(byteArray)) != -1) {
            shaDigest.update(byteArray, 0, bytesCount);
        }

        is.close();

        byte[] bytes = shaDigest.digest();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            stringBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return stringBuilder.toString();
    }

    @SneakyThrows
    private String uploadContent(MultipartFile file, ContentEntity contentEntity) {
        final ResponseEntity<FileDto> response = client.uploadFile(file);
        String fileId = response.getBody().getId();
        return fileId;
    }

    private ContentEntity deleteContent(ContentEntity content) {
        client.deleteFile(content.getFileId());
        //todo send to drives delete command
        content.setStatus(ContentStatus.DELETED);
        contentRepository.save(content);
        return content;
    }
}
