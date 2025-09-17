package com.vinylshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email cannot be empty")
    @Size(max = 15, message = "The email must contain to 255 characters")
    @Email(message = "Invalid the email format")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 50, message = "The password must contain 8 to 50 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$",
            message = "The password must contain at least one uppercase letter, " +
                    "one lowercase letter, one number, and one special character."
    )
    private String password;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 100, message = "The name must contain between 2 and 100 characters")
    @Pattern(regexp = "^[А-Яа-яЇїІіЄєҐґA-Za-z]{2,}([ '-][А-Яа-яЇїІіЄєҐґA-Za-z]{2,})*$",
            message = "The name must contain only letters, spaces, hyphens, or apostrophes. " +
                    "Each word must contain at least two letters")
    private String fullName;

    public void cleanFieldsFromExtraSpaces() {

        if (this.fullName != null) {
            this.fullName = this.fullName.trim().replaceAll("\\s{2,}", " ");
        }
    }
}

