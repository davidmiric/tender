package com.example.tender.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;

@Entity(name = "tenders")
@Data
@Accessors(chain = true)
public class Tender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 10, max = 1000)
    @Column(length = 1000)
    private String description;

    @ManyToOne
    private Issuer issuer;

    private boolean isActive = true;

    @OneToOne
    @JoinColumn(name = "best_offer_id")
    private Offer bestOffer;

}
