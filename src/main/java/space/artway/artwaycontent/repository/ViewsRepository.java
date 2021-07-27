package space.artway.artwaycontent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import space.artway.artwaycontent.domain.ViewEntity;

@Repository
public interface ViewsRepository extends JpaRepository<ViewEntity, Long> {
}
