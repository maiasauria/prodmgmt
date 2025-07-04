package com.alkemy.mleon.prodmgmt.mapper;

import com.alkemy.mleon.prodmgmt.dto.UserDto;
import com.alkemy.mleon.prodmgmt.enums.Role;
import com.alkemy.mleon.prodmgmt.model.User;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "Spring")
public interface UserMapper {

    default UserDto toDTO(User user){
        System.out.println("Converting User to UserDTO: " + user);
        if (user==null) return null;
        Set<String> roles = null;
        if (user.getRoles() !=null)
        {
            roles= user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(roles)
                .build();

    }

    default User toEntity(UserDto dto){
        System.out.println("Converting UserDTO to User: " + dto);
        if (dto==null) return null;
        Set<Role> roles = null;
        if (dto.getRoles() !=null)
        {
            System.out.println("Converting roles from String to Role: " + dto.getRoles());
            roles = dto.getRoles().stream()
                    .map(r -> r.replaceAll("[\\[\\]\"]", "")) // Elimina corchetes y comillas si existen
                    .map(String::trim) // Elimina espacios
                    .map(Role::valueOf)
                    .collect(Collectors.toSet());
        }
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .roles(roles)
                .active(true)
                .build();

    }

}