package fork_join_filter;

import java.util.concurrent.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.highgui.HighGui;

public class ImageFilter {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat original = Imgcodecs.imread("sunflowers.jpg",Imgcodecs.IMREAD_UNCHANGED);
		Mat filtered = original.clone();
		
		HighGui.imshow("Original image",original);		
		
		final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

		RecursiveActionFilter recFilter = new RecursiveActionFilter(original,filtered);
		forkJoinPool.submit(recFilter);
		recFilter.join();
		        
        	HighGui.imshow("Filtered image",filtered);
        	HighGui.waitKey();
	}
}
