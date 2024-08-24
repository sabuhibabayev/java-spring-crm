package az.websuper.crm.dtos.transport;

import az.websuper.crm.enums.TransportType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportCreateDto {
    private Long id;
    private String registrationPlate;
    private TransportType transportType;
    private Long companyId;
    private boolean deleted;

}
