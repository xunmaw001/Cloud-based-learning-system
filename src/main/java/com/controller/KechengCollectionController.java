package com.controller;


import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.StringUtil;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;

import com.entity.KechengCollectionEntity;

import com.service.KechengCollectionService;
import com.entity.view.KechengCollectionView;
import com.service.KechengService;
import com.entity.KechengEntity;
import com.service.YonghuService;
import com.entity.YonghuEntity;

import com.utils.PageUtils;
import com.utils.R;

/**
 * 课程信息收藏
 * 后端接口
 * @author
 * @email
 * @date 2021-05-03
*/
@RestController
@Controller
@RequestMapping("/kechengCollection")
public class KechengCollectionController {
    private static final Logger logger = LoggerFactory.getLogger(KechengCollectionController.class);

    @Autowired
    private KechengCollectionService kechengCollectionService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;



    //级联表service
    @Autowired
    private KechengService kechengService;
    @Autowired
    private YonghuService yonghuService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
     
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isNotEmpty(role) && "用户".equals(role)){
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        }
        params.put("orderBy","id");
        PageUtils page = kechengCollectionService.queryPage(params);

        //字典表数据转换
        List<KechengCollectionView> list =(List<KechengCollectionView>)page.getList();
        for(KechengCollectionView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        KechengCollectionEntity kechengCollection = kechengCollectionService.selectById(id);
        if(kechengCollection !=null){
            //entity转view
            KechengCollectionView view = new KechengCollectionView();
            BeanUtils.copyProperties( kechengCollection , view );//把实体数据重构到view中

            //级联表
            KechengEntity kecheng = kechengService.selectById(kechengCollection.getKechengId());
            if(kecheng != null){
                BeanUtils.copyProperties( kecheng , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                view.setKechengId(kecheng.getId());
            }
            //级联表
            YonghuEntity yonghu = yonghuService.selectById(kechengCollection.getYonghuId());
            if(yonghu != null){
                BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                view.setYonghuId(yonghu.getId());
            }
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody KechengCollectionEntity kechengCollection, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,kechengCollection:{}",this.getClass().getName(),kechengCollection.toString());
        Wrapper<KechengCollectionEntity> queryWrapper = new EntityWrapper<KechengCollectionEntity>()
            .eq("kecheng_id", kechengCollection.getKechengId())
            .eq("yonghu_id", kechengCollection.getYonghuId())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        KechengCollectionEntity kechengCollectionEntity = kechengCollectionService.selectOne(queryWrapper);
        if(kechengCollectionEntity==null){
            kechengCollection.setInsertTime(new Date());
            kechengCollection.setCreateTime(new Date());
        //  String role = String.valueOf(request.getSession().getAttribute("role"));
        //  if("".equals(role)){
        //      kechengCollection.set
        //  }
            kechengCollectionService.insert(kechengCollection);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody KechengCollectionEntity kechengCollection, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,kechengCollection:{}",this.getClass().getName(),kechengCollection.toString());
        //根据字段查询是否有相同数据
        Wrapper<KechengCollectionEntity> queryWrapper = new EntityWrapper<KechengCollectionEntity>()
            .notIn("id",kechengCollection.getId())
            .andNew()
            .eq("kecheng_id", kechengCollection.getKechengId())
            .eq("yonghu_id", kechengCollection.getYonghuId())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        KechengCollectionEntity kechengCollectionEntity = kechengCollectionService.selectOne(queryWrapper);
        if(kechengCollectionEntity==null){
            //  String role = String.valueOf(request.getSession().getAttribute("role"));
            //  if("".equals(role)){
            //      kechengCollection.set
            //  }
            kechengCollectionService.updateById(kechengCollection);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        kechengCollectionService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }



    /**
    * 前端列表
    */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isNotEmpty(role) && "用户".equals(role)){
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        }
        // 没有指定排序字段就默认id倒序
        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = kechengCollectionService.queryPage(params);

        //字典表数据转换
        List<KechengCollectionView> list =(List<KechengCollectionView>)page.getList();
        for(KechengCollectionView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c);
        }
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        KechengCollectionEntity kechengCollection = kechengCollectionService.selectById(id);
            if(kechengCollection !=null){
                //entity转view
        KechengCollectionView view = new KechengCollectionView();
                BeanUtils.copyProperties( kechengCollection , view );//把实体数据重构到view中

                //级联表
                    KechengEntity kecheng = kechengService.selectById(kechengCollection.getKechengId());
                if(kecheng != null){
                    BeanUtils.copyProperties( kecheng , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setKechengId(kecheng.getId());
                }
                //级联表
                    YonghuEntity yonghu = yonghuService.selectById(kechengCollection.getYonghuId());
                if(yonghu != null){
                    BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYonghuId(yonghu.getId());
                }
                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody KechengCollectionEntity kechengCollection, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,kechengCollection:{}",this.getClass().getName(),kechengCollection.toString());
        Wrapper<KechengCollectionEntity> queryWrapper = new EntityWrapper<KechengCollectionEntity>()
            .eq("kecheng_id", kechengCollection.getKechengId())
            .eq("yonghu_id", kechengCollection.getYonghuId())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
    KechengCollectionEntity kechengCollectionEntity = kechengCollectionService.selectOne(queryWrapper);
        if(kechengCollectionEntity==null){
            kechengCollection.setInsertTime(new Date());
            kechengCollection.setCreateTime(new Date());
        //  String role = String.valueOf(request.getSession().getAttribute("role"));
        //  if("".equals(role)){
        //      kechengCollection.set
        //  }
        kechengCollectionService.insert(kechengCollection);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }





}

