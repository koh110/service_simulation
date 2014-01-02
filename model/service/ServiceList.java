package model.service;

import java.util.ArrayList;
import java.util.EnumSet;

import util.MyUtil;

import model.simulation.PassStepInterface;

/**
 * サービスの配列を扱うクラス
 *
 * @author kohta
 *
 */
public class ServiceList implements Cloneable, PassStepInterface {
	/**
	 * サービスを保持するリスト
	 */
	private ArrayList<Service> serviceList;

	/**
	 * コンストラクタ
	 */
	public ServiceList() {
		// サービスを保持するリストの初期化
		this.serviceList = new ArrayList<Service>();
	}

	/**
	 * サービスを追加する
	 *
	 * @param service
	 *            追加するサービス
	 */
	public void add(Service service) {
		this.serviceList.add(service);
	}

	/**
	 * サービスを取得する
	 *
	 * @param index
	 *            取得するサービスのindex
	 * @return
	 */
	public Service get(int index) {
		return this.serviceList.get(index);
	}

	/**
	 * サービスを取得する
	 *
	 * @param service
	 *            取得するサービス
	 * @return
	 */
	public Service get(Service service) {
		int index = this.serviceList.indexOf(service);
		return get(index);
	}

	/**
	 * リストのサイズを取得する
	 *
	 * @return
	 */
	public int getSize() {
		return this.serviceList.size();
	}

	/**
	 * そのサービスがリストに存在するか
	 *
	 * @param service
	 * @return
	 */
	public boolean contains(Service service) {
		return this.serviceList.contains(service);
	}

	/**
	 * タイプに一致したサービスを返す
	 *
	 * @param type
	 * @return 一致したサービスがあればそのオブジェクトを返す 存在しなければnullを返す
	 */
	public Service getService(ServiceType.Services type) {
		// 引数のtypeを持つものを探す
		for (Service service : this.serviceList) {
			// タイプが一致していたら
			if (service.getServiceType() == type) {
				return service;
			}
		}
		return null;
	}

	/**
	 * 作業効率の高い順のインデックスを取得する
	 *
	 * @return
	 */
	private int[] sortedIndexByEfficiency() {
		int size = this.serviceList.size();
		// インデックス管理用配列の生成
		int[] index = new int[size];
		// 値管理用配列の生成
		double[] values = new double[size];
		// インデックス管理用配列と値管理用配列の初期化
		for (int i = 0; i < size; i++) {
			index[i] = i;
			// インデックスに対応する作業効率を取得
			values[i] = this.serviceList.get(i).getOperantResource()
					.getEfficiency();
		}
		// ソートする
		sort(index, values);
		return index;
	}

	/**
	 * 作業品質の高い順のインデックスを取得する
	 *
	 * @return
	 */
	private int[] sortedIndexByQuality() {
		int size = this.serviceList.size();
		// インデックス管理用配列の生成
		int[] index = new int[size];
		// 値管理用配列の生成
		double[] values = new double[size];
		// インデックス管理用配列と値管理用配列の初期化
		for (int i = 0; i < size; i++) {
			index[i] = i;
			values[i] = this.serviceList.get(i).getOperantResource()
					.getQuality();
		}
		// ソートする
		sort(index, values);
		return index;
	}

	private int[] sortedIndexByWorkTime() {
		int size = this.serviceList.size();
		// インデックス管理用配列の生成
		int[] index = new int[size];
		// 値管理用配列の生成
		double[] values = new double[size];
		// インデック管理用配列と値管理用配列の初期化
		for (int i = 0; i < size; i++) {
			index[i] = i;
			values[i] = this.serviceList.get(i).getWorkTime();
		}
		// ソートする
		sort(index, values);
		return index;
	}

	/**
	 * valuesの値を元にindexの並び替えを行うソートメソッド
	 *
	 * @param index
	 * @param values
	 */
	private void sort(int[] index, double[] values) {
		// 実装面倒臭いのでバブルソートで……
		for (int i = 0; i < values.length; i++) {
			for (int j = i; j < values.length; j++) {
				if (values[i] < values[j]) { // 大きい値が出てきたら
					// indexとvaluesそれぞれで入れ替え
					swap(index, values, i, j);
					i--;
					break;
				}
			}
		}
	}

	/**
	 * 入れ替えを行うメソッド
	 *
	 * @param index
	 * @param values
	 * @param index1
	 *            入れ替える箇所1つめ
	 * @param jndex2
	 *            入れ替える箇所2つめ
	 */
	private void swap(int[] index, double[] values, int index1, int index2) {
		// valuesの入れ替え
		double crtValue = values[index1];
		values[index1] = values[index2];
		values[index2] = crtValue;
		// indexの入れ替え
		int tmp = index[index1];
		index[index1] = index[index2];
		index[index2] = tmp;
	}

