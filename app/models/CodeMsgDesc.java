package models;
import java.lang.reflect.Field;

import org.bouncycastle.jce.provider.JDKDSASigner.stdDSA;
/**
 * 错误码说明
 * @author 
 * @date 2014-11-18
 */
public class CodeMsgDesc {

//	public static void main(String[] args) {
//		String oldStr = "AAA    BBB";
//		String newStr = oldStr.replaceAll(" {2,}", " ");
//		newStr = oldStr.replaceAll("A{2,}", "D");
//		System.out.println(newStr);
//		String aa = "sdf[],123";
//		System.out.println(aa.replaceAll("[a-zA-Z\\[\\]\\(\\),\\d]", " "));
//	}
	
    public String getClassFieldValue(String field){
    	  
    	   Field tempField;
    	      String value = null;
    	      Class className = this.getClass();
    	      try {
    	     tempField = className.getDeclaredField(field);

    	        value = String.valueOf(tempField.get(this));
    	     
    	     return value;
    	     
    	      } catch (NoSuchFieldException e) {
    	          System.out.println(e);
    	          return "1";
    	      } catch (SecurityException e) {
    	     return "2";
    	      } catch (IllegalAccessException e) {
    	     return "3";
    	      }
    	}
	
    // 公共信息
	public String Code_200 = "成功";
	public String Code_500 = "程序异常，请联系管理员";
	public String Code_400 = "未登录，请先登录";
	public String Code_1021 = "输入参数错误,查询数据为空！";

	public String Code_1001 = "科室成员不能下派任务，只能上报任务！";
	public String Code_1002 = "用户名或密码错误，请重新输入！";
	public String Code_1020 = "用户名异常，请使用手机号登录！";
	public String Code_1003 = "局长没有上级用户！";
	public String Code_1004 = "科室成员没有下级用户！";
	public String Code_1005 = "上传文件为空，请重新上传！";
	public String Code_1006 = "参数错误，user_id和ids两个参数至少要包含一个！";
	public String Code_1007 = "传入参数不能为空！";
	public String Code_1008 = "科室人员只能发布本科室任务！";
	public String Code_1009 = "文件名输入错误，该文件不存在！！";
	public String Code_1010 = "输入参数错误，该条数据不存在！";
	public String Code_1011 = "只能由任务接收人处理任务！";
	public String Code_1012 = "任务接收人不存在！";
	public String Code_1013 = "任务发布人不存在！";
	public String Code_1014 = "该企业已存在，不能重新添加！";
	public String Code_1015 = "科室人员只能查看本科室任务！";
	public String Code_1017 = "输入参数错误，该企业数据不存在";
	public String Code_1016 = "科员不能发布公告！";
	public String Code_1018 = "科室领导只能发布本科室公告！";
	public String Code_1019 = "当前版本已是最新版本！";
	public String Code_1022 = "任务只能由科室领导公开！";


	

}
