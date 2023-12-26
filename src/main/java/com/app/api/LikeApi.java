package com.app.api;

import com.app.entity.Hotel;
import com.app.entity.Like;
import com.app.payload.request.LikeQueryParam;
import com.app.payload.response.APIResponse;
import com.app.security.CurrentUser;
import com.app.security.UserPrincipal;
import com.app.service.LikeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LikeApi {
    @Autowired
    LikeServices likeServices;

    @GetMapping("/user/likes")
    public ResponseEntity<?> filterLike(LikeQueryParam likeQueryParam) {
        return ResponseEntity.ok(likeServices.filterLike(likeQueryParam));
    }

    @PutMapping("/user/likes")
    public ResponseEntity<?> updateLike(@RequestPart(name = "like") Like like) {
        APIResponse response = likeServices.update(like);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/user/likes/upload-excel")
    public ResponseEntity<?> uploadExcel(@RequestPart(name = "excel") MultipartFile excel) {
        APIResponse response = likeServices.uploadExcel(excel);
        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/user/likes/batch")
    public ResponseEntity<?> createLikesBatch(@RequestBody List<Like> likes) {
        APIResponse response = likeServices.createBatch(likes);
        return ResponseEntity.ok().body(response);
    }


    //============

    @PostMapping("/user/likes")
    public ResponseEntity<?> createLike(@RequestParam("userId") Integer userId, @CurrentUser UserPrincipal userPrincipal) {
        APIResponse response = likeServices.create(userId, userPrincipal);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/user/likes")
    public ResponseEntity<?> deleteLike(@RequestParam("userId") Integer userId, @CurrentUser UserPrincipal userPrincipal) {
        APIResponse response = likeServices.delete(userId, userPrincipal);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/follows/list-you-like")
    public ResponseEntity<?> getListYouFollow(@CurrentUser UserPrincipal userPrincipal) {
        return ResponseEntity.ok(likeServices.getListYouLike(userPrincipal));
    }

    @GetMapping("/user/follows/list-like-you")
    public ResponseEntity<?> getListFollowYou(@CurrentUser UserPrincipal userPrincipal) {
        return ResponseEntity.ok(likeServices.getListLikeYou(userPrincipal));
    }
}
