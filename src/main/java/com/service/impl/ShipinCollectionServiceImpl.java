package com.service.impl;

import com.utils.StringUtil;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.util.*;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;

import com.dao.ShipinCollectionDao;
import com.entity.ShipinCollectionEntity;
import com.service.ShipinCollectionService;
import com.entity.view.ShipinCollectionView;

/**
 * 学习视频收藏 服务实现类
 * @author 
 * @since 2021-05-03
 */
@Service("shipinCollectionService")
@Transactional
public class ShipinCollectionServiceImpl extends ServiceImpl<ShipinCollectionDao, ShipinCollectionEntity> implements ShipinCollectionService {

    @Override
    public PageUtils queryPage(Map<String,Object> params) {
        if(params != null && (params.get("limit") == null || params.get("page") == null)){
            params.put("page","1");
            params.put("limit","10");
        }
        Page<ShipinCollectionView> page =new Query<ShipinCollectionView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page,params));
        return new PageUtils(page);
    }


}
