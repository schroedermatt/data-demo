package org.msse.demo.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer")
public class CustomerEntity {
    @Id
    private String id;
    private String type;
    private String gender;
    private String fname;
    private String mname;
    private String lname;
    private String fullname;
    private String suffix;
    private String title;
    private String birthdt;
    private String joindt;
}
