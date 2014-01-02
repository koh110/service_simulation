package view.simulation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import model.simulation.Simulation;

/**
 * シミュレーションのパネル
 *
 * @author kohta
 *
 */
public class SimulationPanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;

	/**
	 * 操作するシミュレーション
	 */
	private Simulation simulation;

	/**
	 * シミュレーションを操作するパネル
	 */
	private SimulationControlPanel simulationContorolPanel;

	/**
	 * ステータスの表示を行うパネル
	 */
	private FieldStatusPanel statusPanel;

	/**
	 * コンストラクタ
	 *
	 * @param simulation
	 */
	public SimulationPanel(Simulation simulation) {
		// シミュレーションポインタの設定
		this.simulation = simulation;
		// シミュレーション操作パネルの初期化
		this.simulationContorolPanel = new SimulationControlPanel(this);
		// ステータス描画用パネルの初期化
		this.statusPanel = new FieldStatusPanel(this.simulation.getField());
		// レイアウトの初期化
		this.setLayout(new BorderLayout());
		this.add(this.statusPanel, BorderLayout.CENTER);
		this.add(this.simulationContorolPanel, BorderLayout.SOUTH);
	}

	/**
	 * ステータス情報を再描画する
	 */
	public void enableRepaintStatus() {
		this.statusPanel.enableRepaint();
	}

	/**
	 * ステータス情報を再描画しない
	 */
	public void disableRepaintStatus() {
		this.statusPanel.disableRepaint();
	}

	/**
	 * タイマーで実行する処理
	 */
	public void timerAction() {
		// drawpanelを再描画
		this.statusPanel.repaint();
		// ステップ数表示の書き換え
		this.simulationContorolPanel.setStepNum(this.simulation.getStepNum());
		// ボタンの表示書き換え
		if (this.simulation.isStart()) {
			this.simulationContorolPanel.getStartButton().setText("stop");
			this.statusPanel.enableRepaint();
		} else {
			this.simulationContorolPanel.getStartButton().setText("start");
			this.statusPanel.disableRepaint();
		}
	}

	/**
	 * アクションメソッド
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// スタートボタン時の処理
		if (e.getSource() == this.simulationContorolPanel.getStartButton()) {
			if (this.simulation.isStart()) { // シミュレーションがすでに動いている時は
				this.simulation.stop(); // 停止
			} else { // シミュレーションが停止している時は
				this.simulation.start(); // 動作
			}
			// シミュレーションのステップ数を設定
			String strStepNum = this.simulationContorolPanel.getInputStepNum();
			int stepNum = Integer.parseInt(strStepNum);
			this.simulation.setEndStepNum(stepNum);
		}
	}
}
