package dhtfs.utils;

import java.util.Map;
import java.util.Set;
/**
 * @author siliu
 * 
 *
 */
public class MapOperator {
		
	public static void printMap(Map<String, Object> map) {

        Set<String> mapKeySet = map.keySet();
        for (String key : mapKeySet) {
            System.out.println(key + ": " + map.get(key));
        }
        System.out.println();
    }//end printMap()
}






































