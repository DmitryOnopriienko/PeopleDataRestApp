package com.peopledata.restapp.service;

import com.peopledata.restapp.dto.PersonDataDto;
import com.peopledata.restapp.dto.PersonSaveDto;
import com.peopledata.restapp.entity.Person;
import com.peopledata.restapp.exception.NotFoundException;
import com.peopledata.restapp.repository.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class PeopleDataServiceImpl implements PeopleDataService {

  private final PeopleRepository peopleRepository;

  @Autowired
  public PeopleDataServiceImpl(PeopleRepository peopleRepository) {
    this.peopleRepository = peopleRepository;
  }

  @Override
  public int save(PersonSaveDto personDto) {
    Person person = new Person();

    person.setName(personDto.getName());
    person.setSurname(personDto.getSurname());
    person.setBirthday(personDto.getBirthday());

    person = peopleRepository.save(person);
    return person.getId();
  }

  @Override
  public PersonDataDto getById(int id) {
    Person person = peopleRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Person with id %d not found".formatted(id)));

    PersonDataDto personDto = new PersonDataDto();
    personDto.setName(person.getName());
    personDto.setSurname(person.getSurname());

    int age = Period.between(person.getBirthday(), LocalDate.now()).getYears();
    personDto.setAge(age);

    return personDto;
  }
}
