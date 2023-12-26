package com.app.service.serviceImpl;

import com.app.entity.Restaurant;
import com.app.payload.request.RestaurantQueryParam;
import com.app.payload.response.APIResponse;
import com.app.payload.response.CloudinaryResponse;
import com.app.payload.response.FailureAPIResponse;
import com.app.payload.response.SuccessAPIResponse;
import com.app.repository.RestaurantRepository;
import com.app.service.RestaurantServices;
import com.app.utils.PageUtils;
import com.app.utils.RequestParamsUtils;
import com.app.speficication.RestaurantSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RestaurantServicesImpl implements RestaurantServices {
    @Autowired
    RestaurantRepository restaurantRepository;
    @Autowired
    RestaurantSpecification restaurantSpecification;
    @Autowired
    RequestParamsUtils requestParamsUtils;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    ImportExcelService importExcelService;

    @Override
    public APIResponse filterRestaurant(RestaurantQueryParam restaurantQueryParam) {
        try {
        Specification<Restaurant> spec = restaurantSpecification.getRestaurantSpecification(restaurantQueryParam);
        Pageable pageable = requestParamsUtils.getPageable(restaurantQueryParam);
        Page<Restaurant> response = restaurantRepository.findAll(spec, pageable);
            if (response.isEmpty()) {
                return new APIResponse(false, "No data found");
            } else {
                return new APIResponse(PageUtils.toPageResponse(response));
            }
        } catch (Exception ex) {
            return new FailureAPIResponse(ex.getMessage());
        }
    }

    @Override
    public APIResponse create(Restaurant restaurant) {
        try {
            if (restaurant.getName() == null || restaurant.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Restaurant name cannot be empty");
            }
            if (restaurant.getAddress() == null || restaurant.getAddress().trim().isEmpty()) {
                throw new IllegalArgumentException("Restaurant name cannot be empty");
            }
            restaurant = restaurantRepository.save(restaurant);
            return new SuccessAPIResponse(restaurant);
        } catch (Exception ex) {
            return new FailureAPIResponse(ex.getMessage());
        }
    }

    @Override
    public APIResponse update(Restaurant restaurant) {
        try {
            if (restaurant == null) {
                return new FailureAPIResponse("Restaurant id is required!");
            }
            if (restaurant.getName() == null || restaurant.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Restaurant name cannot be empty");
            }
            if (restaurant.getAddress() == null || restaurant.getAddress().trim().isEmpty()) {
                throw new IllegalArgumentException("Restaurant name cannot be empty");
            }

            Restaurant exists = restaurantRepository.findById(restaurant.getId()).orElse(null);
            if (exists == null) {
                return new FailureAPIResponse("Cannot find restaurant with id: " + restaurant.getId());
            }

            restaurant = restaurantRepository.save(restaurant);
            return new SuccessAPIResponse(restaurant);
        } catch (Exception ex) {
            return new FailureAPIResponse(ex.getMessage());
        }
    }

    @Override
    public APIResponse delete(Integer id) {
        try {
            restaurantRepository.deleteById(id);
            return new SuccessAPIResponse("Delete successfully!");
        } catch (Exception ex) {
            return new FailureAPIResponse(ex.getMessage());
        }
    }

    @Override
    public APIResponse uploadExcel(MultipartFile excel) {
        return importExcelService.uploadExcel(excel, Restaurant.class, restaurantRepository);
    }
}
