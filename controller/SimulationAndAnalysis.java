package controller;

import util.FileReaderWriter;
import analysis.AnalysisOutFile;
import analysis.ReadAnalysisFile;
import model.simulation.SimulationConsumeEnergy;
import model.simulation.SimulationConsumeService;
import model.simulation.SimulationThread;

/**
 * 指定回数シミュレーションを行って解析を行うクラス
 * @author kohta
 *
 */
public class SimulationAndAnalysis {
	private int LOOP_NUM = 10;
	/**
	 * プログラム開始地点
	 */
	private void start(){
		int loop = 0;
		StringBuffer serviceStrBuff = new StringBuffer();
		StringBuffer energyStrBuff = new StringBuffer();
		// ファイル書き出し用ファイル名
		String serviceFile = "consume";
		String energyFile = "consumeEnergy";
		while(loop<this.LOOP_NUM){
			SimulationConsumeService simulationConsumeService = new SimulationConsumeService();
			SimulationConsumeEnergy simulationConsumeEnergy = new SimulationConsumeEnergy();
			SimulationThread csThread = new SimulationThread(simulationConsumeService);
			SimulationThread ceThread = new SimulationThread(simulationConsumeEnergy);
			csThread.run();
			ceThread.run();
			// 両方のシミュレーションが終わっていたら
			while(true){
				System.out.println(simulationConsumeService.getStepNum());
				if(simulationConsumeService.getStepNum()>0 && !simulationConsumeService.isStart()
						&& simulationConsumeEnergy.getStepNum()>0 && !simulationConsumeEnergy.isStart()){
					break;
				}
			}
			ReadAnalysisFile serviceAnalysis = new ReadAnalysisFile(serviceFile,100,100);
			ReadAnalysisFile energyAnalysis = new ReadAnalysisFile(energyFile,100,100);
			String outStrService = AnalysisOutFile.clip(serviceAnalysis);
			String outStrEnergy = AnalysisOutFile.clip(energyAnalysis);
			serviceStrBuff.append(outStrService+"\n");
			energyStrBuff.append(outStrEnergy+"\n");
			loop++;
			System.out.println(loop);
		}
		FileReaderWriter.write(serviceFile+"Analysis2.csv", serviceStrBuff);
		FileReaderWriter.write(energyFile+"Analysis2.csv", energyStrBuff);
	}

	public static void main(String[] args){
		new SimulationAndAnalysis().start();
	}
}
