package vn.huuhuy.controller.request;

import lombok.Getter;
import lombok.Setter;
import vn.huuhuy.common.enums.AddressType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class AddressRequest implements Serializable {
    private Long id;
    private String city;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longtitude;
    private String postalCode;
    private String state;
    private String street;
    private AddressType addressType;
    private boolean isDefault;
}
