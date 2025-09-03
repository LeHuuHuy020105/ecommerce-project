package vn.huuhuy.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.huuhuy.common.enums.Gender;
import vn.huuhuy.common.enums.ProductType;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "categories")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column
    private ProductType type;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;


}
