package com.lordcard.rule;

import java.util.ArrayList;
import java.util.List;

import com.lordcard.entity.Number;
import com.lordcard.entity.Poker;

/**
 * 锄大地规则
 * 
 */
public class ChoudiRule {

	public final static int Danpai = 1;
	public final static int Yidui = 2;
	public final static int Santiao = 3;
	public final static int shunzi = 4;
	public final static int tonghuashun = 5;
	public final static int tonghuawu = 6;
	public final static int sandaier = 7;
	public final static int sidaiyi = 8;
	public final static int error = 0;

	/**
	 * 提示功能
	 * 
	 * @param cards
	 * @return
	 */
	public static int[] GettiShi(List<Poker> Othercards, List<Poker> Mycards) {
		// 自己剩下的牌少于对方出的牌
		if (Othercards.size() > Mycards.size()) {
			return null;
		}

		int tishi[];
		List<Poker> now = new ArrayList<Poker>();
		for (Poker card : Mycards) {
			now.add(card);
		}
		// 首先检测别人是什么类型的牌
		int cardType = checkpai(Othercards);
		switch (cardType) {
		case Danpai:
			int value = Othercards.get(0).getValue() * 100 + Othercards.get(0).getStyle();
			if (value == 17) {// 如果是火箭的话直接返回
				return null;
			}
			tishi = new int[Othercards.size()];
			for (int i = Mycards.size() - 1; i >= 0; i--) {
				// //System.out.println("最后一张的值为"+Mycards.get(i).getValue());
				if (value < Mycards.get(i).getValue() * 100 + Mycards.get(i).getStyle()) {
					tishi[0] = Mycards.get(i).getNumber();
					return tishi;
				}
			}
			return null;
		case Yidui:
			tishi = new int[Othercards.size()];
			int yiduivalue = Othercards.get(0).getValue() * 100 + Othercards.get(0).getStyle();
			if (yiduivalue == 17) {// 如果是火箭的话直接返回
				return null;
			}
			for (int i = Mycards.size() - 1; i > 0; i--) {
				// //System.out.println("最后一张的值为"+Mycards.get(i).getValue());
				if (yiduivalue < Mycards.get(i - 1).getValue() * 100 + Mycards.get(i - 1).getStyle()) {
					if (Mycards.get(i).getValue() == Mycards.get(i - 1).getValue()) {
						tishi[0] = Mycards.get(i - 1).getNumber();
						tishi[1] = Mycards.get(i).getNumber();
						return tishi;
					}
				}
			}
			return null;
		case Santiao:
			tishi = new int[Othercards.size()];
			int santiaovalue = Othercards.get(0).getValue();

			for (int i = Mycards.size() - 1; i > 1; i--) {
				// //System.out.println("最后一张的值为"+Mycards.get(i).getValue());
				if (santiaovalue < Mycards.get(i).getValue()) {
					if (Mycards.get(i).getValue() == Mycards.get(i - 1).getValue() && Mycards.get(i - 2).getValue() == Mycards.get(i - 1).getValue()) {
						tishi[0] = Mycards.get(i).getNumber();
						tishi[1] = Mycards.get(i - 1).getNumber();
						tishi[2] = Mycards.get(i - 2).getNumber();
						return tishi;
					}
				}
			}
			return null;

		}
		// 使用新的提示函数
		return ChoudiRule.gettiShiFive(Othercards, Mycards, cardType);

	}

