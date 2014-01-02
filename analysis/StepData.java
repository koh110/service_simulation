package analysis;

/**
 * 1ステップの状態を管理するクラス
 * @author kohta
 *
 */
public class StepData {
	/**
	 * セルの情報
	 */
	private ClusterArray cell;

	/**
	 * 交換先の平均
	 */
	private double historyAverage;

	/**
	 * 交換先のヒストグラム
	 */
	private int[] histogram;

	/**
	 * なんステップ目か
	 */
	private int stepNum;

	/**
	 * クラスタサイズの平均
	 */
	private double clusterSizeAverage;

	/**
	 * クラスタ番号に対応したサイズを保持する配列
	 */
	private int[] clusterSize;

	/**
	 * クラスタサイズのヒストグラム
	 */
	private int[] clusterHistgram;

	/**
	 * コンストラクタ
	 * @param cellSizeX
	 * @param cellSizeY
	 */
	public StepData(){
		this.historyAverage = -1.0;
		this.stepNum = -1;
		this.histogram = new int[1];
	}

	public void setCell(ClusterArray cell){
		this.cell = cell;
	}

	public ClusterArray getCell(){
		return this.cell;
	}

	public void setHistoryAverage(double average){
		this.historyAverage = average;
	}

	public double getHistoryAverage(){
		return this.historyAverage;
	}

	public void setStepNum(int step){
		this.stepNum = step;
	}

	public int getStepNum(){
		return this.stepNum;
	}

	public void setHandHistogram(int[] histogram){
		this.histogram = histogram;
	}

	public int[] getHandHistogram(){
		return this.histogram;
	}

	public void setClusterSizeAverage(double sizeAve){
		this.clusterSizeAverage = sizeAve;
	}

	public double getClusterSizeAverage(){
		return this.clusterSizeAverage;
	}

	public void setClusterSize(int[] clusterSize){
		this.clusterSize = clusterSize;
	}

	public int[] getClusterSize(){
		return this.clusterSize;
	}

	public void setClusterHistogram(int[] histogram){
		this.clusterHistgram = histogram;
	}

	public int[] getClusterHistogram(){
		return this.clusterHistgram;
	}
}
