package jMESYS.core.cpu.z80;

public interface Z80Env {
	
	int m1(int pc, int mr);
	int mem(int addr);
	void mem(int addr, int v);
	int in(int port);
	void out(int port, int v);

	int mem16(int addr);
	void mem16(int addr, int v);
	
}
