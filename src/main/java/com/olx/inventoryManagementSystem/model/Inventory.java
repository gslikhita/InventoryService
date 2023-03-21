package com.olx.inventoryManagementSystem.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.olx.inventoryManagementSystem.controller.dto.SecondaryStatus;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Component
@AllArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Inventory {

    public static final String CREATED = "created";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private int id;

    private String sku;
    private String type;
    private String status;
    private String location;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "attributes", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Object attributes;

    @Column(name = "cost_price")
    private float costPrice;

    @Column(name = "sold_at")
    private float soldAt;

    @Column(name = "secondary_status", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private ArrayList<SecondaryStatus> secondaryStatus;

    public Inventory(String type, String location, String email, Object attributes,
                     float costPrice, ArrayList<SecondaryStatus> secondaryStatus) {
        LocalDateTime localDateTime = LocalDateTime.now();
        this.sku = UUID.randomUUID().toString();
        this.type = type;
        this.status = CREATED;
        this.location = location;
        this.createdAt = localDateTime;
        this.updatedAt = localDateTime;
        this.createdBy = email;
        this.updatedBy = email;
        this.attributes = attributes;
        this.costPrice = costPrice;
        this.secondaryStatus = secondaryStatus;
    }

    public void UpdateStatus(ArrayList<SecondaryStatus> secondaryStatus) {
        ArrayList<SecondaryStatus> inventorySecondaryStatus = this.getSecondaryStatus();
        for (SecondaryStatus statuses : secondaryStatus) {
            if (!inventorySecondaryStatus.contains(statuses)) {
                this.addStatus(statuses);
                continue;
            }
            this.changeStatus(statuses);
        }
    }

    private void changeStatus(SecondaryStatus statuses) {
        ArrayList<SecondaryStatus> statusArrayList = this.getSecondaryStatus();
        for (SecondaryStatus status : statusArrayList) {
            if (status.getName().equals(statuses.getName())) {
                status.setStatus(statuses.getStatus());
            }
        }
        this.setSecondaryStatus(statusArrayList);
    }

    private void addStatus(SecondaryStatus statuses) {
        ArrayList<SecondaryStatus> statusArrayList = this.getSecondaryStatus();
        statusArrayList.add(statuses);
    }

    public void updateLastUser(String email) {
        this.setUpdatedBy(email);
    }

    public void updateLastTime() {
        this.setUpdatedAt(LocalDateTime.now());
    }
}
