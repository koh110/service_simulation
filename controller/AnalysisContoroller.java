package controller;

import analysis.ReadAnalysisFile;
import view.analysis.AnalysisView;

/**
 * 分析結果表示用のcontorollerクラス
 * @author kohta
 *
 */
public class AnalysisContoroller {
	/**
	 * 表示用インスタンス
	 */
	private AnalysisView view;

	/**
	 * 分析ファイルの読み込み
	 */
	private ReadAnalysisFile readAnalysisFile;

	/**
	 * セルのx方向のサイズ
	 */
	private final int cellSizeX = 100;
	/**
	 * セルのy方向のサイズ
	 */
	private final int cellSizeY = 100;

	/**
	 * コンストラクタ
	 */
	public AnalysisContoroller(){
		String fileName;
		//fileName = "base.csv";
		//fileName = "eternity.csv";
		fileName = "consume.csv";
		//fileName = "consumeEnergy.csv";

		// 分析ファイルの読み込み
		this.readAnalysisFile = new ReadAnalysisFile(fileName, this.cellSizeX, this.cellSizeY);

		// 表示様インスタンスの初期化
		this.view = new AnalysisView(this.readAnalysisFile);
	}

	/**
	 * プログラム開始地点
	 */
	public void start(){
		this.view.show();
	}

	public static void main(String[] args) {
		new AnalysisContoroller().start();
	}
}
