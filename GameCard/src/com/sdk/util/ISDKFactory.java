/**
 * IPayView.java [v 1.0.0]
 * classes : com.lordcard.common.pay.IPayView
 * auth : yinhongbiao
 * time : 2013 2013-3-20 下午3:04:23
 */
package com.sdk.util;

import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;


/**
 * com.lordcard.common.pay.IPayView
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-20 下午3:04:23
 */
public abstract class ISDKFactory {
	public ISDKFactory() {}

	/**
	 *  加载支付方式
	 * @Title: loadPay  
	 * @param payInit	初始化的基本信息
	 * @return void	
	 * @throws
	 */
	public abstract void loadPay(PayInit payInit);
	
	/**
	 * 获取支付类型编号
	 */
	public abstract String getPayCode();
	
	/**
	 * 支付
	 * @Title: goPay  
	 * @param  payPoint 具体的充值计费点
	 * @param  paySiteTag 计费位置点
	 * @return void
	 * @throws
	 */
	public abstract void goPay(PayPoint payPoint,String paySiteTag);
	
	/**
	 * 本地账号单机支付
	 * @Title: goPay  
	 * @param  payPoint 具体的充值计费点
	 * @param  paySiteTag 计费位置点
	 * @return void
	 * @throws
	 */
	public abstract void localPay(PayPoint payPoint,String paySiteTag);
}
