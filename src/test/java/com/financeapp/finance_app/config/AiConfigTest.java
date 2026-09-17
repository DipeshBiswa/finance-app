package com.financeapp.finance_app.config;

import com.financeapp.finance_app.controller.LLMController;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.service.GoalService;
import com.financeapp.finance_app.service.LLMService;
import com.financeapp.finance_app.service.TransactionService;
import com.financeapp.finance_app.service.UserService;
import com.financeapp.finance_app.service.Tools.UserDataTool;
import com.financeapp.finance_app.service.Tools.UserGoalDataTool;
import com.financeapp.finance_app.service.Tools.UserTransactionDataTool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AiConfigTest {
    private final UserService users = mock(UserService.class);
    private final UserDataTool userData = new UserDataTool(users);
    private final UserTransactionDataTool transactions =
            new UserTransactionDataTool(mock(TransactionService.class), users);
    private final UserGoalDataTool goals = new UserGoalDataTool(mock(GoalService.class), users);

    @Test
    void missingApiKeyLeavesChatUnavailableWithoutPreventingStartup() {
        var config = spy(new AiConfig());
        var assistant = assistant(config, " ");
        var currentUser = new user("alice", "unused", "alice@example.test");
        currentUser.setId(7L);
        when(users.loadUserByUsername("alice")).thenReturn(currentUser);

        var response = new LLMController(assistant, users).chat("Hello", () -> "alice");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isEqualTo("AI Service is currently unavailable. Please try again later.");
        verify(config, never()).createChatModel(anyString(), anyString(), anyDouble());
    }

    @Test
    void configuredAssistantCanBeConstructedWithoutCallingGemini() {
        assertThat(assistant(new AiConfig(), "test-key-never-sent")).isNotNull();
    }

    @Test
    void conversationMemoryIsIsolatedByAuthenticatedUser() {
        var model = new RecordingModel();
        var config = configWith(model);
        var assistant = assistant(config, "test-key-never-sent");

        assistant.chat(7L, "Alice's private message");
        assistant.chat(8L, "Bob's message");
        assistant.chat(7L, "Alice's follow-up");

        assertThat(userMessages(model.requests.get(1))).containsExactly("Bob's message");
        assertThat(userMessages(model.requests.get(2)))
                .containsExactly("Alice's private message", "Alice's follow-up");
        assertThat(model.tools).extracting(ToolSpecification::name).containsExactlyInAnyOrder(
                "getUserData", "getUserTransactionData", "getUserTransactionByCategory",
                "getUserAccountBalance", "getUserGoals");
    }

    @Test
    void toolUsesAuthenticatedUserIdEvenWhenModelRequestsAnotherUser() {
        var model = new RecordingModel();
        model.requestUserData = true;
        var currentUser = new user("alice", "password-never-sent", "alice@example.test");
        when(users.findById(7L)).thenReturn(Optional.of(currentUser));

        assistant(configWith(model), "test-key-never-sent").chat(7L, "Who am I?");

        verify(users).findById(7L);
        verify(users, never()).findById(999L);
        assertThat(model.requests.get(1).toString()).contains("alice@example.test")
                .doesNotContain("password-never-sent");
        assertThat(model.tools).allSatisfy(tool ->
                assertThat(tool.parameters() == null || tool.parameters().properties().isEmpty()).isTrue());
    }

    private AiConfig configWith(ChatLanguageModel model) {
        var config = spy(new AiConfig());
        doReturn(model).when(config).createChatModel(anyString(), anyString(), anyDouble());
        return config;
    }

    private LLMService assistant(AiConfig config, String apiKey) {
        return config.llmService(apiKey, "gemini-2.5-flash", 0.7,
                config.chatMemoryProvider(), userData, transactions, goals);
    }

    private List<String> userMessages(List<ChatMessage> messages) {
        return messages.stream().filter(UserMessage.class::isInstance)
                .map(UserMessage.class::cast).map(UserMessage::singleText).toList();
    }

    private static class RecordingModel implements ChatLanguageModel {
        final List<List<ChatMessage>> requests = new ArrayList<>();
        List<ToolSpecification> tools;
        boolean requestUserData;

        @Override
        public Response<AiMessage> generate(List<ChatMessage> messages) {
            return generate(messages, List.of());
        }

        @Override
        public Response<AiMessage> generate(List<ChatMessage> messages, List<ToolSpecification> tools) {
            requests.add(List.copyOf(messages));
            this.tools = tools;
            if (requestUserData && requests.size() == 1) {
                return Response.from(AiMessage.from(ToolExecutionRequest.builder()
                        .name("getUserData").arguments("{\"userId\":999}").build()));
            }
            return Response.from(AiMessage.from("Test response"));
        }
    }
}
