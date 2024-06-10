package com.br.bills_test.bill.entity;

import com.br.bills_test.bill.BillStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Data
@Entity
@Table(name = "bill")
@NoArgsConstructor
public class Bill {

    @Id
    @GeneratedValue
    private Long id;
    private Date expiringDate;
    private Date paymentDate;
    private BigDecimal amount;
    private String description;

    @Enumerated(EnumType.STRING)
    private BillStatus status = BillStatus.OPEN;

    /**
     * Determines if the given expiration date has passed.
     *
     * @param  expiringDate  the date to check against the current time
     * @return                true if the expiration date has passed, false otherwise
     */
    private boolean isExpired(Date expiringDate) {
        return expiringDate.toInstant().isBefore(Instant.now());
    }

    public Bill(Date expiringDate, Date paymentDate, BigDecimal amount, String description) {
        this.expiringDate = expiringDate;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.description = description;
        this.status = getStatus(expiringDate, paymentDate);
    }
    /**
     * Determines the status of a bill based on the given expiration date and payment date.
     *
     * @param  expiringDate  the expiration date of the bill
     * @param  paymentDate   the payment date of the bill
     * @return                the status of the bill (PAID, EXPIRED, or OPEN)
     */
    private BillStatus getStatus(Date expiringDate, Date paymentDate) {
        if (paymentDate != null) {
            return BillStatus.PAID;
        }
        if (isExpired(expiringDate)) {
            return BillStatus.EXPIRED;
        }
        return BillStatus.OPEN;
    }
    /**
     * Pay the bill by updating the status to PAID and setting the payment date to the current time.
     *
     * @throws IllegalStateException if the bill is already paid
     */
    public void pay() {
        if (BillStatus.PAID.equals(this.status)) {
            throw new IllegalStateException("Bill was already paid");
        }
        this.status = BillStatus.PAID;
        paymentDate = Date.from(Instant.now());
    }
}
