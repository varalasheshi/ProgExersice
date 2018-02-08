package com.neo.reader;

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;


public class NEOReader {

	/**
	 * @param args
	 */
	String nearNEOLink=null;
	Double smallDistKm = Double.MAX_VALUE;
	int totNearNEO=0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		NEOReader neo = new NEOReader();
		neo.getLargestNEO();
		neo.getNearestNEO();
	}

	 /**
     * Gets the NEO Json elements and  finds the largest planet depending on the estimated_diameter .
     *
     *
     */
	public void getLargestNEO(){
	
			try {
				
				JSONParser parser = new JSONParser();
				
				Object jsonObj = parser.parse(invokeWebService("https://api.nasa.gov/neo/rest/v1/neo/browse?api_key=DEMO_KEY"));
				JSONObject jsonObject = (JSONObject) jsonObj;
				
				JSONArray neoarray = (JSONArray) jsonObject.get("near_earth_objects");
				Iterator<JSONObject> it = neoarray.iterator();
				Double bigNEO = Double.valueOf("0");
				String bigNEOLink =  null;
				int totNEO =0;
				while (it.hasNext()) {
					totNEO = totNEO+1;
					JSONObject neo = (JSONObject)it.next();
					JSONObject links = (JSONObject)neo.get("links");
					String self = (String)links.get("self");
					JSONObject diam = (JSONObject) neo.get("estimated_diameter");
					JSONObject diaK = (JSONObject) diam.get("kilometers");
					Double dia_min = (Double)diaK.get("estimated_diameter_min");
					Double dia_max = (Double)diaK.get("estimated_diameter_max");
					Double diaAvg = (dia_min+dia_max)/2;
					if(diaAvg > bigNEO ){
						bigNEO = diaAvg;
						bigNEOLink = self;
					}
					
				}
				
				System.out.println("Total no of NEOs = " + totNEO);
				System.out.println("*************************************");
				System.out.println(":::::::Largest NEO in size is:::::::");
				System.out.println("*************************************");
				System.out.println(invokeWebService(bigNEOLink));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	/**
     * Gets the NEO Json elements and  finds the nearest planet to earth depending on the "kilometers" of "miss_distance".
     *
     *
     */
	public void getNearestNEO(){


			try {
				JSONParser parser = new JSONParser();
				Object jsonObj = parser.parse(invokeWebService("https://api.nasa.gov/neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=DEMO_KEY"));
				JSONObject jsonObject = (JSONObject) jsonObj;
				JSONObject neoObject = (JSONObject) jsonObject.get("near_earth_objects");
				findNearestNEO((JSONArray)neoObject.get("2015-09-08"));
				findNearestNEO((JSONArray)neoObject.get("2015-09-07"));
							
				System.out.println("Total no of NEOs = " + this.totNearNEO);
				System.out.println("*************************************");
				System.out.println(":::::::::::::Closest NEO is:::::::::");
				System.out.println("*************************************");
				System.out.println(invokeWebService(this.nearNEOLink));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		
	}
	
	/**
     * Finds the NEO  nearest  to earth depending on the "kilometers" of "miss_distance".
     *
     *  @param neoarray JSONArray object for the given dates .
     */
	void findNearestNEO(JSONArray neoarray){
		Iterator<JSONObject> it = neoarray.iterator();
		Double distKm = Double.valueOf("0");
		while (it.hasNext()) {
			this.totNearNEO = this.totNearNEO+1;
			JSONObject neo = (JSONObject)it.next();
			JSONObject links = (JSONObject)neo.get("links");
			String self = (String)links.get("self");
			JSONArray closeAppData = (JSONArray) neo.get("close_approach_data");
			Iterator<JSONObject> itr  = closeAppData.iterator();
			while (itr.hasNext()) {
				JSONObject clsAppdata = (JSONObject)itr.next();
				JSONObject missDist = (JSONObject)clsAppdata.get("miss_distance");
				distKm = (Double)Double.valueOf(missDist.get("kilometers").toString());
			}
			
			if(smallDistKm > distKm ){
				this.smallDistKm = distKm;
				this.nearNEOLink = self;
			}
			
		}
		
	}
	
	/**
     * Invokes the REST Webservice and returns the response.
     * 
     * @param resource REST Webservice URL .
     * @return response of the given REST webservice.
     */
	public String invokeWebService(String resource){
		String response = null;
		try {
		
			Client client = Client.create();
			WebResource webResource = client.resource(resource);
			ClientResponse clientResponse = webResource.accept("application/json").get(ClientResponse.class);
			response = clientResponse.getEntity(String.class);
			
		
		} catch (UniformInterfaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	

}

