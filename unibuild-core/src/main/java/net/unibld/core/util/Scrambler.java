package net.unibld.core.util;

public class Scrambler {
	/*  
	  |0000 0000|0400 0020|0000 0400|0020 0000|0000 0000|0020 8000|0020 0000|0000 0001|
	  |0000 8020|0401 0020|0020 0401|0020 0001|0000 0000|0020 8020|0020 0000|0020 0001|
	  |0020 8020|0401 0020|0420 0401|0020 0001|0000 8021|0020 8020|0020 0001|8021 0001|
	  |0021 8020|0401 0020|0420 0421|0420 8021|0400 8021|0020 8020|0020 0401|8021 0401|
	  |8021 8020|0401 0420|0420 0421|0420 8421|0400 8021|8420 8020|0420 0421|8021 8401|
	  |8021 8021|0401 0420|8421 0421|0420 8421|0421 8021|8420 8021|0420 8421|8021 8421|
	  |8021 8021|0421 8420|8421 8421|8421 8421|0421 8421|8420 8021|8420 8421|8421 8421|
	  |8421 8421|8421 8421|8421 8421|8421 8421|8421 8421|8421 8421|8421 8421|8421 8421|
	*/          
	              
	private static final byte[] bitscramble = new byte[]{
	  3, 0x20, 1, 0x40, 2, 0x04, 7, 0x01, 6, 0x20, 5, 0x20, 5, 0x08, 1, 0x02,
	  2, 0x20, 2, 0x01, 3, 0x01, 5, 0x02, 1, 0x10, 7, 0x20, 0, 0x08, 0, 0x02,
	  4, 0x08, 6, 0x01, 4, 0x01, 4, 0x02, 0, 0x20, 2, 0x40, 7, 0x10, 7, (byte) 0x80,
	  0, 0x10, 3, 0x02, 4, 0x40, 2, 0x02, 7, 0x04, 6, 0x04, 3, 0x08, 3, 0x40,
	  3, 0x04, 5, 0x40, 5, (byte) 0x80, 7, 0x08, 6, 0x40, 6, 0x02, 0, (byte) 0x80, 1, 0x04,
	  0, 0x01, 7, 0x02, 5, 0x01, 2, (byte) 0x80, 2, 0x10, 4, 0x20, 4, 0x10, 6, 0x08,
	  2, 0x08, 7, 0x40, 1, 0x20, 3, 0x10, 4, 0x04, 3, (byte) 0x80, 6, (byte) 0x80, 1, 0x08,
	  1, 0x01, 1, (byte) 0x80, 6, 0x10, 4, (byte) 0x80, 5, 0x10, 5, 0x04, 0, 0x40, 0, 0x04};

	
	private static final byte[] senderid = new byte[]{0x4E, 0x31, (byte) 0xC4, 0x2A, 0x57, 0x1C, (byte) 0xB9, (byte) 0xDA};
	
	public static byte[] scramble64(byte[] streamin,byte[] scrambler)
	{
	  char i, j;
	  byte[] streamout=new byte[streamin.length];
	  
	  if (scrambler==null) {
		  scrambler=bitscramble;
	  }
	  
	  j=0x80;
	  for(i=0; i<128; i+=2)
	  {
	    if((streamin[(i/16)] & j) != 0) streamout[scrambler[i]] |= scrambler[i+1]; //set bit in desired position
	    else streamout[scrambler[i]] &= (scrambler[i+1] ^ 0xFF); //clear bit in desired position
	    j >>= 1;
	    if(j == 0) j = 0x80;
	  }
	  return streamout;
	}

	public static byte[] deScramble64(byte[] streamin,byte[] scrambler)
	{
	  char i, j;
	  byte[] streamout=new byte[streamin.length];
		 
	  if (scrambler==null) {
		  scrambler=bitscramble;
	  }
	  j = 0x80;
	  for(i=0; i<128; i+=2)
	  {
	    if((streamin[scrambler[i]] & scrambler[i+1]) != 0) streamout[(i/16)] |= j; //set bit in desired position
	    else streamout[(i/16)] &= (j ^ 0xFF); //clear bit in desired position
	    j >>= 1;
	    if(j == 0) j = 0x80;
	  }
	  return streamout;
	}
	
	

	
} 