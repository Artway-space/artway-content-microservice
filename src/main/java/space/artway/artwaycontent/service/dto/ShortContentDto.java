package space.artway.artwaycontent.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import space.artway.artwaycontent.service.ContentStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortContentDto {
    private Long id;
    private String name;
    private ContentStatus status;

}
