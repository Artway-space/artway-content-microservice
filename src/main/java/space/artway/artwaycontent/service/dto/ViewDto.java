package space.artway.artwaycontent.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewDto {
    private Long contentId;
    private Long userId;
}
