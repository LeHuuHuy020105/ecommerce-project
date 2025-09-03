package backend_for_react.backend_for_react.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "address")
public class Address extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserHasAddress> userHasAddresses;

    @Column
    private String street;

    @Column
    private String city;

    @Column
    private String district;

    @Column
    private String ward;

    @Column(name = "postal_code")
    private String postalCode;

    @Column
    private BigDecimal latitude;

    @Column
    private BigDecimal longtitude;

}
