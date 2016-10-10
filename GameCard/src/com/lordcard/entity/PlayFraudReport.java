package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlayFraudReport {
	//@Expose @SerializedName("a") private String	accuser;	// 原告
		@Expose @SerializedName("d") private String	defendant;	// 被告
//		@Expose @SerializedName("c") private String	code;		// 举报相应关联编号
		public final String getDefendant() {
			return defendant;
		}
		public final void setDefendant(String defendant) {
			this.defendant = defendant;
		}
	}
