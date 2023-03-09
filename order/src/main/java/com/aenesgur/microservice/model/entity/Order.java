package com.aenesgur.microservice.model.entity;

import com.aenesgur.microservice.model.enums.OrderStatus;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "orders")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ref;
    private Long userId;
    private String orderNote;
    private String cardNo;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private String message;

    /*@OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;*/
}
