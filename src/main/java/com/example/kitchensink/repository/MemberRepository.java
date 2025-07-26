package com.example.kitchensink.repository;

import com.example.kitchensink.entity.MemberEntity;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends MongoRepository<MemberEntity, String> {

  default List<MemberEntity> findAllOrderedByName() {
    return findAll(Sort.by(Sort.Direction.ASC, "name"));
  }
}
