package analysis;

import util.FileReaderWriter;

/**
 * AnalysisOutFileで書きだされた内容を抜き出して追記する
 *
 * @author kohta
 *
 */
public class AppendAnalysisFile {
	/**
	 * プログラム開始地点
	 */
	public void start() {
		// 分析クラス変数の初期化
		String fileName = "consume";
		String fileName2 = "consumeEnergy";
		append(fileName);
		append(fileName2);
		System.out.println("append end");
	}

	/**
	 * ファイルに追記
	 * @param fileName
	 */
	public static void append(String fileName){
		String read = FileReaderWriter.read(fileName+"Analysis.csv");
		FileReaderWriter.writeAppend(fileName+"Append.csv", read);
	}

	public static void main(String[] args) {
		new AppendAnalysisFile().start();
	}
}
