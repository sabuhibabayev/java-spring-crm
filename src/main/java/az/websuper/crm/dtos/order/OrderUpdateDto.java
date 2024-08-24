package az.websuper.crm.dtos.order;

import az.websuper.crm.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateDto {
    private Long number;
    private OrderStatus orderStatus;
    private Date pickupDate;
    private Date deliveryDate;
    private String pickupAddress;
    private String deliveryAddress;
    private Long weight;
    private Long price;
    private Long tax;
    private Long customerId;
    private Long driverId;
    private Long employeeId;
    private Long companyId;
}
