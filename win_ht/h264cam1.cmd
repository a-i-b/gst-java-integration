gst-launch-1.0 -v ksvideosrc  device-index=0 ! videoconvert ! x264enc bitrate=15000 ! openh264dec ! videoconvert ! autovideosink
