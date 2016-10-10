/**
 */
package com.lordcard.entity;

import com.google.gson.annotations.Expose;

/**
 * @ClassName: GameUserAddress
 * @Description: 游戏用户收货地址
 * @author shaohu
 * @date 2013-5-6 下午04:12:22
 * 
 */
public class GameUserAddress {

	private String id;
	private String account;
	@Expose
	private String addressee; // 收件人
	@Expose
	private String address; // 收件地址
	@Expose
	private String zip; // 邮编
	@Expose
	private String phone; // 电话
	private String createTime; // 创建时间

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the addressee
	 */
	public String getAddressee() {
		return addressee;
	}

	/**
	 * @param addressee
	 *            the addressee to set
	 */
	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}

	/**
	 * @param zip
	 *            the zip to set
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
