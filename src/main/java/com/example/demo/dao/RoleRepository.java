package com.example.demo.dao;

import com.example.demo.entities.Role;
import com.example.demo.utils.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {

    Optional<Role> findByName(ERole name);

}
