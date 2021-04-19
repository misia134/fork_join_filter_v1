package fork_join_filter;

import java.util.concurrent.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.highgui.HighGui;

public class ImageFilter {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat image = Imgcodecs.imread("sunflowers.jpg",Imgcodecs.IMREAD_UNCHANGED);
		
		HighGui.imshow("Window 1",image);		
		
		final int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        final ForkJoinPool forkJoinPool = new ForkJoinPool(numberOfProcessors);

        forkJoinPool.submit(new RecursiveActionFilter(image));
        
        HighGui.imshow("Filtered image",image);
	}
}
