package space.artway.artwaycontent.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaycontent.domain.ContentEntity;
import space.artway.artwaycontent.domain.Genre;
import space.artway.artwaycontent.service.dto.ContentDto;
import space.artway.artwaycontent.service.dto.ShortContentDto;

@Mapper(componentModel = "spring")
public interface ContentMapper {

    @Mappings({
            @Mapping(target = "metaData.likes", expression = "java(new Long(contentEntity.getLikes().size()))"),
            @Mapping(target = "metaData.dislikes", expression = "java(new Long(contentEntity.getDislikes().size()))"),
            @Mapping(target = "metaData.views", expression = "java(new Long(contentEntity.getViews().size()))"),
            @Mapping(target = "createDate", source = "createdAt"),
            @Mapping(target = "contentType", source = "entity.contentType.code"),
            @Mapping(target = "section", source = "entity.section.name"),
            @Mapping(target = "genres", qualifiedByName = "toDtoGenresMapping")
    })
    ContentDto convertToDto(ContentEntity entity);

    @Mappings({
            @Mapping(target = "section", ignore = true),
            @Mapping(target = "genres", ignore = true),
            @Mapping(target = "size", source="multipartFile.size"),
            @Mapping(target = "name", source = "dto.name"),
            @Mapping(target = "contentType", expression = "java(ContentType.findValueByCode(multipartFile.getContentType()))")
    })
    ContentEntity convertToEntity(ContentDto dto, MultipartFile multipartFile);
 /*   {
        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setContentType(ContentType.findValueByCode(multipartFile.getContentType()));
        contentEntity.setSize(multipartFile.getSize());
        return contentEntity;
    }*/


   default ContentEntity convertToEntity(ContentDto dto) {
        throw new UnsupportedOperationException();
    }


    @Mappings({
            @Mapping(target = "id", source = "entity.id"),
            @Mapping(target = "name", source = "entity.name"),
            @Mapping(target = "status", source = "entity.status")
    })
    ShortContentDto convertToShortedDto(ContentEntity entity);

   @Named("toDtoGenresMapping")
   default String toDtoGenresMapping(Genre genre){
       return genre.getName();
   }

//    private MetaData getMetaData(ContentEntity entity) {
//        MetaData metaData = new MetaData();
//        metaData.setLikes((long) entity.getLikes().size());
//        metaData.setDislikes((long) entity.getDislikes().size());
//        metaData.setViews((long) entity.getViews().size());
//        return metaData;
//    }
}
