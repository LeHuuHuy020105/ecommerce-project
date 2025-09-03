package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String url;

    @ManyToOne
    private Product product;

    @Enumerated(EnumType.STRING)
    private Status status ;

}
