package analysis;

import java.util.ArrayList;

import util.FileReaderWriter;

/**
 * 解析結果を読み込むクラス
 * @author kohta
 *
 */
public class ReadAnalysisFile {
	/**
	 * 読み込むファイルの名前
	 */
	private String fileName;

	/**
	 * 読み込んだファイルの行ごとの内容を保持するリスト
	 */
	private ArrayList<String[]> lines;

	/**
	 * x方向のセルのサイズ
	 */
	private int cellSizeX;
	/**
	 * y方向のセルのサイズ
	 */
	private int cellSizeY;

	/**
	 * ステップの情報を格納するリスト
	 */
	private ArrayList<StepData> stepDataList;

	/**
	 * コンストラクタ
	 * @param fileName 読み込むファイルの名前
	 * @param sizeX x方向のサイズ
	 * @param sizeY y方向のサイズ
	 */
	public ReadAnalysisFile(String fileName,int sizeX,int sizeY){
		this.fileName = fileName;
		this.cellSizeX = sizeX;
		this.cellSizeY = sizeY;
		this.stepDataList = new ArrayList<StepData>();
		// ファイルの内容を読み込む
		this.lines = FileReaderWriter.readCSV(this.fileName);
		convertCell();
	}

	/**
	 * ファイルの内容をセルへ落とし込む
	 */
	private void convertCell(){
		int crtLine=0;
		ClusterArray cArray = new ClusterArray(this.cellSizeX,this.cellSizeY);
		StepData stepData = new StepData();
		int y=0;
		// ファイルの内容を行ごとに読み出す
		while(crtLine<this.lines.size()){
			// 行の内容
			String[] line = this.lines.get(crtLine);
			if(line.length>1){	// 行の内容がある場合
				String first = line[0];
				if(first.equals("stepNum")){	// ステップ数の読み込み
					int step = Integer.parseInt(line[1]);
					stepData.setStepNum(step);
				}
				else if(first.equals("history")){	// 交換先個数の平均
					double history = Double.parseDouble(line[1]);
					//this.historyAverage.add(history);
					stepData.setHistoryAverage(history);
				}else if(first.equals("histogram")){	// ヒストグラムの読み込み
					int[] histgram = new int[line.length-1];
					for(int i=1;i<line.length-1;i++){
						histgram[i-1] = Integer.parseInt(line[i]);
					}
					stepData.setHandHistogram(histgram);
				}else if(first.equals("clusterSizeAve")){	// クラスタサイズの平均
					double sizeAve = Double.parseDouble(line[1]);
					stepData.setClusterSizeAverage(sizeAve);
				}
				else if(first.equals("clusterSize")){	// クラスターサイズの読み込み
					int[] size = new int[line.length-1];
					for(int i=1;i<line.length-1;i++){
						size[i-1] = Integer.parseInt(line[i]);
					}
					stepData.setClusterSize(size);
				}else if(first.equals("clusterHistogram")){	// クラスターサイズヒストグラム読み込み
					int[] histogram = new int[line.length-1];
					for(int i=1;i<line.length-1;i++){
						histogram[i-1] = Integer.parseInt(line[i]);
					}
					stepData.setClusterHistogram(histogram);
				}
				else{	// セルデータの読み込み
					for(int x=0;x<this.cellSizeX;x++){
						cArray.set(Integer.parseInt(line[x]), x, y);
					}
					y++;	// セルの書き込み行を1行下へ
				}

			}else{	// 行の内容がない->次の状態のセル情報がその次の行から入っている
				stepData.setCell(cArray);
				this.stepDataList.add(stepData);
				stepData = new StepData();
				cArray = new ClusterArray(this.cellSizeX,this.cellSizeY);
				y=0;
			}
			crtLine++;	// 読み込み行を一行下へ
		}
	}

	/**
	 * index番目のステップ数を取得
	 * @param index
	 * @return
	 */
	public int getStepNum(int index){
		return this.stepDataList.get(index).getStepNum();
	}

	/**
	 * step数の最大値を取得
	 * @return
	 */
	public int getMaxStepNum(){
		return this.stepDataList.size();
	}

	/**
	 * 引数step目のセルの状態を取得する
	 * @param index
	 * @return
	 */
	public ClusterArray getCell(int index){
		return this.stepDataList.get(index).getCell();
	}

	/**
	 * 引数step目のクラスタの数を取得する
	 * @param step
	 * @return
	 */
	public int getClusterNum(int step){
		ArrayList<Integer> list = getClusterIndex(step);
		return list.size();
	}

	/**
	 * 引数step番目の交換数の平均を取得する
	 * @param step
	 * @return
	 */
	public double getHistoryAverage(int step){
		return this.stepDataList.get(step).getHistoryAverage();
	}

	/**
	 * 引数step目の交換先ヒストグラムを取得する
	 * @param step
	 * @return
	 */
	public int[] getHistogram(int step){
		return this.stepDataList.get(step).getHandHistogram();
	}

	/**
	 * 引数step目のクラスタサイズの平均を取得
	 * @param step
	 * @return
	 */
	public double getClusterSizeAverage(int step){
		return this.stepDataList.get(step).getClusterSizeAverage();
	}

	/**
	 * 引数step目のクラスタサイズ配列を取得
	 * @param step
	 * @return
	 */
	public int[] getClusterSize(int step){
		return this.stepDataList.get(step).getClusterSize();
	}

	/**
	 * 引数step目のクラスタヒストグラムを取得
	 * @param step
	 * @return
	 */
	public int[] getClusterHistogram(int step){
		return this.stepDataList.get(step).getClusterHistogram();
	}

	/**
	 * クラスタに所属するエージェントの数を数える
	 * @param step
	 * @return
	 */
	public int getAgentNum(int step){
		ClusterArray cell = getCell(step);
		int[] array = cell.getArray();
		int counter=0;
		for(int value:array){
			if(value!=0){
				counter++;
			}
		}
		return counter;
	}

	/**
	 * 引数step目の使用クラスタ群を取得
	 * @param index
	 * @return
	 */
	public ArrayList<Integer> getClusterIndex(int step){
		ArrayList<Integer> list = new ArrayList<Integer>();
		ClusterArray cell = getCell(step);
		int[] array = cell.getArray();
		for(int i=0;i<array.length;i++){
			int crtCluster = array[i];
			if(!list.contains(crtCluster)){
				list.add(crtCluster);
			}
		}
		return list;
	}

	public int getCellSizeX(){
		return this.cellSizeX;
	}

	public int getCellSizeY(){
		return this.cellSizeY;
	}
}
