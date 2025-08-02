package org.hibernate.bugs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SequenceGenerator;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Contract extends LogSupport {

    @Id
    @Column(name = "PK")
    @SequenceGenerator(name = "CONTRACT_GENERATOR", sequenceName = "CONTRACT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTRACT_GENERATOR")
    private Long syntheticId;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;
}
