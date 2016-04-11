package jMESYS.core.video.cards;

import jMESYS.core.cpu.CPU;

public interface jMESYS_VideoCard {
	boolean init( char nPortBase );
	char read( char nPort );
	void write( char nPort, char bData );
	boolean reset();
	
	// ICycleTimedEvent methods
	boolean EventNotification( CPU cpu );
}
