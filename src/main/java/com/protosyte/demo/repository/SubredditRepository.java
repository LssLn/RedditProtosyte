package com.protosyte.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.protosyte.demo.model.Subreddit;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit,Long>{

}