	/**
	 * 对手是五张牌时取提示
	 * 
	 * @param Othercards
	 *            对手牌
	 * @param Mycards
	 *            我的牌
	 * @return 牌的Number队列
	 */
	public static int[] gettiShiFive(List<Poker> Othercards, List<Poker> Mycards, int cardType) {
		// 设置一个矩阵，将自己的牌放进去,每行代表一个花色（1为方片、2为梅花、3为红桃、4为黑桃），
		// 每列得下标是牌的value，比如cards[0][3]代表方片三，cards[3][3]代表黑桃三
		// 需要特别说明的是，列坐标1代表A，2代表2,14也代表A，15代表2，这在做顺子判断时比较容易，在其他判断中，忽略坐标1和2
		// cards[0]不使用，每行的第一个元素也不使用
		boolean cards[][] = new boolean[5][16];
		// 将自己的牌填充进去
		for (int i = 0; i < Mycards.size(); ++i) {
			Poker c = Mycards.get(i);
			cards[c.getStyle()][c.getValue()] = true;
			if (c.getValue() == 14) {
				// 牌为A，同时写到列坐标1中
				cards[c.getStyle()][1] = true;
			} else if (c.getValue() == 15) {
				// 牌为2，同时写到列坐标2中
				cards[c.getStyle()][2] = true;
			}
		}
		int ret[] = null;
		switch (cardType) {
		case ChoudiRule.shunzi:
			if (Othercards.get(0).getValue() == 15 && Othercards.get(1).getValue() == 14) {
				// 唯一情况：1,2,3,4,5
				ret = getShunziFromCards(cards, 5, Othercards.get(2).getStyle());
			} else if (Othercards.get(0).getValue() == 15) {
				// w唯一情况：2,3,4,5,6
				ret = getShunziFromCards(cards, 6, Othercards.get(1).getStyle());
			} else {
				ret = getShunziFromCards(cards, Othercards.get(0).getValue(), Othercards.get(0).getStyle());
			}
			if (ret != null) {
				return ret;
			}
		case ChoudiRule.tonghuawu:
			if (cardType == tonghuawu) {
				ret = getTongHuaWuFromCards(cards, Othercards.get(0).getValue(), Othercards.get(0).getStyle());
			} else {
				// 牌型不是同花五，任何一个同花五都能胜，
				ret = getTongHuaWuFromCards(cards, 0, 0);
			}
			if (ret != null) {
				return ret;
			}
		case ChoudiRule.sandaier:
			// 对手五张牌中第三张牌必定是三张中的一张
			if (cardType == sandaier) {
				ret = getSanDaiErFromCards(cards, Othercards.get(2).getValue());
			} else {
				// 牌型不是三带二，任何一个同花顺都能胜
				ret = getSanDaiErFromCards(cards, 0);
			}
			if (ret != null) {
				return ret;
			}
		case ChoudiRule.sidaiyi:
			// 对手五张牌中第三张牌必定是四张中的一张
			if (cardType == sidaiyi) {
				ret = getSiDaiYiFromCards(cards, Othercards.get(2).getValue());
			} else {
				// 牌型不是四带一，任何牌都能胜
				ret = getSiDaiYiFromCards(cards, 0);
			}
			if (ret != null) {
				return ret;
			}
		case ChoudiRule.tonghuashun:
			if (cardType == tonghuashun) {
				if (Othercards.get(0).getValue() == 15 && Othercards.get(0).getValue() == 14) {
					// 唯一情况：1,2,3,4,5
					ret = getTongHuaShunFromCards(cards, 5, Othercards.get(2).getStyle());
				} else if (Othercards.get(0).getValue() == 15) {
					// w唯一情况：2,3,4,5,6
					ret = getTongHuaShunFromCards(cards, 6, Othercards.get(1).getStyle());
				} else {
					ret = getTongHuaShunFromCards(cards, Othercards.get(0).getValue(), Othercards.get(0).getStyle());
				}
			} else {
				// 牌型不是同花顺，任何一个同花都能胜
				ret = getTongHuaShunFromCards(cards, 0, 0);
			}
			if (ret != null) {
				return ret;
			}

		}

		return null;
	}

