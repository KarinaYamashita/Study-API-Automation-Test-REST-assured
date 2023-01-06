package study.automation.restassured.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Data
public class Pet {
    private Integer id;
    private Category category;
    private String name;
    private String[] photoUrls;
    private PetTag[] tags;
    private String status;
   
}
