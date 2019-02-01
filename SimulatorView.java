import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SimulatorView extends JFrame {
	private static final long serialVersionUID = 1L;

	// Colours used for empty locations.
	private static final Color EMPTY_COLOR = Color.white;

	// Colour used for objects that have no defined colour.
	private static final Color UNKNOWN_COLOR = Color.gray;

	private final String STEP_PREFIX = "Step: ";
	private final String POPULATION_PREFIX = "Population: ";
	private JLabel stepLabel, population;
	private FieldView fieldView;

	// A map for storing colours for participants in the simulation
	private Map<Class, Color> colours;
	// A statistics object computing and storing simulation information
	private FieldStats stats;

	/**
	 * Create a view of the given width and height.
	 * 
	 * @param height The simulation's height.
	 * @param width  The simulation's width.
	 */
	public SimulatorView(int height, int width) {
		stats = new FieldStats();
		colours = new LinkedHashMap<Class, Color>();

		setTitle("Fox and Rabbit Simulation");
		stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
		population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);

		setLocation(100, 50);

		fieldView = new FieldView(height, width);

		Container contents = getContentPane();
		contents.add(stepLabel, BorderLayout.NORTH);
		contents.add(fieldView, BorderLayout.CENTER);
		contents.add(population, BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}

	/**
	 * Define a colour to be used for a given class of animal.
	 * 
	 * @param animalClass The animal's Class object.
	 * @param colour       The colour to be used for the given class.
	 */
	public void setColor(Class animalClass, Color colour) {
		colours.put(animalClass, colour);
	}

	/**
	 * @return The colour to be used for a given class of animal.
	 */
	private Color getColor(Class animalClass) {
		Color col = colours.get(animalClass);
		if (col == null) {
			// no colour defined for this class
			return UNKNOWN_COLOR;
		} else {
			return col;
		}
	}

	/**
	 * Show the current status of the field.
	 * 
	 * @param step  Which iteration step it is.
	 * @param field The field whose status is to be displayed.
	 */
	public void showStatus(int step, Field field) {
		if (!isVisible()) {
			setVisible(true);
		}

		stepLabel.setText(STEP_PREFIX + step);
		stats.reset();

		fieldView.preparePaint();

		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Object animal = field.getObjectAt(row, col);
				if (animal != null) {
					stats.incrementCount(animal.getClass());
					fieldView.drawMark(col, row, getColor(animal.getClass()));
				} else {
					fieldView.drawMark(col, row, EMPTY_COLOR);
				}
			}
		}
		stats.countFinished();

		population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
		fieldView.repaint();
	}

	/**
	 * Determine whether the simulation should continue to run.
	 * 
	 * @return true If there is more than one species alive.
	 */
	public boolean isViable(Field field) {
		return stats.isViable(field);
	}

	/**
	 * Provide a graphical view of a rectangular field. This is a nested class (a
	 * class defined inside a class) which defines a custom component for the user
	 * interface. This component displays the field. This is rather advanced GUI
	 * stuff - you can ignore this for your project if you like.
	 */
	private class FieldView extends JPanel {
		private final int GRID_VIEW_SCALING_FACTOR = 6;

		private int gridWidth, gridHeight;
		private int xScale, yScale;
		Dimension size;
		private Graphics g;
		private Image fieldImage;

		/**
		 * Create a new FieldView component.
		 */
		public FieldView(int height, int width) {
			gridHeight = height;
			gridWidth = width;
			size = new Dimension(0, 0);
		}

		/**
		 * Tell the GUI manager how big we would like to be.
		 */
		public Dimension getPreferredSize() {
			return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR, gridHeight * GRID_VIEW_SCALING_FACTOR);
		}

		/**
		 * Prepare for a new round of painting. Since the component may be resized,
		 * compute the scaling factor again.
		 */
		public void preparePaint() {
			if (!size.equals(getSize())) { // if the size has changed...
				size = getSize();
				fieldImage = fieldView.createImage(size.width, size.height);
				g = fieldImage.getGraphics();

				xScale = size.width / gridWidth;
				if (xScale < 1) {
					xScale = GRID_VIEW_SCALING_FACTOR;
				}
				yScale = size.height / gridHeight;
				if (yScale < 1) {
					yScale = GRID_VIEW_SCALING_FACTOR;
				}
			}
		}

		/**
		 * Paint on grid location on this field in a given colour.
		 */
		public void drawMark(int x, int y, Color colour) {
			g.setColor(colour);
			g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
		}

		/**
		 * The field view component needs to be redisplayed. Copy the internal image to
		 * screen.
		 */
		public void paintComponent(Graphics g) {
			if (fieldImage != null) {
				Dimension currentSize = getSize();
				if (size.equals(currentSize)) {
					g.drawImage(fieldImage, 0, 0, null);
				} else {
					// Rescale the previous image.
					g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
				}
			}
		}
	}
}
