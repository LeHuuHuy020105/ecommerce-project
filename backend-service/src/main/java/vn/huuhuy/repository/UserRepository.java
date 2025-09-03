package vn.huuhuy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.huuhuy.model.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "select u from User u where u.status= 'ACTIVE' AND (u.firstName like:keyword or u.lastName like:keyword or " +
            "u.phone like:keyword or u.email like:keyword or u.userName like:keyword) ")
    Page<User> searchByKeyword(String keyword, Pageable pageable);

    User findByEmail(String email);
    User findByUserName(String userName);
    User findByPhone(String phone);
}
