package inc.hokage.controller;

import inc.hokage.model.Family;
import inc.hokage.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class FamilyController {

    @Autowired
    FamilyService familyService;

    @PostMapping("family/v1")
    public Family addFamily(@RequestBody Family family) {
        familyService.add(family);

        return family;
    }

    @DeleteMapping("family/v1/{id}")
    public long removeFamily(@PathVariable long id) {
        return familyService.remove(id);
    }
}
