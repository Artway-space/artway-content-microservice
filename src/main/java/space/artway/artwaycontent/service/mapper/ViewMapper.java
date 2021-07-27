package space.artway.artwaycontent.service.mapper;

import org.springframework.stereotype.Service;
import space.artway.artwaycontent.domain.ViewEntity;
import space.artway.artwaycontent.service.dto.ViewDto;

@Service
public class ViewMapper extends AbstractMapper<ViewEntity, ViewDto>{

    @Override
    public ViewDto convertToDto(ViewEntity entity) {
        return new ViewDto(entity.getContent().getId(), entity.getUserId());
    }

    @Override
    public ViewEntity convertToEntity(ViewDto dto) {
        return null;
    }
}
