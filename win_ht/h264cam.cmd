gst-launch-1.0 -v ksvideosrc  device-index=0 ! capsfilter caps="video/x-raw,width=640,height=480,framerate=30/1" ! videoconvert ! x264enc bitrate=15000 ! openh264dec ! videoconvert ! autovideosink
