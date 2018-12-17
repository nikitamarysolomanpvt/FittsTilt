package ca.yorku.cse.mack.fittstilt;

public class Circle
{
	final static int NORMAL = 1;
	final static int TARGET = 2;
	
	public float x, y, radius;
	int status; // indicates if this circle is the target circle or a normal circle

	Circle(float xArg, float yArg, float radiusArg, int statusArg)
	{
		x = xArg;
		y = yArg;
		radius = radiusArg;
		status = statusArg;
	}
	
	public boolean inCircle(float xTest, float yTest, float diameterArg)
	{
		float dx = xTest - x;
		float dy = yTest - y;
		float distanceFromCenter = (float)Math.sqrt(dx * dx + dy * dy);
		return distanceFromCenter + diameterArg / 2f < radius;
	} 
}