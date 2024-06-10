package com.br.bills_test.bill.dto;

import com.br.bills_test.bill.BillStatus;
import com.br.bills_test.bill.entity.Bill;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BillResponse(
        Long id,
        @Nullable
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        Date expiringDate,
        @Nullable
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        Date paymentDate,
        BigDecimal amount,
        String description,
        BillStatus status
) {
    public static BillResponse create(Bill bill) {
        return new BillResponse(
                bill.getId(),
                bill.getExpiringDate(),
                bill.getPaymentDate(),
                bill.getAmount(),
                bill.getDescription(),
                bill.getStatus()
        );
    }
}
