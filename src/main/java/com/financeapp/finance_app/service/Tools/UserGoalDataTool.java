package com.financeapp.finance_app.service.Tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.service.GoalService;
import com.financeapp.finance_app.service.UserService;

import dev.langchain4j.agent.tool.Tool;

@Component
public class UserGoalDataTool {

    @Autowired
    private GoalService goalService;
    @Autowired
    private UserService userService;

    public UserGoalDataTool(GoalService goalService, UserService userService){
        this.goalService = goalService;
        this.userService = userService;
    }

    @Tool("Gets all of the goals of the user")
    public String getUserGoals(Long userId){
        user user = userService.findById(userId).orElseThrow();
        return "User Goals: " + goalService.getGoalsByUser(user).toString();
        

    }
    
}
