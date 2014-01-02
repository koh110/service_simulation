package view.simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

/**
 * 描画を管理するgui label
 *
 * @author kohta
 *
 */
public class DrawPanel extends JPanel {
	static final long serialVersionUID = 0L;

	/**
	 * コンストラクタ
	 */
	DrawPanel() {
		super();
		// 背景色を白に指定
		// this.setBackground(Color.BLUE);
	}

	/**
	 * 色を設定する
	 *
	 * @param g
	 * @param color
	 */
	public void setColor(Graphics g, Color color) {
		g.setColor(color);
	}

	/**
	 * 色を設定する
	 *
	 * @param g
	 * @param red
	 * @param green
	 * @param blue
	 */
	public void serColor(Graphics g, int red, int green, int blue) {
		setColor(g, new Color(red, green, blue));
	}

	/**
	 * 色を設定する
	 *
	 * @param g
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 *            透明度
	 */
	public void setColor(Graphics g, int red, int green, int blue, int alpha) {
		setColor(g, new Color(red, green, blue, alpha));
	}

	/**
	 * 線を描く
	 *
	 * @param g
	 */
	public void drawLine(Graphics g, double startX, double startY, double endX,
			double endY) {
		draw(g, new Line2D.Double(startX, startY, endX, endY));
	}

	/**
	 * 箱を描く
	 *
	 * @param x
	 *            左上のX座標
	 * @param y
	 *            左上のY座標
	 * @param w
	 *            幅
	 * @param h
	 *            高さ
	 */
	public void drawRect(Graphics g, double x, double y, double w, double h) {
		draw(g, new Rectangle2D.Double(x, y, w, h));
	}

	/**
	 * 塗りつぶしで箱を描く
	 *
	 * @param x
	 *            左上のX座標
	 * @param y
	 *            左上のY座標
	 * @param w
	 *            幅
	 * @param h
	 *            高さ
	 */
	public void fillRect(Graphics g, double x, double y, double w, double h) {
		fill(g, new Rectangle2D.Double(x, y, w, h));
	}

	/**
	 * shapeに入れた図形を描く
	 *
	 * @param g
	 * @param shape
	 */
	private void draw(Graphics g, Shape shape) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.draw(shape);
	}

	/**
	 * shapeに入れた図形を描く
	 *
	 * @param g
	 * @param shape
	 */
	private void fill(Graphics g, Shape shape) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.fill(shape);
	}

	public void drawString(Graphics g,String str,double x,double y){
		Graphics2D g2 = (Graphics2D)g;

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		                      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2.drawString(str, (int)x, (int)y);
	}

	/**
	 * 表示ルーチン イメージバッファの内容を全面的にコピーする
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}
