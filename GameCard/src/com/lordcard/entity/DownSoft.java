/**
 * DownloadSoft.java [v 1.0.0]
 * classes : model.DownloadSoft
 * auth : yinhongbiao
 * time : 2013 2013-1-8 上午11:23:41
 */
package com.lordcard.entity;

import com.google.gson.annotations.Expose;

/**
 * 下载软件 model.DownloadSoft
 * 
 * @author yinhb <br/>
 *         create at 2013 2013-1-8 上午11:23:41
 */
public class DownSoft {

	@Expose
	private int id;
	@Expose
	private String name; // 应用名称
	@Expose
	private String packageName; // 应用包名
	@Expose
	private String iconName; // 图片地址　　downUrl +　iconName
	@Expose
	private String apkName; // APK名称 apk下载地址 downUrl +　apkName
	@Expose
	private String downUrl; // 应用下载相对路径
	@Expose
	private String description; // 内容描述

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getDownUrl() {
		return downUrl;
	}

	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

}
