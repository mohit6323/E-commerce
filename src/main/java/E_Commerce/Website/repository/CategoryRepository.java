package E_Commerce.Website.repository;

import E_Commerce.Website.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}