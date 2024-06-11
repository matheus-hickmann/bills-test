package com.br.bills_test.bill.dto;

import com.br.bills_test.bill.entity.Bill;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.opencsv.bean.CsvDate;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillRequest {

    public Bill toBill() {
        return new Bill(expiringDate, paymentDate, amount, description);
    }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @CsvDate("dd-MM-yyyy")
    Date expiringDate;
    @Nullable
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @CsvDate("dd-MM-yyyy")
    Date paymentDate;
    @Nullable
    BigDecimal amount;
    @Nullable
    String description;
}
