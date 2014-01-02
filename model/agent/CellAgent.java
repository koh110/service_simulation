package model.agent;

import java.util.ArrayList;
import java.util.HashMap;

import util.MyUtil;

import model.field.CellInfo;
import model.field.CellInfoInterface;
import model.field.CellScope;
import model.field.Field;
import model.service.ServiceList;
import model.simulation.PassStepInterface;

/**
 * セルシミュレーション用のエージェント
 *
 * @author kohta
 *
 */
public class CellAgent extends Agent implements PassStepInterface,
		CellInfoInterface, Cloneable {
	/**
	 * エージェントの視界
	 */
	private CellScope eyeSightScope;

	/**
	 * 交換可能範囲
	 */
	private CellScope exchangeScope;

	/**
	 * このオブジェクトが配置されているセルのポインタ
	 */
	private CellInfo cell;

	/**
	 * 交換の履歴
	 */
	private ArrayList<CellAgent> history;

	/**
	 * コンストラクタ
	 */
	public CellAgent(CellInfo cell) {
		this(ServiceList.createServiceList(), cell);
	}

	public CellAgent(ServiceList serviceList, CellInfo cell) {
		super(serviceList);
		// セルポインタの初期化
		this.cell = cell;
		this.cell.setObject(this);
		// 視界の初期化
		// this.eyesight = 7;
		this.eyeSightScope = new CellScope(7, this);
		// 交換可能距離の初期化
		this.exchangeScope = new CellScope(3, this);
		// 交換履歴の初期化
		this.history = new ArrayList<CellAgent>();
	}

	/**
	 * フィールドでの動き
	 *
	 * @param field
	 */
	public void action(Field field) {
		// 移動先の候補を取得
		HashMap<CellInfo,ArrayList<CellAgent>> cellMap = moveTo(field);
		if(cellMap == null){	// 移動先がなければ終了
			return ;
		}
		// 移動先の候補から乱数で移動先を決定
		ArrayList<CellInfo> cellList = new ArrayList<CellInfo>();	// 移動先を全て取得するリスト
		for(CellInfo cell:cellMap.keySet()){
			cellList.add(cell);
		}
		// 移動可能先の数分の乱数発生
		int randNum = (int) MyUtil.random(0, cellList.size() - 1);
		// 乱数で得た移動先を取得
		CellInfo moveCell = cellList.get(randNum);
		// 移動させる
		move(field,moveCell);
		// 対応する場所で交換できるエージェント
		ArrayList<CellAgent> agentList = cellMap.get(moveCell);

		//double preServiceTime = calcTotalServiceTime();

		// 交換
		exchange(agentList);

		//System.out.println("CellAgent,action:[before:"+preServiceTime+",after:"+calcTotalServiceTime()+"]");
	}

	/**
	 * 交換を行う
	 * @param agentList 交換するエージェントのリスト
	 */
	protected void exchange(ArrayList<CellAgent> agentList) {
		ServiceExchange.exchange(this,agentList); // 自分と相手の交換を行う
		this.history=agentList;	// 交換の履歴を保存
	}

	/**
	 * 視界内で1番効率よく交換できる場所の探索
	 *
	 * @param field
	 *            移動するフィールドインスタンス
	 * @return 交換するエージェントと位置を返す
	 */
	protected HashMap<CellInfo, ArrayList<CellAgent>> moveTo(Field field) {
		// 現在の合計サービス価値
		double nowTotalValue = calcTotalServiceTime();
		// 視界内のセルを取得
		ArrayList<CellInfo> eyeSightCell = getCellInEyesight(field);

		// 今より1番大きい場所を保存しておく
		double betterTotalValue = nowTotalValue; // 合計作業時間
		ArrayList<CellInfo> cellList = new ArrayList<CellInfo>(); // 合計作業時間が同じ時保存する
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
				double totalValue = copyExchanged.calcTotalServiceTime(); // サービスの合計時間を計算

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
		//if(cellMap.keySet().contains(this.cell)){
		if(betterTotalValue==nowTotalValue){
			return null;
		}
		else if (cellList.size() > 0) { // 移動先が見つかっている時
			return cellMap;
		}

		return null;
	}

	/**
	 * 引数で指定したセルへ移動する
	 *
	 * @param field
	 * @param destinationCell
	 */
	public void move(Field field, CellInfo destinationCell) {
		// 移動させる
		int crtX = this.cell.getX();
		int crtY = this.cell.getY();
		field.moveCellObject(crtX, crtY, destinationCell.getX(),
				destinationCell.getY());
	}

	/**
	 * x,yにいる時の範囲内にいるエージェントを取得
	 *
	 * @param field
	 * @param x
	 * @param y
	 * @param scope
	 *            探索する範囲
	 * @return
	 */
	private ArrayList<CellAgent> serchAgentOnScope(Field field, int x, int y,
			CellScope scope) {
		// 返却用リスト
		ArrayList<CellAgent> agentList = new ArrayList<CellAgent>();
		// 範囲内のセルの取得
		ArrayList<CellInfo> scopeCell = scope.searchCell(field, x, y);
		// 範囲内のエージェントを探索
		for (CellInfo cell : scopeCell) {
			// オブジェクトがエージェントであれば
			if (cell.getType() == CellInfo.ObjectType.AGENT) {
				agentList.add((CellAgent) cell.getObject());
			}
		}
		return agentList;
	}

	/**
	 * 自分の現在地の視界の範囲にいるエージェントを探す
	 *
	 * @param field
	 * @return
	 */
	public ArrayList<CellAgent> searchAgentOnEyesight(Field field) {
		return searchAgentOnEyesight(field, this.cell.getX(), this.cell.getY());
	}

	/**
	 * 自分がx,yに移動した時に視界内にいるエージェントを探す
	 *
	 * @param field
	 * @param x
	 * @param y
	 * @return
	 */
	public ArrayList<CellAgent> searchAgentOnEyesight(Field field, int x, int y) {
		return serchAgentOnScope(field, x, y, this.eyeSightScope);
	}

	/**
	 * 自分の交換可能範囲にいるエージェントを探す
	 *
	 * @param field
	 * @return
	 */
	public ArrayList<CellAgent> searchAgentOnExchange(Field field) {
		return serchAgentOnExchange(field, this.cell.getX(), this.cell.getY());
	}

	/**
	 * x,yに移動した時に交換可能範囲にいるエージェントを探す
	 *
	 * @param field
	 * @param x
	 * @param y
	 * @return
	 */
	public ArrayList<CellAgent> serchAgentOnExchange(Field field, int x, int y) {
		return serchAgentOnScope(field, x, y, this.exchangeScope);
	}

	/**
	 * 相手とのx方向距離を取得
	 *
	 * @param agent
	 * @return
	 */
	public double distanceX(CellAgent agent) {
		return distanceX(this.getX(), agent);
	}

	/**
	 * 指定したx座標と引数とのx座標の距離
	 *
	 * @param x
	 * @param agent
	 * @return
	 */
	private double distanceX(int x, CellAgent agent) {
		return Math.abs(x - agent.getX());
	}

	/**
	 * 相手とのy方向距離を取得
	 *
	 * @param agent
	 * @return
	 */
	public double distanceY(CellAgent agent) {
		return distanceY(this.cell.getY(), agent);
	}

	/**
	 * 指定したy座標とと引数とのy座標の距離
	 *
	 * @param y
	 * @param agent
	 * @return
	 */
	private double distanceY(int y, CellAgent agent) {
		return Math.abs(y - agent.getY());
	}

	/**
	 * 相手との距離を返す
	 * @param agent
	 * @return
	 */
	public double distance(CellAgent agent){
		return distance(agent.getCellFieldPointer());
	}

	/**
	 * セルとの距離を返す
	 * @param cell
	 * @return
	 */
	public double distance(CellInfo cell){
		double x = this.cell.getX()-cell.getX();
		double y = this.cell.getY()-cell.getY();
		return Math.sqrt(x*x+y*y);
	}

	/**
	 * 履歴を取得する
	 * @return
	 */
	public ArrayList<CellAgent> getHistory(){
		return this.history;
	}

	/**
	 * x座標の取得
	 *
	 * @return
	 */
	public int getX() {
		return this.cell.getX();
	}

	/**
	 * y座標の取得
	 *
	 * @return
	 */
	public int getY() {
		return this.cell.getY();
	}

	/**
	 * 視界の範囲を取得
	 *
	 * @return
	 */
	public CellScope getEyesightScope() {
		return this.eyeSightScope;
	}

	/**
	 * 交換の範囲を取得
	 *
	 * @return
	 */
	public CellScope getExchangeScope() {
		return this.exchangeScope;
	}

	/**
	 * 視界内にあるセルを取得
	 *
	 * @param field
	 * @return
	 */
	public ArrayList<CellInfo> getCellInEyesight(Field field) {
		return this.eyeSightScope.searchCell(field);
		// return getCellInScope(field, this.cell.getX(),
		// this.cell.getY(),this.eyesight);
	}

	/**
	 * 交換可能範囲内にあるセルを取得
	 *
	 * @param field
	 * @return
	 */
	public ArrayList<CellInfo> getCellInExchange(Field field) {
		return this.exchangeScope.searchCell(field);
		// return getCellInScope(field, this.cell.getX(),
		// this.cell.getY(),this.exchangeDistance);
	}

	/**
	 * サービスの交換価値を計算する
	 * 子クラスでオーバーライドして利用する
	 * 標準は単なるサービスの合計作業時間
	 * @return
	 */
	public double calcServiceValue(){
		return calcTotalServiceTime();
	}

	/**
	 * 1ステップ経過時に行う処理
	 */
	@Override
	public void passStep() {
		super.passStep();
	}

	/**
	 * ディープコピーメソッド
	 */
	@Override
	public CellAgent clone() {
		// セルのコピーを作成
		CellInfo cell = this.cell.clone();

		// コピー用エージェントの生成
		CellAgent agent = new CellAgent(getServiceList().clone(), cell);

		// agent.setCellFieldPointer(cell);
		// cell.setObject(agent);

		return agent;
	}

	@Override
	public void setCellFieldPointer(CellInfo cellField) {
		this.cell = cellField;
	}

	@Override
	public CellInfo getCellFieldPointer() {
		return this.cell;
	}

	@Override
	public String toString() {
		String str = new String();
		str += "[x:" + this.cell.getX() + "y:" + this.cell.getY() + "]\n";
		str += super.toString();
		return str;
	}
}