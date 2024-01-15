/*
Author: Ene Bogdan
Country: Romania
*/
package entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log
@Log4j2
@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Product_ID")
    private int id;

    @Column(name = "Product_name")
    private String name;

    @Column(name = "Product_price")
    private float price;

    @ManyToMany(mappedBy = "products")
    @ToString.Exclude
    private List<Orders> orders;


}
