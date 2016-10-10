package com.lordcard.entity;

public class Grade {
	private int point;//�ȼ�
	private String 	explanation;//˵��
	private boolean has;//�Ƿ�ﵽ
	private boolean first=false;
	private boolean end=false;
	
	
	public final int getPoint() {
		return point;
	}
	public final void setPoint(int point) {
		this.point = point;
	}
	public final String getExplanation() {
		return explanation;
	}
	public final void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	public final boolean isHas() {
		return has;
	}
	public final void setHas(boolean has) {
		this.has = has;
	}
	public final boolean isFirst() {
		return first;
	}
	public final void setFirst(boolean first) {
		this.first = first;
	}
	public final boolean isEnd() {
		return end;
	}
	public final void setEnd(boolean end) {
		this.end = end;
	}
	
}
