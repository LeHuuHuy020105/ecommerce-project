package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.Product;
import backend_for_react.backend_for_react.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query(value = "select u from Product u where u.productStatus = :status AND (u.name like:keyword or u.description like:keyword)")
    Page<Product> searchByKeyword(String keyword, ProductStatus status, Pageable pageable);

    Page<Product> findAllByProductStatus(ProductStatus status, Pageable pageable);

}
