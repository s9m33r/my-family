package inc.hokage.model;

import lombok.Builder;
import lombok.Data;

@Data
public class Person {
    private String name;
    private String nickName;
    private String occupation;
    private OccupationStatus occupationStatus;
    private String city;
    private String country;
    private boolean alive = true;
}
