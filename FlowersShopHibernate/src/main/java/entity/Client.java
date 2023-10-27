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

public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Client_ID")
    private int id;

    @Column(name = "Client_name")
    private String name;

    @Column(name = "Client_email")
    private String emailAddress;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Orders> orders;

}
