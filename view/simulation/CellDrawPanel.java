package view.simulation;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * セルを描画するパネル
 * @author kohta
 *
 */
public class CellDrawPanel extends DrawPanel{
	private static final long serialVersionUID = 1L;
	/**
	 * 描画開始x地点
	 */
	private final double startX = 10;
	/**
	 * 描画開始y地点
	 */
	private final double startY = 10;

	/**
	 * セルのX方向の大きさ
	 */
	private int cellNumX;
	/**
	 * セルのY方向の大きさ
	 */
	private int cellNumY;

	/**
	 * クリックされた位置を取得
	 */
	private Point point;

	/**
	 * セルの大きさの初期値
	 */
	private final double DEF_CELL_SIZE = 10.0;

	/**
	 * コンストラクタ
	 * @param lengthX x方向のセルの数
	 * @param lengthY y方向のセルの数
	 */
	public CellDrawPanel(int lengthX,int lengthY){
		super();
		this.cellNumX = lengthX;
		this.cellNumY = lengthY;
		// ポイントの初期化
		this.point = new Point();

		// マウスイベントを追加
		this.addMouseListener(new DrawListener());
	}

	/**
	 * 表示ルーチン イメージバッファの内容を全面的にコピーする
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// フィールドを描画する
		drawCell(g);
	}

	/**
	 * セルを描画する
	 * @param g
	 */
	public void drawCell(Graphics g){
		// 1マスの大きさを計算
		double length = calcCellLength();

		// x方向の線を描く
		int fSizeX = this.cellNumX;
		for (int i = 0; i <= fSizeX; i++) {
			drawLine(g, startX, startY + length * i, startX + length * fSizeX,
					startY + length * i);
		}

		// y方向の線を描く
		int fSizeY = this.cellNumY;
		for (int i = 0; i <= fSizeY; i++) {
			drawLine(g, startX + length * i, startY, startX + length * i,
					startY + length * fSizeY);
		}
	}

	/**
	 * セルのサイズを計算する
	 * @return
	 */
	public double calcCellLength(){
		// パネルの大きさ
		double panelWidth = this.getSize().width;
		// パネルの大きさ
		double panelHeight = this.getSize().height;

		// 1マスのx方向の大きさ
		double lengthX = (panelWidth - this.startX * 2) / this.cellNumX;
		// 1マスのy方向の大きさ
		double lengthY = (panelHeight - this.startY * 2) / this.cellNumY;

		// 1セルの大きさ
		// x,yの大きさの小さいほうに合わせる
		double length;
		if (lengthX > lengthY) {
			length = lengthY;
		} else {
			length = lengthX;
		}

		// 正の大きさの時のみ
		if(length>0){
			return length;
		}
		// それ以外の時はデフォルトの大きさで出力
		return this.DEF_CELL_SIZE;
	}

	/**
	 * クリックされたセルの位置を取得
	 * @return
	 */
	public Point getClickedCellPoint(){
		// セルの大きさの取得
		double cellSize = calcCellLength();
		int cellPointX = (int)((this.point.getX() - this.startX) / cellSize);
		int cellPointY = (int)((this.point.getY() - this.startY) / cellSize);
		return new Point(cellPointX,cellPointY);
	}

	/**
	 * クリックしているセルを強制的にセットする
	 * @param cellX 強制的にセットするセルのx座標
	 * @param cellY 強制的にセットするセルのy座標
	 */
	public void setClickPoint(int cellX,int cellY){
		double cellSize = calcCellLength(); // セルの大きさを取得
		// 選択セルの座標を相対座標に変換
		double x = cellX * cellSize + getDrawStartPointX();
		double y = cellY * cellSize + getDrawStartPointY();
		this.point.setLocation(x, y);
	}

	/**
	 * x方向の描画開始地点を取得
	 * @return
	 */
	public double getDrawStartPointX(){
		return this.startX;
	}

	/**
	 * y方向の描画開始地点を取得
	 * @return
	 */
	public double getDrawStartPointY(){
		return this.startY;
	}

	public int getCellSizeX(){
		return this.cellNumX;
	}

	public int getCellSizeY(){
		return this.cellNumY;
	}

	/**
	 * マウスイベントを処理するクラス
	 *
	 * @author kohta
	 */
	private class DrawListener extends MouseAdapter {
		/**
		 * マウス押下時のイベント
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			// 相対位置を取得
			Point p = e.getPoint();
			point = p;
		}
	}
}
