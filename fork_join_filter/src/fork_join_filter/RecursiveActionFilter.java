package fork_join_filter;

import java.util.concurrent.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class RecursiveActionFilter extends RecursiveAction {
	private final int threshold = 100000; //próg
	private Mat original;
	private Mat filtered; 
	
	public RecursiveActionFilter(Mat sourceImage, Mat targetImage) {
		original = sourceImage;
		filtered = targetImage;
	}	
	
	@Override
	protected void compute() {
		if(measure() < threshold) {			
			Imgproc.bilateralFilter(original,filtered,9,75,75); //parametry filtrowania					
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
}
