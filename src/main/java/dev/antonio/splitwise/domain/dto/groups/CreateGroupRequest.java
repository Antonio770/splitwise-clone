package dev.antonio.splitwise.domain.dto.groups;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateGroupRequest {

    @NotBlank(message = "Group name is required")
    @Size(max = 50, message = "Group name should have a maximum of {max} characters")
    @Pattern(regexp = "^[\\w\\s-]+$", message = "Group name should not contain special characters")
    private String name;

}
