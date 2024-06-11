package com.br.bills_test.bill.controller;

import com.br.bills_test.bill.BillStatus;
import com.br.bills_test.bill.dto.BillRequest;
import com.br.bills_test.bill.dto.BillResponse;
import com.br.bills_test.bill.dto.CustomPage;
import com.br.bills_test.bill.dto.TotalPaidResponse;
import com.br.bills_test.bill.entity.Bill;
import com.br.bills_test.bill.service.BillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillControllerTest {

    @Mock
    private BillServiceImpl billService;

    private BillController billController;

    @BeforeEach
    public void setup() {
        billController = new BillController(billService);
    }

    @Test
    public void testGetUnpaidBills_returnsPageOfBillResponses() {
        // Arrange
        List<Bill> bills = Arrays.asList(
                new Bill(1L, null, null, BigDecimal.valueOf(100), "Bill 1", BillStatus.OPEN),
                new Bill(2L, null, null, BigDecimal.valueOf(200), "Bill 2", BillStatus.OPEN)
        );
        Page<Bill> billPage = new PageImpl<>(bills);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("expiringDate").ascending());
        when(billService.getUnpaidBills(pageable)).thenReturn(billPage);

        // Act
        ResponseEntity<CustomPage<BillResponse>> response = billController.getUnpaidBills(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<BillResponse> billResponses = response.getBody().getContent();
        assertEquals(2, billResponses.size());
        assertEquals(Long.valueOf(1L), billResponses.get(0).id());
        assertEquals(BigDecimal.valueOf(100), billResponses.get(0).amount());
        assertEquals(Long.valueOf(2L), billResponses.get(1).id());
        assertEquals(BigDecimal.valueOf(200), billResponses.get(1).amount());
    }

    @Test
    public void testGetPaidAmount_WithValidDates_ReturnsTotalPaidResponse() {
        // Arrange
        Date startDate = new Date();
        Date endDate = new Date();
        BigDecimal totalPaid = BigDecimal.TEN;
        when(billService.getTotalPaid(startDate, endDate)).thenReturn(totalPaid);

        // Act
        ResponseEntity<TotalPaidResponse> response = billController.getPaidAmount(startDate, endDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(totalPaid, response.getBody().totalPaid());
    }

    @Test
    void pay_BillFound_ReturnsNoContent() {
        // Arrange
        Long billId = 1L;
        Bill bill = new Bill();
        Optional<Bill> paidBill = Optional.of(bill);
        when(billService.pay(billId)).thenReturn(paidBill);

        // Act
        ResponseEntity<BillResponse> response = billController.pay(billId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void pay_BillNotFound_ReturnsNotFound() {
        // Arrange
        Long billId = 1L;
        Optional<Bill> paidBill = Optional.empty();
        when(billService.pay(billId)).thenReturn(paidBill);

        // Act
        ResponseEntity<BillResponse> response = billController.pay(billId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateBill_ValidIdAndRequest_ReturnsNoContent() {
        // Arrange
        Long id = 1L;
        BillRequest billRequest = new BillRequest( new Date(), null, BigDecimal.valueOf(100), "Bill 1");
        Bill bill = new Bill(id, new Date(), null, BigDecimal.valueOf(100), "Bill 1", BillStatus.OPEN);
        Optional<Bill> updatedBill = Optional.of(bill);
        when(billService.update(id, billRequest.toBill())).thenReturn(updatedBill);

        // Act
        ResponseEntity<BillResponse> response = billController.update(id, billRequest);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testUpdateBill_InvalidIdAndValidRequest_ReturnsNotFound() {
        // Arrange
        Long id = null;
        BillRequest billRequest = new BillRequest( new Date(), null, BigDecimal.valueOf(100), "Bill 1");

        // Act
        ResponseEntity<BillResponse> response = billController.update(id, billRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateBill_ValidIdAndInvalidRequest_ReturnsNotFound() {
        // Arrange
        Long id = 1L;
        BillRequest billRequest = new BillRequest( new Date(), null, BigDecimal.valueOf(100), "Bill 1");

        // Act
        ResponseEntity<BillResponse> response = billController.update(id, billRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetById_BillFound() {
        // Arrange
        Long billId = 1L;
        Bill bill = new Bill();
        bill.setId(billId);
        Optional<Bill> optionalBill = Optional.of(bill);
        when(billService.get(billId)).thenReturn(optionalBill);

        BillController controller = new BillController(billService);

        // Act
        ResponseEntity<BillResponse> response = controller.getById(billId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(billId, response.getBody().id());
    }

    @Test
    public void testGetById_BillNotFound() {
        // Arrange
        Long billId = 1L;
        Optional<Bill> optionalBill = Optional.empty();
        when(billService.get(billId)).thenReturn(optionalBill);

        BillController controller = new BillController(billService);

        // Act
        ResponseEntity<BillResponse> response = controller.getById(billId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateBill() {
        // Arrange
        BillRequest billRequest = new BillRequest( new Date(), null, BigDecimal.valueOf(100), "Bill 1");
        Bill bill = new Bill( 1L, new Date(), null, BigDecimal.valueOf(100), "Bill 1", BillStatus.OPEN);
        BillResponse billResponse = BillResponse.create(bill);
        when(billService.create(billRequest.toBill())).thenReturn(bill);

        // Act
        ResponseEntity<BillResponse> response = billController.createBill(billRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(billResponse, response.getBody());
    }

}