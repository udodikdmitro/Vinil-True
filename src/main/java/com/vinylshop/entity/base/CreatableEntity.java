package com.vinylshop.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class CreatableEntity<ID> extends BaseEntity<ID> {

    @CreationTimestamp
    @Column(updatable = false)
    protected LocalDateTime createdAt;

    public CreatableEntity() {
        super();
    }

    public CreatableEntity(LocalDateTime createdAt) {
        super();
        this.createdAt = createdAt;
    }

}