	/**
	 * 作業効率順にソートされたサービスリストを返す
	 *
	 * @return
	 */
	public ServiceList getSortedServiceListByEfficiency() {
		int[] index = sortedIndexByEfficiency();
		return getSortedServiceList(index);
	}

	/**
	 * 品質順にソートされたサービスリストを返す
	 *
	 * @return
	 */
	public ServiceList getSortedServiceListByQuality() {
		int[] index = sortedIndexByQuality();
		return getSortedServiceList(index);
	}

	/**
	 * 作業時間でソートされたサービスリストを返す
	 *
	 * @return
	 */
	public ServiceList getSortedServiceListByWorkTime() {
		int[] index = sortedIndexByWorkTime();
		return getSortedServiceList(index);
	}

	/**
	 * index順にソートされたサービスリストを返す
	 *
	 * @param index
	 * @return
	 */
	private ServiceList getSortedServiceList(int[] index) {
		ServiceList sList = new ServiceList();
		for (int i = 0; i < index.length; i++) {
			sList.add(get(index[i]));
		}
		return sList;
	}

	/**
	 * 1番大きな交換可能なサービスを取得する
	 *
	 * @return 対応するサービス。存在しない場合null
	 */
	public Service getMaxServiceByEfficiency() {
		int[] index = sortedIndexByEfficiency();
		return getMaxService(index);
	}

	/**
	 * 1番大きな交換可能なサービスを取得する
	 *
	 * @return 対応するサービス。存在しない場合null
	 */
	public Service getMaxServiceByQuality() {
		int[] index = sortedIndexByQuality();
		return getMaxService(index);
	}

	/**
	 * index順にサービスを検索して最初に出てきた交換可能サービスを取得
	 *
	 * @param index
	 * @return 交換できるサービスが存在すればそれを返す。存在しない場合nullを返す
	 */
	private Service getMaxService(int[] index) {
		// サービスのサイズ分まわす
		for (int i = 0; i < this.serviceList.size(); i++) {
			Service service = get(index[i]);
			if (service.isExchangeable()) { // 交換可能な場合
				return service;
			}
		}
		return null;
	}

	/**
	 * サービスの合計実作業時間を求める
	 *
	 * @return
	 */
	public double calcTotalServiceTime() {
		double total = 0;
		for (Service service : this.serviceList) {
			total += service.calcServiceTime();
		}
		return total;
	}

	/**
	 * 引数のサービスを手放した場合の作業時間
	 *
	 * @param releaseService
	 *            手放すサービス
	 * @param releaseTime
	 *            手放す時間
	 * @return
	 */
	public double calcTotalServiceTime(Service releaseService,
			double releaseTime) {
		double total = 0;
		// 引数以外のサービスをトータルに加算する
		for (Service service : this.serviceList) {
			if (!service.equals(releaseService)) {
				total += service.calcServiceTime();
			}
		}
		// 手放す時間分の作業を引いた分の作業時間を加算する
		total += get(releaseService).calcServiceTimeSub(releaseTime);
		return total;
	}

	/**
	 * 引数のサービスを交換した場合の作業時間を計算する
	 *
	 * @param holdService
	 *            取得するサービス
	 * @param holdTime
	 *            取得する時間
	 * @param releaseService
	 *            手放すサービス
	 * @param releaseTime
	 *            手放す時間
	 *
	 * @return
	 */
	public double calcTotalServiceTime(Service holdService, double holdTime,
			Service releaseService, double releaseTime) {
		double total = 0;
		// 引数以外のサービスをトータルに加算する
		for (Service service : this.serviceList) {
			// 引数以外のサービスの場合
			if (!service.equals(holdService) && !service.equals(releaseService)) {
				total += service.calcServiceTime();
			}
		}
		// 手放す時間分の作業を引いた分の作業時間を加算する
		Service rService = get(releaseService);
		total += rService.calcServiceTimeSub(releaseTime);
		// 取得する時間分の作業を加算した分の作業時間を加算する
		Service hService = get(holdService);
		total += hService.calcServiceTimeAdd(holdTime);
		return total;
	}

