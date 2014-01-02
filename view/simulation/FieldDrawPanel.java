package view.simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import model.agent.CellAgent;
import model.field.CellInfo;
import model.field.CellScope;
import model.field.Field;
import model.service.Service;

/**
 * フィールドの表示を行うパネル
 *
 * @author kohta
 *
 */
public class FieldDrawPanel extends CellDrawPanel {
	private static final long serialVersionUID = 1L;
	/**
	 * 表示を行うフィールドのポインタ
	 */
	private Field field;

	/**
	 * 現在選択されているエージェント
	 */
	private CellAgent selectAgent;

	/**
	 * 視界の色
	 */
	private Color eyesightColor;

	/**
	 * 交換範囲の色
	 */
	private Color exechangeColor;

	/**
	 * コンストラクタ
	 *
	 * @param field
	 */
	public FieldDrawPanel(Field field) {
		super(field.getSizeX(),field.getSizeY());
		// フィールドポインタの初期化
		this.field = field;
		// 現在選択されているエージェントの初期化
		setSelectAgent(0);
		// 色の初期化
		initColor();
	}

	/**
	 * 色の初期化
	 */
	private void initColor(){
		// 視界の色の初期化
		// デフォルトの色
		Color def = Color.CYAN;
		this.eyesightColor = new Color(def.getRed(),def.getGreen(),def.getBlue(),50);

		// 交換範囲の色の初期化
		def = Color.MAGENTA;
		this.exechangeColor = new Color(def.getRed(),def.getGreen(),def.getBlue(),50);
	}

	/**
	 * 表示ルーチン イメージバッファの内容を全面的にコピーする
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// エージェントを描画する
		drawAgent(g);
		// 選択されたエージェントのセルを描画
		drawSelectCell(g);
	}

	/**
	 * 選択されたセルを描画する
	 * @param g
	 */
	private void drawSelectCell(Graphics g){
		// 1マスの大きさを取得
		double length = calcCellLength();
		// エージェントの位置を取得
		int agentX = this.selectAgent.getX();
		int agentY = this.selectAgent.getY();
		// 描画色の設定
		setColor(g, Color.BLACK);
		// 描画
		drawRect(g, agentX * length + getDrawStartPointX()-1, agentY * length-2
							+ getDrawStartPointY(), length+2, length+2);
		// 視界範囲座標
		CellScope eyesight = this.selectAgent.getEyesightScope();
		ArrayList<CellInfo> eyesightCell = eyesight.searchCell(this.field);
		setColor(g,this.eyesightColor);
		for(CellInfo cell:eyesightCell){
			fillRect(g,cell.getX() * length + getDrawStartPointX()-1, cell.getY() * length-2 + getDrawStartPointY(), length+2, length+2);
		}
		// 交換範囲座標
		CellScope exchange = this.selectAgent.getExchangeScope();
		ArrayList<CellInfo> exhangeCell = exchange.searchCell(this.field);
		setColor(g,this.exechangeColor);
		for(CellInfo cell:exhangeCell){
			fillRect(g,cell.getX() * length + getDrawStartPointX()-1, cell.getY() * length-2 + getDrawStartPointY(), length+2, length+2);
		}
	}

	/**
	 * エージェントを描画する
	 *
	 * @param g
	 */
	private void drawAgent(Graphics g) {
		// 1マスの大きさを取得
		double length = calcCellLength();
		// 描画するエージェントのリストを取得する
		ArrayList<CellAgent> fieldAgent = this.field.getAgentList();
		// エージェントを描画する
		for (CellAgent agent : fieldAgent) {
			// 色を判定するサービスを取得
			Service service = agent.getPreServiceList()
					.getMaxServiceByEfficiency();
			// 色の設定
			switch (service.getServiceType()) {
			case COOKING:
				setColor(g, Color.RED);
				break;
			case FARMING:
				setColor(g, Color.GREEN);
				break;
			case FISHING:
				setColor(g, Color.BLUE);
				break;
			case HUNTING:
				setColor(g, Color.YELLOW);
				break;
			case CONSTRUCTING:
				setColor(g, Color.CYAN);
				break;
			case TEACHING:
				setColor(g, Color.MAGENTA);
				break;
			default:
				setColor(g, Color.BLACK);
			}
			// setColor(g,Color.RED);

			// エージェントの位置を取得
			int agentX = agent.getX();
			int agentY = agent.getY();
			// 描画
			fillRect(g, agentX * length + getDrawStartPointX(), agentY * length
					+ getDrawStartPointY(), length, length);
		}
		// 色の設定
		setColor(g, Color.BLACK);
	}



	/**
	 * 選択されたエージェントを取得
	 *
	 * @return
	 */
	public CellAgent getSelectAgent() {
		// クリックされたセルの位置を取得
		Point clickedCellPoint = getClickedCellPoint();

		// フィールド上のセルを取得
		CellInfo cell = this.field.getCell((int)clickedCellPoint.getX(), (int)clickedCellPoint.getY());
		// セルの範囲外だった場合
		if(cell==null){
			// 現在の選択エージェントを返して終了
			return this.selectAgent;
		}

		// 取得したセルがエージェントだったら
		if (cell.getType() == CellInfo.ObjectType.AGENT) {
			this.selectAgent = (CellAgent) cell.getObject();
		}
		return this.selectAgent;
	}

	/**
	 * フィールドのindex番目のエージェントに選択エージェントを変更する
	 *
	 * @param index
	 */
	public void setSelectAgent(int index) {
		// 選択エージェントを変更
		this.selectAgent = this.field.getAgentList().get(index);

		// ポイントを選択エージェントの位置にずらす
		CellInfo cell = this.selectAgent.getCellFieldPointer(); // セルの情報を取得
		setClickPoint(cell.getX(), cell.getY());
	}

	/**
	 * 選択されたエージェントのインデックスを取得する
	 */
	public int getSelectAgentIndex() {
		return this.field.indexOf(this.selectAgent);
	}
}
