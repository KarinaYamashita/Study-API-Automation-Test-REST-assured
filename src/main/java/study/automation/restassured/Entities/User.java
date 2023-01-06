package study.automation.restassured.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Data
public class User {
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phone;
}
