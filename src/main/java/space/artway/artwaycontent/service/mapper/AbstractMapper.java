package space.artway.artwaycontent.service.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *  Convert entity to DTO and DTO to entity
 *
 * @param <E> entity
 * @param <D> dto
 */
public abstract class AbstractMapper<E, D>{
    public abstract D convertToDto(E entity);

    public abstract E convertToEntity(D dto);

    public List<D> convertToDto(List<E> entities){
        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<E> convertToEntity(List<D> dto){
        return dto.stream()
                .filter(Objects::nonNull)
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }
}
