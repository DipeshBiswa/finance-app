package com.financeapp.finance_app.controller;

import com.financeapp.finance_app.model.Goal;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.service.GoalService;
import com.financeapp.finance_app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api/goal")
@RestController
public class GoalController {
    private final GoalService goalService;
    private final UserService userService;

    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal, Principal principal) {
        user user = (user) userService.loadUserByUsername(principal.getName());
        Goal createdGoal = goalService.createGoal(user, goal.getTitle(), goal.getDescription(),
                goal.getTarget_amount(), goal.getDate(), goal.getCategory());
        return new ResponseEntity<>(createdGoal, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Goal>> getGoals(Principal principal) {
        user user = (user) userService.loadUserByUsername(principal.getName());
        goalService.updateGoalProgress(user);
        List<Goal> goals = goalService.getGoalsByUser(user);
        return new ResponseEntity<>(goals, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoalByGoalId(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
