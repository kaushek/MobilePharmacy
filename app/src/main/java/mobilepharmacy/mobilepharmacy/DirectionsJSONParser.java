package mobilepharmacy.mobilepharmacy;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kaushek on 02/05/2018.
 */

public class DirectionsJSONParser {
    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jsRoutes = null;
        JSONArray jsLegs = null;
        JSONArray jsSteps = null;

        try {

            jsRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jsRoutes.length();i++){
                jsLegs = ( (JSONObject)jsRoutes.get(i)).getJSONArray("legs");
                List hashmapPath = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jsLegs.length();j++){
                    jsSteps = ( (JSONObject)jsLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jsSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jsSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePolyLine(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hashMap.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            hashmapPath.add(hashMap);
                        }
                    }
                    routes.add(hashmapPath);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return routes;
    }

    private List decodePolyLine(String encoded) {

        List poly = new ArrayList();
        int index = 0;
        int length = encoded.length();
        int lati = 0;
        int lngi = 0;

        while (index < length) {
            int ch;
            int shift = 0;
            int result = 0;
            do {
                ch = encoded.charAt(index++) - 63;
                result |= (ch & 0x1f) << shift;
                shift += 5;
            } while (ch >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lati += dlat;

            shift = 0;
            result = 0;
            do {
                ch = encoded.charAt(index++) - 63;
                result |= (ch & 0x1f) << shift;
                shift += 5;
            } while (ch >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lngi += dlng;

            LatLng latLngPos = new LatLng((((double) lati / 1E5)),
                    (((double) lngi / 1E5)));
            poly.add(latLngPos);
        }

        return poly;
    }
}
