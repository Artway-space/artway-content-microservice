package space.artway.artwaycontent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import space.artway.artwaycontent.domain.ContentEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, Long> {

    Optional<ContentEntity> findContentEntityById(Long id);

    Optional<List<ContentEntity>> findContentByAuthorId(Long authorId);

    Optional<ContentEntity> findContentByNameAndAuthorId(String contentName, long authorId);

    @Query("from ContentEntity c left join c.views v where v.userId = (:userId)")
    Optional<List<ContentEntity>> findContentEntitiesWatchedByUserId(Long userId);

}
