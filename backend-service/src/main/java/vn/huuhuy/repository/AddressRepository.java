package vn.huuhuy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.huuhuy.model.entity.Address;
import vn.huuhuy.model.entity.UserHasAddress;

@Repository
public interface AddressRepository extends JpaRepository<UserHasAddress,Long> {
}
