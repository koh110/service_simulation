package model.simulation;

import model.agent.CellAgent;
import model.agent.ConsumeServiceAgent;
import model.field.CellInfo;

/**
 * サービスの消費行動を行うシミュレーション
 * @author kohta
 *
 */
public class SimulationConsumeService extends SimulationEternityField{
	/**
	 * シミュレーションの名前
	 */
	private String name = "consume";

	/**
	 * コンストラクタ
	 */
	public SimulationConsumeService() {
		super();
		setName(this.name);
	}

	/**
	 * セルエージェントの生成
	 * @param cell
	 * @return
	 */
	public CellAgent createCellAgent(CellInfo cell){
		return new ConsumeServiceAgent(cell);
	}

}
