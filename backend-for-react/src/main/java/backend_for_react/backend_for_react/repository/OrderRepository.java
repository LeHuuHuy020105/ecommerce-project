package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.model.Order;
import backend_for_react.backend_for_react.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOrderByUser(User user);
}
