package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import util.FileReaderWriter;

import model.agent.CellAgent;
import model.field.CellInfo;
import model.field.Field;
import model.simulation.Simulation;

/**
 * クラスタ分析を行うクラス
 *
 * @author kohta
 *
 */
public class ClusterAnalysis {
	/**
	 * 分析するシミュレーション
	 */
	private Simulation simulation;

	/**
	 * 最終的に書きだすための文字列
	 */
	private StringBuffer outputStr;

	/**
	 * コンストラクタ
	 *
	 * @param field
	 */
	public ClusterAnalysis(Simulation sim) {
		this.simulation = sim;

		// 書き出し用文字列の初期化
		this.outputStr = new StringBuffer();
	}

	/**
	 * 初期のエージェントクラスタわけ
	 */
	private void initCluster(ClusterArray initcluster) {
		// エージェントリストを取得
		ArrayList<CellAgent> agentList = this.simulation.getField().getAgentList();
		int clusterNum = 1; // 振るクラスタ番号
		// クラスタ番号を振る
		for (CellAgent agent : agentList) {
			initcluster.set(clusterNum, agent.getX(), agent.getY());
			clusterNum++;
		}
	}

	/**
	 * 毎ステップに呼んでクラスタ分類する
	 */
	public void analysis(int stepNum) {
		// 交換相手の数の平均
		ArrayList<CellAgent> agentList = this.simulation.getField().getAgentList();
		double total = 0;
		for (CellAgent agent : agentList) {
			ArrayList<CellAgent> history = agent.getHistory();
			total += history.size();
		}
		double ave = total / agentList.size();

		// クラスタ化
		ClusterArray cluster = distance2();

		int[] histogram = handHistogram();	// 密集度のヒストグラムを生成
		StringBuffer histogramStr = new StringBuffer();
		for(int i=0;i<histogram.length;i++){
			histogramStr.append(histogram[i]+",");
		}

		// クラスタサイズ
		int[] clusterSize = clusterSize(cluster);
		StringBuffer sizeStr = new StringBuffer();
		for(int i=0;i<clusterSize.length;i++){
			sizeStr.append(clusterSize[i]+",");
		}

		// クラスタサイズの平均
		double clusterSizeAve = 0;
		int clusterNum = 0;
		for(int i=0;i<clusterSize.length;i++){
			int size = clusterSize[i];
			if(size>2){
				clusterSizeAve+=size;
				clusterNum++;
			}
		}
		clusterSizeAve = clusterSizeAve/clusterNum;

		// クラスタサイズのヒストグラム
		int[] clusterSizeHistogram = clusterSizeHistogram(clusterSize);
		StringBuffer sizeHistogramStr = new StringBuffer();
		for(int i=0;i<clusterSizeHistogram.length;i++){
			sizeHistogramStr.append(clusterSizeHistogram[i]+",");
		}

		// ファイルに書き出し用文字列に追加
		StringBuffer outStr = new StringBuffer();
		outStr.append("stepNum,"+stepNum+"\n");
		outStr.append("history," + ave + "\n");
		outStr.append("histogram,"+histogramStr+"\n");
		outStr.append("clusterSizeAve,"+clusterSizeAve+"\n");
		outStr.append("clusterSize,"+sizeStr+"\n");
		outStr.append("clusterHistogram,"+sizeHistogramStr+"\n");
		StringBuffer outClusterStr = toStrArray(cluster);
		outStr.append(outClusterStr);
		this.outputStr.append(outStr.toString());
	}

	/**
	 * クラスタの大きさのヒストグラムを作る
	 * @param clusterSize クラスタ番号の
	 * @return
	 */
	private int[] clusterSizeHistogram(int[] clusterSize){
		int[] histogram = new int[this.simulation.getField().getAgentList().size()];
		for(int i=0;i<clusterSize.length;i++){
			int size = clusterSize[i];	// クラスタ番号に対応するクラスタサイズを取得
			histogram[size]++;	// サイズに当てはまる箇所を増やす
		}

		return histogram;
	}

