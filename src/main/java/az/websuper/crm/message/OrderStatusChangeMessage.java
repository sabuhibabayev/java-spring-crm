package az.websuper.crm.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusChangeMessage {
    private  Long orderId;
    private String customerFullName;
    private String phoneNumber;
    private  String email;


}
