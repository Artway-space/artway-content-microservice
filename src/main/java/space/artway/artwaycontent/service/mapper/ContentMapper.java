package space.artway.artwaycontent.service.mapper;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaycontent.domain.ContentEntity;
import space.artway.artwaycontent.service.ContentType;
import space.artway.artwaycontent.service.dto.ContentDto;
import space.artway.artwaycontent.service.dto.MetaData;

@Service
public class ContentMapper extends AbstractMapper<ContentEntity, ContentDto>{

    @Override
    public ContentDto convertToDto(ContentEntity entity) {
        ContentDto contentDto = new ContentDto();
        contentDto.setName(entity.getName());
        contentDto.setLink(entity.getLink());
        contentDto.setSize(entity.getSize());
        contentDto.setCreateDate(entity.getCreatedAt());
        contentDto.setMetaData(getMetaData(entity));
        contentDto.setContentType(entity.getContentType().getCode());
        return contentDto;
    }

    public ContentEntity convertToEntity(ContentDto dto, MultipartFile multipartFile) {
        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setContentType(ContentType.findValueByCode(multipartFile.getContentType()));
        contentEntity.setSize(multipartFile.getSize());
        return contentEntity;
    }

    @Override
    public ContentEntity convertToEntity(ContentDto dto) {
        return null;
    }

    private MetaData getMetaData(ContentEntity entity){
        MetaData metaData = new MetaData();
        metaData.setLikes((long) entity.getLikes().size());
        metaData.setDislikes((long) entity.getDislikes().size());
        metaData.setViews((long) entity.getViews().size());
        return metaData;
    }
}