	/**
	 * クラスタの大きさをはかる
	 * @param cluster クラスタ番号
	 */
	private int[] clusterSize(ClusterArray cluster){
		// 各クラスタに対応したサイズを保持するマップ
		// key:クラスタ番号
		// value:カウンター
		HashMap<Integer,Integer> counter = new HashMap<Integer,Integer>();
		int[] clusterCell = cluster.getArray();	// クラスタ情報を一次元配列にする
		int maxClusterNum = 0;	// 最大のクラスタ番号を取得
		for(int i=0;i<clusterCell.length;i++){
			int clusterValue = clusterCell[i];	// セルに対応するクラスタ番号を取得
			Set<Integer> keySet = counter.keySet();	// キーセットを取得
			int count = 1;
			if(keySet.contains(clusterValue)&&clusterValue!=0){	// すでに出てきているクラスタ番号であれば
				// 保存されているクラスタのカウント数を加算
				count += counter.get(clusterValue);
			}
			counter.put(clusterValue, count);

			if(maxClusterNum<clusterValue){	// 最大クラスタ番号の更新
				maxClusterNum = clusterValue;
			}
		}

		// エージェント数分の配列
		int[] clusterCounter = new int[maxClusterNum+1];
		Set<Integer> set = counter.keySet();	// マップのキーセット
		for(Integer key:set){
			clusterCounter[key] = counter.get(key);
		}
		return clusterCounter;
	}

	/**
	 * 密集度のヒストグラムを作る
	 * @return
	 */
	private int[] handHistogram(){
		int[] counter = new int[30];
		ArrayList<CellAgent> agentList = this.simulation.getField().getAgentList();
		for(CellAgent agent:agentList){
			int history = agent.getHistory().size();
			counter[history]++;
		}
		return counter;
	}

	/**
	 * ふるいおとしクラスタリングによるクラスタ分類
	 * @return クラスタ分類されたクラスタを取得
	 */
	private ClusterArray distance2(){
		//this.cluster.init();
		Field field = this.simulation.getField();
		int sizeX = field.getSizeX();
		int sizeY = field.getSizeY();

		// クラスタ分類を管理する変数
		ClusterArray cluster = new ClusterArray(sizeX,sizeY);
		// エージェント全てにクラスタを与える
		initCluster(cluster);

		// ふるい落としクラスタリング用の変数
		ClusterArray clusterDistance = new ClusterArray(sizeX,sizeY);
		clusterDistance.init();
		clusterDistance.update(0, 1);	// 全てのセルを1にする

		// ふるい落としクラスタリング
		for(int x=0;x<sizeX;x++){
			for(int y=0;y<sizeY;y++){
				ArrayList<CellInfo> list = getPoint(x,y);	// 周囲のエージェントがいるセルを取得
				// エージェントの数を数える
//				int counter=0;
//				for(CellInfo cell:list){
//					if(cell.getType()==CellInfo.ObjectType.AGENT){
//						counter++;
//					}
//				}
				if(list.size()<5){
					clusterDistance.set(-1, x, y);
				}
			}
		}

		// 割り振るクラスタ番号
		int clusterNum = 2;
		ClusterArray check = new ClusterArray(sizeX,sizeY);
		for(int x=0;x<sizeX;x++){
			for(int y=0;y<sizeY;y++){
				int value = clusterDistance.get(x, y);
				if(value==1){	// ふるい落としクラスタでクラスタ番号がふられている場所であれば
					fill(clusterDistance,x,y,clusterNum,check);	// その周囲をクラスタ番号で塗りつぶす
					clusterNum++;	// 次のクラスタ番号にする
				}
			}
		}


		// クラスタにふるい落としクラスタをアップデート
		for(int x=0;x<sizeX;x++){
			for(int y=0;y<sizeY;y++){
				int value = clusterDistance.get(x, y);	// ふるい落としクラスタのクラスタ番号
				int baseValue = cluster.get(x, y);	// 元のクラスタ番号
				if(value==0||value==-1){	// ふるい落としクラスタでクラスタ番号が降られていない場合
					cluster.set(baseValue, x, y);	// 元の番号
				}else{	// ふるい落としクラスタでクラスタ番号が降られているセルの場合
					if(cluster.get(x, y)>0){	// そこにエージェントがいれば
						cluster.set(value, x, y);	// クラスタ番号をふるい落としクラスタのものへアップデート
					}
				}
			}
		}
		return cluster;
	}

