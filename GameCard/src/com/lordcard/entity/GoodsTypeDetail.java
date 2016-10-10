package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoodsTypeDetail {
	@Expose
	@SerializedName("i")
	private String id; //
	@Expose
	@SerializedName("n")
	private String name; // 物品名称
	@Expose
	@SerializedName("rk")
	private String remark; // 备注说明
	@Expose
	@SerializedName("lp")
	private String largePicPath; // 大图路径

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLargePicPath() {
		return largePicPath;
	}

	public void setLargePicPath(String largePicPath) {
		this.largePicPath = largePicPath;
	}

}
