package model.agent;

import java.util.ArrayList;

import model.service.Service;
import model.service.ServiceList;

/**
 * サービスの交換を管理するクラス
 *
 * @author kohta
 *
 */
public class ServiceExchange {
	/**
	 * 2つのサービスの交換を行う
	 *
	 * @param main
	 *            メインのエージェント
	 * @param mainService
	 *            メインのサービスリストが受け取るサービス
	 * @param sub
	 *            サブのエージェント
	 * @param subService
	 *            サブのサービスリストが受け取るサービス
	 * @return
	 */
	private static void serviceExchange(Agent main,Service mainService, Agent sub, Service subService) {
		// メインからサブに渡すサービス
		Service mainToSubService = main.getServiceList().get(subService);
		double mainToSubTime = mainToSubService.getWorkTime(); // 交換に渡す時間
		mainToSubService.setWorkTime(0); // 交換した後だから0時間
		// サブからメインに渡すサービス
		Service subToMainService = sub.getServiceList().get(mainService);
		double subToMainTime = subToMainService.getWorkTime(); // 交換に渡す時間
		subToMainService.setWorkTime(0); // 交換した後だから0時間
		// メインとサブの交換に出したサービスの時間を加える
		mainService.addWorkTime(subToMainTime);
		subService.addWorkTime(mainToSubTime);
	}

	/**
	 * 2体のエージェントが単純な交換を行うメソッド
	 *
	 * @param agent1
	 *            交換を行うエージェント
	 * @param agent2
	 *            交換を行うエージェント
	 * @return 交換成功していればtrue
	 */
	public static boolean exchange(Agent main, Agent sub) {
		// return simpleExchange(main, sub);
		return maxExchange(main,sub);
		//return exchangeCalcServiceTime(main, sub);
	}

	/**
	 * メインのエージェントとエージェントのリストが交換を行う
	 * @param main
	 * @param agentList
	 * @return
	 */
	public static boolean exchange(Agent main, ArrayList<Agent> agentList){
		double totalTime = main.getServiceList().calcTotalServiceTime();
		// リスト全ての相手と交換を行う
		for (Agent agent : agentList) {
			// コピー同士でサービス交換を行う
			exchange(main, agent);
		}
		double exchangedTotalTime = main.getServiceList().calcTotalServiceTime();
		if(totalTime<=exchangedTotalTime){
			return false;
		}
		//System.out.println("ServiceExchange:"+totalTime+"->"+exchangedTotalTime);
		return true;
	}

	public static boolean exchange(CellAgent main,ArrayList<CellAgent> agentList){
		ArrayList<Agent> aList = new ArrayList<Agent>();
		for(CellAgent agent:agentList){
			aList.add(agent);
		}
		return exchange(main,aList);
	}

