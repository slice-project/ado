package org.etri.ado.viewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.javatuples.Pair;

public class Agent {
	
	private static final float Scale = 310f;
	private static final int OFFSET = 10;
	public static final int WIDTH = 640;
	public static final int HEIGHT = 660; 
	
	private final String m_id;
	private final Color m_color;
	private Point m_loc = new Point(0,0);
		
	private static Random s_rand = new Random();
	private static final Color[] s_colors = {Color.BLACK, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, 
			Color.MAGENTA, Color.ORANGE, Color.PINK, Color.WHITE};
	private static Map<String, Color> s_colorMap = new HashMap<String, Color>();
	private static int Index = s_rand.nextInt(8);
	
	static {
		s_colorMap.put("predator1", Color.BLUE);
		s_colorMap.put("predator2", Color.BLUE);
		s_colorMap.put("predator3", Color.BLUE);
		s_colorMap.put("prey", Color.RED);
	}
	
	public Agent(String id) {
		m_id = id;
		if ( s_colorMap.containsKey(id) ) {
			m_color = s_colorMap.get(id);
		}
		else {
			m_color = s_colors[Index++ % 9];
		}
	}
	
	public String getId() {
		return m_id;
	}
	
	public synchronized void paint(Graphics g) {
		if ( m_loc == null ) {
			System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			return;
		}
		
		g.setColor(m_color);
		g.fillOval((m_loc.x - 40) + WIDTH, HEIGHT - (m_loc.y + 40), 80, 80);
		
		Point upperLeft = getPoint(-2, 2); 
		Point upperRight = getPoint(2, 2);
		Point lowerLeft = getPoint(-2, -2);
		Point lowerRight = getPoint(2, -2);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("굴림", Font.BOLD, 20));
		g.drawString(m_id, (m_loc.x - 42) + WIDTH, HEIGHT - (m_loc.y + 45));
		g.drawLine(upperLeft.x, upperLeft.y, upperRight.x, upperRight.y);
		g.drawLine(upperLeft.x, upperLeft.y, lowerLeft.x, lowerLeft.y);
		g.drawLine(lowerLeft.x, lowerLeft.y, lowerRight.x, lowerRight.y);
		g.drawLine(upperRight.x, upperRight.y, lowerRight.x, lowerRight.y);
	}
		
	public synchronized void setLocation(Pair<Float,Float> loc) {
		int x = (int) (loc.getValue0().floatValue() * Scale);
		int y = (int) (loc.getValue1().floatValue() * Scale) + OFFSET;
		m_loc = new Point(x, y);
	}
	
	private Point getPoint(int x, int y) {
		Point pt = new Point(x * (int)Scale, y * (int)Scale + OFFSET);
		pt.x = pt.x + WIDTH;
		pt.y = HEIGHT - pt.y;
		
		return pt;
	}
}
