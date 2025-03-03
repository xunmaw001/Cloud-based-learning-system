package com.dao;

import com.entity.KechengCollectionEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.view.KechengCollectionView;

/**
 * 课程信息收藏 Dao 接口
 *
 * @author 
 * @since 2021-05-03
 */
public interface KechengCollectionDao extends BaseMapper<KechengCollectionEntity> {

   List<KechengCollectionView> selectListView(Pagination page,@Param("params")Map<String,Object> params);

}
