/**
*/ 
package com.sdk.util.vo;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.annotations.Expose;

/**
* @ClassName: PayType
* @Description: 支付充值位置充值方式配置
*/
public class PaySiteConfig implements java.io.Serializable {
	
	private static final long serialVersionUID = 1076078121058499214L;
	
	@Expose private String site;		//支付位置编号
	@Expose private String payconf;		//支付配置 		３大运营商支付方式，备用支付方式，弹出框模式，提示文字
	
	private HashMap<String,ArrayList<PaySiteConfigItem>> siteItemMap;
	
	public String getSite() {
		return site;
	}
	
	public void setSite(String site) {
		this.site = site;
	}
	
	public String getPayconf() {
		return payconf;
	}
	
	public void setPayconf(String payconf) {
		this.payconf = payconf;
	}
	
	public HashMap<String, ArrayList<PaySiteConfigItem>> getSiteItemMap() {
		return siteItemMap;
	}
	
	public void setSiteItemMap(HashMap<String, ArrayList<PaySiteConfigItem>> siteItemMap) {
		this.siteItemMap = siteItemMap;
	}
	
}
