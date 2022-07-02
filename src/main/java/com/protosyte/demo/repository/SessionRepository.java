package com.protosyte.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.protosyte.demo.dto.SessionLogin;

@Repository
public interface SessionRepository extends JpaRepository<SessionLogin,Long>{
	Optional<SessionLogin> findBySessionLoginId(String username);
}
