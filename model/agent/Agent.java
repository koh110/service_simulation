package model.agent;

import model.service.Service;
import model.service.ServiceList;
import model.simulation.PassStepInterface;

/**
 * エージェントを扱うクラス
 * サービスを持つ
 * @author kohta
 *
 */
public class Agent implements PassStepInterface,Cloneable{
	/**
	 *  サービスのリスト
	 */
	private ServiceList serviceList;

	/**
	 * 一つ前の状態のサービスリスト
	 */
	private ServiceList preServiceList;

	/**
	 *  コンストラクタ
	 */
	public Agent(){
		// サービスリストの初期化
		this(ServiceList.createServiceList());
	}
	public Agent(ServiceList serviceList){
		this.serviceList = serviceList;
		this.preServiceList = serviceList;
	}

	/**
	 * サービスのリストを初期化する
	 */
	public void initServiceList(){
		this.serviceList = new ServiceList();
	}

	/**
	 * サービスをサービスのリストに加える
	 * @param service
	 */
	public void addService(Service service){
		this.serviceList.add(service);
	}

	/**
	 * サービスのリストを取得する
	 * @return
	 */
	public ServiceList getServiceList(){
		return this.serviceList;
	}

	/**
	 * 一つ前のサービスのリストを取得する
	 * @return
	 */
	public ServiceList getPreServiceList(){
		return this.preServiceList;
	}

	/**
	 * サービスリストを設定する
	 * @param list
	 */
	protected void setServiceList(ServiceList list){
		this.serviceList = list;
	}

	/**
	 * サービスの合計実作業時間を求める
	 * @return
	 */
	public double calcTotalServiceTime(){
		return this.serviceList.calcTotalServiceTime();
	}

	/**
	 * 1ステップ経過時に行う処理
	 */
	public void passStep(){
		// 一つ前の状態を保存する
		this.preServiceList = this.serviceList.clone();
		// 持っているサービス全てに1ステップ時の処理をさせる
		this.serviceList.passStep();
	}

	/**
	 * ディープコピーメソッド
	 */
	@Override
	public Agent clone(){
		// 現在持っているサービスリストのクローンを生成する
		ServiceList cloneList = this.serviceList.clone();
		// コピー用エージェントの生成
		Agent agent = new Agent(cloneList);

		return agent;
	}

	@Override
	public String toString(){
		String str = new String();
		str += this.serviceList.toString();
		return str;
	}

}
