package com.example.auth.service;

import com.example.auth.model.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final WebClient webClient;

    public List<UserResponse> fetchUsers(Collection<String> userNames) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users")
                        .queryParam("userNames", userNames)
                        .build()
                )
                .retrieve()
                .bodyToFlux(UserResponse.class) // list of objects
                .collectList()
                .block();
    }
}
