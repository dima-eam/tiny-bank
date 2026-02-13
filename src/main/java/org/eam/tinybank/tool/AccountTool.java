package org.eam.tinybank.tool;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eam.tinybank.api.CreateAccountRequest;
import org.eam.tinybank.api.CreateUserRequest;
import org.eam.tinybank.service.AccountService;
import org.eam.tinybank.service.UserService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class AccountTool {

    private final AccountService accountService;
    private final UserService userService;

    @Tool(description = "If a user asks for account creation, request for their email, and call this method.")
    public String createAnonymousAccount(String email) { // TODO add anon status for accounts
        var response = userService.create(new CreateUserRequest("", "", email));
        if (response.statusCode() != HttpStatus.OK) {
            return response.message();
        }

        return accountService.create(new CreateAccountRequest(email)).message();
    }

}