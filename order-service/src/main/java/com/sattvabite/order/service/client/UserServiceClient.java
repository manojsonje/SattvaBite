package com.sattvabite.order.service.client;

import com.sattvabite.order.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with the User Service.
 */
@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient {
    
    /**
     * Fetches user details by user ID.
     *
     * @param userId the ID of the user to fetch
     * @return the user details
     */
    @GetMapping("/api/v1/users/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);
}
