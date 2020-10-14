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
		JFrame frame = new JFrame();

		frame.setLayout(new BorderLayout());

		frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
		frame.setResizable(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
				} else if (radioPolygon.isSelected() && e.getButton() == MouseEvent.BUTTON1)
				{
					polygons.get(polygons.size() - 1).points.add(new Point(e.getX(),e.getY()));
					areaPolygon.append("[" + e.getX() + "," + e.getY() + "] \n");
				} else if (radioPolygon.isSelected() && e.getButton() == MouseEvent.BUTTON3)
				{
					redrawAll();
					//polygons.add(new Polygon());

				}





			}
		});

		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				/*
				x2 = e.getX();
				y2 = e.getY();


				draw();

				 */
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

	public void redrawAll(){
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

	public void draw(){

		clear(0x222222);
		rasterizer.line(x,y,x2,y2);
		panel.repaint();
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Main(800, 600).start());
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource() == radioPolygon)
		{
			polygons.add(new Polygon());
			areaPolygon.setText("");


		}

	}
}
