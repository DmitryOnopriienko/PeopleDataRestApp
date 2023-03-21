package com.peopledata.restapp.service;

import com.peopledata.restapp.dto.PersonDataDto;
import com.peopledata.restapp.dto.PersonSaveDto;

public interface PeopleDataService {
  int save(PersonSaveDto person);

  PersonDataDto getById(int id);
}