	private boolean fillParts(ClusterArray cluster,int x,int y,int clusterValue,ClusterArray check){
		Field field = this.simulation.getField();
		CellInfo cell = field.getCell(x, y);
		x = cell.getX();
		y = cell.getY();
		int value = getCellValue(cluster, x, y);
		if(value == 1){
			if(check.get(x, y)==1){
				return false;
			}
			check.set(1, x, y);
			cluster.set(clusterValue, x, y);
			fill(cluster, x, y, clusterValue,check);
		}
		return true;
	}
	/**
	 * クラスタを引数で与えたクラスタ番号で塗りつぶす
	 * @param cluster
	 * @param x
	 * @param y
	 * @param clusterValue　塗りつぶすクラスタ番号
	 * @param check すでに塗りつぶした場所をチェックする
	 */
	private void fill(ClusterArray cluster,int x,int y,int clusterValue,ClusterArray check){
		// 自分のいる位置にクラスタ番号を入れる
		cluster.set(clusterValue, x, y);

		int serchPointX = x;
		int serchPointY = y-1;
		if(!fillParts(cluster,serchPointX,serchPointY,clusterValue,check)){
			return ;
		}
		serchPointX = x+1;
		serchPointY = y;
		if(!fillParts(cluster,serchPointX,serchPointY,clusterValue,check)){
			return ;
		}
		serchPointX = x;
		serchPointY = y+1;
		if(!fillParts(cluster,serchPointX,serchPointY,clusterValue,check)){
			return ;
		}
		serchPointX = x-1;
		serchPointY = y;
		if(!fillParts(cluster,serchPointX,serchPointY,clusterValue,check)){
			return ;
		}
	}

	/**
	 * 引数ので与えた地点の周囲に存在するエージェントがいるセルを取得する
	 * @param x
	 * @param y
	 * @return
	 */
	public ArrayList<CellInfo> getPoint(int x,int y) {
		Field field = this.simulation.getField();
		int size = 3;
		// セル管理リストを初期化
		ArrayList<CellInfo> cellInScope = new ArrayList<CellInfo>();
		// セル管理リストに範囲内のセルを記録
		// 周囲から半径分の矩形空間を探索
		for (int searchX = x - size + 1; searchX < x + size; searchX++) {
			for (int searchY = y - size + 1; searchY < y + size; searchY++) {
				// 対象のセルを取得
				CellInfo cell = field.getCell(searchX, searchY);
				if (cell != null) { // 対象のセルが存在
					if(cell.getType()==CellInfo.ObjectType.AGENT){
						int distX = x - searchX;
						int distY = y - searchY;
						// 円形の範囲内
						if (distX * distX + distY * distY <= size * size) {
							cellInScope.add(cell);
						}
					}
				}
			}
		}
		return cellInScope;
	}


	/**
	 * ファイルに結果を書き出す
	 */
	public void outputFile() {
		// 書きだすファイル名の初期化
		String outputFileName = this.simulation.getName() + ".csv";
		// CSVReaderWriter.write(outputFileName, ""); // 書き出しファイルの初期化
		FileReaderWriter.write(outputFileName, this.outputStr);
	}

	/**
	 * 配列を文字列化する
	 *
	 * @return
	 */
	private StringBuffer toStrArray(ClusterArray cluster) {
		int x = cluster.getSizeX();
		int[] array = cluster.getArray();
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if (i % x == 0) {
				str.append(((i != 0) ? "\n" : ""));
			} else {
				str.append(",");
			}
			str.append(array[i]);
		}
		str.append("\n\n");
		return str;
	}

