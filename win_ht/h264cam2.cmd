gst-launch-1.0 -v autovideosrc ! videoconvert ! capsfilter caps=video/x-raw,width=640,height=480 ! fakesink
