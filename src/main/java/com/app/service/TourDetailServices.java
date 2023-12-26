package com.app.service;

import com.app.entity.Follow;
import com.app.entity.TourDetail;
import com.app.payload.request.TourDetailQueryParam;
import com.app.payload.response.APIResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TourDetailServices {
    APIResponse filterTourDetail(TourDetailQueryParam tourDetailQueryParam);
    APIResponse create(TourDetail tourDetail);
    APIResponse update(TourDetail tourDetail);
    APIResponse delete(Integer id);
    APIResponse uploadExcel(MultipartFile excel);
    APIResponse createBatch(List<TourDetail> tourDetails);
}
