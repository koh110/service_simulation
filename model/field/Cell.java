package model.field;

import java.util.ArrayList;

import model.simulation.PassStepInterface;

/**
 * セルを扱うクラス
 *
 * @author kohta
 *
 */
public class Cell implements PassStepInterface{
	/**
	 * フィールドの配置を行うセル
	 */
	private CellInfo[][] cell;

	/**
	 * セルのx方向のサイズ
	 */
	private int sizeX;

	/**
	 * セルのy方向のサイズ
	 */
	private int sizeY;

	/**
	 * コンストラクタ
	 *
	 * @param sizeX
	 * @param sizeY
	 */
	public Cell(int sizeX, int sizeY) {
		// サイズの初期化
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		// 配列の初期化
		this.cell = new CellInfo[this.sizeX][this.sizeY];
		// 全てのセルの初期化
		for (int x = 0; x < this.sizeX; x++) {
			for (int y = 0; y < this.sizeY; y++) {
				// 対応する位置にnullオブジェクトを生成
				this.cell[x][y] = new CellInfo(x, y);
			}
		}
	}

	/**
	 * フィールドの何も配置されていないセルの座標を取得する
	 * @return 何も配置されていない場所のarrayList
	 */
	public ArrayList<CellInfo> serchNullCell(){
		// 空白管理配列の初期化
		ArrayList<CellInfo> nullCell = new ArrayList<CellInfo>();
		// セル全てを調べる
		for(int x=0;x<this.sizeX;x++){
			for(int y=0;y<this.sizeY;y++){
				// 調べるセル
				CellInfo crtCell = this.cell[x][y];
				if(crtCell.isNull()){	// その場所に何もいなければ
					if(!nullCell.contains(crtCell)){	// 空白管理配列にそのセルが記録されていなければ
						nullCell.add(crtCell);	// リストに追加
					}
				}
			}
		}

		return nullCell;
	}

	/**
	 * セルを取得する
	 * @param x
	 * @param y
	 * @return 対応する箇所のセルオブジェクト
	 * 			対応する場所がセルの外である場合OUT_OF_CELLを返す
	 */
	public CellInfo getCell(int x,int y){
		if(0<=x && x<this.sizeX && 0<=y && y<this.sizeY){
			return this.cell[x][y];
		}
		// セル外オブジェクトの生成
		//CellInfo outOfCell = new CellInfo(-1,-1);
		//outOfCell.setObjectType(CellInfo.ObjectType.OUT_OF_CELL);
		//return outOfCell;
		return null;
	}

	/**
	 * セルに情報をセットする
	 * @param cell
	 * @param x
	 * @param y
	 */
	public void setCell(CellInfo cell){
		this.cell[cell.getX()][cell.getY()] = cell;
	}

	/**
	 * セルにオブジェクトが置けるかどうか
	 * @param x
	 * @param y
	 * @return	セルに置ける場合trueを返す
	 * 			それ以外はfalseを返す
	 */
	public boolean isPutObject(int x,int y){
		// そのセルが空白であれば
		if(getCell(x,y).isNull()){
			return true;
		}
		return false;
	}

	/**
	 * オブジェクトを移動させる
	 * 移動出来ない場合何もせずにfalseを返して終了
	 * @param fromX	移動元
	 * @param fromY
	 * @param toX	移動先
	 * @param toY
	 * @return	移動成功した場合true
	 * 			失敗した場合false
	 */
	public boolean moveCellObject(int fromX,int fromY,int toX,int toY){
		if(!isPutObject(toX,toY)){	// 移動先に置けない場合
			return false;
		}
		// 移動元を取得
		CellInfo fromCell = getCell(fromX,fromY);
		// 移動先を取得
		CellInfo setCell = getCell(toX,toY);
		// 移動先のオブジェクトに移動元のオブジェクトをセットする
		setCell.setObject(fromCell.getObject());	// オブジェクトを移動先にセット
		setCell.setObjectType(fromCell.getType());	// オブジェクトのタイプを移動先にセット
		// 移動元のオブジェクトを初期化する
		fromCell.initCell();
		return true;
	}

	/**
	 * セルからオブジェクトを削除する
	 * @param x
	 * @param y
	 * @return 削除したオブジェクトのポインタ
	 */
	public void removeCellObject(int x,int y){
		// セルにnullオブジェクトを代入して削除する
		getCell(x, y).setObjectType(CellInfo.ObjectType.NULL);
		getCell(x, y).setObject(null);
	}

	/**
	 * x方向のサイズを取得する
	 * @return
	 */
	public int getSizeX(){
		return this.sizeX;
	}

	/**
	 * y方向のサイズを取得する
	 * @return
	 */
	public int getSizeY(){
		return this.sizeY;
	}

	/**
	 * セルの面積を取得
	 * @return
	 */
	public int getArea(){
		return this.sizeX*this.sizeY;
	}

	@Override
	public void passStep() {

	}
}
