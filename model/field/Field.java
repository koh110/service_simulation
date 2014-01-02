package model.field;

import java.util.ArrayList;

import model.agent.CellAgent;
import model.simulation.PassStepInterface;
import model.simulation.Simulation;

import util.MyUtil;

/**
 * シミュレーションを行うフィールドを扱うクラス セルプログラミング
 *
 * @author kohta
 *
 */
public class Field implements PassStepInterface{
	/**
	 * エージェントのリスト
	 */
	private ArrayList<CellAgent> agentList;

	/**
	 * フィールドの配置を扱うセル
	 */
	private Cell cell;

	/**
	 * エージェントのセルに占める割合(%)
	 */
	private double AGENT_PERCENTAGE = 5;

	/**
	 * コンストラクタ
	 *@param sim このフィールドが使われるシミュレーション
	 *
	 * @param sizeX
	 *            シミュレーション用セルのX方向のサイズ
	 * @param sizeY
	 *            シミュレーション用セルのY方向のサイズ
	 */
	public Field(Simulation sim,int sizeX, int sizeY) {
		// セルの初期化
		this.cell = initCell(sizeX, sizeY);
		// エージェントリストの初期化
		initAgentList(sim);
	}

	/**
	 * セルの初期化
	 *
	 * @param sizeX
	 * @param sizeY
	 * @return
	 */
	protected Cell initCell(int sizeX, int sizeY) {
		return new Cell(sizeX, sizeY);
	}

	/**
	 * エージェントリストの初期化
	 */
	public void initAgentList(Simulation sim) {
		// エージェントリストの初期化
		this.agentList = new ArrayList<CellAgent>();
		// 空白のセルを取得
		ArrayList<CellInfo> nullCell = this.cell.serchNullCell();
		// エージェントの生成
		for (int i = 0; i < getAgentNum(); i++) {
			// 何も配置されていない場所から乱数で1箇所配置するセルを取得
			int index = (int) MyUtil.random(0, nullCell.size() - 1); // 空白セルからランダムで取得する場所
			CellInfo setCell = nullCell.get(index); // セルを取得
			CellAgent agent = sim.createCellAgent(setCell);
			this.agentList.add(agent);
			// 空白セルから配置した場所を削除
			nullCell.remove(index);
		}
	}

//	/**
//	 * エージェントの初期配置
//	 */
//	public void initSetAgent() {
//		// 空白のセルを取得
//		ArrayList<CellInfo> nullCell = this.cell.serchNullCell();
//
//		// エージェントをセルに配置
//		for (CellAgent agent : this.agentList) {
//			// 何も配置されていない場所から乱数で1箇所配置するセルを取得
//			int index = (int) MyUtil.random(0, nullCell.size() - 1); // 空白セルからランダムで取得する場所
//			CellInfo setCell = nullCell.get(index); // セルを取得
//			// エージェントをセルに配置
//			setAgent(agent, setCell.getX(), setCell.getY());
//			// 空白セルから配置した場所を削除
//			nullCell.remove(index);
//		}
//	}

	// ================================================================================
	// getter,setter
	// ================================================================================
	/**
	 * セルの情報を取得する
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public CellInfo getCell(int x, int y) {
		return this.cell.getCell(x, y);
	}

	/**
	 * エージェントをセルに配置する処理
	 *
	 * @param agent
	 *            セットするエージェント
	 * @param x
	 *            セットするx座標
	 * @param y
	 *            セットするy座標
	 */
	public void setAgent(CellAgent agent, int x, int y) {
		// セットするセルの取得
		CellInfo setCell = this.cell.getCell(x, y);
		// セルにオブジェクトを配置
		setCell.setObject(agent);
	}

	/**
	 * オブジェクトを移動させる 移動出来ない場合何もせずにfalseを返して終了
	 *
	 * @param fromX
	 *            移動元
	 * @param fromY
	 * @param toX
	 *            移動先
	 * @param toY
	 * @return 移動成功した場合true 失敗した場合false
	 */
	public boolean moveCellObject(int fromX, int fromY, int toX, int toY) {
		return this.cell.moveCellObject(fromX, fromY, toX, toY);
	}

	/**
	 * エージェントのリストを取得
	 *
	 * @return
	 */
	public ArrayList<CellAgent> getAgentList() {
		return this.agentList;
	}

	/**
	 * x方向の大きさの取得
	 *
	 * @return
	 */
	public int getSizeX() {
		return this.cell.getSizeX();
	}

	/**
	 * y方向の大きさを取得
	 *
	 * @return
	 */
	public int getSizeY() {
		return this.cell.getSizeY();
	}

	/**
	 * セルの大きさを取得
	 *
	 * @return
	 */
	public int getArea() {
		return this.cell.getArea();
	}

	/**
	 * エージェントの数を取得 セル面積にエージェントのセルに占める割合を掛けたもの
	 *
	 * @return
	 */
	public int getAgentNum() {
		return (int) (getArea() * AGENT_PERCENTAGE / 100);
	}

	/**
	 * 引数のエージェントがエージェントリストの何番目に存在するかを返す
	 *
	 * @param agent
	 * @return
	 */
	public int indexOf(CellAgent agent) {
		return this.agentList.indexOf(agent);
	}

	@Override
	public void passStep() {
		this.cell.passStep();
	}
}
