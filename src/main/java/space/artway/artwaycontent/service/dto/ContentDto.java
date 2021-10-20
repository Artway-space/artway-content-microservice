package space.artway.artwaycontent.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXXX")
    private LocalDateTime createDate;
    private Long size;
    private MetaData metaData;
    private String section;
    private Set<String> genres;
}
