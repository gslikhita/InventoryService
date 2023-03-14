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
import org.springframework.security.core.context.SecurityContextHolder;
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

    public Inventory(String type, String location, Object attributes,
                     float costPrice, ArrayList<SecondaryStatus> secondaryStatus) {
        LocalDateTime localDateTime = LocalDateTime.now();
        this.sku = UUID.randomUUID().toString();
        this.type = type;
        this.status = CREATED;
        this.location = location;
        this.createdAt = localDateTime;
        this.updatedAt = localDateTime;
        this.createdBy = getEmail();
        this.updatedBy = getEmail();
        this.attributes = attributes;
        this.costPrice = costPrice;
        this.secondaryStatus = secondaryStatus;
    }

    public static String getEmail() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return email;
    }

    public void UpdateStatus(Inventory inventory, ArrayList<SecondaryStatus> secondaryStatus) {
        ArrayList<SecondaryStatus> inventorySecondaryStatus = inventory.getSecondaryStatus();
        for (SecondaryStatus statuses : secondaryStatus) {
            if (!inventorySecondaryStatus.contains(statuses)) {
                this.addStatus(inventory, statuses);
                continue;
            }
            this.changeStatus(inventory, statuses);
        }
    }

    private void changeStatus(Inventory inventory, SecondaryStatus statuses) {
        ArrayList<SecondaryStatus> statusArrayList = inventory.getSecondaryStatus();
        for (SecondaryStatus status : statusArrayList) {
            if (status.getName().equals(statuses.getName())) {
                status.setStatus(statuses.getStatus());
            }
        }
        inventory.setSecondaryStatus(statusArrayList);
    }

    private void addStatus(Inventory inventory, SecondaryStatus statuses) {
        ArrayList<SecondaryStatus> statusArrayList = inventory.getSecondaryStatus();
        statusArrayList.add(statuses);
    }

    public void updateLastUser(Inventory inventory) {
        inventory.setUpdatedBy(getEmail());
    }

    public void updateLastTime(Inventory inventory) {
        inventory.setUpdatedAt(LocalDateTime.now());
    }
}
