package com.app.api;

import com.app.entity.Booking;
import com.app.entity.BookingCancel;
import com.app.payload.request.BookingCancelQueryParam;
import com.app.payload.response.APIResponse;
import com.app.service.BookingCancelServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingCancelApi {
    @Autowired
    BookingCancelServices bookingCancelServices;
    @GetMapping("/user/booking-cancels")
    public ResponseEntity<?> filterBookingCancel(BookingCancelQueryParam bookingCancelQueryParam) {
        return ResponseEntity.ok(bookingCancelServices.filterBookingCancel(bookingCancelQueryParam));
    }

    @PostMapping("/user/booking-cancels/upload-excel")
    public ResponseEntity<?> uploadExcel(@RequestPart(name = "excel") MultipartFile excel) {
        APIResponse response = bookingCancelServices.uploadExcel(excel);
        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/user/booking-cancels/batch")
    public ResponseEntity<?> createBookingCancelsBatch(@RequestBody List<BookingCancel> bookingCancels) {
        APIResponse response = bookingCancelServices.createBatch(bookingCancels);
        return ResponseEntity.ok().body(response);
    }
}
