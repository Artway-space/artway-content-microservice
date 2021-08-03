package space.artway.artwaycontent.service.dto;

import lombok.Data;
import space.artway.artwaycontent.service.ContentStatus;

import java.util.Date;
import java.util.Set;

@Data
public class ContentDto {
    private String name;
    private String link;
    private String contentType;
    private ContentStatus status;
    private Date createDate;
    private Long size;
    private MetaData metaData;
    private String section;
    private Set<String> genres;
}
