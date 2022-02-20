package com.itmo.microservices.shop.catalog.api.controller;

import com.itmo.microservices.shop.catalog.api.exceptions.BookingNotFoundException;
import com.itmo.microservices.shop.catalog.api.model.BookingLogRecordDTO;
import com.itmo.microservices.shop.catalog.impl.service.ItemServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/_internal")
@AllArgsConstructor
public class BookingInternalController {

    private ItemServiceImpl itemService;

    @GetMapping("/bookingHistory/{bookingId}")
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<List<BookingLogRecordDTO>> getCountOfItem(@PathVariable UUID bookingId)
            throws BookingNotFoundException {
        // TODO implement exception throw on non-existing booking id request: @Joiner-dot
        return ResponseEntity.ok(itemService.listBookingLogRecords(bookingId));
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public void handleItemNotFoundException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}

