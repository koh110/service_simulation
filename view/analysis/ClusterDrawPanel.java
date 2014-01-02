package view.analysis;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import analysis.ClusterArray;
import analysis.ReadAnalysisFile;

import view.simulation.CellDrawPanel;

public class ClusterDrawPanel extends CellDrawPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * 分析結果クラスのポインタ
	 */
	private ReadAnalysisFile analysis;

	/**
	 * 再現するステップ
	 */
	private int step;

	/**
	 * 使う色の配列
	 */
	private Color[] colorArray = {Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW,Color.CYAN,Color.MAGENTA
			,Color.ORANGE,Color.PINK,Color.DARK_GRAY,Color.BLACK};

	/**
	 * コンストラクタ
	 *
	 * @param lengthX
	 *            x方向のセルの大きさ
	 * @param lengthY
	 *            y方向のセルの大きさ
	 */
	public ClusterDrawPanel(ReadAnalysisFile analysis, int lengthX, int lengthY) {
		super(lengthX, lengthY);
		this.analysis = analysis;
		this.step = 0;
	}

	/**
	 * 表示ルーチン イメージバッファの内容を全面的にコピーする
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// エージェントを描画する
		drawAgentCluster(g);
	}

	/**
	 * エージェントを描画する
	 *
	 * @param g
	 */
	private void drawAgentCluster(Graphics g) {
		// 描画するクラスタの配列を取得する
		//ArrayList<CellArray> clusterCellList = this.analysis.getCellList();
		//for (CellArray cell : clusterCellList) {
		//	drawCluster(g, cell);
		//}
		ClusterArray cell = this.analysis.getCell(this.step);
		drawCluster(g,cell);
	}

	/**
	 * セルの内容を描画する
	 *
	 * @param g
	 * @param cell
	 */
	private void drawCluster(Graphics g,ClusterArray cell) {
		// 1マスの大きさを取得
		double length = calcCellLength();
		// エージェントを描画する
		int sizeX = this.getCellSizeX();
		int sizeY = this.getCellSizeY();
		double startPointX = getDrawStartPointX();
		double startPointY = getDrawStartPointY();
		// 色管理用リスト
		ArrayList<Integer> colorList = new ArrayList<Integer>();
		for(int x=0;x<sizeX;x++){
			for(int y=0;y<sizeY;y++){
				// クラスタを取得
				int cluster = cell.get(x, y);
				if(cluster>0){
					if(!colorList.contains(cluster)){	// 初めて出てきたクラスタなら
						colorList.add(cluster);	// 色選択用配列に追加
					}
					// クラスタに対する色のインデックスを取得
					int colorIndex = colorList.indexOf(cluster);
					if(colorIndex>this.colorArray.length-1){	// 色の数が足りなかったら
						colorIndex %= this.colorArray.length;	// 色を先頭からループ
					}
					// セルに色を塗る
					if(colorIndex==-1){
						System.out.println();
					}
					Color def = this.colorArray[colorIndex];
					Color setColor = new Color(def.getRed(),def.getGreen(),def.getBlue(),200);
					setColor(g, setColor);
					fillRect(g, x * length + startPointX, y * length
							+ startPointY, length, length);
					// 文字を書く
					setColor(g, Color.BLACK);
					drawString(g,String.valueOf(cluster),x*length+startPointX,(y+1)*length+startPointY);
				}
			}
		}
		setColor(g, Color.BLACK);
	}

	/**
	 * 見るステップを設定する
	 */
	public void setStep(int step){
		this.step = step;
	}

	/**
	 * ステップ数の最大値を返す
	 * @return
	 */
	public int getMaxStepNum(){
		return this.analysis.getMaxStepNum();
	}
}
