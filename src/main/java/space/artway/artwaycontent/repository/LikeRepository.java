package space.artway.artwaycontent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import space.artway.artwaycontent.domain.LikeEntity;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
}
