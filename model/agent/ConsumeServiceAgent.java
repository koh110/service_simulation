package model.agent;

import model.field.CellInfo;
import model.service.Service;
import model.service.ServiceList;

/**
 * サービスの消費行動を行うセルエージェント
 * @author kohta
 *
 */
public class ConsumeServiceAgent extends CellAgent{
	/**
	 * 作業可能な上限時間
	 */
	public final double LIMIT_SERVICE_TIME = 12.0;

	/**
	 * コンストラクタ
	 * @param cell
	 */
	public ConsumeServiceAgent(CellInfo cell) {
		super(cell);
	}

	/**
	 * 1ステップ経過時に行う処理
	 */
	public void passStep(){
		// 1つ前の状態を保存する
		//this.preServiceList = this.serviceList.clone();

		// サービスの消費行動
		doWork();

		// 持っているサービス全てに1ステップ時の処理をさせる
		getServiceList().passStep();
	}

	/**
	 * サービスの消費行動を行う
	 */
	protected void doWork() {
		// 消費可能時間
		double limitTime = this.LIMIT_SERVICE_TIME;
		// 消費可能時間まで消費する
		consumeService(limitTime);
	}

	/**
	 * 消費可能時間まで実時間を消費する
	 * @param limitTime
	 */
	protected void consumeService(double limitTime) {
		// 作業時間順にソートされたサービスリストを取得する
		ServiceList sListSortByWorkTime = getServiceList().getSortedServiceListByWorkTime();

		// 実時間を消費する
		int size = sListSortByWorkTime.getSize();
		for(int i=0;i<size;i++){
			// 作業時間の高い順にサービスを確認
			Service service = sListSortByWorkTime.get(i);
			// 実時間の消費
			double odd = service.doWork(limitTime);
			limitTime = odd;	// 消費して余った値に消費可能時間を更新
			if(odd<=0){	// あまりの実時間がなくなった時
				break;
			}
		}
	}
}
