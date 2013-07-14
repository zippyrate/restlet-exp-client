package exp.restlet.client;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
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

		logger.info("Hello");

		try {
			Representation request = new JsonRepresentation("{ \"terms\": [] }");
			request.setMediaType(MediaType.APPLICATION_JSON); // CharacterSet.UTF_8);

			Representation response = clientResource.post(request);

			Status status = clientResource.getStatus();
			logger.info("SS: " + status);
			logger.info("RE: " + clientResource.getResponseEntity());

			JsonRepresentation returnedRepresentation = new JsonRepresentation(response);

			if (returnedRepresentation == null)
				logger.warn("ret = null");
			else
				logger.info("X: " + returnedRepresentation.getSize());
			JSONObject responseObject = returnedRepresentation.getJsonObject();

			// Clean-up
			request.release();
			returnedRepresentation.release();
			response.release();
			clientResource.release();
		} catch (ResourceException e) {
			Status status = clientResource.getStatus();
			logger.info("S: " + status);
			logger.info("RE: " + clientResource.getResponseEntity());
			logger.info("RE.size: " + clientResource.getResponseEntity().getSize());
		} catch (IOException e) {
			logger.warn("IOException", e);
		} catch (JSONException e) {
			logger.warn("JSONException", e);
		}
	}
}
