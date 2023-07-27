package com.campus.contact.controller;

import com.campus.common.util.FormTemplate;
import com.campus.common.util.R;
import com.campus.contact.domain.Comment;
import com.campus.contact.domain.Dynamic;
import com.campus.contact.dto.AddCommentForm;
import com.campus.contact.dto.AddDynamicForm;
import com.campus.contact.dto.DeleteCommentForm;
import com.campus.contact.service.impl.CommentServiceImpl;
import com.campus.contact.service.impl.DynamicServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("动态模块接口")
@RestController
@RequestMapping("/dynamic")
public class DynamicController {

    @Autowired
    DynamicServiceImpl dynamicService;

    @Autowired
    CommentServiceImpl commentService;


    /**
     * 发布动态
     *
     * @param form 动态实体
     * @return (dynamicId - > 该动态的id)
     */
    @PostMapping("/insertDynamic")
    @ApiOperation("发布动态")
    public R insertDynamic(@RequestBody AddDynamicForm form) {
        Dynamic dynamic = FormTemplate.analyzeTemplate(form,Dynamic.class);
        String dynamicId = dynamicService.insertDynamic(dynamic);
        if (dynamicId == null)
            return R.failed();
        return R.ok(dynamicId);
    }

    /**
     * 关键字搜索内容（动态内容）
     *
     * @param content 搜索关键字
     * @return (result - > 动态列表)
     */
    @GetMapping("/searchDynamic")
    @ApiOperation("模糊查询动态")
    public R searchDynamic(@RequestParam(value = "content",required = false) String content,@RequestHeader("uid") String uid) {
        if(content==null){
            content = "";
        }
        List<Dynamic> list = dynamicService.searchDynamic(content,uid);
        if (list == null)
            return R.failed();
        return R.ok(list);
    }

    /**
     * 查找同城动态
     *
     * @param city 当前用户城市
     * @return
     */
    @GetMapping("/searchCityWide")
    @ApiOperation("切换为同城")
    public R searchCityWide(@RequestParam("city") String city,@RequestHeader("uid") String uid) {
        List<Dynamic> list = dynamicService.searchCityWide(city,uid);
        if (list == null)
            return R.failed();
        return R.ok(list);
    }

    /**
     * 查看用户发布的动态
     *
     * @param uid uid
     */
    @GetMapping("/searchOnesDynamic")
    @ApiOperation("查看用户发布的动态")
    public R searchOnesDynamic(@RequestParam("uid") String uid) {
        List<Dynamic> list = dynamicService.searchOnesDynamic(uid);
        return R.ok(list);
    }


    /**
     * 逻辑删除动态
     *
     * @param dynamicId 动态id
     * @return 是否删除成功
     */
    @ApiOperation("逻辑删除动态")
    @PostMapping("/delete")
    public R deleteDynamicById(@RequestParam("dynamicId") String dynamicId) {
        long l = dynamicService.deleteDynamicById(dynamicId);
        if (l == 0)
            return R.failed("删除失败");
        return R.ok("删除成功");
    }


    /**
     * 点赞
     *
     * @param dynamicId 动态id
     * @param userId    userId
     * @return 是否点赞成功
     */
    @ApiOperation("点赞动态")
    @GetMapping("/insertLike")
    public R insertLike(@Param("dynamicId") String dynamicId, @RequestHeader("uid") String userId, @Param("username") String username) {
        long l = dynamicService.insertLike(dynamicId, userId, username);
        if (l == 0L)
            return R.failed();
        return R.ok();
    }

    /**
     * 取消点赞
     *
     * @param dynamicId 动态id
     * @param userId    userId
     * @return 是否删除成功
     */
    @ApiOperation("取消点赞")
    @GetMapping("/deleteLike")
    public R deleteLike(@Param("dynamicId") String dynamicId, @Param("username") String username, @RequestHeader("uid") String userId) {
        long l = dynamicService.deleteLike(dynamicId, String.valueOf(userId), username);
        if (l == 0L)
            return R.failed();
        return R.ok();
    }

//    /**
//     * 在动态下发布直接评论
//     *
//     * @param dynamicId 动态id
//     * @param comment   要添加的评论
//     * @return (result - > 新添加的动态id)
//     */
//    @ApiOperation("在动态下发布直接评论")
//    @PostMapping("/addComment/{dynamicId}")
//    public R addComment(@PathVariable String dynamicId, @RequestBody Comment comment) {
//        comment.setParent(dynamicId);
//        String commentId = dynamicService.insertComment(dynamicId, comment);
//        return R.ok(commentId);
//    }
//
//    /**
//     * 删除直接下级评论
//     *
//     * @param dynamicId 动态id
//     * @param commentId 一级评论id
//     * @return 是否删除成功
//     */
//    @ApiOperation("删除直接下级评论")
//    @GetMapping("/removeComment")
//    public R deleteComment(@RequestParam("dynamicId") String dynamicId, @Param("commentId") String commentId) {
//        long l = dynamicService.deleteComment(dynamicId, commentId);
//        if (l == 0L)
//            return R.failed("删除失败");
//        return R.ok("删除成功");
//    }
//
//
//    /**
//     * 添加评论下的评论
//     *
//     * @param superId 该评论回复的评论
//     * @param comment 要添加的评论
//     * @return (result - > 添加评论后 ， 该动态下的所有评论区)
//     */
//    @PostMapping("/insertComment")
//    @ApiOperation("添加评论下的评论")
//    public R insertComment(@Param("superId") String superId, @RequestBody Comment comment) {
//        List<Comment> list = commentService.insertComment(superId, comment);
//        if (list == null)
//            return R.failed();
//        return R.ok(list);
//    }
//
//    /**
//     * 删除评论下的评论
//     *
//     * @param dynamicId 要删除的评论id
//     * @return (result - > 删除后 ， 该动态下的所有评论区)
//     */
//    @PostMapping("/deleteComment")
//    @ApiOperation("删除评论下的评论")
//    public R deleteComment(@Param("dynamicId") String dynamicId) {
//        List<Comment> list = commentService.deleteComment(dynamicId);
//        if (list == null)
//            return R.failed();
//        return R.ok(list);
//    }

    /**
     * 在动态下发布评论
     * */
    @ApiOperation("在动态下发布直接评论")
    @PostMapping("/addComment")
    public R addComment(@RequestBody AddCommentForm form){
        String dynamicId = form.getDynamicId();
        Comment comment = FormTemplate.analyzeTemplate(form, Comment.class);
        assert comment != null;
        Comment com = dynamicService.insertComment(dynamicId, comment);
        if(com!=null){
            return R.ok(com);
        }
        return R.failed();
    }

    /**
     * 删除动态下的评论
     * */
    @ApiOperation("删除动态下的评论")
    @PostMapping("/deleteComment")
    public R deleteComment(@RequestBody DeleteCommentForm form){
        String dynamicId = form.getDynamicId();
        String commentId = form.getCommentId();
        if(dynamicService.deleteComment(dynamicId,commentId)){
            return R.ok();
        }
        return R.failed();
    }

}
