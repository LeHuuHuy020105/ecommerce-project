package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.model.UserHasAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHasAddressRepository extends JpaRepository<UserHasAddress,Long> {
}