//	private void fill2(ClusterArray cluster,int x,int y,int clusterValue){
//		cluster.set(clusterValue, x, y);	// 開始地点のクラスタを変える
//
//		LinkedList<CellInfo> buff = new LinkedList<CellInfo>();	// 塗りつぶし用バッファ
//
//		while(buff.size()>0){
//			// バッファからひとつ取り出す
//			CellInfo seed = buff.pop();
//
//			if(getCellValue(cluster,seed.getX(),seed.getY())==clusterValue){
//				continue;
//			}
//
//			int leftX=seed.getX();
//			for(;0<=leftX;leftX--){
//
//			}
//		}
//	}
//
//	private void fillPaint(LinkedList<CellInfo> buff,int clusterValue){
//
//	}
//
//	private void scanLine(LinkedList<CellInfo> buff,ClusterArray cluster,int startX,int endX,int y){
//		Field field = this.simulation.getField();
//		while(startX<=endX){
//			CellInfo cell;
//			int value;
//			for(;startX<=endX;startX++){
//				cell = field.getCell(startX, y);
//				value = cluster.get(cell.getX(), cell.getY());
//				if(value==1){
//					break;
//				}
//			}
//			cell = field.getCell(startX, y);
//			value = cluster.get(cell.getX(), cell.getY());
//			if(value!=1){
//				break;
//			}
//
//			for(;startX<=endX;startX++){
//				cell = field.getCell(startX, y);
//				value = cluster.get(cell.getX(), cell.getY());
//				if(value!=1){
//					break;
//				}
//			}
//
//			CellInfo seed = new CellInfo(startX-1,y);
//			buff.push(seed);
//		}
//	}
//
	private int getCellValue(ClusterArray cluster, int x, int y) {
		Field field = this.simulation.getField();
		CellInfo getCell = field.getCell(x, y);
		int value = cluster.get(getCell.getX(), getCell.getY());
		return value;
	}
