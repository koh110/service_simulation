package view.simulation;

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

import model.agent.CellAgent;
import model.field.Field;

/**
 * フィールドの情報を表示するパネル
 *
 * @author kohta
 *
 */
public class FieldStatusPanel extends JPanel implements ActionListener ,MouseWheelListener{
	private static final long serialVersionUID = 1L;

	/**
	 * 描画等に使うフィールドのポインタ
	 */
	private Field field;

	/**
	 * フィールドの表示を行うパネル
	 */
	private FieldDrawPanel fieldDrawPanel;

	/**
	 * 選択されているエージェントのインデックスを表示するui
	 */
	private JSpinner selectAgentIdx;

	/**
	 * インデックスで指定したエージェントを取得するボタン
	 */
	private JButton selectButton;

	/**
	 * フィールドの情報を表示するテキストエリア
	 */
	private JTextArea statusTextField;

	/**
	 * 前回選択されたエージェントを保持するエージェント
	 */
	private CellAgent selectedAgent;

	/**
	 * フィールドの情報を常に再描画するかのフラグ
	 */
	private boolean repaintStatusTextFlag = false;

	/**
	 * コンストラクタ
	 *
	 * @param field
	 */
	public FieldStatusPanel(Field field) {
		super();
		// フィールドのポインタの初期化
		this.field = field;
		// フィールド描画パネルの初期化
		this.fieldDrawPanel = new FieldDrawPanel(this.field);
		// 選択されているエージェントのインデックスを初期化
		// インデックスの取得
		int index = this.fieldDrawPanel.getSelectAgentIndex();
		int max = this.field.getAgentList().size();
		SpinnerModel model = new SpinnerNumberModel(index, 0, max, 1);
		this.selectAgentIdx = new JSpinner(model);
		this.selectAgentIdx.addMouseWheelListener(this);

		// セレクトボタンの初期化
		this.selectButton = new JButton("select");
		this.selectButton.addActionListener(this); // アクションのセット
		// 高さを変えずに横幅一杯にサイズ変更
		int height = this.selectButton.getMaximumSize().height;
		this.selectButton
				.setMaximumSize(new Dimension(Short.MAX_VALUE, height));
		// フィールドの情報表示テキストフィールドの初期化
		this.statusTextField = new JTextArea();
		// ステータステキスト表示の更新
		updateStatusText();
		// レイアウトの初期化
		initLayout();
		setSize(700,500);
	}

	/**
	 * レイアウトの初期化
	 */
	private void initLayout() {
		// パネルのレイアウトをborder layoutに
		this.setLayout(new BorderLayout());
		// パネルに描画フィールドを配置
		this.add(this.fieldDrawPanel, BorderLayout.CENTER); // 中央に配置
		// 情報テキストフィールドを配置
		JPanel eastPanel = new JPanel(); // 右側に表示するパネル
		eastPanel.setLayout(new BorderLayout()); // 縦ならびレイアウト
		JPanel eastUpper = new JPanel();
		eastUpper.setLayout(new BorderLayout());
		eastUpper.add(this.selectAgentIdx,BorderLayout.NORTH);
		eastUpper.add(this.selectButton,BorderLayout.SOUTH);
		eastPanel.add(eastUpper,BorderLayout.NORTH);
		// スクロールバーを追加する
		eastPanel.add(new JScrollPane(this.statusTextField),BorderLayout.CENTER);
		this.add(eastPanel, BorderLayout.EAST); // 右に配置
	}

	/**
	 * 再描画フラグを立てる
	 */
	public void enableRepaint() {
		this.repaintStatusTextFlag = true;
	}

	/**
	 * 再描画フラグを折る
	 */
	public void disableRepaint() {
		this.repaintStatusTextFlag = false;
	}

	/**
	 * 再描画処理
	 */
	@Override
	public void repaint() {
		super.repaint();
		if (this.fieldDrawPanel != null) {
			// フィールドを再描画
			this.fieldDrawPanel.repaint();
		}
		if (this.statusTextField != null) {
			// 再描画フラグがたっていたら
			if (this.repaintStatusTextFlag) {
				updateStatusText();
			}
			// フィールド側でエージェントが選択されていたら
			if (this.selectedAgent != this.fieldDrawPanel.getSelectAgent()) {
				updateStatusText();
			}
		}

	}

	/**
	 * エージェントの表示を更新
	 */
	private void updateStatusText() {
		// 選択エージェント更新
		this.selectedAgent = this.fieldDrawPanel.getSelectAgent();
		// インデックスの更新
		int idx = this.fieldDrawPanel.getSelectAgentIndex();
		this.selectAgentIdx.setValue(idx);
		// 表示するエージェントの取得
		CellAgent selectAgent = this.fieldDrawPanel.getSelectAgent();
		// 表示するエージェント情報の更新
		this.statusTextField.setText(selectAgent.toString());
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
		// 取得するエージェントのインデックスを取得
		int selectIdx = (Integer)this.selectAgentIdx.getValue();
		this.fieldDrawPanel.setSelectAgent(selectIdx);
		// ステータス表示を更新
		updateStatusText();
	}

	/**
	 * マウスホイール時のイベント
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getSource()==this.selectAgentIdx){
			// スピナーのイベントを取得
			//JSpinner source = (JSpinner)e.getSource();
			// スピナーのモデルを取得
			SpinnerNumberModel model = (SpinnerNumberModel)this.selectAgentIdx.getModel();
			// 現在の値を取得
			Integer oldValue = (Integer)this.selectAgentIdx.getValue();
			// 新しい値を計算
			// ホイール回転が下方向の場合増加、上方向の場合減少
			int newValue = oldValue.intValue() - e.getWheelRotation()*model.getStepSize().intValue();
			// 最大値と最小値を取得
			int max = ((Integer)model.getMaximum()).intValue();
			int min = ((Integer)model.getMinimum()).intValue();
			// 値が超えないようにセット
			if(min<=newValue && newValue<=max){
				this.selectAgentIdx.setValue(newValue);
			}
		}
	}
}
