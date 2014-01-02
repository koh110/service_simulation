package view.simulation;

import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.simulation.Simulation;

/**
 * シミュレーションの操作を行うパネル
 * @author kohta
 *
 */
public class SimulationControlPanel extends JPanel{
	private static final long serialVersionUID = 1L;

	/**
	 * シミュレーション開始ボタン
	 */
	private JButton startButton;

	/**
	 * ステップ数入力用パネル
	 * 何stepシミュレーションを進めるか
	 */
	private LabelTextPanel inputStepNum;

	/**
	 * 現在のステップ数を表示するパネル
	 */
	private LabelTextPanel showSimStep;

	/**
	 * コンストラクタ
	 * @param actionListener ボタンのアクション
	 */
	public SimulationControlPanel(ActionListener actionListener){
		super();
		// シミュレーション開始ボタンの初期化
		this.startButton = new JButton("start");
		this.startButton.addActionListener(actionListener);
		// デフォルトのシミュレーションステップ数を取得
		String strSimNum = String.valueOf(Simulation.STEP_NUM_DEF);
		// ステップ数入力用テキストフィールドの初期化
		this.inputStepNum = new LabelTextPanel("シミュレーションを行うstep数",strSimNum);
		// 現在のステップ数表示用パネルの初期化
		this.showSimStep = new LabelTextPanel("現在のステップ数","0");
		// レイアウトの初期化
		initLayout();
	}

	/**
	 * レイアウトの初期化
	 * ボックスレイアウトで縦に
	 */
	private void initLayout(){
		// パネルのレイアウトをbox layoutに
		// 縦方向
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		// 1段目用レイアウトパネル
		JPanel first = initStepLayout();
		first.add(this.startButton);	// スタートボタンをパネルに配置
		first.add(this.inputStepNum);	// 回数入力用パネルをパネルに配置
		this.add(first);	// 1段目用レイアウトパネルをセット
		// 2段目用レイアウトパネル
		JPanel second = initStepLayout();
		second.add(this.showSimStep);	// 現在のステップ数表示を追加
		this.add(second);
	}

	/**
	 * 1段のレイアウトの生成
	 * @return
	 */
	private JPanel initStepLayout(){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		return panel;
	}

	/**
	 * 現在のステップ数への書き換え
	 * @param stepNum
	 */
	public void setStepNum(int stepNum){
		this.showSimStep.setText(String.valueOf(stepNum));
	}

	/**
	 * シミュレーションスタート用ボタンの取得
	 * @return
	 */
	public JButton getStartButton(){
		return this.startButton;
	}

	/**
	 * 入力されたシミュレーションstep回数を取得
	 * @return
	 */
	public String getInputStepNum(){
		return this.inputStepNum.getText();
	}

	/**
	 * ラベルとテキストフィールドを一緒に扱うためのクラス
	 * @author kohta
	 *
	 */
	private class LabelTextPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		/**
		 * テキストフィールドの内容説明用ラベル
		 */
		private JLabel label;
		/**
		 * テキストフィールド
		 */
		private JTextField textField;
		/**
		 * コンストラクタ
		 * @param label ラベルに表示する内容
		 * @param text テキストフィールドに表示する内容
		 */
		public LabelTextPanel(String label,String text){
			// 初期化
			this.label = new JLabel(label);
			this.textField = new JTextField(text);
			// レイアウトの初期化
			initLayout();
		}

		/**
		 * レイアウトの初期化
		 */
		private void initLayout(){
			// パネルのレイアウトをbox layoutに
			this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
			this.add(this.label);
			this.add(this.textField);
		}

		/**
		 * テキストフィールドの内容を書き換える
		 * @param text
		 */
		public void setText(String text){
			this.textField.setText(text);
		}

		/**
		 * テキストフィールドの内容を取得
		 * @return
		 */
		public String getText(){
			return this.textField.getText();
		}
	}
}
