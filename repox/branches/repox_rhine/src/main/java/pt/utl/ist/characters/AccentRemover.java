/*
 * AccentRemover.java
 *
 * Created on 4 de Junho de 2003, 23:28
 */

package pt.utl.ist.characters;

/**
 *
 * @author  Nuno Freire
 */
public class AccentRemover {
    
	
	
    public static char unaccentedUppercaseChar(char c){
        c=Character.toLowerCase(c);
        switch (c){
            case 'á':
            case 'à':
            case 'ã':
            case 'â':
            case 'ä':
                return 'A';
            case 'é':
            case 'è':
            case 'e':
            case 'ë':
                return 'E';
            case 'í':
            case 'ì':
            case 'î':                
            case 'ï':
                return 'I';
            case 'ó':
            case 'ò':
            case 'õ':
            case 'ô':
            case 'ö':
                return 'O';
            case 'ú':
            case 'ù':
            case 'û':
            case 'ü':
                return 'U';
            case 'ý':
            case 'ÿ':
                return 'Y';
            case 'ç':
                return 'C';
            case 'ñ':
                return 'N';
            default:
                return Character.toUpperCase(c);
        }
    }
	
    public static String convertToUnaccentedUppercaseChars(String s){
		StringBuffer ret=new StringBuffer(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			ret.append(unaccentedUppercaseChar(c));
		}		
		return ret.toString();
    }
	
	
}
