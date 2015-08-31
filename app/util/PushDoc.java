package util;


import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import models.AndroidNoticeInfo;
import models.ResultInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import play.mvc.Http.Request;
import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.device.DeviceClient;
import cn.jpush.api.device.TagAliasResult;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

/**
 *
 * All right reserved.
 *
 * 极光推送，往科研快讯apk推送消息
 */
public class PushDoc {

    public static ResourceBundle rb = ResourceBundle.getBundle("config");
    static String jg_mastersecret = rb.getString("jpush.jg_mastersecret");
    static String jg_appkey = rb.getString("jpush.jg_appkey");
   /**
     * 日志输出
     */
    static Logger logger = Logger.getLogger(PushDoc.class);

    /**
     * 构造函数
     * 
     * @param NewsPushApi
     *            新闻推送API地址
     */
    public PushDoc() {
    }
    /**
     * 推送某篇文章
     * 
     * @param doc
     *  待推送消息记录
     * @param cdfApp
     * 极光推送配置信息
     */
    public long pushDoc(AndroidNoticeInfo androidNoticeInfo) {

	JPushClient jpushClient = new JPushClient(jg_mastersecret, jg_appkey);
	long msg_id = 0;
	// For push, all you need do is to build PushPayload object.
	PushPayload payload = buildPushObject_android_and_ios(androidNoticeInfo);
	try {
	    PushResult result = jpushClient.sendPush(payload);
	    logger.info("推送成功 msg_id : " + result.msg_id + " ; newsId : "
			+ String.valueOf(androidNoticeInfo.id) + "; title : "
			+ androidNoticeInfo.title);
	    System.out.println("Got result - " + result);
	    msg_id = result.msg_id;

	} catch (APIConnectionException e) {
	    logger.error("Connection error. Should retry later. " + e);
	    return msg_id;
	} catch (APIRequestException e) {
	    logger.error("推送失败  err_Code : " + e.getErrorCode()
		    	+ "; HTTP Status: " + e.getStatus()
		    	+ ";Error Message: " + e.getErrorMessage()
			+ "; newsId : " + String.valueOf(androidNoticeInfo.id)
			+ " ; title : " + androidNoticeInfo.title);
	    return msg_id;
	}
	return msg_id;
    }
    

