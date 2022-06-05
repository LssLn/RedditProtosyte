package com.protosyte.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.protosyte.demo.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long>{

}
