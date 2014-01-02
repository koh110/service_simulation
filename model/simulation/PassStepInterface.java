package model.simulation;

/**
 * 1step経過時の処理をさせるインターフェース
 * @author kohta
 *
 */
public interface PassStepInterface {
	/**
	 * このメソッド内で1ステップ経過時の処理をさせる
	 */
	abstract public void passStep();
}
