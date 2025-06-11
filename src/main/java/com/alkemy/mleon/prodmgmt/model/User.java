package com.alkemy.mleon.prodmgmt.model;

import com.alkemy.mleon.prodmgmt.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Document(collection="users")
@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private  String id;

    @NotBlank
    private String name;

    @Indexed
    @Email
    private String username;

    @Size(min=8 , max=20)
    private String password;

    @Field("roles")
    private Set<Role> roles;
}