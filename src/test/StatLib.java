package test;



public class StatLib {

	

	// simple average
	public static float avg(float[] x){
		float sum =0;
		for (int i=0; i< x.length; i++)
			sum += x[i];
		return (sum / x.length);
	}

	// returns the variance of X and Y
	public static float var(float[] x){
		float n = 1/ (float)x.length;
		float sumFirst = 0;
		float moshe =0;
		for (int i=0; i < x.length; i++){
			sumFirst += (x[i] * x[i]);
			moshe += x[i];
		}
		moshe = (n* moshe) * (n* moshe);
		sumFirst = n * sumFirst;
		return (sumFirst - moshe);
	}
	// note to self, when done, change moshe into avg func, we don't need moshe...

	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y){
		float sum=0;

		for (int i =0; i<x.length; i++){
			sum = sum + ((x[i] - avg(x)) *(y[i] - avg(y)));
		}
		return (sum/x.length);
	}


	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y){
		float covariance = cov(x,y);
		float var1, var2;

		var1= (float) Math.sqrt(var(x));
		var2= (float) Math.sqrt(var(y));

		return covariance/(var1*var2);
	}

	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points){
		float a, b;
		float[] x = new float[points.length];
		float[] y = new float[points.length];

		for (int i =0; i< points.length; i++){
			x[i] = points[i].x;
			y[i] = points[i].y;
		}
		a = cov(x,y) / var(x);
		b = avg(y) - (a*avg(x));

		Line line = new Line(a,b);

		return line;
	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points){
		float dev;
		Line line;

		line = linear_reg(points);
		dev = dev(p,line);

		return dev;
	}

	// returns the deviation between point p and the line
	public static float dev(Point p,Line l){
		float dev;
		dev = Math.abs(p.y - l.f(p.x));
		return dev;
	}
}
