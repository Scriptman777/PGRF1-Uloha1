import model.Line;
import model.Point;
import model.Polygon;
import rasterize.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * trida pro kresleni na platno: vyuzita tridy RasterBufferedImage
 * 
 * @author PGRF FIM UHK
 * @version 2020
 */

public class Main implements ActionListener{

	//Dispatcher pro
	private class DeleteDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyChar() == 'c') {
				clearAll();
			}
			return false;
		}
	}

	private JPanel panel;
	private JRadioButton radioPolygon;
	private JTextArea areaPolygon;
	private RasterBufferedImage raster;
	private int x = -1,y, x2, y2;
	private FilledLineRasterizer rasterizer;
	private DashedLineRasterizer dshRasterizer;
	private DottedLineRasterizer dotRasterizer;
	private PolygonRasterizer polyRasterizer;
	private List<Line> lines = new ArrayList<Line>();
	private List<Line> dashedLines = new ArrayList<Line>();
	private List<Line> dottedLines = new ArrayList<Line>();
	private List<Polygon> polygons = new ArrayList<Polygon>();


	public Main(int width, int height) {

		//Detekce zmáčknutí klávesy
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new DeleteDispatcher());

		JFrame frame = new JFrame();

		frame.setLayout(new BorderLayout());

		frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
		frame.setResizable(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		//Rasterizery
		raster = new RasterBufferedImage(width, height);
		rasterizer = new FilledLineRasterizer(raster);
		dshRasterizer = new DashedLineRasterizer(raster);
		dotRasterizer = new DottedLineRasterizer(raster);
		polyRasterizer = new PolygonRasterizer(raster);

		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				present(g);
			}
		};
		panel.setPreferredSize(new Dimension(width, height));


		frame.add(panel, BorderLayout.CENTER);

		//"Menu" pro výběr nástroje
		JPanel selector = new JPanel();
		selector.setLayout(new BoxLayout(selector, BoxLayout.PAGE_AXIS));
		frame.add(selector, BorderLayout.WEST);

		JRadioButton radioLine = new JRadioButton("Line");
		selector.add(radioLine);

		JRadioButton radioDotted = new JRadioButton("Dotted line");
		selector.add(radioDotted);

		JRadioButton radioDashed = new JRadioButton("Dashed line");
		selector.add(radioDashed);

		radioPolygon = new JRadioButton("Polygon");
		radioPolygon.addActionListener(this);
		selector.add(radioPolygon);

		areaPolygon = new JTextArea(20, 5);
		selector.add(areaPolygon);


		//Group pro radio tlačítka
		ButtonGroup bg = new ButtonGroup();
		bg.add(radioDotted);
		bg.add(radioDashed);
		bg.add(radioLine);
		bg.add(radioPolygon);



		frame.pack();
		frame.setVisible(true);




		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				if (radioLine.isSelected() || radioDashed.isSelected() || radioDotted.isSelected())
				{
					if (x == -1) {
						x = e.getX();
						y = e.getY();
					}
					else
					{
						x2 = x;
						y2 = y;
						Line ln = new Line(e.getX(),e.getY(),x2,y2,0x00ffff);
						if (radioLine.isSelected())
						{
							lines.add(ln);
						}
						if (radioDashed.isSelected())
						{
							dashedLines.add(ln);
						}
						if (radioDotted.isSelected())
						{
							dottedLines.add(ln);
						}
						redrawAll();
						//Nastavení x na nesmyslnou hodnotu, aby bylo možné kreslit novou čáru
						x = -1;
					}
				}

				if (radioPolygon.isSelected() && e.getButton() == MouseEvent.BUTTON3) {
					//Ukončení kreslení polygonu, začátek nového
					stageNewPolygon();

				}

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (radioPolygon.isSelected() && e.getButton() == MouseEvent.BUTTON1)
				{
					//Přidání nového bodu do polygonu
					polygons.get(polygons.size() - 1).points.add(new Point(e.getX(),e.getY()));
					areaPolygon.append("[" + e.getX() + "," + e.getY() + "] \n");
					raster.setPixel(e.getX(),e.getY(),0xffff00);
					redrawAll();
				}

			}

		});

		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {

				//Vykreslování pružné čáry
				if (radioPolygon.isSelected()) {
					int endX = e.getX();
					int endY = e.getY();
					drawDynamicLine(endX,endY);
				}
			}


		});

		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (panel.getWidth()<1 || panel.getHeight()<1)
					return;
				if (panel.getWidth()<=raster.getWidth() && panel.getHeight()<=raster.getHeight()) //no resize if new one is smaller
					return;
				RasterBufferedImage newRaster = new RasterBufferedImage(panel.getWidth(), panel.getHeight());

				newRaster.draw(raster);
				raster = newRaster;
				rasterizer = new FilledLineRasterizer(raster);

			}
		});

	}

	public void redrawAll() {
		//Metoda pro vykreslení všech uložených struktur
		clear(0x222222);
		for (Line ln:lines){
		rasterizer.line(ln.getX1(),ln.getY1(),ln.getX2(),ln.getY2());
		}
		for (Line dsh: dashedLines) {
		dshRasterizer.line(dsh.getX1(),dsh.getY1(),dsh.getX2(),dsh.getY2());
		}
		for (Line dtt: dottedLines) {
			dotRasterizer.line(dtt.getX1(),dtt.getY1(),dtt.getX2(),dtt.getY2());
		}
		for (Polygon pll: polygons) {
			polyRasterizer.drawPolygon(pll);
		}
		panel.repaint();
	}

	public void drawDynamicLine(int endX, int endY) {
		//Vykresluje čáru při tažení
		Point polyPoint = polygons.get(polygons.size()-1).getLastPoint();
		if (polyPoint != null)
		{
			clear(0x222222);
			redrawAll();
			rasterizer.line(polyPoint.x,polyPoint.y,endX,endY);
			panel.repaint();
		}

	}
	public void clear(int color) {
		raster.setClearColor(color);
		raster.clear();
	}


	public void present(Graphics graphics) {
		raster.repaint(graphics);
	}

	public void start() {
		clear(0xaaaaaa);
		raster.getGraphics().drawString("Use mouse buttons and try resize the window", 5, 15);
		panel.repaint();
	}

	public void clearAll()
	{
		clear(0x222222);
		lines.clear();
		dottedLines.clear();
		dashedLines.clear();
		polygons.clear();
		if (radioPolygon.isSelected())
		{
			//Přidání nového polygonu aby bylo kam ukládat nové body
			polygons.add(new Polygon());
		}
		panel.repaint();
		areaPolygon.setText("");

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Main(800, 600).start());
	}

	public void stageNewPolygon() {
		/*
		Podmínka zajišťující, aby nebylo možné vytvořit mnoho "prázdných" polygonů s 0 body. Vždy májí alespoň 1
		Polygon se vytvoří vždy pokud žádný ještě neexistuje (po spuštění nebo vymazání)
		*/
		if (polygons.size() == 0 || polygons.get(polygons.size()-1).points.size()>0)
		{
			polygons.add(new Polygon());
			areaPolygon.setText("");
		}
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource() == radioPolygon)
		{
			//Vytvoření nového polygonu když uživatel chce začít kreslit polygon
			stageNewPolygon();



		}

	}
}
