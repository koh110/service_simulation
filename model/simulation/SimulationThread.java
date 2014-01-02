package model.simulation;

/**
 * シミュレーションを動かすスレッド
 * @author kohta
 *
 */
public class SimulationThread extends Thread{
	/**
	 * スレッド実行するシミュレーション
	 */
	private Simulation simulation;
	/**
	 * コンストラクタ
	 * @param simulation スレッド実行するシミュレーションオブジェクト
	 */
	public SimulationThread(Simulation simulation){
		super();
		this.simulation = simulation;
	}

	/**
	 * スレッド実行時の処理
	 */
	@Override
	public void run(){
		//simlationTask();
		this.simulation.simulationLoop();
	}
}
