package controllers.notice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import models.AndroidNoticeInfo;
import models.ResultInfo;
import play.Logger;
import play.mvc.Controller;
import service.notice.CustomDocinfoService;



/**
 * 后台自定义资讯添加接口
 * @author 
 *
 */
public class CustomDocinfo extends Controller{
    
    /**
     * 添加一个文章
     * CustomDocinfo.java
     * @param androidNoticeInfo
     * @param receive_user_ids 接收用户列表，主要针对消息发送
     * @param receive_type 接受用户类型，主要针对科长发布公告，0：本科室；1：全部；2：指定人（具体接收人在receive_user_ids中指明）
     * 2016年1月21日
     */
    public static void addNoticeInfo(AndroidNoticeInfo androidNoticeInfo, String receive_user_ids, Integer receive_type) {
	if (receive_type == null) {
	    receive_type = 0;
	}
	if (receive_user_ids == null) {
	    receive_user_ids = "";
	}
	ResultInfo info = CustomDocinfoService.addNoticeInfo(androidNoticeInfo, receive_user_ids,receive_type,request);

	renderJSON(info);

    }
    
    /**
     * 修改文章信息 修改方式为新建一条记录，将修改的数据存储到新纪录中，然后删除历史记录,
     * 同时删除push表的记录,使得修改记录在推荐列表中可以显示在最新位置
     * 
     * @param App
     *            文章信息 <必填>
     */
    public static void editNoticeInfo(AndroidNoticeInfo androidNoticeInfo) {
	ResultInfo info = CustomDocinfoService.editNoticeInfo(androidNoticeInfo,  request);

	renderJSON(info);

    }
    
    /**
     * 查询用户公告/消息详情,
     * 公告:对于发布人查看公告消息，同步返回已读和未读用户列表；
     * 		对于接收人查看公告消息，不返回已读和未读用户列表；
     * 消息：不需要显示已读回执

     * @param id
     * @param user_id 阅读人
     */
    public static void getNoticeInfo(Integer id, Integer user_id) {
	ResultInfo info = CustomDocinfoService.getNoticeInfo(id,user_id,request);
	renderJSON(info);
    }





    /**
     * 根据id删除文章
     * 
     * @param id
     *            文章id <必填>
     */
    public static void deleteNoticeInfo(Long id) {
	ResultInfo info = CustomDocinfoService.deleteNoticeInfo(id, request);
	
	renderJSON(info);

    }
    
    /**
     * 查询用户消息列表接口
     * 消息列表：同步返回每个用户的已读回执
     * 公告列表：只需要返回公告标题和内容，不需要返回已读回执，已读回执在详情页展示
     * CustomDocinfo.java
     * @param id
     * @param user_id 登录用户编号
     * @param notice_type 0：公告；1：消息；公告获取全部， 消息只能获取自己发送或接受到的消息
     * @param if_read 是否已读；0：未读；1：已读,为null时查询全部
     * 2015年6月15日
     */
    public static void getNoticeList(Integer user_id,Integer notice_type,String keywords, Integer if_read,Integer p, Integer ps) {
		if (user_id == null) {
		    user_id = 0;
		}
		if (notice_type == null) {
		    notice_type =0;
		}
		if (p == null) {
		    p = Integer.valueOf(ResourceBundle.getBundle("config").getString("page"));
		}
		if (ps == null) {
		    ps = Integer.valueOf(ResourceBundle.getBundle("config").getString("pagecount"));
		}
	
		ResultInfo info = CustomDocinfoService.getNoticeList(user_id,  notice_type, if_read,keywords,p, ps, request);
		renderJSON(info);

    }

    /**
     * 查询公告/消息未读数
     * @param user_id 用户id
     * @param notice_type 0：公告；1：消息；
     */
    public static void getUnReadNoticeCount(Integer user_id,Integer notice_type) {
		if (user_id == null) {
		    user_id = 0;
		}
		if (notice_type == null) {
		    notice_type =0;
		}
	
		ResultInfo info = CustomDocinfoService.getUnReadNoticeCount(user_id,  notice_type, request);
		renderJSON(info);

    }


//     /**
//     * 查询文章名称是否已存在
//     * 
//     * @param <必填>
//     */
//    public static void isExistDoc(String title, Long id, Long category_id) {
//	ResultInfo info =CustomDocinfoService.isExistDoc(title, id, category_id, request);
//	renderJSON(info);
//    }
    
}
