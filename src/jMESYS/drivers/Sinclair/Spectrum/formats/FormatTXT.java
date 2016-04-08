package jMESYS.drivers.Sinclair.Spectrum.formats;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Vector;

import jMESYS.drivers.jMESYSComputer;
import jMESYS.drivers.Sinclair.Spectrum.Spectrum48k;
import jMESYS.files.FileFormat;
import jMESYS.gui.jMESYSDisplay;

public class FormatTXT extends FileFormat {

	// extension
	private static String strExtension = ".TXT";
	
	private Vector datos = new Vector();
		
	public String getExtension() {
		return strExtension;
	}

	public void loadFormat(String name, InputStream is, jMESYSComputer cpu) throws Exception {
		//int PROG = elS.laMemoria().cpu.mem16(0x5C53);
		int PROG = cpu.mem16(0x5C53);
	    //int linea = elS.laMemoria().cpu.mem(PROG);
		int linea = cpu.mem(PROG);
	    linea = (linea << 8) + cpu.mem(PROG + 1);
	    
	    if(linea < 10000){
	    	
	    	File f = new File("/a.txt");
	    	escribe_datos(PROG, new FileOutputStream(f), linea, (Spectrum48k)cpu);
	    }
		
	}

	public Image getScreen(String name, InputStream is, jMESYSDisplay display, Graphics g) throws Exception {
		return null;
	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//Escribe un programa BASIC en un fichero dado
	  public int escribe_datos(int PROG, FileOutputStream salida, int f_linea, Spectrum48k cpu) throws Exception{
	    int linea = f_linea;
	    int i = 2;
	    String S = "";
	    while(linea < 10000){
	      S = Integer.toString(linea) + " ";
	      byte lin[] = S.getBytes();
	      salida.write(lin);
	      linea = cpu.mem16(PROG + i);
	      i += 2;
	      for(int j = 0; j < (linea - 1);){
	        int code = cpu.mem(PROG + i);
	        i++;
	        j++;
	        switch(code){
	          case 0x04:      /*TRUE VIDEO*/
	          case 0x05:      /*INVERSE VIDEO*/
	          case 0x06:      /*CAPS LOCK*/
	          case 0x07:      /*EDIT*/
	          case 0x08:      /*CURSOR LEFT*/
	          case 0x09:      /*CURSOR RIGHT*/
	          case 0x0A:      /*CURSOR DOWN*/
	          case 0x0B:      /*CURSOR UP*/
	          case 0x0C:      /*DELETE*/
	          case 0x0D:      /*ENTER*/
	          case 0x0F:{     /*GRAPHICS*/
	            System.out.println(Integer.toHexString(code));
	            return -1;
	          }
	          case 0x0E:{     /*SYMBOL SHIFT*/
	            S = "";
	            i += 5;
	            j += 5;
	            break;
	          }

	          case 0x10:
	          case 0x11:
	          case 0x12:
	          case 0x13:
	          case 0x14:
	          case 0x15:
	          case 0x16:
	          case 0x17:{ //System.out.println(code + "  " + cpu.mem(PROG + i));
	                      S = "";   i++;  j++;  break;  }

	          case 0x20:{ S = " ";  break;  }     /*SPACE*/
	          case 0x21:{ S = "!";  break;  }     /*!*/
	          case 0x22:{ S = "\"";  break;  }    /*"*/
	          case 0x23:{ S = "#";  break;  }     /*#*/
	          case 0x24:{ S = "$";  break;  }     /*$*/
	          case 0x25:{ S = "%";  break;  }     /*%*/
	          case 0x26:{ S = "&";  break;  }     /*&*/
	          case 0x27:{ S = "'";  break;  }     /*'*/
	          case 0x28:{ S = "(";  break;  }     /*(*/
	          case 0x29:{ S = ")";  break;  }     /*)*/
	          case 0x2A:{ S = "*";  break;  }     /***/
	          case 0x2B:{ S = "+";  break;  }     /*+*/
	          case 0x2C:{ S = ",";  break;  }     /*,*/
	          case 0x2D:{ S = "-";  break;  }     /*-*/
	          case 0x2E:{ S = ".";  break;  }     /*.*/
	          case 0x2F:{ S = "/";  break;  }     /*/*/

	          case 0x30:{ S = "0";  break;  }     /*0*/
	          case 0x31:{ S = "1";  break;  }     /*1*/
	          case 0x32:{ S = "2";  break;  }     /*2*/
	          case 0x33:{ S = "3";  break;  }     /*3*/
	          case 0x34:{ S = "4";  break;  }     /*4*/
	          case 0x35:{ S = "5";  break;  }     /*5*/
	          case 0x36:{ S = "6";  break;  }     /*6*/
	          case 0x37:{ S = "7";  break;  }     /*7*/
	          case 0x38:{ S = "8";  break;  }     /*8*/
	          case 0x39:{ S = "9";  break;  }     /*9*/
	          case 0x3A:{ S = ":";  break;  }     /*:*/
	          case 0x3B:{ S = ";";  break;  }     /*;*/
	          case 0x3C:{ S = "<";  break;  }     /*<*/
	          case 0x3D:{ S = "=";  break;  }     /*=*/
	          case 0x3E:{ S = ">";  break;  }     /*>*/
	          case 0x3F:{ S = "?";  break;  }     /*?*/

	          case 0x40:{ S = "@";  break;  }     /*@*/
	          case 0x41:{ S = "A";  break;  }     /*A*/
	          case 0x42:{ S = "B";  break;  }     /*B*/
	          case 0x43:{ S = "C";  break;  }     /*C*/
	          case 0x44:{ S = "D";  break;  }     /*D*/
	          case 0x45:{ S = "E";  break;  }     /*E*/
	          case 0x46:{ S = "F";  break;  }     /*F*/
	          case 0x47:{ S = "G";  break;  }     /*G*/
	          case 0x48:{ S = "H";  break;  }     /*H*/
	          case 0x49:{ S = "I";  break;  }     /*I*/
	          case 0x4A:{ S = "J";  break;  }     /*J*/
	          case 0x4B:{ S = "K";  break;  }     /*K*/
	          case 0x4C:{ S = "L";  break;  }     /*L*/
	          case 0x4D:{ S = "M";  break;  }     /*M*/
	          case 0x4E:{ S = "N";  break;  }     /*N*/
	          case 0x4F:{ S = "O";  break;  }     /*O*/

	          case 0x50:{ S = "P";  break;  }     /*P*/
	          case 0x51:{ S = "Q";  break;  }     /*Q*/
	          case 0x52:{ S = "R";  break;  }     /*R*/
	          case 0x53:{ S = "S";  break;  }     /*S*/
	          case 0x54:{ S = "T";  break;  }     /*T*/
	          case 0x55:{ S = "U";  break;  }     /*U*/
	          case 0x56:{ S = "V";  break;  }     /*V*/
	          case 0x57:{ S = "W";  break;  }     /*W*/
	          case 0x58:{ S = "X";  break;  }     /*X*/
	          case 0x59:{ S = "Y";  break;  }     /*Y*/
	          case 0x5A:{ S = "Z";  break;  }     /*Z*/
	          case 0x5B:{ S = "[";  break;  }     /*[*/
	          case 0x5C:{ S = "\\";  break;  }     /*\*/
	          case 0x5D:{ S = "]";  break;  }     /*]*/
	          case 0x5E:{ S = "^";  break;  }     /*^*/
	          case 0x5F:{ S = "_";  break;  }     /*_*/

	          case 0x60:{ S = "£";  break;  }     /*£*/
	          case 0x61:{ S = "a";  break;  }     /*a*/
	          case 0x62:{ S = "b";  break;  }     /*b*/
	          case 0x63:{ S = "c";  break;  }     /*c*/
	          case 0x64:{ S = "d";  break;  }     /*d*/
	          case 0x65:{ S = "e";  break;  }     /*e*/
	          case 0x66:{ S = "f";  break;  }     /*f*/
	          case 0x67:{ S = "g";  break;  }     /*g*/
	          case 0x68:{ S = "h";  break;  }     /*h*/
	          case 0x69:{ S = "i";  break;  }     /*i*/
	          case 0x6A:{ S = "j";  break;  }     /*j*/
	          case 0x6B:{ S = "k";  break;  }     /*k*/
	          case 0x6C:{ S = "l";  break;  }     /*l*/
	          case 0x6D:{ S = "m";  break;  }     /*m*/
	          case 0x6E:{ S = "n";  break;  }     /*n*/
	          case 0x6F:{ S = "o";  break;  }     /*o*/

	          case 0x70:{ S = "p";  break;  }     /*p*/
	          case 0x71:{ S = "q";  break;  }     /*q*/
	          case 0x72:{ S = "r";  break;  }     /*r*/
	          case 0x73:{ S = "s";  break;  }     /*s*/
	          case 0x74:{ S = "t";  break;  }     /*t*/
	          case 0x75:{ S = "u";  break;  }     /*u*/
	          case 0x76:{ S = "v";  break;  }     /*v*/
	          case 0x77:{ S = "w";  break;  }     /*w*/
	          case 0x78:{ S = "x";  break;  }     /*x*/
	          case 0x79:{ S = "y";  break;  }     /*y*/
	          case 0x7A:{ S = "z";  break;  }     /*z*/
	          case 0x7B:{ S = "{";  break;  }     /*{*/
	          case 0x7C:{ S = "|";  break;  }     /*|*/
	          case 0x7D:{ S = "}";  break;  }     /*}*/
	          case 0x7E:{ S = "~";  break;  }     /*~*/
	          case 0x7F:{ S = "©";  break;  }     /*©*/

	          case 0x80:
	          case 0x81:
	          case 0x82:
	          case 0x83:
	          case 0x84:
	          case 0x85:
	          case 0x86:
	          case 0x87:
	          case 0x88:
	          case 0x89:
	          case 0x8A:
	          case 0x8B:
	          case 0x8C:
	          case 0x8D:
	          case 0x8E:
	          case 0x8F:{ S = "¿"; break;  }

	          case 0x90:{ S = "A";  break;  }            /*A*/
	          case 0x91:{ S = "B";  break;  }            /*B*/
	          case 0x92:{ S = "C";  break;  }            /*C*/
	          case 0x93:{ S = "D";  break;  }            /*D*/
	          case 0x94:{ S = "E";  break;  }            /*E*/
	          case 0x95:{ S = "F";  break;  }            /*F*/
	          case 0x96:{ S = "G";  break;  }            /*G*/
	          case 0x97:{ S = "H";  break;  }            /*H*/
	          case 0x98:{ S = "I";  break;  }            /*I*/
	          case 0x99:{ S = "J";  break;  }            /*J*/
	          case 0x9A:{ S = "K";  break;  }            /*K*/
	          case 0x9B:{ S = "L";  break;  }            /*L*/
	          case 0x9C:{ S = "M";  break;  }            /*M*/
	          case 0x9D:{ S = "N";  break;  }            /*N*/
	          case 0x9E:{ S = "O";  break;  }            /*O*/
	          case 0x9F:{ S = "P";  break;  }            /*P*/

	          case 0xA0:{ S = "Q";  break;  }            /*Q*/
	          case 0xA1:{ S = "R";  break;  }            /*R*/
	          case 0xA2:{ S = "S";  break;  }            /*S*/
	          case 0xA3:{ S = "T";  break;  }            /*T*/
	          case 0xA4:{ S = "U";  break;  }            /*U*/
	          case 0xA5:{ S = "RND ";  break;  }         /*RND*/
	          case 0xA6:{ S = "INKEY$ ";  break;  }      /*INKEY$*/
	          case 0xA7:{ S = "PI ";  break;  }          /*PI*/
	          case 0xA8:{ S = "FN ";  break;  }          /*FN*/
	          case 0xA9:{ S = "POINT ";  break;  }       /*POINT*/
	          case 0xAA:{ S = "SCREEN$ ";  break;  }     /*SCREEN$*/
	          case 0xAB:{ S = "ATTR ";  break;  }        /*ATTR*/
	          case 0xAC:{ S = " AT ";  break;  }         /*AT*/
	          case 0xAD:{ S = "TAB ";  break;  }         /*TAB*/
	          case 0xAE:{ S = "VAL$ ";  break;  }        /*VAL$*/
	          case 0xAF:{ S = "CODE ";  break;  }        /*CODE*/

	          case 0xB0:{ S = "VAL ";  break;  }     /*VAL*/
	          case 0xB1:{ S = "LEN ";  break;  }     /*LEN*/
	          case 0xB2:{ S = "SIN ";  break;  }     /*SIN*/
	          case 0xB3:{ S = "COS ";  break;  }     /*COS*/
	          case 0xB4:{ S = "TAN ";  break;  }     /*TAN*/
	          case 0xB5:{ S = "ASN ";  break;  }     /*ASN*/
	          case 0xB6:{ S = "ACS ";  break;  }     /*ACS*/
	          case 0xB7:{ S = "ATN ";  break;  }     /*ATN*/
	          case 0xB8:{ S = "LN ";  break;  }      /*LN*/
	          case 0xB9:{ S = "EXP ";  break;  }     /*EXP*/
	          case 0xBA:{ S = "INT ";  break;  }     /*INT*/
	          case 0xBB:{ S = "SQR ";  break;  }     /*SQR*/
	          case 0xBC:{ S = "SGN ";  break;  }     /*SGN*/
	          case 0xBD:{ S = "ABS ";  break;  }     /*ABS*/
	          case 0xBE:{ S = "PEEK ";  break;  }    /*PEEK*/
	          case 0xBF:{ S = "IN ";  break;  }      /*IN*/

	          case 0xC0:{ S = "USR ";  break;  }     /*USR*/
	          case 0xC1:{ S = "STR$ ";  break;  }    /*STR$*/
	          case 0xC2:{ S = "CHR$ ";  break;  }    /*CHR$*/
	          case 0xC3:{ S = "NOT ";  break;  }     /*NOT*/
	          case 0xC4:{ S = "BIN ";  break;  }     /*BIN*/
	          case 0xC5:{ S = " OR ";  break;  }     /*OR*/
	          case 0xC6:{ S = " AND ";  break;  }    /*AND*/
	          case 0xC7:{ S = "<=";  break;  }       /*<=*/
	          case 0xC8:{ S = ">=";  break;  }       /*>=*/
	          case 0xC9:{ S = "<>";  break;  }       /*<>*/
	          case 0xCA:{ S = "LINE ";  break;  }    /*LINE*/
	          case 0xCB:{ S = " THEN ";  break;  }   /*THEN*/
	          case 0xCC:{ S = " TO ";  break;  }     /*TO*/
	          case 0xCD:{ S = " STEP ";  break;  }   /*STEP*/
	          case 0xCE:{ S = "DEF FN ";  break;  }  /*DEF FN*/
	          case 0xCF:{ S = "CAT ";  break;  }     /*CAT*/

	          case 0xD0:{ S = "FORMAT ";  break;  }     /*FORMAT*/
	          case 0xD1:{ S = "MOVE ";  break;  }       /*MOVE*/
	          case 0xD2:{ S = "ERASE ";  break;  }      /*ERASE*/
	          case 0xD3:{ S = "OPEN # ";  break;  }     /*OPEN #*/
	          case 0xD4:{ S = "CLOSE # ";  break;  }    /*CLOSE #*/
	          case 0xD5:{ S = "MERGE ";  break;  }      /*MERGE*/
	          case 0xD6:{ S = "VERIFY ";  break;  }     /*VERIFY*/
	          case 0xD7:{ S = "BEEP ";  break;  }       /*BEEP*/
	          case 0xD8:{ S = "CIRCLE ";  break;  }     /*CIRCLE*/
	          case 0xD9:{ S = "INK ";  break;  }        /*INK*/
	          case 0xDA:{ S = "PAPER ";  break;  }      /*PAPER*/
	          case 0xDB:{ S = "FLASH ";  break;  }      /*FLASH*/
	          case 0xDC:{ S = "BRIGHT ";  break;  }     /*BRIGHT*/
	          case 0xDD:{ S = "INVERSE ";  break;  }    /*INVERSE*/
	          case 0xDE:{ S = "OVER ";  break;  }       /*OVER*/
	          case 0xDF:{ S = "OUT ";  break;  }        /*OUT*/

	          case 0xE0:{ S = "LPRINT ";  break;  }     /*LPRINT*/
	          case 0xE1:{ S = "LLIST ";  break;  }      /*LLIST*/
	          case 0xE2:{ S = "STOP ";  break;  }       /*STOP*/
	          case 0xE3:{ S = "READ ";  break;  }       /*READ*/
	          case 0xE4:{ S = "DATA ";  break;  }       /*DATA*/
	          case 0xE5:{ S = "RESTORE ";  break;  }    /*RESTORE*/
	          case 0xE6:{ S = "NEW ";  break;  }        /*NEW*/
	          case 0xE7:{ S = "BORDER ";  break;  }     /*BORDER*/
	          case 0xE8:{ S = "CONTINUE ";  break;  }   /*CONTINUE*/
	          case 0xE9:{ S = "DIM ";  break;  }        /*DIM*/
	          case 0xEA:{ S = "REM ";  break;  }        /*REM*/
	          case 0xEB:{ S = "FOR ";  break;  }        /*FOR*/
	          case 0xEC:{ S = "GO TO ";  break;  }      /*GO TO*/
	          case 0xED:{ S = "GO SUB ";  break;  }     /*GO SUB*/
	          case 0xEE:{ S = "INPUT ";  break;  }      /*INPUT*/
	          case 0xEF:{ S = "LOAD ";  break;  }       /*LOAD*/

	          case 0xF0:{ S = "LIST ";  break;  }       /*LIST*/
	          case 0xF1:{ S = "LET ";  break;  }        /*LET */
	          case 0xF2:{ S = "PAUSE ";  break;  }      /*PAUSE*/
	          case 0xF3:{ S = "NEXT ";  break;  }       /*NEXT*/
	          case 0xF4:{ S = "POKE ";  break;  }       /*POKE*/
	          case 0xF5:{ S = "PRINT ";  break;  }      /*PRINT*/
	          case 0xF6:{ S = "PLOT ";  break;  }       /*PLOT*/
	          case 0xF7:{ S = "RUN ";  break;  }        /*RUN*/
	          case 0xF8:{ S = "SAVE ";  break;  }       /*SAVE*/
	          case 0xF9:{ S = "RANDOMIZE ";  break;  }  /*RANDOMIZE*/
	          case 0xFA:{ S = "IF ";  break;  }         /*IF*/
	          case 0xFB:{ S = "CLS ";  break;  }        /*CLS*/
	          case 0xFC:{ S = "DRAW ";  break;  }       /*DRAW*/
	          case 0xFD:{ S = "CLEAR";  break;  }       /*CLEAR*/
	          case 0xFE:{ S = "RETURN ";  break;  }     /*RETURN*/
	          case 0xFF:{ S = "COPY ";  break;  }       /*COPY*/

	          default:{   System.out.println("MAL: " + code + "  " + PROG + "  " + i); S = ""; break; }
	        }
	        byte b[] = S.getBytes();
	        salida.write(b);
	      }
	      int code = cpu.mem(PROG + i);
	      i++;
	      if(code != 0x0D){
	        System.out.println("NO RETURN");
	        return -1;
	      }
	      S = "\n";
	      byte b[] = S.getBytes();
	      salida.write(b);
	      linea = cpu.mem(PROG + i);
	      linea = (linea << 8) + cpu.mem(PROG + i + 1);
	      i += 2;
	    }
	    return 0;
	  }

	  //Guarda temporalmente los datos de un programa BASIC leido desde fichero
	  public void Carga_BASIC(Vector data){
	    datos = data;
	  }

	  //Indica si quedan datos en el Vector
	  public boolean Cargando(){
	    return (datos.size() > 0);
	  }

	  //Carga la cabecera del programa BASIC leido desde fichero
	  public void Carga_cabecera_BASIC(int dire, Spectrum48k cpu){
	    int i = 0;
	    cpu.mem(dire + i, 0);
	    i++;
	    for(int j = 0; j < 10; j++){
	      cpu.mem(dire + i, 32);
	      i++;
	    }
	    int aux = datos.size();
	    cpu.mem16(dire + i, aux);
	    i += 2;
	    cpu.mem16(dire + i, 40000);
	    i += 2;
	    cpu.mem16(dire + i, aux);
	    i += 2;
	  }

	  //Carga los datos del programa BASIC leido desde fichero
	  public void Carga_datos_BASIC(int dire, int lon, Spectrum48k cpu){
	    int leido = 0;
	    for(int i = 0; i < lon; i++){
	      leido = Integer.parseInt((String) datos.elementAt(i));
	      cpu.mem(dire + i, leido);
	    }
	    datos.removeAllElements();
	  }


}
