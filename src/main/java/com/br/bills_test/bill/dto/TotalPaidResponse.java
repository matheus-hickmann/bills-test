package com.br.bills_test.bill.dto;

import java.math.BigDecimal;

public record TotalPaidResponse(
        BigDecimal totalPaid
) {
}
