package com.vinylshop.entity.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditableEntity<ID> extends CreatableEntity<ID> {

    @UpdateTimestamp
    protected LocalDateTime updatedAt;

    public AuditableEntity() {
        super();
    }

    public AuditableEntity(LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt);
        this.updatedAt = updatedAt;
    }

}
