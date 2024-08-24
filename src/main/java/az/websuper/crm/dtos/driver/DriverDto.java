package az.websuper.crm.dtos.driver;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
