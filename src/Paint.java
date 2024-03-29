//////////////////////////////////////////////////////////////////////////////
// file    : Paint.java
// content : basic painting app
//////////////////////////////////////////////////////////////////////////////

/* Imports */
import static java.lang.Math.*;
import java.util.Vector;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Point;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

/* Class Paint */

@SuppressWarnings("serial")
class Paint extends JFrame {
	Vector<Shape> shapes = new Vector<Shape>();
	Vector<Color> colors = new Vector<Color>();
	Color couleur = Color.BLACK;
	int menuWidth = 300, menuHeight = 235, radius=65, buttonSize=40, buttonSize2=40;
	CircularMenu circularMenu;
	Tool tool;
	JPanel panel;
	
	class ColorButton extends JButton implements MouseListener {
        Color  buttonColor;

        public ColorButton(int red, int green, int blue) {
            buttonColor = new Color(red, green, blue);
            setBackground(buttonColor);
            setText(" ");
            addMouseListener(this);
        }
        public void mouseClicked(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 * When the mouse passed on the button the color is selected and the circular menu removed
		 */
		public void mouseEntered(MouseEvent e) {
			couleur = buttonColor;
			if(circularMenu != null) {
				panel.remove(circularMenu);
				panel.repaint();
				openActionMenu();
			}
		}
		public void mouseExited(MouseEvent e) {}
    }
	
	// The color button on the toolbar
    ColorButton[] colorButtons = { new ColorButton(0, 0, 0), new ColorButton(255, 0, 0),
            new ColorButton(0, 255, 0), new ColorButton(0, 0, 255) };
    // The color button on the circular menu
    ColorButton[] colorButtonsCircular = { new ColorButton(0, 0, 0), new ColorButton(255, 0, 0),
            new ColorButton(108, 56, 187), new ColorButton(0, 0, 255), new ColorButton(0, 255, 255), new ColorButton(255, 0, 255) };

    /* Class Tool */ 
	class Tool extends AbstractAction implements MouseInputListener {
		Point o;
		Shape shape;

		
		public Tool(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			System.out.println("using tool " + this);
			panel.removeMouseListener(tool);
			panel.removeMouseMotionListener(tool);
			tool = this;
			panel.addMouseListener(tool);
			panel.addMouseMotionListener(tool);
		}
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 * When mouse clicked with right button CircularMenu is created and print
		 */
		public void mouseClicked(MouseEvent e) {
			if(e.getButton()==MouseEvent.BUTTON3) {
				if (circularMenu == null) {
					circularMenu = new CircularMenu(colorButtonsCircular,menuWidth, menuHeight, radius, buttonSize, buttonSize2);
					panel.add(circularMenu);
					circularMenu.setLocation(getMousePosition().x-menuWidth/2-buttonSize, getMousePosition().y-menuHeight/2-buttonSize);
					circularMenu.setVisible(true);
				}else if (circularMenu != null) {
					circularMenu.setVisible(false);
					circularMenu=null;
				}
			}
		}
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 * When mouse pressed with left button the circular menu is hidden
		 */
		public void mousePressed(MouseEvent e) {
				o = e.getPoint();
				if(e.getButton()==MouseEvent.BUTTON1) {
					if (circularMenu != null) {
						circularMenu.setVisible(false);
						circularMenu=null;
					}
				}
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) { shape = null; }
		public void mouseDragged(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
	}

	Tool tools[] = { new Tool("pen") {
		public void mouseDragged(MouseEvent e) {
			Path2D.Double path = (Path2D.Double) shape;
			if (path == null) {
				path = new Path2D.Double();
				path.moveTo(o.getX(), o.getY());
				shapes.add(shape = path);
				colors.add(couleur);
			}
			path.lineTo(e.getX(), e.getY());
			panel.repaint();
		}
	}, new Tool("rect") {
		public void mouseDragged(MouseEvent e) {
			Rectangle2D.Double rect = (Rectangle2D.Double) shape;
			if (rect == null) {
				rect = new Rectangle2D.Double(o.getX(), o.getY(), 0, 0);
				shapes.add(shape = rect);
				colors.add(couleur);
			}
			rect.setRect(min(e.getX(), o.getX()), min(e.getY(), o.getY()), abs(e.getX() - o.getX()),
					abs(e.getY() - o.getY()));
			panel.repaint();
		}
	}, new Tool("ellipse") {
		public void mouseDragged(MouseEvent e) {
			Ellipse2D.Double ellipse = (Ellipse2D.Double) shape;
			if (ellipse == null) {
				ellipse = new Ellipse2D.Double(o.getX(), o.getY(), 0, 0);
				shapes.add(shape = ellipse);
				colors.add(couleur);
			}
			ellipse.setFrame(min(e.getX(), o.getX()), min(e.getY(), o.getY()), abs(e.getX() - o.getX()),
					abs(e.getY() - o.getY()));
			panel.repaint();
		}
	} };
	
	/* Class ToolButton */
	class ToolButton extends JButton implements MouseListener{
        public ToolButton(AbstractAction action, String toolName) {
        	super(action);
        	this.setText(toolName);
            setBackground(Color.LIGHT_GRAY);
            addMouseListener(this);
        }
		public void mouseClicked(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 * When mouse enter in the button area 
		 */
		public void mouseEntered(MouseEvent e) {
			this.doClick();
			panel.remove(circularMenu);
			circularMenu.setVisible(false);
			circularMenu = null;
			panel.repaint();
		}
    }
    
	// We create a ToolButton list containing our actions
	ToolButton[] actionButtons = { new ToolButton(tools[0],"pen"),new ToolButton(tools[1],"rect"),new ToolButton(tools[2],"ell")};

	/*
	 * Function create a new circular menu
	 *  create new object CircularMenu and located it around the mouse position
	 *  and add it to the draw panel
	 */
	public void openActionMenu() {
		circularMenu = new CircularMenu(actionButtons,menuWidth+20, menuHeight, (int)(radius*1.2), (int)(buttonSize*1.55), buttonSize2);
		panel.add(circularMenu);
		circularMenu.setLocation(getMousePosition().x-menuWidth/2-buttonSize, getMousePosition().y-menuHeight/2-buttonSize);
	}
	
	/* Constructor */
	public Paint(String title) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(800, 600));
		
		/* 
		 * Add a tool bar on top
		 *  with all tool buttons
		 *  and all color buttons
		 */
		add(new JToolBar() {
			{
				for (AbstractAction tool : tools) {	add(tool); }
	        	for (JButton button : colorButtons) { add(button); }
			}
		}, BorderLayout.NORTH);

		/*
		 * Add a draw panel
		 */
		add(panel = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setColor(Color.WHITE);
				g2.fillRect(0, 0, getWidth(), getHeight());

				g2.setColor(couleur);
				for (int i=0; i < shapes.size() ;i++) {
					g2.setColor(colors.get(i));
					g2.draw(shapes.get(i));
				}
			}
		});

		pack();
		setVisible(true);
	}

	/* Main */
	public static void main(String argv[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Paint paint = new Paint("paint");
			}
		});
	}
}
