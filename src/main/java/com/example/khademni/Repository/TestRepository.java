package com.example.khademni.Repository;


import com.example.khademni.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByStack(String stack);

}
