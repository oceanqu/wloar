package models;

import java.io.UnsupportedEncodingException;

import org.eclipse.jdt.core.dom.ThisExpression;
import play.mvc.Controller;

/**
 * 返回结果信息类
 * 
 * @author 
 * 
 * @date 2014-11-18
 */
public class ResultInfo extends Controller {

	// 返回状态
	private Integer code;
	//状态描述信息
	private String msg;
	//当前页码
	private int page;
	//当页文章数
	private int pageSize;
	//是否有下一页（为提高效率，改为后台判断，此处均返回true)
	private boolean hasNextPage;
	//Info数据量
	private long count;
	//返回数据列表
	private Object info;
	//访问路径
	private String request;

	public Integer getCode() {
	    return code;
	}
	public void setCode(Integer code) {
	    this.code = code;
	}
	/**
	 * 设置错误码，同时设置错误码提示
	 * ResultInfo.java
	 * @param code
	 * 2014年11月18日
	 */
	public void setCodeAndMsg(Integer code) {
	    this.code = code;
	    String msg = GetDescByStatus(code.toString());
	    setMsg(msg);
	}

	public String getMsg() {
	    return msg;
	}

	public void setMsg(String msg) {
	    this.msg = msg;
	}

	public int getPage() {
	    return page;
	}

	public void setPage(int page) {
	    this.page = page;
	}

	public int getPageSize() {
	    return pageSize;
	}

	public void setPageSize(int pageSize) {
	    this.pageSize = pageSize;
	}

	public boolean isHasNextPage() {
	    return hasNextPage;
	}

	public void setHasNextPage(boolean hasNextPage) {
	    this.hasNextPage = hasNextPage;
	}

	public long getCount() {
	    return count;
	}

	public void setCount(long count) {
	    this.count = count;
	}

	public Object getInfo() {
	    return info;
	}

	public void setInfo(Object info) {
	    this.info = info;
	}

	public String getRequest() {
	    return request;
	}
	/**
	 * @param request
	 *            the request to set
	 */
	public void setRequest(String request) {
		this.request = request;
	}

    /**
     * 获取映射消息
     * @param status_init
     * @return
     */
    public static String GetDescByStatus(String status_code) {
    	String status_desc_init = "";
    	CodeMsgDesc sdc = new CodeMsgDesc();
	String status_init_code = "Code_" + status_code;
        status_desc_init = sdc.getClassFieldValue(status_init_code);
   	return status_desc_init;
}
  
    
    
    /**
     * 默认构造函数
     */
	public ResultInfo() {
		super();
	}
   
}
