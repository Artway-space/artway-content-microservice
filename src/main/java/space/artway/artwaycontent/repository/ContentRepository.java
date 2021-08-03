package space.artway.artwaycontent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import space.artway.artwaycontent.domain.ContentEntity;
import space.artway.artwaycontent.service.ContentStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, Long> {

    Optional<ContentEntity> findContentEntityById(Long id);

    Optional<List<ContentEntity>> findContentByAuthorIdAndStatusNotIn(Long authorId, Collection<ContentStatus> statuses);

    Optional<ContentEntity> findContentByNameAndAuthorId(String contentName, long authorId);

    Optional<List<ContentEntity>> findContentEntitiesByStatus(ContentStatus status);

    @Query("from ContentEntity c left join c.views v where v.userId = (:userId)")
    Optional<List<ContentEntity>> findContentEntitiesWatchedByUserId(Long userId);

    @Query("from ContentEntity c left join c.likes l where l.userId = (:userId)")
    Optional<List<ContentEntity>> findContentEntitiesLikedByUserId(Long userId);

}
