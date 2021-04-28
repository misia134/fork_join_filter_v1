package fork_join_filter;

import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class RecursiveActionFilter extends RecursiveAction {
	private final int threshold = 100000; //próg
	private Mat original;
	private Mat filtered; 
	private Mat kernel;
	private List<Mat> rgb;
	private Mat r,g,b;
	private int kernelSum;
	
	public RecursiveActionFilter(Mat sourceImage, Mat targetImage) {
		original = sourceImage;
		filtered = targetImage;
		rgb = new ArrayList<Mat>();		
		kernel = Mat.ones(3,3,CvType.CV_32F);		
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {				
				if(i!=j) {
					kernel.put(i,j,2);
				}
				else {
					kernel.put(i,j,4);
				}				
			}
		}	
		kernelSum = 16;
	}	
	
	@Override
	protected void compute() {
		if(measure() < threshold) {			
			//Imgproc.bilateralFilter(original,filtered,9,75,75); //parametry filtrowania	
			Core.split(original,rgb);
			r = rgb.get(2);
			g = rgb.get(1);
			b = rgb.get(0);
			for(int i=1;i<original.rows()-1;i++) {
				for(int j=1;j<original.cols()-1;j++) {
					r.put(i,j,sum(r,i,j)/kernelSum);
					g.put(i,j,sum(g,i,j)/kernelSum);
					b.put(i,j,sum(b,i,j)/kernelSum);
				}
			}
			rgb.set(2,r);
			rgb.set(1,g);
			rgb.set(0,b);
			Core.merge(rgb,filtered);					
		}
		else {
			RecursiveActionFilter leftFilter = new RecursiveActionFilter(original.submat(new Range(0,original.rows()),new Range(0,original.cols()/2)),filtered.submat(new Range(0,filtered.rows()),new Range(0,filtered.cols()/2))); 
			RecursiveActionFilter rightFilter = new RecursiveActionFilter(original.submat(new Range(0,original.rows()),new Range(original.cols()/2,original.cols())),filtered.submat(new Range(0,filtered.rows()),new Range(filtered.cols()/2,filtered.cols())));
			
			leftFilter.fork();
			
			rightFilter.compute();
						
			leftFilter.join();						
		}
	}
	
	private int measure() {
		return original.rows()*original.cols();
	}
	
	private double sum(Mat source, int row, int col) {
		double sum = 0;		
		for(int i=-1;i<2;i++) {
			for(int j=-1;j<2;j++) {
				sum += source.get(row+i,col+j)[0]*kernel.get(1+i,1+j)[0];
			}
		}
		return sum;
	}
}
