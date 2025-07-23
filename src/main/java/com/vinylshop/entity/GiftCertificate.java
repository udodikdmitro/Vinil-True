package com.vinylshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "gift_certificates",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_gift_certificates_code", columnNames = "code")
    }
)
@DiscriminatorValue("GIFT_CERTIFICATE")
public class GiftCertificate extends Product {

    // TODO: визначити формат і як генерувати
    @Column(nullable = false)
    private String code;

    // фіксована сума грошей,
    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    private boolean isUsed;

    /*
        якщо продається як товар то скоріш за все,
        тут буде null до поки якийсь користувач не купить його,
        після цього сюди за сетиться обєкт User того хто купив.
    */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issued_to_id", foreignKey = @ForeignKey(
        name = "fk_gift_certificates_users_issued_to_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User issuedTo;

    // дата і час придатності, буде повністю вводитись користувачем
    // TODO: що робити з протермінованими сертифікатами, архівувати чи видаляти?
    //  коли і як запускатиметься процес чищення?
    @Column(nullable = false)
    private LocalDateTime expiresAt;

}
