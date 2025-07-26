package com.example.kitchensink.repository;

import com.example.kitchensink.entity.MemberDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends MongoRepository<MemberDocument, String> {

  default List<MemberDocument> findAllOrderedByName() {
    return findAll(Sort.by(Sort.Direction.ASC, "name"));
  }

  Optional<MemberDocument> findByEmail(String email);

}
