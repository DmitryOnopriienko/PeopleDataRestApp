package com.peopledata.restapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Data
@Jacksonized
public class PersonSaveDto {

  @NotBlank(message = "name must be not blank")
  private String name;

  @NotBlank(message = "surname must be not blank")
  private String surname;

  @NotNull(message = "birthday must be provided")
  private LocalDate birthday;
}