//
//	private void serchArroundCell(ClusterArray cluster,int x,int y,int distance,int clusterValue){
//		Field field = this.simulation.getField();
//		int sizeX = field.getSizeX();
//		int sizeY = field.getSizeY();
//
//		// 自分のいる位置にクラスタ番号を入れる
//		cluster.set(clusterValue, x, y);
//
//		for(int searchX=-sizeX;searchX<sizeX;searchX++){
//			for(int searchY=-sizeX;searchY<sizeY;searchY++){
//				int pointX = x+searchX;
//				int pointY = y+searchY;
//				CellInfo getCell = field.getCell(pointX, pointY);
//				boolean breakFlag = false;
//				for(int i=-1;i<1;i++){
//					if(breakFlag){
//						break;
//					}
//					for(int j=-1;j<1;j++){
//						if(breakFlag){
//							break;
//						}
//						CellInfo arroundCell = field.getCell(pointX+i, pointY+j);
//						int arroundValue = cluster.get(arroundCell.getX(), arroundCell.getY());
//						if(arroundValue>1){
//							cluster.set(arroundValue, getCell.getX(), getCell.getY());
//							breakFlag = true;
//						}
//					}
//				}
//			}
//		}
//
//
//	}
//
//	private void distance() {
//		this.cluster.init();
//		// エージェント全てにクラスタを与える
//		initCluster();
//
//		Field field = this.simulation.getField();
//		int sizeX = field.getSizeX();
//		int sizeY = field.getSizeY();
//
//		ClusterArray clusterDistance = new ClusterArray(sizeX,sizeY);
//		clusterDistance.init();
//		clusterDistance.update(0, 1);
//
//		/*
//		 * for(int x=0;x<this.cluster.getSizeX();x++){ for(int
//		 * y=0;y<this.cluster.getSizeY();y++){ this.cluster.set(-1, x, y); } }
//		 */
//		//this.cluster.update(0, 1);
//
//		// 山を分ける
//		for(int x=0;x<sizeX;x++){
//			for(int y=0;y<sizeY;y++){
//				ArrayList<CellInfo> list = getPoint(x,y);
//				int counter=0;
//				for(CellInfo cell:list){
//					if(cell.getType()==CellInfo.ObjectType.AGENT){
//						counter++;
//					}
//				}
//				if(counter<4){
//					clusterDistance.set(-1, x, y);
//				}
//			}
//		}
//
//		int cluster = 2;
//		for(int x=0;x<sizeX;x++){
//			for(int y=0;y<sizeY;y++){
//				int value = this.cluster.get(x, y);
//				if(value==1){
//					// 自分の周囲を途切れるまで探索
//					for(int searchX=0;searchX<sizeX;searchX++){
//						for(int searchY=0;searchY<sizeY;searchY++){
//							boolean is = search(sizeX, sizeY, clusterDistance, cluster, x,
//									y, searchX, searchY);
//							if(is){
//								break;
//							}
//						}
//						for(int searchY=sizeY-1;searchY>=0;searchY--){
//							boolean is = search(sizeX, sizeY, clusterDistance, cluster, x,
//									y, searchX, searchY);
//							if(is){
//								break;
//							}
//						}
//					}
//					for(int searchX=sizeX-1;searchX>=0;searchX--){
//						for(int searchY=0;searchY<sizeY;searchY++){
//							boolean is = search(sizeX, sizeY, clusterDistance, cluster, x,
//									y, searchX, searchY);
//							if(is){
//								break;
//							}
//						}
//						for(int searchY=sizeY-1;searchY>=0;searchY--){
//							boolean is = search(sizeX, sizeY, clusterDistance, cluster, x,
//									y, searchX, searchY);
//							if(is){
//								break;
//							}
//						}
//					}
//				}
//				cluster++;
//			}
//		}
//
//
//		// クラスタにアップデート
//		for(int x=0;x<sizeX;x++){
//			for(int y=0;y<sizeY;y++){
//				int value = clusterDistance.get(x, y);
//				if(value==1){
//					this.cluster.set(value, x, y);
//				}
//			}
//		}
//	}
//
//	private boolean search(int sizeX, int sizeY, ClusterArray clusterDistance,
//			int cluster, int x, int y, int searchX, int searchY) {
//		int pointX = x+searchX;	// 探索先
//		int pointY = y+searchY;	// 探索先
//		if(pointX>=sizeX){
//			pointX -= sizeX;
//		}
//		if(pointY>=sizeY){
//			pointY -= sizeY;
//		}
//
//		boolean isCluster = seachCluster(clusterDistance,cluster, pointX, pointY);
//		if(isCluster){
//			clusterDistance.set(cluster, pointX, pointY);
//		}else{
//			return true;
//		}
//
//		return false;
//	}
//
//	private boolean seachCluster(ClusterArray clusterDistance, int cluster, int pointX, int pointY) {
//		// 探索先のクラスタ
//		int pointCluster = clusterDistance.get(pointX, pointY);
//		if(pointCluster==-1){
//			return false;
//		}else if(pointCluster==1){
//			return true;
//		}
//
//		return false;
//	}
//
//	private void hand3() {
//		// エージェント全てにクラスタを与える
//		initCluster();
//		int num = 10;
//		ClusterArray[] cArrays = new ClusterArray[num];
//		Field field = this.simulation.getField();
//		int sizeX = field.getSizeX();
//		int sizeY = field.getSizeY();
//		for (int i = 0; i < num; i++) {
//			cArrays[i] = this.cluster.clone();
//		}
//		for (int i = 0; i < num; i++) {
//			hand3Parts(cArrays[i]);
//		}
//		for (int x = 0; x < sizeX; x++) {
//			for (int y = 0; y < sizeY; y++) {
//				// クラスタをキーにしてそれが何回出てくるかを数えるマップ
//				HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
//				for (int i = 0; i < num; i++) {
//					int cluster = cArrays[i].get(x, y);
//					if (map.containsKey(cluster)) { // 既にあった時
//						int counter = map.get(cluster);
//						map.put(cluster, counter + 1);
//					} else { // まだ出てなかった時
//						map.put(cluster, 1);
//					}
//				}
//				int max = 0;
//				int maxKey = 0;
//				Set<Integer> keySet = map.keySet();
//				Iterator<Integer> keyIte = keySet.iterator();
//				while (keyIte.hasNext()) {
//					Integer key = keyIte.next();
//					Integer value = map.get(key);
//					if (max < value) {
//						max = value;
//						maxKey = key;
//					}
//				}
//				this.cluster.set(maxKey, x, y);
//			}
//		}
//	}
//
//	/**
//	 * 乱数で取得した順番でエージェントを見てクラスタ分けをする
//	 */
//	private void hand3Parts(ClusterArray cArray) {
//		// エージェントリストを取得
//		ArrayList<CellAgent> agentList = this.simulation.getField()
//				.getAgentList();
//		ArrayList<CellAgent> shuffleList = MyUtil.shuffle(agentList);
//		// クラスタわけ
//		for (CellAgent agent : shuffleList) {
//			int hand = agent.getHistory().size();
//			int clusterNum = cArray.get(agent.getX(), agent.getY());
//			for (CellAgent history : agent.getHistory()) {
//				int historyHand = history.getHistory().size();
//				if (hand < historyHand) {
//					int x = history.getX();
//					int y = history.getY();
//					clusterNum = cArray.get(x, y);
//				}
//			}
//			for (CellAgent history : agent.getHistory()) {
//				int x = history.getX();
//				int y = history.getY();
//				cArray.set(clusterNum, x, y);
//			}
//			cArray.set(clusterNum, agent.getX(), agent.getY());
//		}
//	}
//
//	private void hand2() {
//		// エージェント全てにクラスタを与える
//		initCluster();
//		for (int i = 0; i < 3; i++) {
//			hand2Parts();
//		}
//	}
//
//	/**
//	 * 乱数で取得した順番でエージェントを見てクラスタ分けをする
//	 */
//	private void hand2Parts() {
//		// エージェントリストを取得
//		ArrayList<CellAgent> agentList = this.simulation.getField()
//				.getAgentList();
//		// クラスタわけ
//		for (CellAgent agent : agentList) {
//			int hand = agent.getHistory().size();
//			int clusterNum = this.cluster.get(agent.getX(), agent.getY());
//			for (CellAgent history : agent.getHistory()) {
//				int historyHand = history.getHistory().size();
//				if (hand < historyHand) {
//					int x = history.getX();
//					int y = history.getY();
//					clusterNum = this.cluster.get(x, y);
//				}
//			}
//			for (CellAgent history : agent.getHistory()) {
//				int x = history.getX();
//				int y = history.getY();
//				this.cluster.set(clusterNum, x, y);
//			}
//			this.cluster.set(clusterNum, agent.getX(), agent.getY());
//		}
//	}
//
//	/**
//	 * つながっている数
//	 */
//	private void hand() {
//		// エージェント全てにクラスタを与える
//		initCluster();
//		// エージェントリストを取得
//		ArrayList<CellAgent> agentList = this.simulation.getField()
//				.getAgentList();
//		// クラスタわけ
//		// 交換経路の最大値を求める
//		int maxHand = 0;
//		for (CellAgent agent : agentList) {
//			int hand = agent.getHistory().size();
//			if (maxHand < hand) {
//				maxHand = hand;
//			}
//		}
//		for (int i = 0; i < 10; i++) {
//			handClustering(agentList, maxHand - i);
//		}
//	}
//
//	/**
//	 * handに対応する場所から伸びているセルを同じクラスタにする
//	 *
//	 * @param agentList
//	 * @param hand
//	 */
//	private void handClustering(ArrayList<CellAgent> agentList, int hand) {
//		for (CellAgent agent : agentList) {
//			int crtHand = agent.getHistory().size();
//			int clusterNum = this.cluster.get(agent.getX(), agent.getY());
//			if (clusterNum == hand) {
//				for (CellAgent history : agent.getHistory()) {
//					int historyHand = history.getHistory().size();
//					int x = history.getX();
//					int y = history.getY();
//					if (crtHand < historyHand) {
//						clusterNum = this.cluster.get(x, y);
//					}
//				}
//				for (CellAgent history : agent.getHistory()) {
//					int x = history.getX();
//					int y = history.getY();
//					this.cluster.set(clusterNum, x, y);
//				}
//			}
//			this.cluster.set(clusterNum, agent.getX(), agent.getY());
//		}
//	}
//
//	private void center3() {
//		// エージェント全てにクラスタを与える
//		initCluster();
//		// エージェントリストを取得
//		ArrayList<CellAgent> agentList = this.simulation.getField()
//				.getAgentList();
//		// クラスタわけ
//		for (CellAgent agent : agentList) {
//			// 見ているエージェントのクラスタ番号を取得
//			int clusterNum = this.cluster.get(agent.getX(), agent.getY());
//			ArrayList<CellAgent> history = agent.getHistory(); // エージェントの交換履歴
//			for (CellAgent historyAgent : history) {
//				// 交換履歴のクラスタ番号を取得
//				int historyX = historyAgent.getX();
//				int historyY = historyAgent.getY();
//				this.cluster.set(clusterNum, historyX, historyY);
//			}
//		}
//		// 最後にクラスタを合成
//		ArrayList<Integer> clusterList = this.cluster.useCluster();
//		for (Integer cluster : clusterList) {
//			if (this.cluster.contains(cluster)) {
//				CellInfo center = this.cluster.getCenter(cluster);
//				for (Integer otherCluster : clusterList) {
//					if (!cluster.equals(otherCluster)
//							&& this.cluster.contains(otherCluster)) {
//						CellInfo otherCenter = this.cluster
//								.getCenter(otherCluster);
//						if (center.distance(otherCenter) <= 10) {
//							this.cluster.update(cluster, otherCluster);
//						}
//					}
//				}
//			}
//		}
//
//		// 周囲にある一番多いクラスタに属する
//		int sizeX = this.cluster.getSizeX();
//		int sizeY = this.cluster.getSizeY();
//		for (int x = 0; x < sizeX; x++) {
//			for (int y = 0; y < sizeY; y++) {
//				if (this.cluster.get(x, y) != 0) {
//					serchArround(x, y);
//				}
//			}
//		}
//	}
//
//	/**
//	 * 引数の位置の周りを調べる
//	 *
//	 * @param x
//	 * @param y
//	 */
//	private void serchArround(int x, int y) {
//		ArrayList<Integer> clusterList = new ArrayList<Integer>();
//		HashMap<Integer, Integer> clusterCounter = new HashMap<Integer, Integer>();
//		for (int i = -3; i <= 3; i++) {
//			for (int j = -3; j <= 3; j++) {
//				if (i != 0 && j != 0) {
//					if (0 <= x + i && x + i < this.cluster.getSizeX()) {
//						if (0 <= y + j && y + j < this.cluster.getSizeY()) {
//							int cluster = this.cluster.get(x + i, y + j);
//							if (cluster != 0) {
//								if (!clusterList.contains(cluster)) {
//									clusterList.add(cluster);
//									clusterCounter.put(cluster, 1);
//								} else {
//									Integer value = clusterCounter.get(cluster);
//									clusterCounter.put(cluster, value++);
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		if (clusterList.size() > 0) {
//			int maxCluster = clusterList.get(0);
//			for (Integer cluster : clusterList) {
//				int counter = clusterCounter.get(cluster);
//				int maxCounter = clusterCounter.get(maxCluster);
//				if (counter > maxCounter) {
//					maxCluster = cluster;
//				}
//			}
//
//			this.cluster.set(maxCluster, x, y);
//		}
//	}
//
//	private void center2() {
//		// エージェント全てにクラスタを与える
//		initCluster();
//		// エージェントリストを取得
//		ArrayList<CellAgent> agentList = this.simulation.getField()
//				.getAgentList();
//		// クラスタわけ
//		for (CellAgent agent : agentList) {
//			// 見ているエージェントのクラスタ番号を取得
//			int clusterNum = this.cluster.get(agent.getX(), agent.getY());
//			ArrayList<CellAgent> history = agent.getHistory(); // エージェントの交換履歴
//			for (CellAgent historyAgent : history) {
//				// 交換履歴のクラスタ番号を取得
//				int historyX = historyAgent.getX();
//				int historyY = historyAgent.getY();
//				this.cluster.set(clusterNum, historyX, historyY);
//			}
//		}
//		// 最後にクラスタを合成
//		ArrayList<Integer> clusterList = this.cluster.useCluster();
//		for (Integer cluster : clusterList) {
//			if (this.cluster.contains(cluster)) {
//				CellInfo center = this.cluster.getCenter(cluster);
//				for (Integer otherCluster : clusterList) {
//					if (!cluster.equals(otherCluster)
//							&& this.cluster.contains(otherCluster)) {
//						CellInfo otherCenter = this.cluster
//								.getCenter(otherCluster);
//						if (center.distance(otherCenter) <= 10) {
//							this.cluster.update(cluster, otherCluster);
//						}
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * 重心によるクラスタわけ
//	 */
//	private void center() {
//		// エージェント全てにクラスタを与える
//		initCluster();
//		// エージェントリストを取得
//		ArrayList<CellAgent> agentList = this.simulation.getField()
//				.getAgentList();
//		// クラスタわけ
//		for (CellAgent agent : agentList) {
//			// 見ているエージェントのクラスタ番号を取得
//			int clusterNum = this.cluster.get(agent.getX(), agent.getY());
//			CellInfo center = this.cluster.getCenter(clusterNum);
//			ArrayList<CellAgent> history = agent.getHistory(); // エージェントの交換履歴
//			for (CellAgent historyAgent : history) {
//				// 交換履歴のクラスタ番号を取得
//				int historyX = historyAgent.getX();
//				int historyY = historyAgent.getY();
//				int historyCluster = this.cluster.get(historyX, historyY);
//				// 交換履歴のクラスタの重心を取得
//				CellInfo historyCenter = this.cluster.getCenter(historyCluster);
//				// 重心同士の距離を計算
//				double distance = center.distance(historyCenter);
//				if (distance <= 10) {
//					this.cluster.set(clusterNum, historyX, historyY);
//				}
//			}
//		}
//	}
//
//	private void exchange3() {
//		// エージェント全てにクラスタを与える
//		initCluster();
//		// エージェントリストを取得
//		ArrayList<CellAgent> agentList = this.simulation.getField()
//				.getAgentList();
//		// クラスタわけ
//		for (CellAgent agent : agentList) {
//			int clusterNum = this.cluster.get(agent.getX(), agent.getY());
//			ArrayList<CellAgent> history = agent.getHistory();
//			for (CellAgent historyAgent : history) {
//				int historyX = historyAgent.getX();
//				int historyY = historyAgent.getY();
//				int historyCluster = this.cluster.get(historyX, historyY);
//				this.cluster.update(historyCluster, clusterNum);
//				// this.cluster.set(clusterNum, historyX,historyY);
//			}
//		}
//	}
//
//	private void exchange2() {
//		// エージェント全てにクラスタを与える
//		initCluster();
//		// エージェントリストを取得
//		ArrayList<CellAgent> agentList = this.simulation.getField()
//				.getAgentList();
//		// クラスタ番号を振る
//		for (CellAgent agent : agentList) {
//			int clusterNum = this.cluster.get(agent.getX(), agent.getY());
//			ArrayList<CellAgent> history = agent.getHistory();
//			for (CellAgent historyAgent : history) {
//				this.cluster.set(clusterNum, historyAgent.getX(),
//						historyAgent.getY());
//			}
//		}
//	}
//
//
//	/**
//	 * 交換した先を同じクラスタとみなす
//	 */
//	private void exchange() {
//		// フィールドのポインタ取得
//		Field field = this.simulation.getField();
//		// 使用しているクラスタ番号のインデックスを管理する
//		boolean[] useClusterIndex = new boolean[field.getAgentNum()];
//
//		initUseClusterIndex(useClusterIndex);
//
//		// エージェントを取得する
//		ArrayList<CellAgent> agentList = this.simulation.getField()
//				.getAgentList();
//		for (CellAgent agent : agentList) {
//			// どのセルにいるかを取得
//			CellInfo cell = agent.getCellFieldPointer();
//			// クラスタ番号を振る
//			// 振るクラスタ番号を取得
//			int cluster = this.cluster.get(cell.getX(), cell.getY());
//			if (cluster == 0) { // まだクラスタが振られていなければ
//				cluster = serchUseCluster(useClusterIndex);
//				useClusterIndex[cluster] = true;
//			}
//			// 現在地のクラスタを設定する
//			this.cluster.set(cluster, cell.getX(), cell.getY());
//			// 交換履歴を取得
//			ArrayList<CellAgent> history = agent.getHistory();
//			// 履歴のクラスタを全て同じクラスタにする
//			for (CellAgent agentHistory : history) {
//				CellInfo cellHistory = agentHistory.getCellFieldPointer();
//				int historyX = cellHistory.getX();
//				int historyY = cellHistory.getY();
//				int value = this.cluster.get(historyX, historyY);
//				if (value > 0) {
//					this.cluster.update(value, cluster);
//					useClusterIndex[value] = false;
//				}
//				this.cluster.set(cluster, cellHistory.getX(),
//						cellHistory.getY());
//			}
//		}
//	}

//	/**
//	 * 使用しているクラスタのインデックスを初期化する
//	 *
//	 * @param useClusterIndex
//	 *            初期化する配列
//	 */
//	private void initUseClusterIndex(boolean[] useClusterIndex) {
//		for (int i = 0; i < useClusterIndex.length; i++) {
//			useClusterIndex[i] = false;
//		}
//		useClusterIndex[0] = true;
//	}

//	/**
//	 * 使用していないクラスタ番号を検索する
//	 *
//	 * @param useClusterIndex
//	 *            探索する配列
//	 * @return
//	 */
//	private int serchUseCluster(boolean[] useClusterIndex) {
//		// 使ってないクラスタ番号を調べる
//		int i = 0;
//		for (boolean idx : useClusterIndex) {
//			if (idx == false) {
//				return i;
//			}
//			i++;
//		}
//		return -1;
//	}
}
