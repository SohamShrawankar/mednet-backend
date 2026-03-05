package com.mednet.repository;

import com.mednet.entity.Prefix;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrefixRepository extends JpaRepository<Prefix, Long> {
}
