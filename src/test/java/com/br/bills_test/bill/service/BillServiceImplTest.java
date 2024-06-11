package com.br.bills_test.bill.service;

import com.br.bills_test.bill.BillStatus;
import com.br.bills_test.bill.entity.Bill;
import com.br.bills_test.bill.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {

    @Mock
    BillRepository repository;

    BillService service;

    @BeforeEach
    void setUp() {
        service = new BillServiceImpl(repository);
    }

    @Test
    void test_pay() {
        // Arrange
        Bill bill = new Bill();
        bill.setId(1L);
        bill.setStatus(BillStatus.OPEN);
        when(repository.findById(bill.getId())).thenReturn(Optional.of(bill));
        when(repository.save(bill)).thenReturn(bill);

        // Act
        Optional<Bill> result = service.pay(bill.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(BillStatus.PAID, result.get().getStatus());
        verify(repository, times(1)).save(bill);
    }

    @Test
    void test_pay_notFound() {
        // Arrange
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<Bill> result = service.pay(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void test_create() {
        // Arrange
        Bill bill = new Bill();
        when(repository.save(bill)).thenReturn(bill);

        // Act
        Bill result = service.create(bill);

        // Assert
        assertNotNull(result);
        assertEquals(bill, result);
        verify(repository, times(1)).save(bill);
    }

    @Test
    void test_update() {
        // Arrange
        Long id = 1L;
        Bill bill = new Bill();
        bill.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(bill));
        when(repository.save(bill)).thenReturn(bill);

        // Act
        Optional<Bill> result = service.update(id, bill);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(bill, result.get());
        verify(repository, times(1)).save(bill);
    }

    @Test
    void test_update_notFound() {
        // Arrange
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Bill> result = service.update(id, new Bill());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetTotalPaid_WithPaidBills_ReturnsTotalAmount() {
        // Arrange
        Date startDate = new Date();
        Date endDate = new Date();
        List<Bill> paidBills = List.of(
                new Bill(BillStatus.PAID, BigDecimal.valueOf(10)),
                new Bill(BillStatus.PAID, BigDecimal.valueOf(20)),
                new Bill(BillStatus.PAID, BigDecimal.valueOf(30))
        );
        when(repository.findByStatusAndPaymentDateBetween(BillStatus.PAID, startDate, endDate)).thenReturn(paidBills);

        // Act
        BigDecimal totalAmount = service.getTotalPaid(startDate, endDate);

        // Assert
        assertEquals(BigDecimal.valueOf(60), totalAmount);
    }

    @Test
    public void testGetTotalPaid_WithNoPaidBills_ReturnsZero() {
        // Arrange
        Date startDate = new Date();
        Date endDate = new Date();
        when(repository.findByStatusAndPaymentDateBetween(BillStatus.PAID, startDate, endDate)).thenReturn(List.of());

        // Act
        BigDecimal totalAmount = service.getTotalPaid(startDate, endDate);

        // Assert
        assertEquals(BigDecimal.ZERO, totalAmount);
    }

}