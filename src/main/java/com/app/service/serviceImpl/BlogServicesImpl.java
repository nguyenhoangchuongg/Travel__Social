package com.app.service.serviceImpl;

import com.app.dto.AccountCommentDto;
import com.app.dto.BlogDto;
import com.app.entity.Account;
import com.app.entity.Blog;
import com.app.entity.BlogComment;
import com.app.entity.BlogReaction;
import com.app.mapper.AccountMapper;
import com.app.mapper.BlogMapper;
import com.app.modal.BlogModal;
import com.app.modal.CommentModel;
import com.app.payload.request.BaseQueryRequest;
import com.app.payload.request.BlogModalQueryParam;
import com.app.payload.request.BlogQueryParam;
import com.app.payload.response.APIResponse;
import com.app.payload.response.FailureAPIResponse;
import com.app.payload.response.SuccessAPIResponse;
import com.app.repository.*;
import com.app.security.TokenProvider;
import com.app.service.BlogServices;
import com.app.speficication.BlogModalSpecification;
import com.app.speficication.BlogSpecification;
import com.app.utils.PageUtils;
import com.app.utils.RequestParamsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogServicesImpl implements BlogServices {
    @Autowired
    BlogRepository blogRepository;
    @Autowired
    BlogInteractionResponsitory blogInteractionResponsitory;
    @Autowired
    BlogSpecification blogSpecification;
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    BlogModalSpecification blogModalSpecification;
    @Autowired
    BlogReactionRepository blogReactionRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    RequestParamsUtils requestParamsUtils;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    ImportExcelService importExcelService;
    @Autowired
    BlogCommentRepository blogCommentRepository;
    @Autowired
    BlogLikeRepository blogLikeRepository;
    @Autowired
    BlogMapper blogMapper;

    @Autowired
    AccountMapper accountMapper;

    @Override
    public List<Blog> findAll() {
        return blogRepository.findAll();
    }

    @Override
    public Optional<Blog> findById(Integer id) {
        return blogRepository.findById(id);
    }


    @Override
    public APIResponse filterBlog(BlogQueryParam blogQueryParam) {
        try {

            Specification<Blog> spec = blogSpecification.getBlogSpecification(blogQueryParam);
            Pageable pageable = requestParamsUtils.getPageable(blogQueryParam);
            Page<Blog> response = blogRepository.findAll(spec, pageable);
            if (response.isEmpty()) {
                return new APIResponse(false, "No data found");
            } else {
                List<BlogDto> blogDtoList = response.getContent().stream()
                        .map(blog -> {
                            BlogDto blogDto = blogMapper.blogDto(blog);
                            blogDto.setTotalLike(blogLikeRepository.countByBlog(blog));
                            blogDto.setTotalComment(blogCommentRepository.countByBlog(blog));
                            return blogDto;
                        })
                        .collect(Collectors.toList());

                Page<BlogDto> blogDtoPage = new PageImpl<>(blogDtoList, pageable, response.getTotalElements());

                return new APIResponse(PageUtils.toPageResponse(blogDtoPage));
            }
        } catch (Exception ex) {
            return new FailureAPIResponse(ex.getMessage());
        }
    }

    @Override
    public APIResponse getAllBlogWithAccount(BlogModalQueryParam blogModalQueryParam) {
        try {
            Specification<BlogModal> spec = blogModalSpecification.getBlogModalSpecification(blogModalQueryParam);
            Pageable pageable = requestParamsUtils.getPageable(blogModalQueryParam);
            Page<BlogModal> response = blogRepository.getAllBlogWithAccount(spec, pageable);
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
    public APIResponse getComment(Integer blogId , BaseQueryRequest baseQueryRequest) {
        Pageable pageable = requestParamsUtils.getPageable(baseQueryRequest);
        Optional<Blog> existBlog = blogRepository.findById(blogId);
        if (existBlog.isEmpty()) {
            return new FailureAPIResponse("This blogId does not exist");
        }
        try {
            Page<BlogComment> blogCommentList = blogCommentRepository.findByBlog(existBlog.get(), pageable);
            List<AccountCommentDto> commentDtoList = blogCommentList.stream()
                    .map(blogComment -> accountMapper.mapToAccountCommentDto(blogComment))
                    .collect(Collectors.toList());

            Page<AccountCommentDto> commentDtoPage = new PageImpl<>(commentDtoList, pageable, blogCommentList.getTotalElements());
            return new SuccessAPIResponse(PageUtils.toPageResponse(commentDtoPage));
        } catch (Exception e) {
            return new FailureAPIResponse("Error: " + e.getMessage());
        }
    }

    @Override
    public APIResponse getBlogAccount(Integer id, BlogModalQueryParam blogModalQueryParam) {
        try {
            Pageable pageable = requestParamsUtils.getPageable(blogModalQueryParam);
            Page<BlogModal> response = blogRepository.getBlogAccount(id, pageable);

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
    public APIResponse getBlogComment(Integer id, BlogModalQueryParam blogModalQueryParam) {
        try {
            Pageable pageable = requestParamsUtils.getPageable(blogModalQueryParam);
            Page<CommentModel> response = blogRepository.getBlogComment(id, pageable);

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
    public APIResponse filterBlogNotSeen(BlogQueryParam blogQueryParam) {
        try {
            Specification<Blog> spec = blogSpecification.getBlogSpecification(blogQueryParam);
            Pageable pageable = requestParamsUtils.getPageable(blogQueryParam);
            Page<Blog> response = blogRepository.findAllNotSeen(spec, pageable);
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
    public APIResponse filterLeastBlog(BlogQueryParam blogQueryParam) {
        try {
            Specification<Blog> spec = blogSpecification.getBlogSpecification(blogQueryParam);
            Pageable pageable = requestParamsUtils.getPageable(blogQueryParam);
            Page<Blog> response = blogRepository.findLatestBlogs(spec, pageable);
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
    public APIResponse create(Blog blog, HttpServletRequest request) {
        String token = getTokenFromHeader(request);
        int id = tokenProvider.getIdFromToken(token);
        Account account = accountRepository.findById(id).orElse(null);
        try {
            blog = blogRepository.save(blog);
            return new SuccessAPIResponse(blog);
        } catch (Exception ex) {
            return new FailureAPIResponse(ex.getMessage());
        }
    }

    @Override
    public APIResponse update(Blog blog, HttpServletRequest request) {
        try {

            if (blog == null) {
                return new FailureAPIResponse("Blog id is required!");
            }
            Blog exists = blogRepository.findById(blog.getId()).orElse(null);
            if (exists == null) {
                return new FailureAPIResponse("Cannot find blog with id: " + blog.getId());
            }

            blog = blogRepository.save(blog);
            return new SuccessAPIResponse(blog);
        } catch (Exception ex) {
            return new FailureAPIResponse(ex.getMessage());
        }
    }

    @Override
    public APIResponse delete(Integer id) {
        try {
            blogRepository.deleteById(id);
            return new SuccessAPIResponse("Delete successfully!");
        } catch (Exception ex) {
            return new FailureAPIResponse(ex.getMessage());
        }
    }

    @Override
    public List<Blog> findByTitle(BlogQueryParam blogQueryParam) {
        return null;
    }

    @Override
    public APIResponse uploadExcel(MultipartFile excel) {
        return importExcelService.uploadExcel(excel, Blog.class, blogRepository);
    }

    @Override
    public APIResponse createBatch(List<Blog> blogs) {
        List<Blog> createdBlogs = new ArrayList<>();
        for (Blog blog : blogs) {
            Blog createdBlog = blogRepository.save(blog);
            createdBlogs.add(createdBlog);
        }
        return new SuccessAPIResponse(createdBlogs);
    }

    @Override
    public APIResponse getAccountByBlogId(Integer id, BlogQueryParam blogQueryParam) {
        Blog blog = new Blog();
        Pageable pageable = requestParamsUtils.getPageable(blogQueryParam);
        Page<Blog> response = blogRepository.BlogID(id, pageable);
        Blog blogg = blogRepository.findById(id).orElse(null);
        BlogReaction blogReaction = new BlogReaction();
        blogReaction.setBlog(blog);
        blogReaction.setShare(true);
        if (response.hasContent()) {
            blog.setDescription(response.getContent().get(0).getDescription());
            blog.setPlaceId(response.getContent().get(0).getPlaceId());
            blog.setTour(response.getContent().get(0).getTour());
            blog.setImage(response.getContent().get(0).getImage());
            blog.setCloudinaryId(response.getContent().get(0).getCloudinaryId());
            blog.setCreatedAt(response.getContent().get(0).getCreatedAt());
            blog.setCreatedBy(response.getContent().get(0).getCreatedBy());
        }
        blog = blogRepository.save(blog);
        BlogReaction savedReaction = blogReactionRepository.save(blogReaction);
        return new SuccessAPIResponse(blog);
    }

    public String getTokenFromHeader(HttpServletRequest request) {
        // Lấy header authorization
        String authorization = request.getHeader("Authorization");

        // Kiểm tra header authorization tồn tại và có dạng Bearer <token>
        if (authorization != null && authorization.startsWith("Bearer ")) {
            // Lấy token từ header
            String token = authorization.substring(7);
            // Trả về token
            return token;
        } else {
            // Không có token
            return null;
        }
    }
}
