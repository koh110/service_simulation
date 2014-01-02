package util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * 自分用ライブラリ
 * 2013/02/09
 * @author kohta
 *
 */
public class MyUtil {
	/**
	 * min~maxまでの指定範囲乱数の取得
	 *
	 * @param min
	 * @param max
	 * @return min~maxまでの乱数
	 */
	public static double random(double min, double max) {
		return (Math.random() * (max - min + 1.0)) + min;
	}

	/**
	 * 引数(%)の割合でtrueを返す
	 *
	 * @param percent
	 * @return percentの確率でtrue
	 */
	public static boolean probability(double percent) {
		double rand_num = random(0, 100);
		if (rand_num < percent) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ランダム用インスタンス
	 */
	private static Random random = new Random();

	/**
	 * ガウス分布のシード値を初期化する
	 */
	public static void clearGausian(){
		random.setSeed(System.currentTimeMillis());
	}
	/**
	 * 正規分布に従う乱数
	 * @return
	 */
	public static double gausianRandNum(){
		return random.nextGaussian();
	}
	/**
	 * 正規分布に従う乱数
	 * @param deviation 標準偏差(広がり)
	 * @param ave 平均(軸)
	 * @return
	 */
	public static double gausian(double deviation,double ave){
		return random.nextGaussian()*deviation+ave;
	}

	/**
	 * ArrayListをシャッフルする
	 *
	 * @param list
	 * @return シャッフルしたArrayList
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList shuffle(ArrayList list) {
		ArrayList shuffle = (ArrayList) list.clone();
		for (int i = 0; i < shuffle.size(); i++) {
			int random = (int) MyUtil.random(0, shuffle.size() - 1);
			Object tmp = shuffle.get(0);
			shuffle.set(0, shuffle.get(random));
			shuffle.set(random, tmp);
		}
		return shuffle;
	}

	/**
	 *  引数で指定した配列をシャッフルする
	 * @param arr
	 */
	public static int[] shuffle(int[] arr) {
		for (int i = arr.length - 1; i > 0; i--) {
			int t = (int) (Math.random() * i); // 0～i-1の中から適当に選ぶ

			// 選ばれた値と交換する
			int tmp = arr[i];
			arr[i] = arr[t];
			arr[t] = tmp;
		}
		return arr;
	}
	public static double[] shuffle(double[] arr){
		for (int i = arr.length - 1; i > 0; i--) {
			int t = (int) (Math.random() * i); // 0～i-1の中から適当に選ぶ

			// 選ばれた値と交換する
			double tmp = arr[i];
			arr[i] = arr[t];
			arr[t] = tmp;
		}
		return arr;
	}

	/**
	 * LinkedListをシャッフルする
	 *
	 * @param list
	 * @return シャッフルしたLinkedList
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LinkedList shuffle(LinkedList list) {
		LinkedList shuffle = (LinkedList) list.clone();
		for (int i = 0; i < shuffle.size(); i++) {
			int random = (int) MyUtil.random(0, shuffle.size() - 1);
			Object tmp = shuffle.get(0);
			shuffle.set(0, shuffle.get(random));
			shuffle.set(random, tmp);
		}
		return shuffle;
	}
}