	/**
	 * 从牌矩阵中取一个杂顺，其最小的值要大于minValue
	 * 
	 * @param cards
	 *            牌矩阵
	 * @param maxValue
	 *            对手最大的牌 ，为0则表示对手不是同花顺，任何牌型都能胜
	 * @param maxStyle
	 *            对手最大的牌的花色 ，maxValue为0时忽略此参数
	 * @return null则找不到，非null则为找到的杂顺
	 */
	private static int[] getTongHuaShunFromCards(boolean[][] cards, int maxValue, int maxStyle) {

		int ret[] = new int[5];
		int retIndex = 0;
		int loopEnd = cards[0].length - 1;// 顺子不能算入最后的2，因为 “jqkA2”不是顺子
		if (maxValue == 0) {
			maxValue = 5;
			maxStyle = 0;
		}
		// if (maxValue == 0){
		// //任何牌都能胜的情况下，不需要进行同种花色的计算
		// maxStyle = 0;
		// }else{
		// //同种花色下的计算
		// for (int i = maxValue + 1; i < loopEnd; ++i){
		// if (cards[maxStyle][i] == true && cards[maxStyle][i - 1] == true &&
		// cards[maxStyle][i - 2] == true &&
		// cards[maxStyle][i - 3] == true && cards[maxStyle][i - 4] == true){
		// ret[retIndex++] = getCardNumber(maxStyle,i);
		// ret[retIndex++] = getCardNumber(maxStyle,i - 1);
		// ret[retIndex++] = getCardNumber(maxStyle,i - 2);
		// ret[retIndex++] = getCardNumber(maxStyle,i - 3);
		// ret[retIndex++] = getCardNumber(maxStyle,i - 4);
		// return ret;
		// }
		// }
		// }
		for (int j = maxValue; j < loopEnd; ++j) {
			for (int i = 1; i < 5; ++i) {

				if (j == maxValue && i <= maxStyle) {
					continue;
				}
				if (cards[i][j] == true && cards[i][j - 1] == true && cards[i][j - 2] == true && cards[i][j - 3] == true && cards[i][j - 4] == true) {
					ret[retIndex++] = getCardNumber(i, j);
					ret[retIndex++] = getCardNumber(i, j - 1);
					ret[retIndex++] = getCardNumber(i, j - 2);
					ret[retIndex++] = getCardNumber(i, j - 3);
					ret[retIndex++] = getCardNumber(i, j - 4);
					return ret;
				}
			}
		}

		return null;
	}

