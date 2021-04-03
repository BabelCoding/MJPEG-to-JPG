
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;


public class MJPEGParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		MJPEGParser mp = new MJPEGParser("http://192.168.1.4:8080/?action=stream", "username", "password");
	}

	
	
	public MJPEGParser(String mjpeg_url, String username, String password)
	{

		try {
			if (username != null && password != null)
			{
			    Authenticator.setDefault(new HTTPAuthenticator(username, password));
			}
					
			 int inputLine;
     		 String s="";
             byte[] buf = new byte[1]; //reading byte by byte 
             boolean headerRead = false;
             boolean saveImage = false; 
             boolean endJPG=false;
     		 int imageCount = 0; 

             
             URL url = new URL(mjpeg_url);
             BufferedInputStream in = new BufferedInputStream(url.openStream());
             OutputStream out = new FileOutputStream(imageCount+".jpg");
			
            while ((inputLine = in.read(buf)) != -1) {

            	if (s.contains("boundarydonotcross")){
            		System.out.println("Found a new header:");
     
            		headerRead = true;
            		saveImage = false;
                    endJPG=false;
            		s="";
            		
            		// in my case the header has this structure but might differ slightly
            		// --boundarydonotcross
            		// 
            		
            		
            		
            	}
            	else if (headerRead)
            	{
	        		s= s+ new String(buf, 0, inputLine);
	
	        		if (s.contains("X-Timestamp: 0.000000")){
	        			System.out.println(s);
	            		System.out.println("<<header ends>>");
	            		for (int i=0; i<=3; i++) in.read(buf);   //skip next 4 bytes (there is an empty line after the header)

	        			s="";
	        			imageCount++;

	        			headerRead = false;
	        			saveImage = true;
	        			endJPG=false;
	        			
	            		out = new FileOutputStream(imageCount+".jpg");
	        		}//end if (end header)
            	}
            	else if (saveImage)
            	{         		
            		// bytes that follow every "-" are temporarily written on a buffer 
            		// if the buffer corresponds to the stop sequence  jpg file is closed
            		// if the buffer does not correspond to the stop sequence, it will be written on file and normal reading resumes.
            		
            		// the stop sequence is the next header --boundarydonotcross
            		String[] stopsequence = "--bound".split(""); 
            		String c;
            		
            		System.out.println("Saving image...");

                    while(!endJPG){
                    	
                    	c = new String(buf, 0, inputLine);
                		byte writebuf[] = new byte [5];
                		int b=0;

                		if(c.contains("-")) 
                		{
	                		//pause writing,  look out for end sequence 
	
	                		for(int i=0;i<=4;i++) {
	                    		writebuf[i] = buf[0];
	                			if( !c.contains(stopsequence[i])) break;		
	                			if(i==4) endJPG=true;
	                			
	                			inputLine=in.read(buf);
	                        	c = new String(buf, 0, inputLine);
	                			b++;

	                		}//end for
	                		
	                		//it was not the end sequence, resume writing on file
	                        if(!endJPG) out.write(writebuf, 0, b+1);		
                                                
                		} else {
	                		
                			//continue writing as normal
                			out.write(buf, 0, inputLine);

                		}
                		//continue reading as normal
	            		inputLine=in.read(buf);

                    }//end while (saving image)
                    
                    saveImage=false;
                    out.close();
                    s="";
                    System.out.println(imageCount+".jpg   Saved. ");

            	}
            	else 
            	{
            		//read next char
            		s= s+ new String(buf, 0, inputLine);
            		
                }

            }//end while
            
        } catch (IOException e) {
            e.printStackTrace();
        }		
		
	}//end 
	

	static class HTTPAuthenticator extends Authenticator {
	    private String username, password;

	    public HTTPAuthenticator(String user, String pass) {
	      username = user;
	      password = pass;
	    }

	    protected PasswordAuthentication getPasswordAuthentication() {
	      System.out.println("Requesting Host  : " + getRequestingHost());
	      System.out.println("Requesting Port  : " + getRequestingPort());
	      System.out.println("Requesting Prompt : " + getRequestingPrompt());
	      System.out.println("Requesting Protocol: "
	          + getRequestingProtocol());
	      System.out.println("Requesting Scheme : " + getRequestingScheme());
	      System.out.println("Requesting Site  : " + getRequestingSite());
	      return new PasswordAuthentication(username, password.toCharArray());
	    }
	  }	

}//end class