    /**
     * 构建推送数据结构
     * PushDoc.java
     * @param doc
     * @return
     * 2014年12月26日
     */
    public static PushPayload buildPushObject_android_and_ios(AndroidNoticeInfo androidNoticeInfo) {

	if (androidNoticeInfo.title.length() > 30) {
	    androidNoticeInfo.title = androidNoticeInfo.title.substring(0, 30);
	    androidNoticeInfo.title += "...";
	}
	//android参数
	String notice_type = "群发消息";
	if (androidNoticeInfo.notice_type == 1) {
	    notice_type = "下发任务";
	}
	
	String summary = "";
	if (androidNoticeInfo.content.length() > 30) {
	    summary= androidNoticeInfo.content.substring(0, 30);
	}else {
	    summary = androidNoticeInfo.content;
	}
	
	AndroidNotification androidNotification = AndroidNotification.newBuilder()
	.setTitle("[" + notice_type + "]" + androidNoticeInfo.title)
	.setAlert(summary)//必填,通知内容
	.addExtra("doc_id", androidNoticeInfo.id)
	.addExtra("from", androidNoticeInfo.notice_type)
	.build();
	
	//ios参数
	IosNotification iosNotification = IosNotification.newBuilder()
	.setSound("default")//可选,通知提示声音
	.setBadge(1)//可选,应用角标,如果不填，表示不改变角标数字；否则把角标数字改为指定的数字；为 0 表示清除。JPush 官方 API Library(SDK) 会默认填充badge值为"+1",详情参考：badge +1
	.setAlert("[" + notice_type + "]" + androidNoticeInfo.title)//必填,通知内容
	.addExtra("doc_id", androidNoticeInfo.id)
	.addExtra("from", androidNoticeInfo.notice_type)
	.build();
	
	
	Options options = Options
		.newBuilder()
		//目标平台为 iOS 平台 需要在 options 中通过 apns_production 字段来制定推送环境。True 表示推送生产环境，False 表示要推送开发环境； 如果不指定则为推送生产环境。
		.setApnsProduction(true)
		//time_to_live用来设置推送当前用户不在线时，为该用户保留多长时间的离线消息，默认 86400 （1 天），最长 10 天。设置为 0 表示不保留离线消息，只有推送当前在线的用户可以收到。
		//.setTimeToLive(86400)
		.build();
	
//	String tag_name = "tag_class_" + doc.class_id;
	String tag_name = "";
	if (androidNoticeInfo.push_range.equals("all")) {
		return PushPayload
			.newBuilder()
			.setPlatform(Platform.android_ios())
			.setAudience(Audience.all())//不打标签，全推送
			//.setAudience(Audience.tag(tag_name))//按标签推送
			.setOptions(options)
			.setNotification(Notification
					.newBuilder()
					.addPlatformNotification(androidNotification)
					.addPlatformNotification(iosNotification)
					.build())
			.build();
	}else {
	    try {
        	    JSONObject jsonObject = JSONObject.fromObject(androidNoticeInfo.push_range);
        	    JSONArray jsonArray_tag = jsonObject.getJSONArray("tag");
        	    for (int i = 0; i < jsonArray_tag.size(); i++) {
//        		JSONObject json_tag = jsonArray_tag.getJSONObject(i);
        		tag_name = jsonArray_tag.getString(i);
        	    }	    
	    } catch (Exception e) {
	    // TODO: handle exception
	    }
	    
	}
	
	return PushPayload
		.newBuilder()
		.setPlatform(Platform.android_ios())
		//.setAudience(Audience.all())//不打标签，全推送
		.setAudience(Audience.tag(tag_name))//按标签推送
		.setOptions(options)
		.setNotification(Notification
				.newBuilder()
				.addPlatformNotification(androidNotification)
				.addPlatformNotification(iosNotification)
				.build())
		.build();
    }
    
    
    
    
    /**
     * 更新极光服务器数据
     * AppLoginService.java
     * @param userInfo 用户信息
     * @param jpush_registration_id 极光服务器为设备分配的注册号
     * @param update_type 更新类型：1：增加；2：删除
     * @return
     * 2015年6月4日
     */
    public static ResultInfo updateJPushGroupTagOrAlias(String jpush_registration_id, Integer user_id, Request request) {
	ResultInfo info = new ResultInfo();
	if (jpush_registration_id == null || jpush_registration_id.length() <= 0) {
	    info.setCodeAndMsg(500);
	    info.setRequest(request.path);
	    return info;
	}
	String base_user_push_mark = "tag_user_";
	String user_push_mark = base_user_push_mark + user_id;
	Set<String> tagsToAdd = new HashSet<String>();// jpush新增tag信息
	tagsToAdd.add(user_push_mark);
		    if (tagsToAdd.size() > 0) {
			// 为当前设备在jpush服务器打标签
			DeviceClient deviceClient = new DeviceClient(jg_mastersecret,
				jg_appkey);
			try {
				// 如果库中没有tag，则需要新插入，将jpush上的tag先删除，首先清空jpush服务器tag
				deviceClient.updateDeviceTagAlias(jpush_registration_id,
					    true, true);
				deviceClient.updateDeviceTagAlias(jpush_registration_id, null,
					tagsToAdd, null);
			   
			} catch (APIConnectionException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			    info.setCode(1005);
			    info.setMsg("连接标签更新服务失败:" + e.getMessage());
			    info.setRequest(request.path);
			    return info;
			  
			} catch (APIRequestException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			    info.setCode(1006);
			    info.setMsg("输入参数错误:" + e.getErrorMessage());
			    info.setRequest(request.path);
			    return info;
			}
		    }

		    info.setCodeAndMsg(200);
		    info.setRequest(request.path);
		    return info;
    }
}
