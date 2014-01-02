package model.field;

import model.agent.CellAgent;

/**
 * セルの情報を扱うクラス
 * @author kohta
 *
 */
public class CellInfo implements Cloneable{
	/**
	 * フィールドに配置されるオブジェクトの種類を扱う列挙型
	 */
	public static enum ObjectType{
		AGENT,	// エージェントを現すオブジェクト
		NULL,	// 何も入ってない
	}

	/**
	 * セルに配置されるデータのポインタ
	 */
	private CellInfoInterface content;

	/**
	 * セルに配置されるデータの種類
	 */
	private ObjectType type;

	/**
	 * セル上のx座標
	 */
	private int x;

	/**
	 * セル上のy座標
	 */
	private int y;

	/**
	 * コンストラクタ
	 * @param x	x座標の値
	 * @param y	y座標の値
	 */
	public CellInfo(int x,int y){
		// セルに配置されるデータの初期化
		this.content = null;
		this.type = ObjectType.NULL;
		// 座標の初期化
		this.x=x;
		this.y=y;
	}

	/**
	 * 引数のセルとの距離を取得
	 * @param cell
	 * @return
	 */
	public double distance(CellInfo cell){
		double distanceX = this.x - cell.getX();
		double distanceY = this.y - cell.getY();
		return Math.sqrt(distanceX*distanceX + distanceY*distanceY);
	}

	//================================================================================
	// getter,setter
	//================================================================================
	/**
	 * オブジェクトのX座標を取得する
	 * @retun x座標の整数値
	 */
	public int getX(){
		return this.x;
	}

	/**
	 * オブジェクトのY座標を取得する
	 * @return y座標の整数値
	 */
	public int getY(){
		return this.y;
	}

	/**
	 * オブジェクトの種類を取得する
	 * @return
	 */
	public ObjectType getType(){
		return this.type;
	}

	/**
	 * オブジェクトの種類を設定する
	 * @param type	セットするオブジェクトの種類
	 */
	public void setObjectType(ObjectType type){
		this.type = type;
	}

	/**
	 * そのセルに配置されるオブジェクトのポインタを設定する
	 * @param object オブジェクトのポインタの設定
	 */
	public void setObject(CellInfoInterface object){
		this.content = object;
		object.setCellFieldPointer(this);
		if(object instanceof CellAgent){
			this.type = ObjectType.AGENT;
		}else{
			this.type = ObjectType.NULL;
		}
	}

	/**
	 * セルのオブジェクトを取得する
	 * @return そこにセットされているオブジェクト
	 */
	public CellInfoInterface getObject(){
		return this.content;
	}

	/**
	 * そのセルが空の状態かどうか
	 * @return 空の時trueを返す。それ以外の時falseを返す
	 */
	public boolean isNull(){
		if(this.type==ObjectType.NULL){
			return true;
		}
		return false;
	}

	/**
	 * そのセルをnullで初期化する
	 */
	public void initCell(){
		this.content = null;
		this.type = ObjectType.NULL;
	}

	/**
	 * コピーメソッド
	 * 中に持つオブジェクトはただのポインタ
	 * それ以外はディープコピー
	 */
	@Override
	public CellInfo clone() {
		// コピー用セルの生成
		CellInfo cell = new CellInfo(this.x,this.y);
		// 中のオブジェクトをセット
		cell.content = this.content;
		// セルのタイプをセット
		cell.type = this.type;
		return cell;
	}

	@Override
	public String toString(){
		return "[x:"+this.x+",y:"+this.y+"]";
	}
}
