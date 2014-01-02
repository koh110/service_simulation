package view.analysis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import analysis.ReadAnalysisFile;

/**
 * クラスタパネルを操作するパネル
 *
 * @author kohta
 *
 */
public class ClusterControllPanel extends JPanel implements ActionListener,
		MouseWheelListener {
	private static final long serialVersionUID = 1L;

	/**
	 * クラスタ描画パネル
	 */
	private ClusterDrawPanel cDrawPanel;

	/**
	 * 分析結果のポインタ
	 */
	private ReadAnalysisFile analysis;

	/**
	 * クラスタの情報を表示するテキストエリア
	 */
	private JTextArea statusTextArea;

	/**
	 * 選択されているstepのインデックスを表示するui
	 */
	private JSpinner selectStepIdx;

	/**
	 * インデックスで指定したエージェントを取得するボタン
	 */
	private JButton selectButton;

	/**
	 * コンストラクタ
	 *
	 * @param analysis
	 * @param cellSizeX
	 * @param cellSizeY
	 */
	public ClusterControllPanel(ReadAnalysisFile analysis, int cellSizeX,
			int cellSizeY) {
		// ポインタの初期化
		this.analysis = analysis;

		// パネルの初期化
		this.cDrawPanel = new ClusterDrawPanel(analysis, cellSizeX, cellSizeY);

		// スピナーの初期化
		int max = this.cDrawPanel.getMaxStepNum() - 1;
		if(max<=0){
			max=1;
		}
		SpinnerModel model = new SpinnerNumberModel(0, 0, max, 1);
		this.selectStepIdx = new JSpinner(model);
		this.selectStepIdx.addMouseWheelListener(this);

		// テキストエリアの初期化
		this.statusTextArea = new JTextArea();
		//updateTextArea();

		// セレクトボタンの初期化
		this.selectButton = new JButton("select");
		this.selectButton.addActionListener(this); // アクションのセット
		// 高さを変えずに横幅一杯にサイズ変更
		int height = this.selectButton.getMaximumSize().height;
		this.selectButton.setMaximumSize(new Dimension(Short.MAX_VALUE, height));

		// レイアウトの初期化
		initLayout();
	}

	/**
	 * レイアウトの初期化
	 */
	private void initLayout() {
		// パネルのレイアウトをborder layoutに
		this.setLayout(new BorderLayout());
		// パネルに描画フィールドを配置
		this.add(this.cDrawPanel, BorderLayout.CENTER); // 中央に配置
		// 情報テキストフィールドを配置
		JPanel eastPanel = new JPanel(); // 右側に表示するパネル
		eastPanel.setLayout(new BorderLayout()); // 縦ならびレイアウト
		JPanel eastUpper = new JPanel();
		eastUpper.setLayout(new BorderLayout());
		eastUpper.add(this.selectStepIdx, BorderLayout.NORTH);
		eastUpper.add(this.selectButton, BorderLayout.SOUTH);
		eastPanel.add(eastUpper, BorderLayout.NORTH);

		// スクロールバーを追加する
		eastPanel.add(new JScrollPane(this.statusTextArea), BorderLayout.CENTER);
		this.add(eastPanel, BorderLayout.EAST); // 右に配置
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getSource() == this.selectStepIdx) {
			// スピナーのイベントを取得
			// JSpinner source = (JSpinner)e.getSource();
			// スピナーのモデルを取得
			SpinnerNumberModel model = (SpinnerNumberModel) this.selectStepIdx
					.getModel();
			// 現在の値を取得
			Integer oldValue = (Integer) this.selectStepIdx.getValue();
			// 新しい値を計算
			// ホイール回転が下方向の場合増加、上方向の場合減少
			int newValue = oldValue.intValue() - e.getWheelRotation()
					* model.getStepSize().intValue();
			// 最大値と最小値を取得
			int max = ((Integer) model.getMaximum()).intValue();
			int min = ((Integer) model.getMinimum()).intValue();
			// 値が超えないようにセット
			if (min <= newValue && newValue <= max) {
				this.selectStepIdx.setValue(newValue);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// select buttonのイベント
		if (e.getSource() == this.selectButton) {
			selectButtonAction();
		}

	}

	/**
	 * セレクトボタンを押した時の処理
	 */
	private void selectButtonAction() {
		this.cDrawPanel.setStep((Integer) this.selectStepIdx.getValue());
		this.repaint();
	}

	/**
	 * テキストエリアの更新
	 */
	private void updateTextArea() {
		StringBuffer strBuff = new StringBuffer();
		// 選択されているインデックスを取得
		Integer index = (Integer) this.selectStepIdx.getValue();

		// ステップ数を表示
		int stepNum = this.analysis.getStepNum(index);
		strBuff.append("stepNum:");
		strBuff.append(stepNum);
		strBuff.append("\n");

		// クラスタの数の取得
		int clusterNum = this.analysis.getClusterNum(index);
		strBuff.append("clusterNum:");
		strBuff.append(clusterNum);
		strBuff.append("\n");

		// 交換回数の平均の取得
		double average = this.analysis.getHistoryAverage(index);
		strBuff.append("history:");
		strBuff.append(average);
		strBuff.append("\n");

		// エージェントの数を表示
		int agentNum = this.analysis.getAgentNum(index);
		strBuff.append("agent:");
		strBuff.append(agentNum);
		strBuff.append("\n");

		this.statusTextArea.setText(strBuff.toString());
	}

	/**
	 * 再描画処理
	 */
	@Override
	public void repaint() {
		super.repaint();
		if (this.cDrawPanel != null) {
			// フィールドを再描画
			this.cDrawPanel.repaint();
		}
		if (this.statusTextArea != null) {
			// テキストエリアを再描画
			updateTextArea();
		}
	}
}
