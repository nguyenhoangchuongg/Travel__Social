package com.app.service;

import com.app.payload.response.APIResponse;
import com.app.security.UserPrincipal;

public interface BlogInteractionService {
    APIResponse addComment (Integer blogId, String content);
    APIResponse replyComment(Integer parentCommentId, String content);
    APIResponse deleteComment (Integer blogCommentId);
    APIResponse updateComment (Integer blogCommentId, String content);
    APIResponse likeBlog (Integer blogId, UserPrincipal userPrincipal);
    APIResponse unlikeBlog (Integer blogId, UserPrincipal userPrincipal);
    APIResponse getListLikeYourBlog(UserPrincipal userPrincipal);
    APIResponse getListCommentYourBlog(UserPrincipal userPrincipal);
}
