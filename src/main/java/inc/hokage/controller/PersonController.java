package inc.hokage.controller;

import inc.hokage.model.Person;
import inc.hokage.model.Relationship;
import inc.hokage.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {

    @Autowired
    PersonService personService;

    @PostMapping("person/v1")
    public long createPerson(@RequestBody Person person) {
        return personService.add(person);
    }

    @PutMapping("person/v1/{personId}/family/{familyId}")
    public long addToFamily(@PathVariable long personId, @PathVariable long familyId) {
        return personService.addToFamily(personId, familyId);
    }

    @DeleteMapping("person/v1/{id}")
    public long removePerson(@PathVariable long id) {
        return personService.remove(id);
    }

    @DeleteMapping("person/v1/{personId}/family/{familyId}")
    public long removeFromFamily(@PathVariable long personId, @PathVariable long familyId) {
        return personService.removeFromFamily(personId, familyId);
    }

    @GetMapping("person/v1/{personId}/cousins")
    public List<String> getCousins(@PathVariable long personId) {
        return personService.getCousins(personId);
    }

    @PutMapping("person/v1/relationship/{firstPersonId}/{relationship}/{secondPersonId}")
    public long addRelationship(@PathVariable long firstPersonId, @PathVariable Relationship relationship, @PathVariable long secondPersonId) {
        return personService.addRelationship(firstPersonId, relationship, secondPersonId);
    }
}
