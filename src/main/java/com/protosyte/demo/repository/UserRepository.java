package com.protosyte.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.protosyte.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

}
