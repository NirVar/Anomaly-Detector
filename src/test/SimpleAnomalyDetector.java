package test;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
	float threshold = 0.9f;
	List<CorrelatedFeatures> correlatedFeaturesList = new ArrayList<>();
	List<AnomalyReport> anomalyReports = new ArrayList<>();

	@Override
	public void learnNormal(@NotNull TimeSeries ts) {

		Set<String> used = new HashSet<>();
		float newThreshold =0;
		float maxPears = 0;
		String header = null;

		for(String k : ts.table.keySet()){
			for(String p:ts.table.keySet()){
				if(used.contains(p) || ts.table.get(k) == ts.table.get(p))
					continue;  //if we already checked the column or if p and k are both the same column move on.
				newThreshold = Math.abs(StatLib.pearson(changeArrayList(ts.table.get(p)),changeArrayList(ts.table.get(k))));
				if(newThreshold >= this.threshold && newThreshold > maxPears) {
					maxPears = newThreshold;
					header = p;
				}
			}
			if(maxPears != 0) {
				Point points[] = pointsMaker(ts, k, header);
				Line correntLine = StatLib.linear_reg(points);
				CorrelatedFeatures correl = new CorrelatedFeatures(k, header, maxPears, correntLine, maxDev(correntLine, points));
				correlatedFeaturesList.add(correl);
			}
			used.add(k);
			maxPears = 0;
		}

	}
	public float[] changeArrayList(ArrayList<Float> first){
		int size = first.size();
		float[] fixed = new float[size];
		for(int i = 0; i < size; i++){
			fixed[i] = first.get(i);
		}
		return fixed;
	}

	public Point[] pointsMaker(@NotNull TimeSeries ts, String a, String b){
		int size = ts.table.get(a).size();
		Point[] points = new Point[size];
		for(int i = 0; i < size; i++){
			points[i] = new Point(ts.table.get(a).get(i),ts.table.get(b).get(i));
		}
		return points;
	}

	// see max dev for the matching columns
	public float maxDev(Line line, Point @NotNull [] points){
		float max = 0,tmp;
		for(int i = 0; i < points.length; i++){
			tmp = StatLib.dev(points[i],line);
			if(tmp > max)
				max = tmp;
		}
		return (max*1.1f);
	}
	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		CorrelatedFeatures corrent;
		Point point;
		for(int i = 0; i < correlatedFeaturesList.size(); i++){
			corrent = correlatedFeaturesList.get(i);
			for(int j = 0; j < ts.table.get(corrent.feature1).size(); j++){
				point = new Point(ts.table.get(corrent.feature1).get(j),ts.table.get(corrent.feature2).get(j));
				if(StatLib.dev(point,corrent.lin_reg) > corrent.threshold){
					AnomalyReport anomalyReport = new AnomalyReport(corrent.feature1 + "-" + corrent.feature2,j + 1);
					this.anomalyReports.add(anomalyReport);
				}
			}
		}
		return this.anomalyReports;
	}

	public List<CorrelatedFeatures> getNormalModel(){
		return this.correlatedFeaturesList;
	}
}