	/**
	 * 对手是四带一时的提示
	 * 
	 * @param cards
	 *            牌矩阵
	 * @param rechargeType
	 *            对手四张的牌的值 为0表示任何四带一都能胜
	 * @return 五张牌的Number
	 */
	private static int[] getSiDaiYiFromCards(boolean[][] cards, int siZhangValue) {
		int ret[] = new int[5];
		int retIndex = 0;
		int iPos = 0;
		if (siZhangValue == 0) {
			siZhangValue = 2;
		}
		for (int i = siZhangValue + 1; i < cards[0].length; ++i) {
			// 将同样数字的牌加入到ret中
			for (int j = 1; j < 5; ++j) {
				if (cards[j][i] == true) {
					ret[retIndex++] = getCardNumber(j, i);
				}
			}
			// 如果牌的张数不是4，则跳到下一张
			if (retIndex == 4) {
				iPos = i;
				break;
			}
			retIndex = 0;
		}
		// 如果有四张，则选择散牌
		if (retIndex == 4) {
			// 找散牌
			for (int i = 3; i < cards[0].length; ++i) {
				if (i == iPos) {
					continue;
				}
				// 将同样数字的牌加入到ret中
				for (int j = 1; j < 5 && retIndex < 5; ++j) {
					if (cards[j][i] == true) {
						ret[retIndex++] = getCardNumber(j, i);
					}
				}

				if (retIndex == 5) {
					break;
				}
			}
		}
		if (retIndex == 5) {
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * 对手是三带对时的提示
	 * 
	 * @param cards
	 * @param sanZhangValue
	 *            对手三张的牌，如果是0则代表任何一个三带二都能胜过
	 * @return
	 */
	private static int[] getSanDaiErFromCards(boolean[][] cards, int sanZhangValue) {
		try {
			int ret[] = new int[5];
			int retIndex = 0;
			int iPos = 0;
			if (sanZhangValue == 0) {
				sanZhangValue = 2;
			}
			for (int i = sanZhangValue + 1; i < cards[0].length; ++i) {
				// 将同样数字的牌加入到ret中
				for (int j = 1; j < 5 && retIndex < 5; ++j) {
					if (cards[j][i]) {
						ret[retIndex++] = getCardNumber(j, i);
					} else {
						continue;
					}
					// 如果牌的张数不是3，则跳到下一张
					if (retIndex == 3) {
						break;
					}
				}
				// 如果牌的张数不是3，则跳到下一张
				if (retIndex == 3) {
					iPos = i;
					break;
				}
				retIndex = 0;
			}
			// 如果有三张，则选择一对
			if (retIndex == 3) {
				// 找对子
				for (int i = 3; i < cards[0].length; ++i) {
					if (i == iPos) {
						continue;
					}
					// 将同样数字的牌加入到ret中
					for (int j = 1; j < 5 && retIndex < 5; ++j) {
						if (cards[j][i]) {
							ret[retIndex++] = getCardNumber(j, i);
						} else {
							continue;
						}
					}
					// 如果牌的张数不是3，则跳到下一张
					if (retIndex == 5) {
						break;
					}
					retIndex = 3;
				}
			}
			if (retIndex == 5) {
				return ret;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 对手是同花五时的提示
	 * 
	 * @param cards
	 * @param maxValue
	 *            对手最大的牌数，为0则表示任何牌都能吃
	 * @param maxStyle
	 *            对手最大的牌的花色 maxValue为0时忽略此参数
	 * @return
	 */
	private static int[] getTongHuaWuFromCards(boolean[][] cards, int maxValue, int maxStyle) {
		int ret[] = new int[5];
		int retIndex = 0;
		int j = 0;
		if (maxValue == 0) {
			maxStyle = 1;
			maxValue = 0;
		}
		for (int i = 0; i < 5; ++i) {
			// 从小到大找出五张同花色的牌
			for (j = 3; j < cards[0].length; ++j) {
				if (cards[i][j] == true) {
					ret[retIndex++] = getCardNumber(i, j);
				}
				if (retIndex == 5) {
					break;
				}
			}
			// 未找齐五张，找下一种花色
			if (retIndex < 5) {
				retIndex = 0;
				continue;
			}
			// 如果和对手是同花色，则需要保证最后的一张比对手最大的牌大
			if (j < maxValue || (j == maxValue && i < maxStyle)) {
				if (i <= maxStyle) {
					for (j = maxValue + 1; j < cards[0].length; ++j) {
						if (cards[i][j] == true) {
							ret[4] = getCardNumber(i, j);
							break;
						}
					}
				} else {
					for (j = maxValue; j < cards[0].length; ++j) {
						if (cards[i][j] == true) {
							ret[4] = getCardNumber(i, j);
							break;
						}
					}
				}

				if (j < cards[0].length) {
					break;
				} else {
					retIndex = 0;
					continue;
				}

			} else {
				break;
			}

		}
		if (retIndex == 5) {
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * 从牌矩阵中取一个杂顺，其最小的值要大于minValue
	 * 
	 * @param cards
	 *            牌矩阵
	 * @param MinValue
	 *            对手最小的牌
	 * @return null则找不到，非null则为找到的杂顺
	 */
	public static int[] getShunziFromCards(boolean cards[][], int maxValue, int maxStyle) {
		int minValue = maxValue - 4;

		int ret[] = new int[5];
		int retIndex = 0;
		int lastValue = 0;
		int j;
		int loopEnd = cards[0].length - 1;// 找顺子时要忽略最后的2，因为他不能作为顺子的最后一个牌
		for (int i = minValue; i < loopEnd; ++i) {
			// 在一个牌子中找一个花色
			for (j = 1; j < 5; ++j) {
				if (cards[j][i] == true) {
					ret[retIndex++] = getCardNumber(j, i);
					lastValue = i;
					break;
				}
			}
			if (retIndex == 5) {
				// 比较最大的牌是否比对手的牌大（需要比较花色）
				if (i > maxValue || j > maxStyle) {
					break;
				} else {
					// 这里只有一种情况，就是开始的时候，牌都一样大，但是最大的那张牌花色比别人小，所以此时从第二张牌（minValue+1）开始查
					i = minValue + 1;
					retIndex = 0;
				}
			}
			// 如果此轮未找到,必须从新开始找
			if (lastValue != i) {
				retIndex = 0;
			}
		}

		if (retIndex == 5) {
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * 计算一个牌的Number参数
	 * 
	 * @param style
	 *            花色
	 * @param value
	 *            牌的值 (14==1,15==2)
	 * @return 牌的Number
	 */
	public static int getCardNumber(int style, int value) {
		if (value == 14) {
			value = 1;
		} else if (value == 15) {
			value = 2;
		}
		return (4 - style) * 13 + value - 1;
	}

	/**
	 * 寻找一张牌中出现次数最多，牌最大的数字,这里取出来的值不是Card中的Number，而是按一下规则定义的牌：牌的数 * 10 +
	 * 牌的花色，这样保证的所有的牌之间有大小关系
	 * 
	 * @param cards
	 * @param type
	 * @return
	 */
	public static int getMaxNumber(List<Poker> cards, int type) {
		int max = 0;
		int pei = 10;
		int count = getPokeCount(cards);

		if (type == shunzi || type == tonghuashun) { // 如果是顺子的话
			if (cards.get(0).getValue() == 15) { // 如果第一位是2的話
				if (cards.get(1).getValue() == 14) {// 如果第2位是1的話
					max = 5 * pei + cards.get(2).getStyle();
					return max;
				} else if (cards.get(1).getValue() == 6) {// 如果第2位是6的話
					max = 6 * pei + cards.get(1).getStyle();
					return max;
				}
			} else {
				max = cards.get(0).getValue() * pei + cards.get(0).getStyle();
				return max;
			}
		} else {
			// //System.out.println("c出现最多是"+count+"次");
			for (int i = 0; i < cards.size(); i++) {
				if (count == numberCount(cards.get(i).getValue(), cards)) {
					max = cards.get(i).getValue() * pei + cards.get(i).getStyle();
					// System.out.println("最大的牌位"+max);
					return max;
				}
			}

		}
		return max;
	}

	/**
	 * 
	 * //检测牌的大小，如果大能出就true
	 * 
	 * @param typeOher
	 * @param typeMe
	 * @param maxOhther
	 * @param maxMe
	 * @return
	 */
	public static boolean compterpai(int typeOher, int typeMe, int maxOhther, int maxMe) {
		boolean compter = false;
		// 如果两个人出的牌类型不一样
		if (typeMe != typeOher) {
			// System.out.println("牌型不一样");
			compter = false;
			switch (typeOher) {
			case tonghuawu:
				if (typeMe == tonghuashun || typeMe == sidaiyi || typeMe == sandaier) { // 别人同花，自己同花顺，4带1，三代二都可以打得过
					compter = true;
				}
				break;
			case sidaiyi:
				if (typeMe == tonghuashun) { // 别人四带1，自己只有同花顺才能打得过
					compter = true;
				}
				break;
			case shunzi:
				if (typeMe == tonghuashun || typeMe == sidaiyi || typeMe == sandaier || typeMe == tonghuawu) { // 别人同花，自己同花顺，4带1，三代二,同花都可以打得过
					compter = true;
				}
				break;
			case sandaier:
				if (typeMe == tonghuashun || typeMe == sidaiyi) { // 别人同花，自己同花顺，4带1，都可以打得过
					compter = true;
				}
				break;
			}

		} else {// 两个人出牌的类型一样
				// System.out.println("牌型一样");
			if (maxOhther >= maxMe) {
				compter = false;
			} else {
				compter = true;
			}
		}
		return compter;

	}

	/**
	 * 检测是什么牌
	 * 
	 * @param cards
	 * @return 0为错误，1为单张，2为一对，3为3张，4为顺子，5为同花顺，6为同花五，7为三带二，8为四带一
	 */
	public static int checkpai(List<Poker> cards) {
		int cardStyle = 0;
		if (cards.size() == 0) {// 如果0张牌
			cardStyle = error; // error
		}
		if (cards.size() > 5) {// 如果0张牌
			cardStyle = error; // error
		}
		int pokerCount = getPokeCount(cards);
		// //System.out.println("相同的牌位："+pokerCount);
		switch (cards.size()) {
		case 1: // 如果是一张牌
			cardStyle = Danpai; // 单牌
			// System.out.println("单牌");
			break;
		case 2:// 如果是两张牌
			if (cards.get(0).getValue() == cards.get(1).getValue()) {
				cardStyle = Yidui;// 一对
				// System.out.println("一对");
			}
			break;
		case 3:// 如果是三张牌
			if (pokerCount == 3) {
				cardStyle = Santiao;
				// System.out.println("三张");
			}
			break;
		case 5:// 如果是五张牌
			if (pokerCount == 3) {// 三待二
				// 最开始两张和最后两张都必须相等
				if (cards.get(3).getValue() == cards.get(4).getValue() && cards.get(0).getValue() == cards.get(1).getValue()) {

					cardStyle = sandaier;

					// System.out.println("三带2");
				}
			} else if (pokerCount == 4) {// 四带1
				cardStyle = sidaiyi;
				// System.out.println("四带一");
			} else if (checkShunpai(cards)) {// 是顺牌
				if (checkTonghua(cards)) {
					cardStyle = tonghuashun;
					// System.out.println("同花顺");
				} else {
					cardStyle = shunzi;
					// System.out.println("杂顺");
				}
			} else {
				if (checkTonghua(cards)) {
					cardStyle = tonghuawu;
					// System.out.println("同花五");
				}
			}
			break;

		}

		return cardStyle;

	}

	/**
	 * 检测是不是同花
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean checkTonghua(List<Poker> cards) {
		int tongHuaCount = 1;
		int Style = cards.get(0).getStyle();
		for (int i = 1; i < cards.size(); i++) {
			if (cards.get(i).getStyle() == Style) {
				tongHuaCount++;
			}
		}
		if (tongHuaCount == 5) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 统计是不是顺牌，顺牌最後不包括2,
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean checkShunpai(List<Poker> cards) {
		if (cards.size() != 5) {
			return false;
		}
		int thirdCard = cards.get(2).getValue();
		int first = cards.get(0).getValue();
		if (thirdCard == 13) { // 第三张牌为k，说明牌是JQKA2，这不是顺子
			return false;
		}
		if (cards.get(0).getValue() == 15// 如果是12345的話，是順子
				&& cards.get(1).getValue() == 14 && cards.get(2).getValue() == 5 && cards.get(3).getValue() == 4 && cards.get(4).getValue() == 3) {
			return true;
		}
		if (cards.get(0).getValue() == 15// 如果是23456的話，是順子
				&& cards.get(1).getValue() == 6 && cards.get(2).getValue() == 5 && cards.get(3).getValue() == 4 && cards.get(4).getValue() == 3) {
			return true;
		}
		for (int i = 0; i < cards.size() - 1; i++) {
			if (cards.get(i + 1).getValue() + 1 == first) {
				first = cards.get(i + 1).getValue();
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 檢測一張牌在一手牌中出現的次數
	 * 
	 * @param number
	 * @param cards
	 * @return
	 */
	public static int numberCount(int number, List<Poker> cards) {
		int count = 0;
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getValue() == number) {
				// //System.out.println("value"+cards.get(i).getValue()+"number"+number);
				count++;
			}
		}
		// //System.out.println("检测count长度"+count);
		return count;
	}

	/**
	 * 统计一手牌中同值的牌出现的次数来判断
	 * 
	 * @param cards
	 * @return
	 */
	public static int getPokeCount(List<Poker> cards) {
		int count = 1;
		int maxcount = 1;
		int nowvalue = cards.get(0).getValue(); // 先取得第一个牌的值
		for (int i = 0; i < cards.size() - 1; i++) {
			if (nowvalue == cards.get(i + 1).getValue()) {// 如果相同count++，不同改变nowvalue值
				count++;
			} else {
				nowvalue = cards.get(i + 1).getValue();
				if (maxcount < count)
					maxcount = count;
				count = 1;
			}
		}
		if (maxcount > count) {
			return maxcount;
		} else {
			return count;
		}
	}

	/**
	 * 将牌排序从大到小排序
	 * 
	 * @param cards
	 * @param poker
	 * @return
	 */
	public static int[] sort(int[] cards, Poker[] poker) {
		int zhi[] = new int[cards.length];
		Number[] num = new Number[cards.length];
		// * 冒泡排序
		for (int i = 0; i < cards.length; i++) {
			num[i] = new Number();
			num[i].setValue(poker[cards[i]].getValue() * 100 + poker[cards[i]].getStyle());
			num[i].setPokerNumber(cards[i]);
			zhi[i] = poker[cards[i]].getValue();
		}

		for (int i = 0; i < num.length; i++) {
			for (int j = i + 1; j < num.length; j++) {
				if (num[i].getValue() < num[j].getValue()) {
					Number a = num[i];
					num[i] = num[j];
					num[j] = a;
				}
			}
		}
		for (int i = 0; i < num.length; i++) {
			zhi[i] = num[i].getPokerNumber();
		}
		return zhi;
	}
}
