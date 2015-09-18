package jMESYS.files.tape;

import java.util.Vector;

public class BasicTape {

		//private int numBlocks = 0;
		private String name = "";
		private byte[] tapeArray;
		private TapeBlock[] blocks;
		private int size = 0;
		private Vector v = new Vector();
		
		public TapeBlock getBlock(int i) {
			return blocks[i];
		}
				
		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

		public int getNumBlocks() {
			return blocks.length;
		}
		
		public void addBlock(TapeBlock tBlk) {
			v.addElement(tBlk);
			
			int numBlocks = v.size();
			blocks = new TapeBlock[ numBlocks ];
			
			for (int i = 0 ; i<numBlocks ; i++) {
				blocks[i] = (TapeBlock) v.elementAt(i);
			}
			
		}
		
		/*public void setNumBlocks(int numBlocks) {
			this.numBlocks = numBlocks;
		}*/
		
		public byte[] getTapeArray() {
			return tapeArray;
		}
		
		public void setTapeArray(byte[] tapeAray) {
			this.tapeArray = tapeAray;
		}
		
		public TapeBlock[] getBlocks() {
			return blocks;
		}
		
		public void setBlocks(TapeBlock[] blocks) {
			this.blocks = blocks;
		}
						
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String toString() {
			String str = "[NAME="+name+", SIZE="+size+" bytes, BLOCKS=[";
			
			for (int i=0 ; i<getNumBlocks() ; i++){
				str +="[";
				str += (blocks[i]).toString();
				str += "]";
				
				if (i != (getNumBlocks()-1)){
					str += ", ";
				}
			}
			
			str +="]]";
			
			return str;
		}
}
