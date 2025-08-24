package com.sattvabite.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Data Transfer Object for user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int userId;
    private String userName;
    private String userPassword;
    private String address;
    private String city;

    /**
     * Copy constructor for UserDTO.
     *
     * @param other the UserDTO to copy from
     */
    public UserDTO(UserDTO other) {
        if (other != null) {
            this.userId = other.userId;
            this.userName = other.userName;
            this.userPassword = other.userPassword;
            this.address = other.address;
            this.city = other.city;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return userId == userDTO.userId &&
               Objects.equals(userName, userDTO.userName) &&
               Objects.equals(userPassword, userDTO.userPassword) &&
               Objects.equals(address, userDTO.address) &&
               Objects.equals(city, userDTO.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, userPassword, address, city);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
