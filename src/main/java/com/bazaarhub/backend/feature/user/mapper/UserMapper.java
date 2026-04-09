package com.bazaarhub.backend.feature.user.mapper;

import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.resource.request.UserRequestDto;
import com.bazaarhub.backend.feature.user.resource.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public User mapToUser(UserRequestDto userRequestDto) {
        User user = new User();
        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        user.setEmail(userRequestDto.getEmail());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setGender(userRequestDto.getGender());
        user.setPassword(userRequestDto.getPassword());
        user.setRole(userRequestDto.getRole());
        return user;
    }

    public UserResponseDto mapToUserResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getVersion(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getGender(),
                user.getRole(),
                user.getUserStatus(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }

}
