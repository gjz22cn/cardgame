package com.lordcard.entity;

public class SignVo {
	private String day;// ǩ������
	private int imgId;// ǩ����ȡ��ƷչʾͼƬID
	private String content;// ǩ����ȡ��Ʒ����
	private boolean isSign;// �Ƿ���ǩ��

	public SignVo(String day, int imgId, String content, boolean isSign) {
		this.day = day;
		this.imgId = imgId;
		this.content = content;
		this.isSign = isSign;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isSign() {
		return isSign;
	}

	public void setSign(boolean isSign) {
		this.isSign = isSign;
	}

}
