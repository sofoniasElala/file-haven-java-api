package com.sofoniaselala.file_haven_java_api.Controller.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @Schema(description = "username", example = "johndoe")
    @NotBlank(message = "username cannot be blank")
    String username,

    @Schema(description = "password", example = "123456")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    String password,

    @Schema(description = "confirmPassword", example = "123456")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    String confirmPassword) {

}