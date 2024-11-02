package org.eam.tinybank.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.eam.tinybank.api.ApiResponse;
import org.eam.tinybank.api.CreateAccountRequest;
import org.eam.tinybank.dao.AccountDao;
import org.eam.tinybank.dao.UserDao;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountService {

    UserDao userDao;
    AccountDao accountDao;

    public ApiResponse create(CreateAccountRequest request) {
        return userDao.retrieve(request.email())
            .map(user -> user.active() ? create(request.email()) : ApiResponse.inactive())
            .orElse(ApiResponse.notFound());
    }

    private ApiResponse create(@NonNull String email) {
        return accountDao.create(email)
            .map(u -> ApiResponse.accountExists())
            .orElseGet(ApiResponse::accountCreated);
    }

}
