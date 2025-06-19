package cn.edu.shiep.backend.meetingroom.repository;

import cn.edu.shiep.backend.meetingroom.enums.ERole;
import cn.edu.shiep.backend.meetingroom.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByName(ERole name);
}
