/*
Author: Ene Bogdan
Country: Romania
*/
package entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log
@Log4j2
@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Order_ID")
    private int id;

    @Column(name = "Sender(Client)_name")
    private String senderName;

    @Column(name = "Recipient_name")
    private String recipientName;

    @Column(name = "Order_value")
    @Transient
    private double value;

    @ManyToOne
    @JoinColumn(name = "Client_ID")
    private Client client;

    @ManyToMany
    @JoinTable(name = "Order_Product",
            joinColumns = @JoinColumn(name = "Order_ID"),
            inverseJoinColumns = @JoinColumn(name = "Product_ID")
    )
    @ToString.Exclude
    @Fetch(FetchMode.JOIN)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Product> products;

    public void calculateOrderValue() {
        float totalValue = 0.0f;

        if (products != null && !products.isEmpty()) {
            for (Product product : products) {
                totalValue += product.getPrice();
            }
        }
        this.value = totalValue;
    }

}
