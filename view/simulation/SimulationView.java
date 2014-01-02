package view.simulation;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.Timer;

import model.simulation.Simulation;

/**
 * シミュレーションのGUI表示を扱うクラス
 * タイマー用のアクションリスナーを持つ
 *
 * @author kohta
 *
 */
public class SimulationView implements ActionListener{
	/**
	 * 表示を行うシミュレーションのオブジェクトポインタのリスト
	 */
	private ArrayList<Simulation> simulationList;

	/**
	 * 表示を行うシミュレーションのオブジェクトポインタ
	 */
	//private Simulation simulation;

	/**
	 * タイトル
	 */
	private final String title = "Simulation Viewer";

	/**
	 * 基本のフレーム
	 */
	private JFrame frame;

	/**
	 * タブ表示用オブジェクト
	 */
	private JTabbedPane tabPane;

	/**
	 * キャンバスの幅
	 */
	private int width = 800;

	/**
	 * キャンバスの高さ
	 */
	private int height = 600;

	/**
	 * シミュレーション系パネルのマップ
	 */
	private HashMap<Simulation,SimulationPanel> simulationPanelMap;

	/**
	 * タイマー
	 */
	private Timer timer;

	/**
	 * コンストラクタ
	 * @param 表示するシミュレーション
	 */
	public SimulationView(ArrayList<Simulation> simulationList) {
		// シミュレーションポインタリストの設定
		this.simulationList = simulationList;
		// シミュレーションポインタの設定
		//this.simulation = simulation;
		// GUIパーツを初期化する
		initGUIParts();
		// レイアウトの初期化
		initLayout();
		// タイマーの初期化
		// 1ms毎にタイマーイベント
		this.timer = new Timer(100,this);
		this.timer.setCoalesce(true);
		this.timer.setRepeats(true);	// 繰り返し
	}

	/**
	 * ボタンやテキストフィールド等のGUIパーツを初期化する
	 */
	private void initGUIParts() {
		// 基本フレームの初期化
		this.frame = new JFrame(this.title);
		// xボタンクリック時の処理の設定
		// xボタンでプログラム終了
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// シミュレーションパネルのマップ初期化
		this.simulationPanelMap = new HashMap<Simulation,SimulationPanel>();
		for(Simulation sim:this.simulationList){	// シミュレーション個数分配置
			this.simulationPanelMap.put(sim,new SimulationPanel(sim));
		}
		// タブの初期化
		this.tabPane = new JTabbedPane();
	}

	/**
	 * パーツのレイアウト
	 */
	private void initLayout() {
		// コンテントペインの取得
		Container contentPane = this.frame.getContentPane();

		// タブに配置
		for(Simulation sim:this.simulationList){
			this.tabPane.add(sim.getName(),this.simulationPanelMap.get(sim));
		}

		// タブをフレームに配置
		contentPane.add(this.tabPane,BorderLayout.CENTER);
	}

	/**
	 * 表示を行うメソッド
	 */
	public void show() {
		// フレームの表示を行う
		this.frame.setVisible(true);
		// サイズの設定
		this.frame.setSize(this.width, this.height);
		// タイマーをスタートする
		this.timer.start();
	}

	/**
	 * ステータス情報を再描画する
	 */
	public void enableRepaintStatus(){
		for(Simulation sim:this.simulationList){
			this.simulationPanelMap.get(sim).enableRepaintStatus();
		}
	}

	/**
	 * ステータス情報を再描画しない
	 */
	public void disableRepaintStatus(){
		for(Simulation sim:this.simulationList){
			this.simulationPanelMap.get(sim).disableRepaintStatus();
		}
	}

	/**
	 * タイマーのアクション
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		for(Simulation sim:this.simulationList){
			this.simulationPanelMap.get(sim).timerAction();
		}
	}
}
