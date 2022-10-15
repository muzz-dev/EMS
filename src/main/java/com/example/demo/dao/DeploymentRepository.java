package com.example.demo.dao;

import com.example.demo.entities.Deployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeploymentRepository extends JpaRepository<Deployment,Integer> {

    @Query(value = "select d from Deployment d where d.isActive=true")
    public List<Deployment> findActiveDeployment();


}
