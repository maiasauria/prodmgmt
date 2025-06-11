package com.alkemy.mleon.prodmgmt.mapper;

import com.alkemy.mleon.prodmgmt.dto.UserDTO;
import com.alkemy.mleon.prodmgmt.enums.Role;
import org.mapstruct.Mapper;
import com.alkemy.mleon.prodmgmt.model.User;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "Spring")
public interface UserMapper {

    default UserDTO toDTO(User user){
        if (user==null) return null;
        Set<String> roles = null;
        if (user.getRoles() !=null)
        {
            roles= user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        }
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(roles)
                .build();

    }

    default User toEntity(UserDTO dto){
        if (dto==null) return null;
        Set<Role> roles = null;
        if (dto.getRoles() !=null)
        {
            roles= dto.getRoles().stream().map(Role::valueOf).collect(Collectors.toSet());
        }
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .roles(roles)
                .build();

    }

}