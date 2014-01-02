package model.service;

import model.simulation.PassStepInterface;

/**
 * サービスを扱うクラス
 *
 * @author kohta
 *
 */
public class Service implements PassStepInterface, Cloneable {
	/**
	 * サービスのオペラントリソース(効率・品質)
	 */
	private OperantResource operantResource;

	/**
	 * サービスの作業時間
	 */
	private double workTime;

	/**
	 * 1ステップに回復するサービスの作業時間
	 */
	private double recoveryWorkTime;

	/**
	 * このサービスにおける交換時の優先度
	 */
	private int priority;

	/**
	 * サービスの種類を示す
	 */
	ServiceType.Services type;

	/**
	 * コンストラクタ
	 *
	 * @param type
	 *            サービスの種類
	 */
	public Service(ServiceType.Services type) {
		this(type, new OperantResource());
	}

	/**
	 * コンストラクタ
	 * @param type
	 * @param operantResource
	 */
	public Service(ServiceType.Services type, OperantResource operantResource) {
		// サービスの種類の設定
		this.type = type;
		// オペラントリソースの初期化
		this.operantResource = operantResource;
		// 作業時間の初期化
		this.workTime = 10.0;
		// 回復作業時間の初期化
		this.recoveryWorkTime = 5.0;
		// 優先度の初期化
		initPriority();
	}

	/**
	 * サービス交換優先度の初期化
	 */
	public void initPriority() {
		priority = 0;
	}

	/**
	 * 同じサービスかどうか
	 * @param service
	 * @return
	 */
	private boolean equals(Service service){
		if(this.type == service.getServiceType()){
			return true;
		}
		return false;
	}
	public boolean equals(Object obj){
		return equals((Service)obj);
	}

	/**
	 * 1ステップ経過時の処理
	 */
	public void passStep() {
		// 作業時間に経過時間を加算する
		this.workTime += this.recoveryWorkTime;
	}

	/**
	 * 現在のサービスの評価値を返す。
	 * 高いほど保持したいサービス。
	 * @return
	 */
	public double eval(){
		double value=-1;
		double serviceTime = calcServiceTime();
		value = 1/serviceTime;
		return value;
	}

	/**
	 * 引数に与えたサービスの実時間の場合、作業時間はいくつになるかを計算する。
	 * @param serviceTime
	 * @return 引数に与えた実時間を作業時間に変換した値
	 */
	private double calcWorkTime(double serviceTime){
		return serviceTime*this.operantResource.getEfficiency();
	}

	/**
	 * 実時間分の作業をする
	 * @param serviceTime 実時間
	 * @return 消費して余った実時間
	 */
	public double doWork(double serviceTime){
		// 消費する作業時間の計算
		double doWorkTime = calcWorkTime(serviceTime);
		// 作業時間が残らなかった場合
		if(this.workTime-doWorkTime<0){
			double preWorkTime = this.workTime;	// 余った作業時間を保存
			this.workTime = 0;	// 作業時間を消費
			return calcServiceTime(doWorkTime-preWorkTime);
		}
		// 作業時間が残っている場合
		this.workTime -= doWorkTime;
		return 0;
	}

	/**
	 * サービスの実時間を計算する。 作業時間を作業効率で割ったもの
	 *
	 * @return
	 */
	public double calcServiceTime() {
		return this.workTime / this.operantResource.getEfficiency();
	}

	/**
	 * 引数に与えた作業時間の場合、サービスの実時間がいくつになるかを計算する
	 * @param workTime
	 * @return
	 */
	public double calcServiceTime(double workTime){
		return workTime / this.operantResource.getEfficiency();
	}

	/**
	 * 引数に与えた時間を差し引いたサービスの実時間を計算する。 作業時間を作業効率で割ったもの
	 * @param time
	 * @return
	 */
	public double calcServiceTimeSub(double time){
		return (this.workTime-time)/this.operantResource.getEfficiency();
	}
	/**
	 * 引数に与えた時間を加算したサービスの実時間を計算する。 作業時間を作業効率で割ったもの
	 * @param time
	 * @return
	 */
	public double calcServiceTimeAdd(double time){
		return (this.workTime+time)/this.operantResource.getEfficiency();
	}

	// ================================================================================
	// getter,setter
	// ================================================================================
	/**
	 * サービスの効率を取得
	 *
	 * @return 効率 値が大きいほど効率が高い
	 */
	public OperantResource getOperantResource() {
		return this.operantResource;
	}

	/**
	 * そのサービスにおける交換の優先度を取得する
	 *
	 * @return 優先度 値が大きいほど優先度が高い
	 */
	public int getPriority() {
		return this.priority;
	}

	/**
	 * そのサービスにおける作業時間を取得する
	 *
	 * @return
	 */
	public double getWorkTime() {
		return this.workTime;
	}

	/**
	 * そのサービスにおける作業時間を設定する
	 *
	 * @param workTime
	 */
	public void setWorkTime(double workTime) {
		this.workTime = workTime;
	}

	/**
	 * そのサービスにおける作業時間を加算する
	 * @param workTime
	 */
	public void addWorkTime(double workTime){
		this.workTime += workTime;
	}

	/**
	 * サービスの種類を取得する
	 *
	 * @return
	 */
	public ServiceType.Services getServiceType() {
		return this.type;
	}

	/**
	 * 交換可能かどうか
	 *
	 * @return
	 */
	public boolean isExchangeable() {
		if (this.workTime <= 0) {
			return false;
		}
		return true;
	}

	/**
	 * ディープコピーメソッド
	 *
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Service clone() throws CloneNotSupportedException {
		// コピー用サービスの生成
		Service service = new Service(this.type,this.operantResource.clone());
		service.setWorkTime(this.workTime);
		service.priority=this.priority;
		return service;
	}

	@Override
	public String toString(){
		String str = new String();
		str += "["+this.type+"]\n";
		str += this.operantResource.toString()+"\n";
		str += "work time:"+this.workTime+"\n";
		str += "priority:"+this.priority;
		return str;
	}
}
