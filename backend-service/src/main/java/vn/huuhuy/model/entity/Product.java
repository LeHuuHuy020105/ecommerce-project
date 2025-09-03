package vn.huuhuy.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.huuhuy.common.enums.Gender;
import vn.huuhuy.common.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ProductVariant> productVariants;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private List<Promotion> promotions;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Image> images;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private BigDecimal price;

    @Column
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column
    private ProductStatus status;

    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender;

}