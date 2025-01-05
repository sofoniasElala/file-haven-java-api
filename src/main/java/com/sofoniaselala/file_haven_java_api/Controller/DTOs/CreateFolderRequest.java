package com.sofoniaselala.file_haven_java_api.Controller.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateFolderRequest(
    @Schema(description = "name", example = "photos")
    @NotBlank(message = "name cannot be blank")
    String name) {

}