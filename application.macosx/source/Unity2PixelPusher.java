import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import com.heroicrobot.dropbit.registry.*; 
import com.heroicrobot.dropbit.devices.pixelpusher.Pixel; 
import com.heroicrobot.dropbit.devices.pixelpusher.Strip; 
import java.util.*; 
import hypermedia.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Unity2PixelPusher extends PApplet {








private Random random = new Random();

DeviceRegistry registry;

class TestObserver implements Observer {
  public boolean hasStrips = false;
  public void update(Observable registry, Object updatedDevice) {
        println("Registry changed!");
        if (updatedDevice != null) {
          println("Device change: " + updatedDevice);
        }
        this.hasStrips = true;
    }
}

TestObserver testObserver;



UDP udp;  // define the UDP object
 
 byte[][] currentPacket;



public void setup() {
 size(7*50, 13*50, P2D); 

 registry = new DeviceRegistry();
 testObserver = new TestObserver();
 registry.addObserver(testObserver);
  
 colorMode(RGB, 255);
 
  udp = new UDP( this, 6000 );  // create a new datagram connection on port 6000

 //udp.log( true );         // <-- printout the connection activity
 udp.listen( true );           // and wait for incoming message  
 
 currentPacket = new byte[18+(130*10)+10][14];//are we getting 8? new byte[18+(130*10)+10][8];
 for(int i = 0;i<currentPacket[0].length;i++){
   for(int cable = 0;cable<currentPacket[1].length;cable++){
     
     
   currentPacket[i][cable] = 0;
 }
 }
}

public void draw() {
 background(0);
  //y is number of strips (14)
  //x is leds per strip (130)
  
  
  if (testObserver.hasStrips) {
        registry.startPushing();
        registry.setAutoThrottle(true);
        //registry.setAntiLog(true);
        int stripy = 0;
        List<Strip> strips = registry.getStrips();
        
        if (strips.size() > 0) {
          int yscale = width / strips.size();
          for(Strip strip : strips) {
            int xscale = height / strip.getLength();
            for (int stripx = 0; stripx < strip.getLength(); stripx++) {
               
            
               
   int pixelNum = 18+(3*stripx);
   
               int red = 0;
   int green = 0;
   int blue = 0;
   red = (currentPacket[pixelNum][stripy] < 0) ? (int)map(currentPacket[pixelNum][stripy], -128, 0, 128, 255) : currentPacket[pixelNum][stripy];  
  green = (currentPacket[pixelNum+1][stripy] < 0) ? (int)map(currentPacket[pixelNum+1][stripy], -128, 0, 128, 255) : currentPacket[pixelNum+1][stripy];  
  blue = (currentPacket[pixelNum+2][stripy] < 0) ? (int)map(currentPacket[pixelNum+2][stripy], -128, 0, 128, 255) : currentPacket[pixelNum+2][stripy];  

   
   int currentColor = color(red, green, blue);
  fill(currentColor);
               //fill(100,100,100);
               rect( (stripy*yscale),(stripx*xscale), yscale,xscale);
                  
               
                  int c;// = get(stripx*xscale, stripy*yscale);
               c = currentColor;
                strip.setPixel(c, stripx);
             }
            stripy++;
          }
        }
    }
}



 public void receive( byte[] data ) {          // <-- default handler
 for(int i = 0;i<data.length;i++){
   currentPacket[i][data[14]] = data[i];
 }
 
 }
 



  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Unity2PixelPusher" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