	/**
	 * ディープコピーメソッド
	 */
	@Override
	public ServiceList clone() {
		ServiceList serviceList = new ServiceList();
		for (Service service : this.serviceList) {
			try {
				serviceList.add(service.clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return serviceList;
	}

	/**
	 * エージェントにサービスをセットする
	 *
	 * @param agent
	 */
	public static ServiceList createServiceList() {
		ServiceList serviceList;
		serviceList = createRandomServiceList();
		//serviceList = createGausianServiceList();

		return serviceList;
	}

	/**
	 * ランダムにサービスリストを生成する
	 *
	 * @return
	 */
	private static ServiceList createRandomServiceList() {
		// サービスのリストを初期化
		ServiceList serviceList = new ServiceList();
		// 生成出来る全てのサービスのセットを取得する
		EnumSet<ServiceType.Services> allSet = EnumSet
				.allOf(ServiceType.Services.class);
		// 生成サービス全てのサービスをセットする
		for (ServiceType.Services sType : allSet) {
			serviceList.add(new Service(sType));
		}
		return serviceList;
	}

	/**
	 * リソースの値をガウス分布で生成する
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	private static double generateResorce(double min, double max) {
		double resorceValue = 0;
		// ガウス分布の中心点
		double center = (max - min) / 2;
		// ガウス分布の広がり
		double div = (max - center) / 4;
		while (true) {
			resorceValue = MyUtil.gausian(div, center);
			if (min <= resorceValue && resorceValue <= max) {
				break;
			}
		}
		return resorceValue;
	}

	/**
	 * リソースの値をガウス分布で生成する 軸を低い方へずらす
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	private static double generateLowResorce(double min, double max) {
		double resorceValue = 0;
		// ガウス分布の中心点
		double center = (max - min) / 4;
		// ガウス分布の広がり
		double div = (max - center) / 4;
		while (true) {
			resorceValue = MyUtil.gausian(div, center);
			if (min <= resorceValue && resorceValue <= max) {
				break;
			}
		}
		return resorceValue;
	}

	/**
	 * 偏りを持ったサービスリストを生成する
	 *
	 * @return
	 */
	private static ServiceList createGausianServiceList() {
		// サービスリストの初期化
		ServiceList serviceList = new ServiceList();
		// 生成できる全てのサービスのセットを取得する
		EnumSet<ServiceType.Services> allSet = EnumSet
				.allOf(ServiceType.Services.class);
		// 生成サービス全てのサービスをセットする
		for (ServiceType.Services sType : allSet) {
			// ガウス分布に従ったオペランドリソースの生成
			double efficiency = 0;
			efficiency = generateResorce(OperantResource.getEfficiencyMin(),OperantResource.getEfficiencyMax());
			//efficiency = generateLowResorce(OperantResource.getEfficiencyMin(), OperantResource.getEfficiencyMax());
			double quality = 0;
			quality = generateResorce(OperantResource.getQualityMin(),OperantResource.getQualityMax());
			//quality = generateLowResorce(OperantResource.getQualityMin(),OperantResource.getQualityMax());
			// オペランドリソース
			OperantResource operant = new OperantResource(efficiency, quality);
			Service service = new Service(sType, operant);
			serviceList.add(service);
		}
		return serviceList;
	}

	/**
	 * ひとつだけオペラントリソースが低いサービスを生成する
	 * @return
	 */
	private static ServiceList createRowGausianServviceList() {
		// サービスリストの初期化
		ServiceList serviceList = new ServiceList();
		// 生成できる全てのサービスのセットを取得する
		EnumSet<ServiceType.Services> allSet = EnumSet
				.allOf(ServiceType.Services.class);
		// 生成サービス全てのサービスをセットする
		for (ServiceType.Services sType : allSet) {
			// ガウス分布に従ったオペランドリソースの生成
			double efficiency = 0;
			double quality = 0;
			if (sType == ServiceType.Services.COOKING) {
				efficiency = generateLowResorce(
						OperantResource.getEfficiencyMin(),
						OperantResource.getEfficiencyMax());
				quality = generateLowResorce(OperantResource.getQualityMin(),
						OperantResource.getQualityMax());
			} else {
				efficiency = generateResorce(
						OperantResource.getEfficiencyMin(),
						OperantResource.getEfficiencyMax());
				quality = generateResorce(OperantResource.getQualityMin(),
						OperantResource.getQualityMax());
			}
			// オペランドリソース
			OperantResource operant = new OperantResource(efficiency, quality);
			Service service = new Service(sType, operant);
			serviceList.add(service);
		}
		return serviceList;
	}

	/**
	 * 1ステップ経過時に行う処理
	 */
	@Override
	public void passStep() {
		for (Service service : this.serviceList) {
			service.passStep();
		}
	}

	public String toString() {
		String str = new String();
		str += "==Service==\n";
		for (Service service : this.serviceList) {
			str += service.toString() + "\n";
		}
		return str;
	}
}
