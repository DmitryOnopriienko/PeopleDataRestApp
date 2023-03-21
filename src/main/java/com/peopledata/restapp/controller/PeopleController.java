package com.peopledata.restapp.controller;

import com.peopledata.restapp.dto.PersonDataDto;
import com.peopledata.restapp.dto.PersonSaveDto;
import com.peopledata.restapp.dto.RestResponse;
import com.peopledata.restapp.service.PeopleDataService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/people")
public class PeopleController {

  private final PeopleDataService peopleDataService;

  @Autowired
  public PeopleController(PeopleDataService peopleDataService) {
    this.peopleDataService = peopleDataService;
  }

  @GetMapping("/{id}")
  public PersonDataDto getById(@PathVariable int id) {
    return peopleDataService.getById(id);
  }

  @PostMapping("/create")
  @ResponseStatus(HttpStatus.CREATED)
  public RestResponse createPerson(@RequestBody @Valid PersonSaveDto person) {
    int id = peopleDataService.save(person);
    return new RestResponse(String.valueOf(id));
  }
}
