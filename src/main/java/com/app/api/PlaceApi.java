package com.app.api;

import com.app.entity.Place;
import com.app.payload.request.PlaceQueryParam;
import com.app.payload.response.APIResponse;
import com.app.service.PlaceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class PlaceApi {
    @Autowired
    PlaceServices placeServices;

    @GetMapping("/public/places")
    public ResponseEntity<?> filterPlace(PlaceQueryParam placeQueryParam) {
        return ResponseEntity.ok(placeServices.filterPlace(placeQueryParam));
    }

    @PostMapping("/company/places")
    public ResponseEntity<?> createPlace(@RequestPart(name = "place") Place place) {
        APIResponse response = placeServices.create(place);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/company/places")
    public ResponseEntity<?> updatePlace(@RequestPart(name = "place") Place place) {
        APIResponse response = placeServices.update(place);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/company/places")
    public ResponseEntity<?> deletePlace(@RequestParam("id") Integer id) {
        APIResponse response = placeServices.delete(id);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/company/places/upload-excel")
    public ResponseEntity<?> uploadExcel(@RequestPart(name = "excel") MultipartFile excel) {
        APIResponse response = placeServices.uploadExcel(excel);
        return ResponseEntity.ok().body(response);
    }
}

