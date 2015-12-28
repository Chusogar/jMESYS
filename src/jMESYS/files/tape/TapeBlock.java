package jMESYS.files.tape;

public class TapeBlock {
	
	// variables
	private int blockNumber = 0;
	private int typeBlock = 0;
	private String toStr = "";
	private byte[] content;
	private int size = 0;
		
	public int getBlockNumber() {
		return blockNumber;
	}
	
	public void setBlockNumber(int blockNumber) {
		this.blockNumber = blockNumber;
	}
	
	public int getTypeBlock() {
		return typeBlock;
	}
	
	public void setTypeBlock(int typeBlock) {
		this.typeBlock = typeBlock;
	}
	
	public String getToStr() {
		return toStr;
	}
	
	public void setToStr(String toStr) {
		this.toStr = toStr;
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	public String toString() {
		String str = 
				"#"+blockNumber+
				", Type="+
				typeBlock+
				", msg="+
				toStr+
				", size="+
				size+" bytes"
				//+", Content="
				//+", Content="+new String(content)
				;
		/*if (typeBlock==0){
			int c=content.length;
			for (int i=0 ; i<c ; i++){
				str += Byte.toString( content[i] )+" ";
			}
		}*/
		
		return str;
	}
}
