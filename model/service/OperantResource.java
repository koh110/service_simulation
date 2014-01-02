package model.service;

import java.util.ArrayList;

import util.MyUtil;

/**
 * オペラントリソースを扱うクラス サービスのアビリティを示す
 *
 * @author kohta
 *
 */
public class OperantResource implements Cloneable{
	/**
	 * サービスの作業効率を表す
	 */
	private double efficiency;

	/**
	 * サービスの追加作業効率を表す
	 */
	private ArrayList<TimeResorce> addEfficiency;

	/**
	 * サービスの品質を表す
	 */
	private double quality;

	/**
	 * サービスの追加品質を表す
	 */
	private ArrayList<TimeResorce> addQuality;

	/**
	 * 作業効率の初期最大値
	 */
	private static final double EFFICIENCY_MAX = 10.0;

	/**
	 * 作業効率の初期最小値
	 */
	private static final double EFFICIENCY_MIN = 1.0;

	/**
	 * 品質の初期最大値
	 */
	private static final double QUALITY_MAX = 10.0;

	/**
	 * 品質の初期最小値
	 */
	private static final double QUALITY_MIN = 1.0;

	/**
	 * コンストラクタ
	 */
	public OperantResource() {
		this(MyUtil.random(EFFICIENCY_MIN, EFFICIENCY_MAX),MyUtil.random(QUALITY_MIN, QUALITY_MAX));
	}

	/**
	 * コンストラクタ
	 * @param efficiency 作業効率
	 * @param quality 品質
	 */
	public OperantResource(double efficiency,double quality){
		// 作業効率の初期化
		this.efficiency = efficiency;
		// 追加の作業効率の初期化
		this.addEfficiency = new ArrayList<TimeResorce>();
		// 品質の初期化
		this.quality = quality;
		// 追加品質の初期化
		this.addQuality = new ArrayList<TimeResorce>();
	}

	/**
	 * 作業効率を加算する
	 * @param addResorce 加算する作業効率
	 */
	public void addEfficiency(TimeResorce addResorce){
		this.addEfficiency.add(addResorce);
	}

	/**
	 * 品質を加算する
	 * @param addResorce 加算する品質
	 */
	public void addQuality(TimeResorce addResorce){
		this.addQuality.add(addResorce);
	}

	/**
	 * 時間経過時の処理
	 */
	public void timePassed(){
		// 削除用ポインタ
		ArrayList<TimeResorce> deadResorce = new ArrayList<TimeResorce>();

		// 追加作業効率の時間経過処理
		for(TimeResorce tr:this.addEfficiency){
			tr.timePassed();
			// 残り時間がなくなっていれば
			if(tr.getRemainingTime()<=0){
				deadResorce.add(tr);	// 削除用にポインタを取得しておく
			}
		}
		// 残り時間のなくなった追加作業効率をリストから削除する
		for(TimeResorce deadTr:deadResorce){
			this.addEfficiency.remove(deadTr);
		}

		// 削除用ポインタの初期化
		deadResorce.clear();

		// 追加品質の時間経過処理
		for(TimeResorce tr:this.addQuality){
			tr.timePassed();
			// 残り時間がなくなっていれば
			if(tr.getRemainingTime()<=0){
				deadResorce.add(tr);	// 削除用にポインタを取得しておく
			}
		}
		// 残り時間のなくなった追加品質をリストから削除する
		for(TimeResorce deadTr:deadResorce){
			this.addQuality.remove(deadTr);
		}
	}

	// ================================================================================
	// getter,setter
	// ================================================================================
	/**
	 * 作業効率を取得する
	 * @return
	 */
	public double getEfficiency() {
		// 初期作業効率に追加の作業効率を加算した値を最終的な作業効率とする
		double totalEfficiency = this.efficiency;
		// 追加の作業効率を加算する
		for (TimeResorce tr : this.addEfficiency) {
			totalEfficiency += tr.getAddValue();
		}
		return totalEfficiency;
	}

	/**
	 * 品質を取得する
	 * @return
	 */
	public double getQuality() {
		// 初期品質に追加の品質を加算した値を最終的な品質とする
		double totalQuality = this.quality;
		// 追加の品質を加算する
		for (TimeResorce tr : this.addQuality) {
			totalQuality += tr.getAddValue();
		}
		return totalQuality;
	}

	/**
	 * 効率の最小値
	 * @return
	 */
	public static double getEfficiencyMin(){
		return EFFICIENCY_MIN;
	}
	/**
	 * 効率の最大値
	 * @return
	 */
	public static double getEfficiencyMax(){
		return EFFICIENCY_MAX;
	}

	/**
	 * 品質の最小値
	 * @return
	 */
	public static double getQualityMin(){
		return QUALITY_MIN;
	}
	/**
	 * 品質の最大値
	 * @return
	 */
	public static double getQualityMax(){
		return EFFICIENCY_MAX;
	}

	/**
	 * ディープコピーメソッド
	 * @throws CloneNotSupportedException
	 */
	@Override
	public OperantResource clone() throws CloneNotSupportedException {
		// コピー用オペラントリソースの生成
		OperantResource operant = new OperantResource(this.efficiency,this.quality);
		// 追加される効率のコピー
		//operant.addEfficiency = new ArrayList<TimeResorce>(this.addEfficiency);
		// 追加される品質のコピー
		//operant.addQuality = new ArrayList<TimeResorce>(this.addQuality);

		return operant;
	}

	@Override
	public String toString(){
		String str = new String();
		str += "efficiency:"+this.efficiency+"\n";
		str += "quality"+this.quality;
		return str;
	}
}
