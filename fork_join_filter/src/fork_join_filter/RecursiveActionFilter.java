package fork_join_filter;

import java.util.concurrent.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class RecursiveActionFilter extends RecursiveAction {
	private final int threshold = 1000000; //wstêpny próg
	private Mat image;
	
	public RecursiveActionFilter(Mat sourceImage) {
		image = sourceImage;
	}
	
	@Override
	protected void compute() {
		if(measure() < threshold) {
			Imgproc.bilateralFilter(image,image,9,75,75); //wstêpne wartoœci			
		}
		else {
			RecursiveActionFilter leftFilter = new RecursiveActionFilter(image.submat(new Range(0,image.rows()),new Range(0,image.cols()/2))); 
			RecursiveActionFilter rightFilter = new RecursiveActionFilter(image.submat(new Range(0,image.rows()),new Range(image.cols()/2,image.cols())));
			
			leftFilter.fork();
			rightFilter.compute();
			leftFilter.join();			
		}
	}
	
	private int measure() {
		return image.rows()*image.cols();
	}
}
