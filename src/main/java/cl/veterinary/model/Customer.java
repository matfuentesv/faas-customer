package cl.veterinary.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "CUSTOMER")
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", sequenceName = "CUSTOMER_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "TELEFONO")
    private String telefono;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "DIRECCION")
    private String direccion;
}
