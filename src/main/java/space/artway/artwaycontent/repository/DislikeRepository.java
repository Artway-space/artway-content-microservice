package space.artway.artwaycontent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import space.artway.artwaycontent.domain.DislikeEntity;

@Repository
public interface DislikeRepository extends JpaRepository<DislikeEntity, Long> {

}
