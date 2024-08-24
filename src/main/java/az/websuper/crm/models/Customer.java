package az.websuper.crm.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private boolean deleted;
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;
}
