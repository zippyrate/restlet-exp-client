package exp.restlet.client;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClientApplication
{
	private static final Logger logger = LoggerFactory.getLogger(MyClientApplication.class);

	public static void main(String[] args)
	{
		Context restletContext = new Context();
		ClientResource clientResource = new ClientResource(restletContext, "http://localhost:8080/coordination/");

		try {
			Representation request = new JsonRepresentation(createTestCoordination());
			request.setMediaType(MediaType.APPLICATION_JSON);
			request.setCharacterSet(CharacterSet.UTF_8);

			Representation response = clientResource.post(request);

			decodeResponse(response);
			response.release();
			request.release();
		} catch (ResourceException e) {
			Status status = clientResource.getStatus();
			logger.info("S: " + status);
			logger.info("RE: " + clientResource.getResponseEntity());
			decodeErrorResponse(clientResource.getResponseEntity());
		} catch (IOException e) {
			logger.warn("IOException", e);
		} catch (JSONException e) {
			logger.warn("JSONException", e);
		} finally {
			clientResource.release();
		}
	}

	private static JSONObject createTestCoordination() throws JSONException
	{
		JSONObject requestObject = new JSONObject();
		JSONArray items = new JSONArray();

		items.put(createTerm("id1", "s", "1.0"));
		items.put(createTerm("id2", "s", "1.0"));

		requestObject.put("terms", items);

		return requestObject;
	}

	private static JSONObject createTerm(String termID, String schemaName, String schemaVersion) throws JSONException
	{
		JSONObject term = new JSONObject();

		term.put("termID", termID);
		term.put("schemaName", schemaName);
		term.put("schemaVersion", schemaVersion);
		term.put("description", "");

		return term;
	}

	private static void decodeErrorResponse(Representation responseRepresentation)
	{
		try {
			JsonRepresentation returnedRepresentation = new JsonRepresentation(responseRepresentation);
			JSONObject responseObject = returnedRepresentation.getJsonObject();
			String error = responseObject.getString("error");
			logger.info(error);
			responseRepresentation.release();
		} catch (Exception e) {
			logger.warn("Bad JSON error response", e);
		}
	}

	private static void decodeResponse(Representation responseRepresentation) throws JSONException, IOException
	{
		JsonRepresentation returnedRepresentation = new JsonRepresentation(responseRepresentation);
		JSONObject responseObject = returnedRepresentation.getJsonObject();
		String termID = responseObject.getString("termID");
		String schemaName = responseObject.getString("schemaName");
		String schemaVersion = responseObject.getString("schemaVersion");
		String description = responseObject.getString("description");
		responseRepresentation.release();

		logger.info("termID " + termID + ", schemaName " + schemaName + ", schemaVersion " + schemaVersion
				+ ", description " + description);
	}
}
