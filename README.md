# MJPEG-to-JPG

This code saves JPG files from a MJPEG video stream. It was developed following the structure described here:

http://www.walking-productions.com/notslop/2010/04/20/motion-jpeg-in-flash-and-java/


The code identifies the header, writes jpg data byte by byte while waiting for the header of the next frame. 
Not all frames are saved due to the time it takes to read and write.


There might be more efficient solutions but this works well for taking a snapshot every second or so.
