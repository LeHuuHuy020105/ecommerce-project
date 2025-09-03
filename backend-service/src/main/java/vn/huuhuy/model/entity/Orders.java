package vn.huuhuy.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.huuhuy.common.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column
    private OrderStatus status;


    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY , mappedBy = "orders")
    private List<OrderItem>orderItems;
}
