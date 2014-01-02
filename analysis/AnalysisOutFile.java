package analysis;

import java.util.ArrayList;

import util.FileReaderWriter;

/**
 * 分析ファイルから新しくファイルを作成してアウトプットする
 * @author kohta
 *
 */
public class AnalysisOutFile {
	/**
	 * セルのx方向のサイズ
	 */
	private static final int cellSizeX = 100;

	/**
	 * セルのy方向のサイズ
	 */
	private static final int cellSizeY = 100;

	/**
	 * コンストラクタ
	 */
	public AnalysisOutFile(){

	}

	/**
	 * プログラムスタート地点
	 */
	private void start(){
		// 分析クラス変数の初期化
		String fileName = "consume";
		String fileName2 = "consumeEnergy";

		clipOut(fileName);
		clipOut(fileName2);
		System.out.println("end");
	}

	/**
	 * 抜き出した文字列をファイルに書き出し
	 * @param fileName
	 */
	public static void clipOut(String fileName){
		ReadAnalysisFile analysis = new ReadAnalysisFile(fileName+".csv",cellSizeX,cellSizeY);
		// データの抜き出し
		String fileOutStr = clip(analysis);
		FileReaderWriter.write(fileName+"Analysis.csv", fileOutStr);
		String histogramStr = clipHistogram(analysis);
		FileReaderWriter.write(fileName+"Histogram.csv", histogramStr);
		String clusterSize = clipClusterSize(analysis);
		FileReaderWriter.write(fileName+"HistogramCluster.csv", clusterSize);
	}

	/**
	 * クラスタサイズ関連を抜き出す
	 * @param analysis
	 * @return
	 */
	public static String clipClusterSize(ReadAnalysisFile analysis){
		// クラスタサイズ関連を抜き出す
		StringBuffer outStr_clusterSize = new StringBuffer("clusterSize,\n");
		StringBuffer outStr_clusterHistogram = new StringBuffer("clusterHistogram,\n");
		int maxSize = analysis.getMaxStepNum();
		for(int step=0;step<maxSize;step++){
			int[] clusterSize = analysis.getClusterSize(step);
			int[] clusterHistogram = analysis.getClusterHistogram(step);
			outStr_clusterSize.append("[size:"+step+"],");
			outStr_clusterHistogram.append("[hist:"+step+"],");
			for(int i=0;i<clusterSize.length;i++){
				outStr_clusterSize.append(clusterSize[i]+",");
			}
			for(int i=0;i<clusterHistogram.length;i++){
				outStr_clusterHistogram.append(clusterHistogram[i]+",");
			}
			outStr_clusterSize.append("\n");
			outStr_clusterHistogram.append("\n");
		}
		return outStr_clusterSize+"\n"+outStr_clusterHistogram;
	}

	/**
	 * ヒストグラムの文字列を取得
	 * @param analysis
	 * @return
	 */
	public static String clipHistogram(ReadAnalysisFile analysis){
		int maxSize = analysis.getMaxStepNum();
		StringBuffer outStr = new StringBuffer();
		for(int i=0;i<maxSize;i++){
			outStr.append("["+i+"],");
			int[] histogram = analysis.getHistogram(i);
			for(int j=0;j<histogram.length;j++){
				if(j!=0){
					outStr.append(",");
				}
				outStr.append(histogram[j]);
			}
			outStr.append("\n");
		}
		return outStr.toString();
	}

	/**
	 * データをcsvファイルに抜き出す
	 * @param analysis
	 */
	public static String clip(ReadAnalysisFile analysis) {
		int maxSize = analysis.getMaxStepNum();
		StringBuffer outStr_hand = new StringBuffer("hand,");
		StringBuffer outStr_cluster = new StringBuffer("clusterNum,");
		StringBuffer outStr_deselectionCluster = new StringBuffer("dClusterNum,");
		StringBuffer outStr_clusterSizeAve = new StringBuffer("clusterSizeAve,");

		// step数対応のものを抜き出す
		for(int i=0;i<maxSize;i++){
			if(i!=0){
				outStr_hand.append(",");
				outStr_cluster.append(",");
				outStr_deselectionCluster.append(",");
				outStr_clusterSizeAve.append(",");
			}
			double hand = analysis.getHistoryAverage(i);
			int cluster = analysis.getClusterNum(i);

			// クラスタの中に存在するエージェント数が2以下の時クラスタとしない
			int deselectionNum = 0;	// 無視するクラスタ数
			ArrayList<Integer> used = new ArrayList<Integer>();	// すでに見たクラスタを無視する
			ClusterArray array = analysis.getCell(i);
			for(int clusterNum:array.getArray()){
				if(!used.contains(clusterNum)){
					used.add(clusterNum);
					if(array.countAgentInCluster(clusterNum)<=2){
						deselectionNum++;
					}
				}
			}
			cluster-=deselectionNum;	// クラスタの数を減らす

			double clusterSizeAve = analysis.getClusterSizeAverage(i);

			outStr_hand.append(hand);
			outStr_cluster.append(cluster);
			outStr_deselectionCluster.append(deselectionNum);
			outStr_clusterSizeAve.append(clusterSizeAve);
		}

		return outStr_hand+"\n"+outStr_cluster+"\n"+outStr_deselectionCluster+"\n"+outStr_clusterSizeAve;
	}

	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args){
		new AnalysisOutFile().start();
	}
}
