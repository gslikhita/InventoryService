package com.olx.inventoryManagementSystem.controller.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserRequest {

    @Email
    @NotBlank
    String email;

    @NotBlank
    String password;
}
