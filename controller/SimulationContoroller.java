package controller;

import java.util.ArrayList;
import java.util.HashMap;

import view.simulation.SimulationView;
import model.simulation.Simulation;
import model.simulation.SimulationConsumeEnergy;
import model.simulation.SimulationConsumeService;
import model.simulation.SimulationEternityField;
import model.simulation.SimulationThread;

/**
 * シミュレーションの表示と実行を管理するcontrollerクラス
 *
 * @author kohta
 *
 */
public class SimulationContoroller {
	/**
	 * シミュレーションオブジェクトを保持するリスト  0
	 */
	private ArrayList<Simulation> simulationList;

	/**
	 * シミュレーション実行用スレッド
	 */
	private HashMap<Simulation,SimulationThread> simulationThreadMap;

	/**
	 * シミュレーション表示インスタンス
	 */
	private SimulationView view;

	/**
	 * コンストラクタ
	 */
	public SimulationContoroller() {
		// シミュレーションの初期化
		initSimulation();
		// シミュレーション表示の初期化
		this.view = new SimulationView(this.simulationList);
		// シミュレーション用スレッドの初期化
		initSimulationThread();
	}

	/**
	 * スレッドの初期化
	 */
	private void initSimulationThread() {
		// スレッドマップの初期化
		this.simulationThreadMap = new HashMap<Simulation,SimulationThread>();
		// シミュレーションスレッドを作成して実行、マップに追加
		for(Simulation sim:this.simulationList){
			SimulationThread simThread = new SimulationThread(sim);
			simThread.start();
			this.simulationThreadMap.put(sim, simThread);
		}
	}

	/**
	 * シミュレーションの初期化
	 */
	private void initSimulation() {
		// シミュレーションリストの初期化
		this.simulationList = new ArrayList<Simulation>();
		// シミュレーションの初期化
		Simulation simulation = new Simulation();
		SimulationEternityField simulationEternityField = new SimulationEternityField();
		SimulationConsumeService simulationConsumeService = new SimulationConsumeService();
		SimulationConsumeEnergy simulationConsumeEnergy = new SimulationConsumeEnergy();
		// シミュレーションリストにシミュレーションを追加
		this.simulationList.add(simulation);
		this.simulationList.add(simulationEternityField);
		this.simulationList.add(simulationConsumeService);
		this.simulationList.add(simulationConsumeEnergy);
	}

	/**
	 * プログラム開始地点
	 */
	private void aplicationStart() {
		this.view.show();
	}

	/**
	 * メインメソッド
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		new SimulationContoroller().aplicationStart();
	}
}
