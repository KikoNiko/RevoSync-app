package com.kbc.kibi_coins.repository;

import com.kbc.kibi_coins.model.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StatementRepository extends JpaRepository<Statement, Long> {
    boolean existsByChecksum(String checksum);
}
