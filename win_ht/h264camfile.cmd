gst-launch-1.0 -v -e ksvideosrc  device-index=0 ! video/x-raw,width=640,height=480,framerate=30/1 ! videoconvert ! x264enc bitrate=15000 ! video/x-h264, codec_data=(buffer)017a001effe1001d677a001ebcd940a03db016a0c020a8000003000800000301e478b16cb001000568ebecb22c,stream-format=avc,width=640,height=480,framerate=30/1 ! mp4mux ! filesink location="D:/gst/videoscr.mp4"