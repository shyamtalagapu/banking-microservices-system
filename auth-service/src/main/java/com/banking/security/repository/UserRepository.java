package com.banking.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Repository;

import com.banking.security.entity.UserMaster;
@Repository
public interface UserRepository extends JpaRepository<UserMaster, Long>{
	Optional<UserMaster>findByUserName(String userName);
	Optional<UserMaster> findByEmail(String email);

}
