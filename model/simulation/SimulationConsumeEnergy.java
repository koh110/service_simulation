package model.simulation;

import model.agent.CellAgent;
import model.agent.ConsumeEnergyAgent;
import model.field.CellInfo;

/**
 * サービスの消費行動に移動時間を考慮したシミュレーション
 * @author kohta
 *
 */
public class SimulationConsumeEnergy extends SimulationEternityField{
	/**
	 * シミュレーションの名前
	 */
	private String name = "consumeEnergy";

	/**
	 * コンストラクタ
	 */
	public SimulationConsumeEnergy(){
		super();
		setName(this.name);
	}

	/**
	 * セルエージェントの生成
	 * @param cell
	 * @return
	 */
	public CellAgent createCellAgent(CellInfo cell){
		return new ConsumeEnergyAgent(cell);
	}
}
