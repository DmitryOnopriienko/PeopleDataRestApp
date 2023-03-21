package com.peopledata.restapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peopledata.restapp.dto.PersonDataDto;
import com.peopledata.restapp.dto.RestResponse;
import com.peopledata.restapp.entity.Person;
import com.peopledata.restapp.exception.NotFoundException;
import com.peopledata.restapp.repository.PeopleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PeopleControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private PeopleRepository peopleRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private final String PEOPLE_CREATE_URI = "/people/create";

  private final String PEOPLE_URI = "/people";

  @Test
  void testCreateNotValidPersons() throws Exception {
    mvc.perform(post(PEOPLE_CREATE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "name": "NoSurname",
                      "birthday": "2020-03-03"
                    }
                    """)
    ).andExpect(status().isBadRequest());

    mvc.perform(post(PEOPLE_CREATE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "surname": "NoName",
                      "birthday": "2020-03-03"
                    }
                    """)
    ).andExpect(status().isBadRequest());

    mvc.perform(post(PEOPLE_CREATE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "name": "No birthday",
                      "surname": "date"
                    }
                    """)
    ).andExpect(status().isBadRequest());
  }

  @Test
  void createValidPerson() throws Exception {
    String name = "Dmitry";
    String surname = "Onopriienko";
    String birthday = "2003-11-14";

    String json = """
            {
                "name": "%s",
                "surname": "%s",
                "birthday": "%s"
            }
            """.formatted(name, surname, birthday);

    MvcResult result = mvc.perform(post(PEOPLE_CREATE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isCreated())
            .andReturn();

    RestResponse response = parseResponse(result, RestResponse.class);
    int personId = Integer.parseInt(response.getResult());
    assertThat(personId).isGreaterThanOrEqualTo(1);

    Person person = peopleRepository.findById(personId)
            .orElseThrow(() -> new NotFoundException("Person with id %d not found"
                    .formatted(personId)));

    assertEquals(name, person.getName());
    assertEquals(surname, person.getSurname());
    assertEquals(birthday, person.getBirthday().toString());
  }

  @Test
  void createAndGetPerson() throws Exception {
    String name = "Dmitry";
    String surname = "Onopriienko";
    String birthday = "2003-11-14";
    LocalDate dayOfBirth = LocalDate.parse(birthday);

    String json = """
            {
                "name": "%s",
                "surname": "%s",
                "birthday": "%s"
            }
            """.formatted(name, surname, birthday);

    MvcResult result = mvc.perform(post(PEOPLE_CREATE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isCreated())
            .andReturn();

    RestResponse response = parseResponse(result, RestResponse.class);
    int personId = Integer.parseInt(response.getResult());
    assertThat(personId).isGreaterThanOrEqualTo(1);

    result = mvc.perform(get(PEOPLE_URI + "/" + personId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isOk())
            .andReturn();

    PersonDataDto person = parseResponse(result, PersonDataDto.class);

    int age = Period.between(dayOfBirth, LocalDate.now()).getYears();

    assertEquals(name, person.getName());
    assertEquals(surname, person.getSurname());
    assertEquals(age, person.getAge());
  }

  private <T>T parseResponse(MvcResult mvcResult, Class<T> c) {
    try {
      return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), c);
    } catch (JsonProcessingException | UnsupportedEncodingException e) {
      throw new RuntimeException("Error parsing json", e);
    }
  }

}
