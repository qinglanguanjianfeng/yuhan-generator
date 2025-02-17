package com.yuhan.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhan.web.common.ErrorCode;
import com.yuhan.web.constant.CommonConstant;
import com.yuhan.web.exception.BusinessException;
import com.yuhan.web.exception.ThrowUtils;
import com.yuhan.web.mapper.GeneratorMapper;
import com.yuhan.web.model.dto.generator.GeneratorQueryRequest;
import com.yuhan.web.model.entity.Generator;
import com.yuhan.web.model.entity.User;
import com.yuhan.web.model.vo.GeneratorVO;
import com.yuhan.web.model.vo.UserVO;
import com.yuhan.web.service.GeneratorService;
import com.yuhan.web.service.UserService;
import com.yuhan.web.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Service
@Slf4j
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator> implements GeneratorService {

    @Resource
    private UserService userService;


    /**
     * 校验
     * @param generator
     * @param add
     */
    @Override
    public void validGenerator(Generator generator, boolean add) {
        if (generator == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = generator.getName();
        String description = generator.getDescription();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name,description), ErrorCode.PARAMS_ERROR);
        }
        // 有参数,不是创建,则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 256) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }

    /**
     * 获取查询包装类,用于动态生成SQL查询条件
     * @param generatorQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest) {

        QueryWrapper<Generator> queryWrapper = new QueryWrapper<>();// MyBatis-Plus 提供的一个工具类，用于动态构建 SQL 查询条件
        if (generatorQueryRequest == null) {
            return queryWrapper;
        }
        //提取查询条件字段
        Long id = generatorQueryRequest.getId();
        Long notId = generatorQueryRequest.getNotId();
        String searchText = generatorQueryRequest.getSearchText();
        List<String> tags = generatorQueryRequest.getTags();
        Long userId = generatorQueryRequest.getUserId();
        String name = generatorQueryRequest.getName();
        String description = generatorQueryRequest.getDescription();
        String basePackage = generatorQueryRequest.getBasePackage();
        String version = generatorQueryRequest.getVersion();
        String author = generatorQueryRequest.getAuthor();
        String distPath = generatorQueryRequest.getDistPath();
        Integer status = generatorQueryRequest.getStatus();
        String sortField = generatorQueryRequest.getSortField();
        String sortOrder = generatorQueryRequest.getSortOrder();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            //对 title 和 content 字段进行模糊搜索（LIKE）
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        //如果 name 或 description 不为空，分别对这些字段进行模糊搜索
        //like(boolean,字段名,匹配值)
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        //如果 tags 不为空，对每个标签进行模糊匹配
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");//通过添加引号 " 匹配具体的标签值
            }
        }
        //使用 ne（Not Equal）和 eq（Equal）方法,根据条件字段是否为空，动态添加不等于或等于的条件。
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);//排除特定 ID
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);//精确匹配 ID
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(StringUtils.isNotEmpty(basePackage), "basePackage", basePackage);
        queryWrapper.eq(StringUtils.isNotEmpty(version), "version", version);
        queryWrapper.eq(StringUtils.isNotEmpty(author), "author", author);
        queryWrapper.eq(StringUtils.isNotEmpty(distPath), "distPath", distPath);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        //如果 sortField 是有效的排序字段，根据 sortOrder 的值（升序或降序）添加排序条件
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),//SqlUtils.validSortField用于验证字段是否允许排序
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     *
     * @param generator
     * @param request
     * @return
     */
    @Override
    public GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request) {
        GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
        long generatorId = generator.getId();
        // 1. 关联查询用户信息
        Long userId = generator.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        generatorVO.setUser(userVO);
        return generatorVO;
    }

    /**
     *
     * @param generatorPage
     * @param request
     * @return
     */
    @Override
    public Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request) {
        List<Generator> generatorList = generatorPage.getRecords();
        Page<GeneratorVO> generatorVOPage = new Page<>(generatorPage.getCurrent(), generatorPage.getSize(), generatorPage.getTotal());
        if (CollUtil.isEmpty(generatorList)) {
            return generatorVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = generatorList.stream().map(Generator::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<GeneratorVO> generatorVOList = generatorList.stream().map(generator -> {
            GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
            Long userId = generator.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            generatorVO.setUser(userService.getUserVO(user));
            return generatorVO;
        }).collect(Collectors.toList());
        generatorVOPage.setRecords(generatorVOList);
        return generatorVOPage;
    }

}




