package com.financeapp.finance_app.controller;

import com.financeapp.finance_app.model.Goal;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.service.GoalService;
import com.financeapp.finance_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequestMapping("/api/goal")
@RestController
public class GoalController {
    @Autowired
    private GoalService goalService;
    @Autowired
    private UserService userService;
    public GoalController(GoalService goalService){
        this.goalService = goalService;
    }

    @PostMapping("/create")
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal, Principal principal){
        user user = (user) userService.loadUserByUsername(principal.getName());
        Goal createdGoal = goalService.createGoal(user, goal.getTitle(),goal.getDescription(),goal.getTarget_amount(),goal.getDate(), goal.getCategory());
        return new ResponseEntity<>(createdGoal, HttpStatus.CREATED);

    }
}
