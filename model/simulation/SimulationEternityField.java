package model.simulation;

import model.field.EternityField;
import model.field.Field;

/**
 * フィールドの端を結んだシミュレーション
 * @author kohta
 *
 */
public class SimulationEternityField extends Simulation{

	/**
	 * シミュレーションの名前
	 */
	private String name = "eternity";

	/**
	 * コンストラクタ
	 */
	public SimulationEternityField() {
		super();
		setName(this.name);
	}

	/**
	 * フィールドの初期化
	 */
	@Override
	protected Field createField(){
		return new EternityField(this,getCellSizeX(),getCellSizeY());
	}
}
