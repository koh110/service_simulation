package model.service;

import java.util.EnumSet;

/**
 * サービスの種類を管理するクラス
 * @author kohta
 *
 */
public class ServiceType {
	/**
	 *  サービスの種類を表す列挙型
	 *
	 */
	public static enum Services{
		COOKING,		// 料理
		HUNTING,		// 狩り
		FARMING,		// 農作業
		FISHING,	// 釣り
		CONSTRUCTING,	// 建築
		TEACHING,	// 教育
		A,
		B,
		C,
		D,
		E,
		F,
//		A1,
//		A2,
//		A3,
//		A4,
//		A5,
//		A6
	}
	/**
	 * 生成するできるサービスの最大数
	 * サービスの種類の数
	 */
	public static final int MAX_SERVICE_NUM = EnumSet.allOf(Services.class).size();
}
