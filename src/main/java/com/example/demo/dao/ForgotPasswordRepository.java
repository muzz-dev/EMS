package com.example.demo.dao;

import com.example.demo.entities.ForgotPassword;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ForgotPasswordRepository extends CrudRepository<ForgotPassword,String> {

    public Optional<ForgotPassword> findByToken(String token);

    @Query(value = "select f from ForgotPassword f where f.userMaster.userName = :username")
    public Optional<ForgotPassword> findByUserName(String username);
}
