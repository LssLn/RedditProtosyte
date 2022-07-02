package com.protosyte.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.protosyte.demo.dto.SessionLoginRequest;
import com.protosyte.demo.model.SessionLogin;


@Repository
public interface SessionRepository extends JpaRepository<SessionLoginRequest,Long>{
	Optional<SessionLoginRequest> findBySessionLoginId(String username);
}