	/**
	 * 効率がよくなるように交換
	 *
	 * @param main
	 * @param sub
	 * @return
	 */
	public static boolean exchangeCalcServiceTime(Agent main, Agent sub) {
		// 現在の効率を取得
		double mainTotalServiceTime = main.calcTotalServiceTime();
		double subTotalServiceTime = sub.calcTotalServiceTime();
		// 効率順に並んだサービスリストを取得
		ServiceList mainServiceList = main.getServiceList().getSortedServiceListByEfficiency();
		ServiceList subServiceList = sub.getServiceList().getSortedServiceListByEfficiency();

		int mainSize = mainServiceList.getSize();	// for文用サイズ取得
		for (int i = 0; i < mainSize; i++) {
			// メインのエージェントの効率の高い順からサービスを取得
			Service mainService = mainServiceList.get(i);
			// メインサービスの作業時間を取得
			double mainTime = mainService.getWorkTime();
			if (mainTime > 0) {	// サービスの時間が残っている場合
				int subSize = subServiceList.getSize();	// for文用サイズ取得
				for (int j = 0; j < subSize; j++) {
					// サブのエージェントの効率の高い順からサービスを取得
					Service subService = subServiceList.get(j);
					// サブのサービスの作業時間を取得
					double subTime = subService.getWorkTime();
					if (!mainService.equals(subService)&&subTime>0) { // 違うサービスの場合かつ作業時間が残っている
						// もし交換した場合の効率計算
						double mainCalc = mainServiceList.calcTotalServiceTime(mainService, mainTime, subService, subTime);
						double subCalc = subServiceList.calcTotalServiceTime(subService, subTime, mainService, mainTime);
						// 最初より効率がよくなってたら
						if (mainCalc < mainTotalServiceTime&& subCalc < subTotalServiceTime) {
							// サービスを交換
							serviceExchange(main, mainService,sub, subService);
							double after = mainServiceList.calcTotalServiceTime();
							if(mainCalc<after){
								System.out.println("ServiceExchange:"+mainCalc+"->"+after);
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 2体のエージェントが交換可能な1番大きい作業効率を持つサービスの交換を行うメソッド。
	 *
	 * @param main
	 *            交換を行うエージェント
	 * @param sub
	 *            交換を行うエージェント
	 * @return 交換成功していればtrue
	 */
	public static boolean maxExchange2(Agent main, Agent sub) {
		// agent1のサービスリストを取得
		ServiceList mainServiceList = main.getServiceList();
		// agent2のサービスリストを取得
		ServiceList subServiceList = sub.getServiceList();

		// 合計の作業時間を取得
		double mainTotalTime = mainServiceList.calcTotalServiceTime();
		double subTotalTime = subServiceList.calcTotalServiceTime();

		// それぞれの交換可能な一番大きなサービスを取得
		Service mainService = mainServiceList.getMaxServiceByEfficiency();
		Service subService = subServiceList.getMaxServiceByEfficiency();

		// どちらかが存在しなければ交換出来ない
		if (mainService == null || subService == null) {
			return false;
		}

		// それぞれ相手の交換する場所を取得
		Service mainToSub = mainServiceList.get(subService);
		Service subToMain = subServiceList.get(mainService);

		// 交換した場合の作業時間を取得
		double mainTotalTimeIfChange = mainServiceList.calcTotalServiceTime(mainService, mainService.getWorkTime(), mainToSub, mainToSub.getWorkTime());
		double subTotalTimeIfChange = subServiceList.calcTotalServiceTime(subService, subService.getWorkTime(), subToMain, subToMain.getWorkTime());

		if(mainTotalTime>mainTotalTimeIfChange && subTotalTime>subTotalTimeIfChange){
			// 交換
			serviceExchange(main,mainService,sub,subService);
			return true;
		}

		return false;
	}

	/**
	 * 2体のエージェントが交換可能な1番大きい作業効率を持つサービスの交換を行うメソッド。
	 *
	 * @param main
	 *            交換を行うエージェント
	 * @param sub
	 *            交換を行うエージェント
	 * @return 交換成功していればtrue
	 */
	public static boolean maxExchange(Agent main, Agent sub) {
		// agent1のサービスリストを取得
		ServiceList mainServiceList = main.getServiceList();
		// agent2のサービスリストを取得
		ServiceList subServiceList = sub.getServiceList();

		// 合計の作業時間を取得
		double mainTotalTime = mainServiceList.calcTotalServiceTime();
		double subTotalTime = subServiceList.calcTotalServiceTime();

		// それぞれの交換可能な一番大きなサービスを取得
		Service mainService = mainServiceList.getMaxServiceByEfficiency();
		Service subService = subServiceList.getMaxServiceByEfficiency();

		// どちらかが存在しなければ交換出来ない
		if (mainService == null || subService == null) {
			return false;
		}

		// それぞれ相手の交換する場所を取得
		Service mainToSub = mainServiceList.get(subService);
		Service subToMain = subServiceList.get(mainService);

		// 交換した場合の作業時間を取得
		double mainTotalTimeIfChange = mainServiceList.calcTotalServiceTime(mainService, mainService.getWorkTime(), mainToSub, mainToSub.getWorkTime());
		double subTotalTimeIfChange = subServiceList.calcTotalServiceTime(subService, subService.getWorkTime(), subToMain, subToMain.getWorkTime());

		if(mainTotalTime>mainTotalTimeIfChange && subTotalTime>subTotalTimeIfChange){
			// 交換
			serviceExchange(main,mainService,sub,subService);
			return true;
		}

		return false;
	}

	/**
	 * 2体のエージェントが単純な交換を行うメソッド 1番高いもの同士を交換する
	 *
	 * @param agent1
	 *            交換を行うエージェント
	 * @param agent2
	 *            交換を行うエージェント
	 * @return 交換成功していればtrue
	 */
	public static boolean simpleExchange(Agent agent1, Agent agent2) {
		// agent1のサービスリストを取得
		ServiceList agent1ServiceList = agent1.getServiceList();
		// agent2のサービスリストを取得
		ServiceList agent2ServiceList = agent2.getServiceList();

		// それぞれの交換可能な一番大きなサービスを取得
		Service agent1Service = agent1ServiceList.getMaxServiceByEfficiency();
		Service agent2Service = agent2ServiceList.getMaxServiceByEfficiency();

		// どちらかが存在しなければ交換出来ない
		if (agent1Service == null || agent2Service == null) {
			return false;
		}

		// それぞれ相手の交換する場所を取得
		Service agent1toAgent2 = agent1ServiceList.get(agent2Service);
		Service agent2toAgent1 = agent2ServiceList.get(agent1Service);

		// 交換
		agent1toAgent2.setWorkTime(agent1Service.getWorkTime());
		agent1Service.setWorkTime(0);
		agent2toAgent1.setWorkTime(agent2Service.getWorkTime());
		agent2Service.setWorkTime(0);

		return true;
	}

	/**
	 * メインのエージェントとサブのエージェントを交換した時にどう変わったかメインのエージェントのコピーを返す
	 *
	 * @param mainAgent
	 * @param subAgent
	 * @return
	 */
	public static Agent ifExchange(Agent mainAgent, Agent subAgent) {
		// 自分のコピーを生成する
		Agent myCopy = mainAgent.clone();
		// 相手のコピーを生成する
		Agent subCopy = subAgent.clone();
		// コピー同士でサービスの交換を行う
		exchange(myCopy, subCopy);
		return myCopy;
	}

	/**
	 * メインのエージェントとエージェントのリストと交換した時にどう変わったかメインのエージェントのコピーを返す
	 *
	 * @param mainAgent
	 * @param agentList
	 * @return
	 */
	public static Agent ifExchange(Agent mainAgent, ArrayList<Agent> agentList) {
		// 自分のコピーを生成する
		Agent myCopy = mainAgent.clone();

		// 相手のコピーを保持する変数
		Agent otherCopy;
		// リスト全ての相手と交換を行う
		for (Agent agent : agentList) {
			otherCopy = agent.clone();
			// コピー同士でサービス交換を行う
			exchange(myCopy, otherCopy);
		}

		return myCopy;
	}

	/**
	 * メインのエージェントとエージェントのリストと交換した時にどう変わったかメインのエージェントのコピーを返す
	 *
	 * @param mainAgent
	 * @param agentList
	 * @return
	 */
	public static CellAgent ifExchange(CellAgent mainAgent,ArrayList<CellAgent> agentList) {
		ArrayList<Agent> aList = new ArrayList<Agent>();
		for(CellAgent agent:agentList){
			aList.add(agent);
		}
		return (CellAgent)ifExchange(mainAgent,aList);
	}
}
