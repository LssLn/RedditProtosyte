package com.protosyte.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.protosyte.demo.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long>{

}
