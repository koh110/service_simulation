package model.simulation;

import java.util.ArrayList;

import analysis.AnalysisOutFile;
import analysis.AppendAnalysisFile;
import analysis.ClusterAnalysis;

import util.MyUtil;
import model.agent.CellAgent;
import model.field.CellInfo;
import model.field.Field;


/**
 * シミュレーションの本体を扱うクラス
 *
 * @author kohta
 *
 */
public class Simulation {
	/**
	 * シミュレーションの名前
	 */
	private String name = "base";

	/**
	 * ステップを動かすかどうかのフラグ
	 */
	private boolean stepStartFlag;

	/**
	 * シミュレーション1回の中にあるステップ数
	 */
	public static final int STEP_NUM_DEF = 50;

	/**
	 * シミュレーションを行うフィールド
	 */
	private Field field;

	/**
	 * シミュレーションフィールドのX方向の大きさ
	 */
	private final int CELL_SIZE_X = 100;

	/**
	 * シミュレーションフィールドのY方向の大きさ
	 */
	private final int CELL_SIZE_Y = 100;

	/**
	 * 分析用クラス
	 */
	private ClusterAnalysis clusterAnalysis;

	/**
	 * 現在のステップ数
	 */
	private int step=0;

	/**
	 * 終了までのステップ数
	 */
	private int endStepNum;

	/**
	 * コンストラクタ
	 */
	public Simulation() {
		// フィールドの初期化
		this.field = createField();
		// シミュレーション動作フラグ初期化
		this.stepStartFlag = false;
		// 分析インスタンスの初期化
		this.clusterAnalysis = new ClusterAnalysis(this);
	}

	/**
	 * フィールドの生成
	 * @return
	 */
	protected Field createField(){
		// フィールドの初期化
		return new Field(this,this.CELL_SIZE_X, this.CELL_SIZE_Y);
	}

	/**
	 * セルエージェントの生成
	 * @param cell
	 * @return
	 */
	public CellAgent createCellAgent(CellInfo cell){
		return new CellAgent(cell);
	}

	/**
	 * シミュレーション内容
	 */
	public void simulationLoop(){
		// 現在のステップ数
		int crtStepNum=0;
		// シミュレーションステップ
		while(true){
			/*if(!this.stepStartFlag){
				continue;
			}*/
			if(this.stepStartFlag){
				// 解析
				this.clusterAnalysis.analysis(crtStepNum);

				stepAction();

				//System.out.println("simulation,simulationLoop:"+crtStepNum);
				// ステップ数更新
				crtStepNum++;
				this.step=crtStepNum;
			}
			if(crtStepNum==this.endStepNum-1 && crtStepNum!=0){
				this.clusterAnalysis.analysis(crtStepNum);
				// ファイル書き出し
				this.clusterAnalysis.outputFile();
				System.out.println("file output!:"+crtStepNum);
				AnalysisOutFile.clipOut(getName());
				AppendAnalysisFile.append(getName());
				System.out.println("append:"+getName());
			}
			if(crtStepNum>=this.endStepNum){
				this.stepStartFlag = false;
			}
		}
	}

	/**
	 * 1ステップの行動
	 */
	private void stepAction() {
		// エージェントを行動させる順番を保存する配列
		int[] order = new int[this.field.getAgentNum()];
		// エージェントのリストを取得
		ArrayList<CellAgent> agentList = this.field.getAgentList();
		// キューの初期化
		// 0から大きさまでで初期化
		for(int i=0;i<order.length;i++){
			order[i] = i;
		}
		// 順番をシャッフル
		order = MyUtil.shuffle(order);

		// 行動順にエージェントを行動させる
		for(int i=0;i<order.length;i++){
			// エージェントのリストから行動するエージェントを取得
			CellAgent agent = agentList.get(order[i]);
			if(order[i]==0){
				System.out.print("");
			}
			// エージェントを行動させる
			agent.action(this.field);
		}

		// エージェント全てに1step経過時の処理
		for(CellAgent agent:agentList){
			agent.passStep();
		}
		// フィールドのステップを進める
		this.field.passStep();
	}

	/**
	 * フィールドインスタンスを取得する
	 * @return
	 */
	public Field getField(){
		return this.field;
	}

	/**
	 * 現在のステップ数を取得する
	 * @return
	 */
	public int getStepNum(){
		return this.step;
	}

	/**
	 * セルのx方向の大きさを取得する
	 * @return
	 */
	public int getCellSizeX(){
		return this.CELL_SIZE_X;
	}

	/**
	 * セルのy方向の大きさを取得する
	 * @return
	 */
	public int getCellSizeY(){
		return this.CELL_SIZE_Y;
	}

	/**
	 * 終了するステップ数
	 * @param num
	 */
	public void setEndStepNum(int num){
		this.endStepNum = num;
	}

	/**
	 * シミュレーションを動かす
	 */
	public void start(){
		this.stepStartFlag = true;
	}

	/**
	 * シミュレーションを停止する
	 */
	public void stop(){
		this.stepStartFlag = false;
	}

	/**
	 * シミュレーションが動いているかどうか
	 * @return 動作している時はtrue。それ以外はfalse
	 */
	public boolean isStart(){
		return this.stepStartFlag;
	}

	/**
	 * シミュレーションの名前を返す
	 * @return
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * シミュレーションの名前をセットする
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}

	/*
	public static void main(String[] args){
		Simulation sim = new Simulation();
		sim.setEndStepNum(Simulation.STEP_NUM_DEF);
		sim.simulationLoop();
		sim.start();
	}
	*/
}
