package org.aib.gst;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.aib.av.IVideoStreamConsumer;
import org.aib.av.IVideoStreamProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.aib.gst", "org.aib.av"})
public class GstIntegrationApplication implements CommandLineRunner {
	static Logger logger = LoggerFactory.getLogger(GstIntegrationApplication.class);
	
	@Autowired
	private IVideoStreamProducer capture;

	@Autowired
	private IVideoStreamConsumer consumer;

	@Value("${gstreamer.pipesrc}")
	private String pipeline;

	@Value("${gstreamer.piperec}")
	private String pipelineRec;

	@Value("${gstreamer.pipefilerec}")
	private String pipelineFileRec;

	private Integer counter = 0;

	private static ExecutorService executor = Executors.newFixedThreadPool(5);

	public static void main(String[] args) {
	    SpringApplication app = new SpringApplication(GstIntegrationApplication.class);
        app.setBannerMode(Mode.OFF);
        app.run(args);
	}
	
	@Override
    public void run(String... args) throws Exception {
    	logger.info("Start...");

    	if(args.length == 0) {
        	logger.error("Parameters: -v or -p");
        	System.exit(0);
    	}
    	
    	Runtime.getRuntime().addShutdownHook(new Thread() {
    	    public void run() {
    	    	logger.info("EOS is sent...");
    	    	consumer.sendEos();
    	    }
    	 });	
    	if(args[0].equals("-v")) {
        	executor.submit(new Runnable() {
                @Override
                public void run() {
                	consumer.startWithoutAppSink(pipelineFileRec);
                }
        	});
    	} else if(args[0].equals("-p")) {    	    	
	    	executor.submit(new Runnable() {
	            @Override
	            public void run() {
	            	consumer.start(pipelineRec, 
	            		data -> {
			    			System.out.println("END "+ (++counter) +": " + data.remaining());
		
		            		try {
		    	    			Path path = Paths.get("D:\\gst\\image" + counter + ".jpg");
		    	    			System.out.println("END "+ (++counter) +": " + data.remaining());
		    		    		byte[] arr = new byte[data.remaining()];
		    		    		data.get(arr);
		    					Files.write(path, arr);
		    				} catch (Exception e) {
		    					e.printStackTrace();
		    				}
		        		});
	            }
	        });
    	}
    	    	
    	Thread.sleep(1000);

    	executor.submit(new Runnable() {
            @Override
            public void run() {
	    	capture.start(
	    		pipeline,
	    		data -> {	    	
					consumer.post(data);
	    	});
            }
    	});
    }
}
