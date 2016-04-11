package jMESYS.core.video.cards.TMS9928A;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

import jMESYS.core.cpu.CPU;
import jMESYS.core.video.cards.jMESYS_VideoCard;

public class TMS9928A implements jMESYS_VideoCard {
	private Component m_parent = null;

	private Image m_image = null;	//	off-screen image
	private MemoryImageSource m_imageSrc = null;
	private IndexColorModel	m_colorModel = null;
	private int	m_nPix[] = null;
	private Graphics m_g = null;	//	associated graphics object

	// TMS9928A specific variables
	private char m_nPortBase = 0;
	private char m_rgbVRAM[] = null;

	private int m_nChrGenerator = 0;
	private int m_nChrTable = 0;
	private int m_nColorTable = 0;
	private int m_nSpriteGenerator = 0;
	private int m_nSpriteTable = 0;
	private int m_nSpriteColor = 0;

	private int m_nFGColor = 0;
	private int m_nBGColor = 0;
	private int m_nScrMode = 0;
	private char m_rgbVDP[] = { 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	private char m_bVDPStatus = 0;
	private int m_bVKey = 0;
	private char m_write_VR = 0;

	private char m_WVAddr = 0, m_RVAddr = 0;

	static byte m_rgbRedPalette[] =
	{
		(byte)0x00,
		(byte)0x00,
		(byte)0x20,
		(byte)0x60,
		(byte)0x20,
		(byte)0x40,
		(byte)0xA0,
		(byte)0x40,
		(byte)0xE0,
		(byte)0xE0,
		(byte)0xC0,
		(byte)0xC0,
		(byte)0x20,
		(byte)0xC0,
		(byte)0xA0,
		(byte)0xE0
	};

	static byte m_rgbGreenPalette[] = 
	{
		(byte)0x00,
		(byte)0x00,
		(byte)0xC0,
		(byte)0xE0,
		(byte)0x20,
		(byte)0x60,
		(byte)0x20,
		(byte)0xC0,
		(byte)0x20,
		(byte)0x60,
		(byte)0xC0,
		(byte)0xC0,
		(byte)0x80,
		(byte)0x40,
		(byte)0xA0,
		(byte)0xE0
	};
	
	static byte m_rgbBluePalette[] = 
	{
		(byte)0x00,
		(byte)0x00,
		(byte)0x20,
		(byte)0x60,
		(byte)0xE0,
		(byte)0xE0,
		(byte)0x20,
		(byte)0xE0,
		(byte)0x20,
		(byte)0x60,
		(byte)0x20,
		(byte)0x80,
		(byte)0x20,
		(byte)0xA0,
		(byte)0xA0,
		(byte)0xE0
	};

	static byte m_rgbModeMasks[][] =
	{
		{ (byte)0x7F, (byte)0x00, (byte)0x3F, (byte)0x00 }, 
		{ (byte)0x7F, (byte)0xFF, (byte)0x3F, (byte)0xFF },
		{ (byte)0x7F, (byte)0x80, (byte)0x3C, (byte)0xFF },
		{ (byte)0x7F, (byte)0xFF, (byte)0x00, (byte)0xFF }
	};

	public TMS9928A(Component parent)
	{
		m_parent = parent;
	}

	public String toString()
	{
		return	"Texas Instruments TMS9928A video chip emulation";
	}

	// I8BitIoDevice implementation
	public boolean init( char nPortBase )
	{
		// Create the pixel buffer...
		m_nPix = new int[272 * 208];

		//	Create palettized color model
		m_colorModel = new IndexColorModel( 8, 16,
			m_rgbRedPalette, m_rgbGreenPalette, m_rgbBluePalette );

		reset();

		return	true;
	}

	public char read( char nPort )
	{

		char	bRetVal = 0;

		// Odd port - read VDP status register
		if ((nPort & 0x01)==0x01)
		{
			bRetVal = m_bVDPStatus;
			m_bVDPStatus = (char)((m_bVDPStatus^0x80) & 0xDF);
			m_bVKey = 1;

//			System.out.println("TMS9928A: ReadDevice( " + Integer.toHexString((int)nPort) +
//				"h ) - Reading video status BYTE (" + Integer.toHexString((int)m_bVDPStatus) +
//				"h)." );
		}
   
		// Even port - read VRAM
		else	
		{
			bRetVal = m_rgbVRAM[ m_RVAddr++ ];
			m_RVAddr &= 0x3FFF;

//			System.out.println("TMS9928A: ReadDevice( " + Integer.toHexString((int)nPort) +
//				"h ) - Reading video RAM offset " + Integer.toHexString((int)m_RVAddr) +
//				"h (" + Integer.toHexString((int)m_rgbVRAM[ m_RVAddr ]) + "h)");
		}

		return	bRetVal;
	}

	public void write( char nPort, char bData )
	{
		// Odd register write
		if ((nPort & 0x01)==0x01)
		{
			if (m_bVKey!=0)
			{
//				System.out.println( "TMS9928A: WriteDevice( " + Integer.toHexString((int)bData) +
//					"h ) - First BYTE of data for video write address (" + Integer.toHexString((int)bData) +
//					"h).");
				
				m_write_VR = bData;
				m_bVKey--;
			}
			else
			{
				m_bVKey++;
				switch( bData & 0xC0 )
				{
					case 0x80:
//						System.out.println( "TMS9928A: WriteDevice( " + Integer.toHexString((int)bData) +
//							"h ) - Writing " + Integer.toHexString((int)m_write_VR) +
//							"h to video register " + Integer.toHexString((int)bData & 0x07) +
//							"h.");

						VDPOut( (char)(bData & 0x07), m_write_VR );
						break;
					
					case 0x40: 
						m_WVAddr = (char)((m_WVAddr & 0xFF00) | (m_write_VR & 0xFF));
						m_WVAddr = (char)((m_WVAddr & 0x00FF) | ((bData & 0x3F) << 8));

//						System.out.println( "TMS9928A: WriteDevice( " + Integer.toHexString((int)bData) +
//							"h ) - Setting VRAM write address to " + Integer.toHexString((int)m_WVAddr) +
//							"h.");
						break;
					
					case 0x00:
						m_RVAddr = (char)((m_RVAddr & 0xFF00) | (m_write_VR & 0xFF));
						m_RVAddr = (char)((m_RVAddr & 0x00FF) | (bData << 8));

//						System.out.println( "TMS9928A: WriteDevice( " + Integer.toHexString((int)bData) +
//							"h ) - Setting VRAM read address to " + Integer.toHexString((int)m_RVAddr) +
//							"h.");
						break;
				}
			}
		}
		
		// Even register write
		else if (m_bVKey > 0)
		{
//			System.out.println("TMS9928A: WriteDevice( " + Integer.toHexString((int)bData) +
//				"h ) - Writing (" + Integer.toHexString((int)bData) +
//				"h) to video RAM at offset " + Integer.toHexString((int)m_WVAddr) +
//				"h.");
			m_rgbVRAM[ m_WVAddr++ ] = bData;
			m_WVAddr &= 0x3FFF;
		}
	}

	public boolean reset()
	{
		char VDPInit[] = { 0x00,0x10,0x00,0x00,0x00,0x00,0x00,0x00 };
		int	i;

		// Allocate video memory array
		m_rgbVRAM = new char[ 16384 ];

		// Setup initial values for VDP
		for (i = 0; i < 8; i++)
			m_rgbVDP[ i ] = VDPInit[ i ];

		m_bVKey = 1;
		m_WVAddr = 0x0000;
		m_RVAddr = 0x0000;
		
		m_bVDPStatus = 0x00;

		m_nFGColor = 
		m_nBGColor = 
		m_nScrMode = 0;

		m_nChrGenerator = 0;
		m_nChrTable = 0;
		m_nColorTable = 0;
		m_nSpriteGenerator = 0;
		m_nSpriteTable = 0;
		m_nSpriteColor = 0;

		// Empty out video ram
		for (i = 0; i < 16384; i++)
			m_rgbVRAM[ i ] = 0;

		return	true;
	}

	public ColorModel	getColorModel()
	{
		return	m_colorModel;
	}

	public void paint(Graphics g)
	{
		switch( m_nScrMode )
		{
			case	0:
				Render_Mode0_8bpp(m_nPix, 272);
				break;

			case	1:
				Render_Mode1_8bpp(m_nPix, 272);
				break;

			case	2:
				Render_Mode2_8bpp(m_nPix, 272);
				break;

			case	3:
				Render_Mode3_8bpp(m_nPix, 272);
				break;

			default:
				Render_ModeF_8bpp(m_nPix, 272);
				break;
		}

		// Create offscreen memory image of our display
		m_image = m_parent.createImage( new MemoryImageSource( 272, 208, m_colorModel,
			m_nPix, 0, 272));

		g.drawImage(m_image, 0, 0, 272, 208, null);
	}

	// ICycleTimedEvent implementation
	public boolean EventNotification( CPU cpu )
	{
		if	(m_nScrMode > 0)
			DoSpriteCollisionCheck();
		
		if ((m_rgbVDP[1] & 0x20)==0x20)
		{
			cpu.nmi();
			return	true;
		}

		return	false;
	}

	// Video mode 0 handlers
	private boolean Init_Mode0()
	{
		return	true;
	}
	
	private void Colors_Mode0()
	{
	}
	
	private void Render_Mode0_8bpp(int pBits[], int nPitch)
	{
		int X, Y, J, K;
		int P, S, T;

		P = 0;

		//	If screen turned on in VDP registers...
		if ( (m_rgbVDP[1] & 0x40)==0x40 )
		{
			//	Skip over border
			P += nPitch * (208 - 192) >> 1;

			// 
			P += (272 - 240) >> 1;
			T = m_nChrTable;

			for( Y=24; Y!=0; Y--, P += nPitch * 8 - 6 * 40 )
			{
				for( X=0; X < 40; X++, T++ )
				{
					// Find character generator index
					S = m_nChrGenerator + (m_rgbVRAM[ T ] << 3);
					
					for( J=0; J < 8; J++ )
					{
						//	Load K with character generator value
						K = m_rgbVRAM[ S++ ];

						pBits[ P++ ] = ((K & 0x80)==0x80) ? m_nFGColor : m_nBGColor;
						pBits[ P++ ] = ((K & 0x40)==0x40) ? m_nFGColor : m_nBGColor;
						pBits[ P++ ] = ((K & 0x20)==0x20) ? m_nFGColor : m_nBGColor;
						pBits[ P++ ] = ((K & 0x10)==0x10) ? m_nFGColor : m_nBGColor;
						pBits[ P++ ] = ((K & 0x08)==0x08) ? m_nFGColor : m_nBGColor;
						pBits[ P ]   = ((K & 0x04)==0x04) ? m_nFGColor : m_nBGColor;

						P += nPitch - 5;
					}
					
					P += 6 - nPitch * 8;
				}
			}
		}
		else
		{
			for (Y=0; Y < 208; Y++, P += nPitch)
				for (X=0; X < 272; X++)
					pBits[ P+X ] = m_nBGColor;
		}
	}

	// Video mode 1 handlers
	private boolean Init_Mode1()
	{
		return	true;
	}
	
	private void Colors_Mode1()
	{
	}
	
	private void Render_Mode1_8bpp(int pBits[], int nPitch)
	{
		int X, Y, J, K, BC, FC;
		int P, S, T;

		// Get index to top of true display area (inside part of display surface not
		//		including NTSC frame).
//		P = (BYTE*)lpvBits;
		P = 0;

		if ( (m_rgbVDP[1] & 0x40)==0x40 )
		{
			P += nPitch * (208 - 192) >> 1;

			// Increment past left side of NTSC frame and get the pointer
			//		to the character table.
			P += (272 - 256) >> 1;
			T = m_nChrTable;

			// Loop through each row and in a left to right algorithm and 
			//		draw the character display.
			for ( Y=24; Y!=0; Y--, P += nPitch * 8 - 8 * 32 )
				for ( X=0; X < 32; X++, T++)
				{
					// Use the current character table index, retrieve
					//		the character pattern index and add it to the
					//		character generator table to get the actual 
					//		character image.  Retrieve the background and
					//		foreground colors from the Color table also.
//					S = m_nChrGenerator + ((long) *T << 3);
					S = m_nChrGenerator + (m_rgbVRAM[ T ] << 3);

//					BC = *(m_nColorTable + (*T >> 3));
					BC = m_rgbVRAM[ m_nColorTable + (m_rgbVRAM[ T ] >> 3) ];

//					FC = m_rgbXPal[ BC >> 4 ];
					FC = BC >> 4;

//					BC = m_rgbXPal[ BC & 0x0F ];
					BC &= 0x0F;
					

					// Draw the character in a top to bottom fashion, moving the
					//		output drawing pointer along the way...
					for( J=0; J < 8; J++ )
					{
						// Get character bit-pattern for the J'th row.
//						K = *S++;
						K = m_rgbVRAM[ S++ ];

						// Render this character raster line
//						*P++ = K & 0x80 ? FC : BC;
//						*P++ = K & 0x40 ? FC : BC;
//						*P++ = K & 0x20 ? FC : BC;
//						*P++ = K & 0x10 ? FC : BC;
//						*P++ = K & 0x08 ? FC : BC;
//						*P++ = K & 0x04 ? FC : BC;
//						*P++ = K & 0x02 ? FC : BC;
//						*P = K & 0x01 ? FC : BC;
						pBits[ P++ ] = ((K & 0x80)==0x80) ? FC : BC;
						pBits[ P++ ] = ((K & 0x40)==0x40) ? FC : BC;
						pBits[ P++ ] = ((K & 0x20)==0x20) ? FC : BC;
						pBits[ P++ ] = ((K & 0x10)==0x10) ? FC : BC;
						pBits[ P++ ] = ((K & 0x08)==0x08) ? FC : BC;
						pBits[ P++ ] = ((K & 0x04)==0x04) ? FC : BC;
						pBits[ P++ ] = ((K & 0x02)==0x02) ? FC : BC;
						pBits[ P ]   = ((K & 0x01)==0x01) ? FC : BC;

						// Goto next row to finish drawing rest of char
						P += nPitch - 7;
					}

					// Move pointer back to top of character row.
					P += 8 - nPitch * 8;
				}

			// After all text, render the sprites...
			RenderSprites_8bpp( pBits, nPitch );

		}
		else
		{
			for (Y=0; Y < 208; Y++, P += nPitch)
				for (X=0; X < 272; X++)
					pBits[ P+X ] = m_nBGColor;
		}
	}

	// Video mode 2 handlers
	private boolean Init_Mode2()
	{
		return	true;
	}
	
	private void Colors_Mode2()
	{
	}
	
	private void Render_Mode2_8bpp(int pBits[], int nPitch)
	{
		int X, Y, J, K, N, BC, FC;
		int P, S, T, PGT, CLT, C;

		// Get index to top of true display area (inside part of display surface not
		//		including NTSC frame).
//		P = (BYTE*)lpvBits;
		P = 0;

		if ( (m_rgbVDP[1] & 0x40)==0x40 )
		{
			P += nPitch * (208 - 192) >> 1;

			P += (272 - 256) >> 1;
			PGT = m_nChrGenerator;
			CLT = m_nColorTable;
			T = m_nChrTable;

			for ( N=0; N < 3; N++, PGT += 0x0800, CLT += 0x0800 )
			{
				for ( Y=8; Y!=0; Y--, P += nPitch * 8 - 8 * 32 )
				{
					for ( X=0; X < 32; X++, T++ )
					{
//						S = PGT + ((long) *T << 3);
						S = PGT + (m_rgbVRAM[ T ] << 3);

//						C = CLT + ((long) *T << 3);
						C = CLT + (m_rgbVRAM[ T ] << 3);

						for ( J=0; J < 8; J++ )
						{
//							BC = *C++;
							BC = m_rgbVRAM[ C++ ];

//							K = *S++;
							K = m_rgbVRAM[ S++ ];

//							FC = m_rgbXPal[BC >> 4];
							FC = (BC >> 4) & 0xFF;

//							BC = m_rgbXPal[BC & 0x0F];
							BC = BC & 0x0F;


//							*P++ = K & 0x80 ? FC : BC;
//							*P++ = K & 0x40 ? FC : BC;
//							*P++ = K & 0x20 ? FC : BC;
//							*P++ = K & 0x10 ? FC : BC;
//							*P++ = K & 0x08 ? FC : BC;
//							*P++ = K & 0x04 ? FC : BC;
//							*P++ = K & 0x02 ? FC : BC;
//							*P = K & 0x01 ? FC : BC;
							pBits[ P++ ] = ((K & 0x80)==0x80) ? FC : BC;
							pBits[ P++ ] = ((K & 0x40)==0x40) ? FC : BC;
							pBits[ P++ ] = ((K & 0x20)==0x20) ? FC : BC;
							pBits[ P++ ] = ((K & 0x10)==0x10) ? FC : BC;
							pBits[ P++ ] = ((K & 0x08)==0x08) ? FC : BC;
							pBits[ P++ ] = ((K & 0x04)==0x04) ? FC : BC;
							pBits[ P++ ] = ((K & 0x02)==0x02) ? FC : BC;
							pBits[ P ]   = ((K & 0x01)==0x01) ? FC : BC;

							P += nPitch - 7;
						}
						P += 8 - nPitch * 8;
					}
				}
			}
			
			RenderSprites_8bpp( pBits, nPitch );
		}
		else
		{
			for (Y=0; Y < 208; Y++, P += nPitch)
				for (X=0; X < 272; X++)
					pBits[ P+X ] = m_nBGColor;
		}
	}

	// Video mode 3 handlers
	private boolean Init_Mode3()
	{
		return	true;
	}
	
	private void Colors_Mode3()
	{
	}

	private void Render_Mode3_8bpp(int pBits[], int nPitch)
	{
		int X, Y, J, C1, C2;
		int P, T, C;

		// Get index to top of true display area (inside part of display surface not
		//		including NTSC frame).
//		P = (BYTE*)lpvBits;
		P = 0;

		if ( (m_rgbVDP[1] & 0x40)==0x40 )
		{
			P += nPitch * (208 - 192) >> 1;
			P += (272 - 256) >> 1;
			T = m_nChrTable;
			C = m_nColorTable;

			for ( Y=0; Y < 24; Y++, P += nPitch * 8 - 8 * 32 )
			{
				for( X=0; X < 32; X++, T++ )
				{
//					C = m_nColorTable + ((long) * T << 3) + ((Y & 3) << 1);
					C = m_nColorTable + (m_rgbVRAM[ T ] << 3) + ((Y & 0x03) << 1);

//					C1 = m_rgbXPal[*C >> 4];
					C1 = (m_rgbVRAM[ C ] >> 4) & 0x0F;

//					C2 = m_rgbXPal[*C & 15];
					C2 = m_rgbVRAM[ C ] & 0x0F;

					for( J=0; J < 4; J++, P += nPitch - 7 )
					{ 
//						*P++ = C1;
//						*P++ = C1;
//						*P++ = C1;
//						*P++ = C1;
//						*P++ = C2;
//						*P++ = C2;
//						*P++ = C2;
//						*P = C2;
						pBits[ P++ ] = C1;
						pBits[ P++ ] = C1;
						pBits[ P++ ] = C1;
						pBits[ P++ ] = C1;
						pBits[ P++ ] = C2;
						pBits[ P++ ] = C2;
						pBits[ P++ ] = C2;
						pBits[ P ] = C2;
					}

					C++;


//					C1 = m_rgbXPal[*C >> 4];
					C1 = (m_rgbVRAM[ C ] >> 4) & 0x0F;

//					C2 = m_rgbXPal[*C & 15];
					C2 = m_rgbVRAM[ C ] & 0x0F;

					for( J=0; J < 4; J++, P += nPitch - 7 )
					{ 
//						*P++ = C1;
//						*P++ = C1;
//						*P++ = C1;
//						*P++ = C1;
//						*P++ = C2;
//						*P++ = C2;
//						*P++ = C2;
//						*P = C2;
						pBits[ P++ ] = C1;
						pBits[ P++ ] = C1;
						pBits[ P++ ] = C1;
						pBits[ P++ ] = C1;
						pBits[ P++ ] = C2;
						pBits[ P++ ] = C2;
						pBits[ P++ ] = C2;
						pBits[ P ] = C2;
					}

					P += 8 - 8 * nPitch;
				}
			}

			RenderSprites_8bpp( pBits, nPitch );

		}
		else
		{
			for (Y=0; Y < 208; Y++, P += nPitch)
				for (X=0; X < 272; X++)
					pBits[ P+X ] = m_nBGColor;
		}
	}

	// Misc handlers
	private boolean Init_ModeF()
	{
		return	true;
	}
	
	private void Colors_ModeF()
	{
	}

	private void Render_ModeF_8bpp(int pBits[], int nPitch)
	{
		int X, Y, P;

		P = 0;

		for (Y=0; Y < 208; Y++, P += nPitch)
			for (X=0; X < 272; X++)
				pBits[ P+X ] = m_nBGColor;
	}

	private void RenderSprites_8bpp(int pBits[], int nPitch)
	{
		int N, I, J, X, Y, C, P, T, S, L;

//		for ( N=0, S=m_lpbSprTab; (N < 32) && (S[0] != 208); N++, S+=4 );

		for ( N=0, S=m_nSpriteTable; (N < 32) && (m_rgbVRAM[S] != 208); N++, S+=4 )
			;

		if ( (m_rgbVDP[1] & 0x02)==0x02 )
		{
			for( S-=4; N!=0; N--, S-=4 )
			{
				L = ((m_rgbVRAM[S+3] & 0x80)==0x80) ? m_rgbVRAM[S+1] - 32 : m_rgbVRAM[S+1];
				C = m_rgbVRAM[S+3] & 0x0F;

				if ((L <= 256 - 16) && ( L >= 0) && C!=0)
				{
//					P = ((BYTE*)lpvBits) + nPitch * ((208 - 192) >> 1) + ((272 - 256) >> 1) + L;
					P = nPitch * ((208 - 192) >> 1) + ((272 - 256) >> 1) + L;
					T = m_nSpriteGenerator + ((m_rgbVRAM[S+2] & 0xFC) << 3);
//					C = m_rgbXPal[C];
					Y = m_rgbVRAM[S+0] + 1;

					if (Y < 192)
					{
						P += nPitch * Y;
						Y = (Y > 176) ? 192 - Y : 16;
					}
					else
					{
						T += 256 - Y;
						Y -= (Y > 240) ? 240 : Y;
					}

					for ( ;Y!=0; Y--, T++, P += nPitch - 8 )
					{
						for ( X=0, J = m_rgbVRAM[ T ], I = m_rgbVRAM[T + 16]; X < 8; X++, J <<= 1,I <<= 1, P++ )
						{
							if ((J & 0x80)==0x80)
								pBits[ P ] = C;

							if ((I & 0x80)==0x80)
								pBits[P + 8]  = C;
						}          
					}
				}
			}
		}
		else
		{
			for ( S -= 4; N!=0; N--, S -=4 )
			{
				L = ((m_rgbVRAM[S+3] & 0x80)==0x80) ? m_rgbVRAM[S+1] - 32 : m_rgbVRAM[S+1];
				C = m_rgbVRAM[S+3] & 0x0F;
				
				if ( (L <= 256 - 8) && (L >= 0) && C!=0 )
				{
//					P =((BYTE*)lpvBits) + nPitch * (208 - 192) / 2 + ((272) - 256) / 2 + L;
					P = nPitch * ((208 - 192) >> 1) + (((272) - 256) >> 1) + L;
					T = m_nSpriteGenerator + (m_rgbVRAM[S+2] << 3);
//					C = m_rgbXPal[C];
					Y = m_rgbVRAM[S+0] + 1;

					if (Y < 192)
					{
						P += nPitch * Y;
						Y = (Y > 184) ? 192 - Y : 8;
					}
					else
					{
						T += 256 - Y;
						Y -= (Y > 248) ? 248 : Y;
					}

					for( ; Y!=0; Y--, T++, P += nPitch - 8 )
					{
						for ( X=0, J = m_rgbVRAM[ T ]; X < 8; X++, J <<= 1, P++ )
						{
							if ((J & 0x80)==0x80)
								pBits[ P ] = C;
						}
					}
				}
			}
		}
	}
	
	private void DoSpriteCollisionCheck()
	{
		int LS, LD, DH, DV, PS, PD, T, I, J, N, S, D;

		m_bVDPStatus &= 0x80;

		for( N=0, S=m_nSpriteTable; (N < 32) && (m_rgbVRAM[S] != 208); N++, S+=4 )
			;

		if ( (m_rgbVDP[1] & 0x02)==0x02 )
		{
			for ( J=0, S=m_nSpriteTable; J < N; J++, S+=4 )
			{
				if ( (m_rgbVRAM[S+3] & 0x0F)==0x0F )
				{
					for( I = J + 1, D = S + 4; I < N; I++, D+=4 )
					{
						if( (m_rgbVRAM[D+3] & 0x0F)==0x0F )
						{
							DV = m_rgbVRAM[S+0] - m_rgbVRAM[D+0];

							if ((DV < 16) || (DV > 240))
							{
								DH = m_rgbVRAM[S+1] - m_rgbVRAM[D+1];

								if ((DH<16) || (DH>240))
								{
									PS = m_nSpriteGenerator + ((m_rgbVRAM[S+2] & 0xFC) << 3);
									PD = m_nSpriteGenerator + ((m_rgbVRAM[D+2] & 0xFC) << 3);
									
									if (DV < 16)
										PD += DV;
									else
									{
										DV = 256 - DV;
										PS += DV;
									}
									
									if (DH > 240)
									{
										DH = 256 - DH;
										T = PS;
										PS = PD;
										PD = T;
									}

									while(DV < 16)
									{
//										LS = ((WORD) *PS << 8) + *(PS + 16);
										LS = (m_rgbVRAM[ PS ] << 8) + m_rgbVRAM[ PS + 16 ];
										
//										LD = ((WORD) *PD << 8) + *(PD + 16);
										LD = (m_rgbVRAM[ PD ] << 8) + m_rgbVRAM[ PD + 16 ];

										if ((LD & (LS >> DH))==(LS >> DH))
											break;
										else
										{
											DV++;
											PS++;
											PD++;
										}
									}
		
									if(DV < 16)
									{
										m_bVDPStatus |= 0x20;
										return;
									}
								}
							}
						}
					}
				}
			}
		}
		else
		{
			for( J=0, S=m_nSpriteTable; J < N; J++, S+=4 )
			{
				if((m_rgbVRAM[S+3] & 0x0F)==0x0F)
				{
					for( I=J+1, D=S+4; I < N; I++, D+=4 )
					{
						if((m_rgbVRAM[D+3] & 0x0F)==0x0F)
						{
							DV = m_rgbVRAM[S+0] - m_rgbVRAM[D+0];
					
							if ((DV < 8) || (DV > 248))
							{
								DH = m_rgbVRAM[S+1] - m_rgbVRAM[D+1];
					
								if ((DH < 8) || (DH > 248))
								{
									PS = m_nSpriteGenerator + (m_rgbVRAM[S+2] << 3);
									PD = m_nSpriteGenerator + (m_rgbVRAM[D+2] << 3);
					
									if (DV < 8)
										PD += DV;
									else
									{
										DV = 256 - DV;
										PS += DV;
									}
									
									if (DH > 248)
									{
										DH = 256 - DH;
										T = PS;
										PS = PD;
										PD = T;
									}
								
									while( (DV < 8) && ((m_rgbVRAM[ PD ] & (m_rgbVRAM[ PS ] >> DH))==0x00) )
									{
										DV++;
										PS++;
										PD++;
									}
									
									if (DV < 8)
									{
										m_bVDPStatus |= 0x20;
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void VDPOut(char R, char V)
	{

		int J;

		switch (R&0xFF)
		{
			case  0:
				switch( ((V & 0x0E) >> 1) | (m_rgbVDP[ 1 ] & 0x18) )
				{
					case 0x10:
						J = 0;
						break;
					
					case 0x00:
						J = 1;
						break;
					
					case 0x01:
						J = 2;
						break;
					
					case 0x08:
						J = 3;
						break;
					
					default:
						J = m_nScrMode;
						break;
				}   

				if ( J != m_nScrMode )
				{
					m_nChrTable = ((m_rgbVDP[ 2 ] & m_rgbModeMasks[ J ][ 0 ]) << 10 );
					m_nColorTable = ((m_rgbVDP[ 3 ] & m_rgbModeMasks[ J ][ 1 ]) << 6 );
					m_nChrGenerator = ((m_rgbVDP[ 4 ] & m_rgbModeMasks[ J ][ 2 ]) << 11 );
					m_nSpriteTable = ((m_rgbVDP[ 5 ] & m_rgbModeMasks[ J ][ 3 ]) << 7 );
					m_nSpriteGenerator = (m_rgbVDP[ 6 ] << 11 );

					m_nScrMode = J;

					switch( J )
					{
						case	0:
							Init_Mode0();
							Colors_Mode0();
							break;

						case	1:
							Init_Mode1();
							Colors_Mode1();
							break;

						case	2:
							Init_Mode2();
							Colors_Mode2();
							break;

						case	3:
							Init_Mode3();
							Colors_Mode3();
							break;
					}
				}
				break;

			case  1:
				switch( ((m_rgbVDP[ 0 ] & 0x0E) >> 1) | (V & 0x18) )
				{
					case 0x10:
						J = 0;
						break;
					
					case 0x00:
						J = 1;
						break;
					
					case 0x01:
						J = 2;
						break;
					
					case 0x08:
						J = 3;
						break;
					
					default:
						J = m_nScrMode;
						break;
				}   

				if ( J != m_nScrMode )
				{
					m_nChrTable = ((m_rgbVDP[ 2 ] & m_rgbModeMasks[ J ][ 0 ]) << 10 );
					m_nColorTable = ((m_rgbVDP[ 3 ] & m_rgbModeMasks[ J ][ 1 ]) << 6 );
					m_nChrGenerator = ((m_rgbVDP[ 4 ] & m_rgbModeMasks[ J ][ 2 ]) << 11 );
					m_nSpriteTable = ((m_rgbVDP[ 5 ] & m_rgbModeMasks[ J ][ 3 ]) << 7 );
					m_nSpriteGenerator = (m_rgbVDP[ 6 ] << 11 );

					m_nScrMode = J;

					switch( J )
					{
						case	0:
							Init_Mode0();
							Colors_Mode0();
							break;

						case	1:
							Init_Mode1();
							Colors_Mode1();
							break;

						case	2:
							Init_Mode2();
							Colors_Mode2();
							break;

						case	3:
							Init_Mode3();
							Colors_Mode3();
							break;
					}
				}
				break;       

			case  2:
				m_nChrTable = ((V & m_rgbModeMasks[ m_nScrMode ][0]) << 10 );
				break;
			
			case  3:
				m_nColorTable = ((V & m_rgbModeMasks[ m_nScrMode ][1]) << 6 );
				break;
			
			case  4:
				m_nChrGenerator = ((V & m_rgbModeMasks[ m_nScrMode ][2]) << 11 );
				break;
			
			case  5:
				m_nSpriteTable = ((V & m_rgbModeMasks[ m_nScrMode ][3]) << 7 );
				break;
			
			case  6:
				V &= 0x3F;
				m_nSpriteGenerator = (V << 11 );
				break;
			
			case  7:
				m_nFGColor = (V >> 4)&0x0F;
				m_nBGColor = V & 0x0F;

				switch( m_nScrMode )
				{
					case	0:
						Colors_Mode0();
						break;

					case	1:
						Colors_Mode1();
						break;

					case	2:
						Colors_Mode2();
						break;

					case	3:
						Colors_Mode3();
						break;
				}
				break;
		}

		m_rgbVDP[ R ] = (char)(V & 0xFF);
	}
}
