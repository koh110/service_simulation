package analysis;

import java.util.ArrayList;

import model.field.CellInfo;

/**
 * クラスタを配列として扱うクラス
 * 1次元配列を2次元配列として扱う
 *
 * @author kohta
 *
 */
public class ClusterArray {
	/**
	 * x方向のサイズ
	 */
	private int sizeX;
	/**
	 * y方向のサイズ
	 */
	private int sizeY;
	/**
	 * 使用する配列
	 */
	private int[] array;

	/**
	 * コンストラクタ
	 *
	 * @param sizeX
	 * @param sizeY
	 */
	public ClusterArray(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.array = new int[sizeX * sizeY];
	}

	/**
	 * indexをx座標に変換
	 * @param index
	 * @return
	 */
	private int convertX(int index){
		return index % this.sizeX;
	}

	/**
	 * indexをy座標に変換
	 * @param index
	 * @return
	 */
	private int convertY(int index){
		return index / this.sizeY;
	}

	/**
	 * 値があるかどうか
	 * @param value
	 * @return
	 */
	public boolean contains(int value){
		for(int i=0;i<this.array.length;i++){
			if(this.array[i]==value){
				return true;
			}
		}
		return false;
	}

	/**
	 * 引数の値を持つセルの重心を取得する
	 * @return
	 */
	public CellInfo getCenter(int value){
		ArrayList<Integer> valueIndex = new ArrayList<Integer>();
		for(int i=0;i<this.array.length;i++){
			if(value==this.array[i]){
				valueIndex.add(i);
			}
		}
		/*
		if(valueIndex.size()<=0){
			return new CellInfo(-1,-1);
		}
		*/
		int totalX=0;
		int totalY=0;
		for(Integer index:valueIndex){
			int x = convertX(index);
			int y = convertY(index);
			totalX += x;
			totalY += y;
		}
		int averageX = totalX/valueIndex.size();
		int averageY = totalY/valueIndex.size();
		return new CellInfo(averageX,averageY);
	}

	/**
	 * 使用されているクラスタ郡を取得
	 * @return
	 */
	public ArrayList<Integer> useCluster(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=0;i<this.array.length;i++){
			if(this.array[i]!=0){
				if(!list.contains(this.array[i])){
					list.add(this.array[i]);
				}
			}
		}
		return list;
	}

	/**
	 * 引数のクラスタに属するエージェントの個数を求める
	 * @param value
	 * @return
	 */
	public int countAgentInCluster(int value){
		int counter=0;
		for(int i=0;i<this.array.length;i++){
			if(this.array[i]==value){
				counter++;
			}
		}
		return counter;
	}

	/**
	 * 初期化する
	 */
	public void init() {
		for (int i = 0; i < this.array.length; i++) {
			this.array[i] = 0;
		}
	}

	/**
	 * fromの値を持つ箇所をtoへ変更する
	 * @param from
	 * @param to
	 */
	public void update(int from,int to){
		for(int i=0;i<this.array.length;i++){
			if(this.array[i]==from){
				this.array[i] = to;
			}
		}
	}

	/**
	 * 引数の座標の値にセットする
	 *
	 * @param value
	 * @param x
	 * @param y
	 * @return
	 */
	public void set(int value, int x, int y) {
		this.array[y * this.sizeX + x] = value;
	}

	/**
	 * 引数の座標の値を取得する
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public int get(int x, int y) {
		int index = y * this.sizeX + x;
		return this.array[index];
	}

	/**
	 * x方向のサイズを取得
	 * @return
	 */
	public int getSizeX() {
		return this.sizeX;
	}

	/**
	 * y方向のサイズを取得
	 * @return
	 */
	public int getSizeY() {
		return this.sizeY;
	}

	/**
	 * 全体のサイズを取得
	 * @return
	 */
	public int getSize(){
		return this.array.length;
	}

	/**
	 * 1次元配列として取得
	 * @return
	 */
	public int[] getArray(){
		return this.array;
	}

	public ClusterArray clone(){
		ClusterArray cloneArray = new ClusterArray(this.sizeX,this.sizeY);
		for(int i=0;i<cloneArray.array.length;i++){
			cloneArray.array[i] = this.array[i];
		}
		return cloneArray;
	}
}
