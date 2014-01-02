package model.field;

import java.util.ArrayList;

import model.agent.CellAgent;

/**
 * セルの範囲を管理するクラス
 *
 * @author kohta
 *
 */
public class CellScope {
	/**
	 * 範囲の中心エージェント
	 */
	private CellAgent agent;

	/**
	 * 範囲の大きさ
	 */
	private int scopeSize;

	/**
	 * コンストラクタ
	 *
	 * @param scope
	 *            範囲の大きさ
	 * @param cell
	 *            範囲を計算する中心のセル
	 */
	public CellScope(int scope, CellAgent agent) {
		// スコープの大きさを初期化
		this.scopeSize = scope;
		// スコープの中心セルを初期化
		this.agent = agent;
	}

	/**
	 * x,yに移動した時の範囲内にいるエージェントを取得
	 *
	 * @param field
	 * @param x
	 * @param y
	 * @return
	 */
	public ArrayList<CellInfo> searchCell(Field field, int x, int y) {
		//return serchCellRectangle(field, x, y);
		return searchCellCircle(field, x, y);
	}

	/**
	 * x,yに移動した時の範囲内にいるエージェントを円形に探索
	 * @param field
	 * @param x
	 * @param y
	 * @return
	 */
	public ArrayList<CellInfo> searchCellCircle(Field field, int x, int y) {
		// セル管理リストを初期化
		ArrayList<CellInfo> cellInScope = new ArrayList<CellInfo>();
		// セル管理リストに範囲内のセルを記録
		// 周囲から半径分の矩形空間を探索
		for (int serchX = x - this.scopeSize + 1; serchX < x + this.scopeSize; serchX++) {
			for (int serchY = y - this.scopeSize + 1; serchY < y
					+ this.scopeSize; serchY++) {
				// 対象のセルを取得
				CellInfo cell = field.getCell(serchX, serchY);
				if (cell != null) { // 対象のセルが存在
					int distX = x-serchX;
					int distY = y-serchY;
					// 円形の範囲内
					if(distX*distX+distY*distY <= this.scopeSize*this.scopeSize){
						cellInScope.add(cell);
					}
				}
			}
		}
		return cellInScope;
	}

	/**
	 * x,yに移動した時の範囲内にいるエージェントを矩形に探索
	 *
	 * @param field
	 * @param x
	 * @param y
	 * @return
	 */
	public ArrayList<CellInfo> searchCellRectangle(Field field, int x, int y) {
		// セル管理リストを初期化
		ArrayList<CellInfo> cellInScope = new ArrayList<CellInfo>();
		// セル管理リストに範囲内のセルを記録
		for (int searchX = x - this.scopeSize + 1; searchX < x + this.scopeSize; searchX++) {
			for (int searchY = y - this.scopeSize + 1; searchY < y
					+ this.scopeSize; searchY++) {
				// 対象のセルを取得
				CellInfo cell = field.getCell(searchX, searchY);
				if (cell != null) { // 対象のセルが存在すれば
					cellInScope.add(cell);
				}
			}
		}
		return cellInScope;
	}

	/**
	 * スコープの中に存在するセルを再計算する
	 */
	public ArrayList<CellInfo> searchCell(Field field) {
		return searchCell(field, this.agent.getX(), this.agent.getY());
	}

	public String toString() {
		return this.agent.getCellFieldPointer().toString();
	}
}
