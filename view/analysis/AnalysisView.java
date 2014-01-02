package view.analysis;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import analysis.ReadAnalysisFile;

/**
 * クラスタの状態を可視化するクラス
 *
 * @author kohta
 *
 */
public class AnalysisView {
	/**
	 * フレームのタイトル
	 */
	private final String title = "cluster viewer";

	/**
	 * 基本のフレーム
	 */
	private JFrame frame;

	/**
	 * キャンバスの幅
	 */
	private int width = 800;

	/**
	 * キャンバスの高さ
	 */
	private int height = 600;

	/**
	 * セルを描画するパネル
	 */
	private ClusterControllPanel cellPanel;

	/**
	 * 分析ファイル読み込みインスタンスのポインタ
	 */
	private ReadAnalysisFile analysis;

	/**
	 * コンストラクタ
	 * @param x x方向のセルの大きさ
	 * @param y y方向のセルの大きさ
	 */
	public AnalysisView(ReadAnalysisFile readAnalysisFile) {
		// フレームの初期化
		this.frame = new JFrame(this.title);
		// xボタンクリック時の処理の設定
		// xボタンでプログラム終了
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// ポインタの初期化
		this.analysis = readAnalysisFile;

		// セル描画パネルの初期化
		this.cellPanel = new ClusterControllPanel(this.analysis,this.analysis.getCellSizeX(),this.analysis.getCellSizeY());
		// セル描画パネルの配置
		this.frame.add(this.cellPanel, BorderLayout.CENTER); // 中央に配置
	}

	/**
	 * 描画処理開始
	 */
	public void show() {
		// フレームの表示を行う
		this.frame.setVisible(true);
		// サイズの設定
		this.frame.setSize(this.width, this.height);
	}
}
