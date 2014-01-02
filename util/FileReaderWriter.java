package util;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * CSV形式ファイルをRead,Writeするクラス
 *  2013/02/11
 * @author kohta
 *
 */
public class FileReaderWriter {
	/**
	 * ファイル出力
	 *
	 * @param writeFileName
	 *            出力先のファイル名
	 * @param ファイルに書き出すリスト
	 */
	public static void write(String writeFileName, ArrayList<String> strlist) {
		StringBuffer strBuff = new StringBuffer();
		for (String str : strlist) {
			strBuff.append(str + "\n");
		}
		write(writeFileName, strBuff);
	}

	/**
	 * stringBufferの内容をファイルに書き出す
	 *
	 * @param writeFileName
	 * @param strBuff
	 */
	public static void write(String writeFileName, StringBuffer strBuff) {
		write(writeFileName, strBuff.toString());
	}

	/**
	 * ファイルに書き出す
	 *
	 * @param writeFileName
	 * @param str
	 */
	public static void write(String writeFileName, String str) {
		try {
			// 出力ファイルを開く
			BufferedWriter out = new BufferedWriter(new FileWriter(
					writeFileName));
			// 書き出し
			out.write(str);
			// 終了
			out.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e);
		}
	}

	/**
	 * ファイルに追記
	 *
	 * @param writeFileName
	 *            追記するファイル名
	 * @param str
	 *            追記する内容
	 */
	public static void writeAppend(String writeFileName, String str) {
		try {
			// 出力ファイルを開く
			BufferedWriter out = new BufferedWriter(new FileWriter(writeFileName,true));
			// 書き出し
			out.write("\n"+str);
			// 終了
			out.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e);
		}
	}

	/**
	 * ファイル入力
	 * @param readFileName
	 * @return
	 */
	public static String read(String readFileName){
		StringBuffer strBuff = new StringBuffer();
		try{
			// 入力ファイルを開く
			BufferedReader in = new BufferedReader(new FileReader(readFileName));
			// 読み出されるString
			String input;
			// ファイルを終端まで読み出す
			while((input=in.readLine())!=null){
				strBuff.append(input+"\n");
			}
			// 終了
			in.close();
		}catch (IOException e) {
			JOptionPane.showMessageDialog(null, readFileName + "がありません");
		}
		return strBuff.toString();
	}

	/**
	 * ファイル入力
	 *
	 * @param readFileName
	 *            入力先のファイル名
	 * @return カンマ区切りにされたString
	 */
	public static ArrayList<String[]> readCSV(String readFileName) {
		ArrayList<String[]> readStr = new ArrayList<String[]>();
		try {
			// 入力ファイルを開く
			BufferedReader in = new BufferedReader(new FileReader(readFileName));
			// 読み出されるString
			String input;
			// ファイルの終端まで読み出す
			while ((input = in.readLine()) != null) {
				// 読み出された行をカンマ区切りにしたもの
				String[] inputline = input.split(",");
				readStr.add(inputline);
			}
			// 終了
			in.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, readFileName + "がありません");
		}
		return readStr;
	}
}
