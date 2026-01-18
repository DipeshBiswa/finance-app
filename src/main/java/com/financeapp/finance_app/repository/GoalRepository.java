package com.financeapp.finance_app.repository;

import com.financeapp.finance_app.model.Goal;
import com.financeapp.finance_app.model.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findAllByUser(user user);
}
