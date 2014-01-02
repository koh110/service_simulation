package model.service;

/**
 * オペラントリソースに加算する値を時間経過で管理するクラス
 * @author kohta
 *
 */
public class TimeResorce {
	/**
	 * 加算する値
	 */
	private double addValue;

	/**
	 * オペラントリソースが生き残れる残り時間
	 */
	private int remainingTime;

	/**
	 * コンストラクタ
	 * @param addValue
	 * @param time
	 */
	public TimeResorce(double addValue,int time){
		this.addValue = addValue;
		this.remainingTime = time;
	}

	/**
	 * 時間経過時の処理
	 */
	public void timePassed(){
		// 残り時間を減少させる
		this.remainingTime--;
	}

	//================================================================================
	// getter,setter
	//================================================================================
	/**
	 * 加算値を取得する
	 * @return
	 */
	public double getAddValue(){
		return this.addValue;
	}

	/**
	 * 残り時間を取得する
	 * @return
	 */
	public int getRemainingTime(){
		return this.remainingTime;
	}
}
