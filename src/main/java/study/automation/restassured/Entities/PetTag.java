package study.automation.restassured.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor 
@Data
public class PetTag {
    private Integer id;
    private String name;
}
