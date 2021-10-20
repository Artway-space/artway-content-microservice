package space.artway.artwaycontent.service.dto;

import lombok.Data;
import space.artway.artwaycontent.service.ContentStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ContentDto {
    private String name;
    private String link;
    private String contentType;
    private ContentStatus status;
    private LocalDateTime createDate;
    private Long size;
    private MetaData metaData;
    private String section;
    private Set<String> genres;
}
