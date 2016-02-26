package jMESYS.core.cpu;

public interface CPU {
	
	public int m1(int pc, int mr);
	public int mem(int addr);
	public void mem(int addr, int v);
	public int in(int port);
	public void out(int port, int v);

	public int mem16(int addr);
	public void mem16(int addr, int v);
	
}
