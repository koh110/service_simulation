package model.field;

import model.simulation.Simulation;

/**
 * 無限の大きさを持つセルを持つフィールドクラス
 * @author kohta
 *
 */
public class EternityField extends Field{
	public EternityField(Simulation sim,int sizeX, int sizeY) {
		super(sim,sizeX, sizeY);
	}

	/**
	 * セルの初期化
	 * @param sizeX
	 * @param sizeY
	 * @return
	 */
	@Override
	protected Cell initCell(int sizeX,int sizeY){
		return new EternityCell(sizeX, sizeY);
	}

	/**
	 * セルの両端を結合して無限にするクラス
	 * @author kohta
	 *
	 */
	public class EternityCell extends Cell{
		/**
		 * コンストラクタ
		 * @param sizeX
		 * @param sizeY
		 */
		public EternityCell(int sizeX, int sizeY) {
			super(sizeX, sizeY);
		}

		/**
		 * セルの情報を取得する
		 * @param x
		 * @param y
		 * @return
		 */
		public CellInfo getCell(int x,int y){
			// サイズを取得
			int sizeX = getSizeX();
			int sizeY = getSizeY();
			// 左端をオーバーしていたら
			// -2,-1->sizeX-2,sizeX-1
			if(x<0){
				while(x<0){
					x=sizeX+x;
				}
			}
			// 右端をオーバーしていたら
			// sizeX,sizeX+1->0,1
			else if(x>=sizeX){
				while(x>=sizeX){
					x=x-sizeX;
				}
			}
			// 上端をオーバーしていたら
			if(y<0){
				while(y<0){
					y=sizeY+y;
				}
			}
			// 下端をオーバーしていたら
			if(y>=sizeY){
				while(y>=sizeY){
					y=y-sizeY;
				}
			}
			return super.getCell(x, y);
		}
	}
}
