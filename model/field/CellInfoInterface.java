package model.field;

/**
 * セルに配置するオブジェクトを管理するインタフェース
 * CellFieldのcontentと双方向のポインタを持たせるためのインタフェース
 * @author kohta
 *
 */
public interface CellInfoInterface {
	/**
	 * このオブジェクトが配置されているセル情報を保持する
	 * @param cellField
	 */
	public void setCellFieldPointer(CellInfo cellField);
	/**
	 * このオブジェクトが配置されているセル情報を取得する
	 * @return
	 */
	public CellInfo getCellFieldPointer();
}
