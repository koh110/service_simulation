package model.agent;

import java.util.ArrayList;
import java.util.HashMap;

import model.field.CellInfo;
import model.field.Field;

/**
 * 移動にエネルギーを消費するエージェント
 * @author kohta
 *
 */
public class ConsumeEnergyAgent extends ConsumeServiceAgent{
	/**
	 * 移動にかかる時間
	 */
	private final double MOVE_TIME = 1.0;

	/**
	 * 実際に移動した際にかかる時間を一時的に保持しておく変数
	 * サービス消費行動に利用する
	 */
	private double moveTime;

	/**
	 * コンストラクタ
	 * @param cell
	 */
	public ConsumeEnergyAgent(CellInfo cell){
		super(cell);
		this.moveTime = 0;
	}

	/**
	 * そのセルまでの移動時間を計算する
	 * @param cell
	 * @return
	 */
	private double calcMoveTime(CellInfo cell){
		return distance(cell)*this.MOVE_TIME;
	}

	/**
	 * 引数で指定したセルへ移動する
	 *
	 * @param field
	 * @param destinationCell
	 */
	@Override
	public void move(Field field, CellInfo destinationCell) {
		// 行き先までの移動時間を一時的に保存
		this.moveTime = calcMoveTime(destinationCell);
		// 移動
		super.move(field, destinationCell);
	}

	/**
	 * 視界内で1番効率よく交換できる場所の探索
	 * サービスの評価方法はサービスの合計時間に移動時間(距離*移動にかかる時間)を加算したもの
	 *
	 * @param field 移動するフィールドインスタンス
	 * @return 交換するエージェントと位置を返す
	 */
	@Override
	protected HashMap<CellInfo, ArrayList<CellAgent>> moveTo(Field field) {
		// 現在の合計サービス価値
		double nowTotalValue = calcTotalServiceTime();
		// 視界内のセルを取得
		ArrayList<CellInfo> eyeSightCell = getCellInEyesight(field);

		// 今より1番大きい場所を保存しておく
		double betterTotalValue = nowTotalValue; // サービス価値
		ArrayList<CellInfo> cellList = new ArrayList<CellInfo>(); // サービス価値が同じ時保存する
		// 移動した位置に対応した交換するエージェントを保存する変数
		// 移動先をキーとするハッシュマップ
		HashMap<CellInfo, ArrayList<CellAgent>> cellMap = new HashMap<CellInfo, ArrayList<CellAgent>>();

		// 視界内の探索
		for (CellInfo serchCell : eyeSightCell) {
			// セルに何もない時
			// その場に移動して交換してみる
			if (serchCell.getType() == CellInfo.ObjectType.NULL) {
				// セルの座標を取得
				int x = serchCell.getX();
				int y = serchCell.getY();
				// x,yに移動した時に交換範囲にいるエージェントを取得する
				ArrayList<CellAgent> agentList = serchAgentOnExchange(field, x,y);
				// 探索範囲内にいたエージェント交換を行った場合の効率を計算
				CellAgent copyExchanged = ServiceExchange.ifExchange(this,agentList); // 交換を行った場合のコピーを取得
				// 探索先と交換した場合のサービス価値を計算
				double totalValue = copyExchanged.calcTotalServiceTime()+calcMoveTime(serchCell);

				// 1番交換の期待値が大きかった場所を記録
				if (betterTotalValue >= totalValue) { // 作業時間がより短い所
					if(betterTotalValue>totalValue){	// 今より短い場合
						betterTotalValue = totalValue;	// 値を更新
						cellList.clear(); // セルリストの初期化
						cellMap.clear(); // 交換するエージェントを保持する変数を初期化
					}
					CellInfo betterCell = field.getCell(x, y);
					cellList.add(betterCell);
					cellMap.put(betterCell, agentList);
				}
			}
		}
		// 今いる場所と期待値が一緒であれば移動候補なし
		if(betterTotalValue==nowTotalValue){
			return null;
		}
		else if (cellList.size() > 0) { // 移動先が見つかっている時
			return cellMap;
		}

		return null;
	}

	/**
	 * サービスの消費行動を行う
	 */
	@Override
	protected void doWork(){
		// 消費可能時間
		double limitTime = this.LIMIT_SERVICE_TIME+moveTime;
		// 消費可能時間まで消費する
		consumeService(limitTime);
	}
}
