package com.example.lwm2m_connect;

import android.util.Log;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.resource.LwM2mInstanceEnabler;
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler;
import org.eclipse.leshan.client.servers.LwM2mServer;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mPath;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.request.DownlinkRequest;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ReadResponse;

import java.util.HashMap;
import java.util.Map;

public class GatewayRouter extends CoapResource {

    private final Map<String, BaseInstanceEnabler> prefixToObject;

    public GatewayRouter(String name, Map<String, BaseInstanceEnabler> Devices) {
        super(name);
        this.prefixToObject = Devices;
        Log.d("DEBUG", "GatewayRouter initialized with name: " + name);
    }

    // Gestion des requêtes GET
    @Override
    public void handleGET(CoapExchange exchange) {
        String path = exchange.getRequestOptions().getUriPathString();
        String[] parts = path.split("/");

        if (parts.length < 4) {
            exchange.respond("Invalid virtual path");
            return;
        }

        Log.d("DEBUG", "Request received for Gateway: " + path);

        String prefix = parts[1];
        int objectId = Integer.parseInt(parts[2]);
        int instanceId = Integer.parseInt(parts[3]);

        // Recherche de l'objet correspondant au préfixe
        BaseInstanceEnabler target = prefixToObject.get(prefix);
        if (target == null) {
            exchange.respond(CoAP.ResponseCode.NOT_FOUND, "Prefix not found");
            return;
        }

        /*// Crée un chemin réel pour l'objet LwM2M
        LwM2mPath realPath = new LwM2mPath(objectId, instanceId);

        // Crée une nouvelle requête en redirigeant vers l'objet réel
        DownlinkRequest<?> redirectedRequest = new DownlinkRequest<>(realPath);

        // Redirige la requête vers l'objet réel (gère la logique de la réponse)
        LwM2mResponse response = target.handleRequest(redirectedRequest);

        if (response != null) {
            // Répondre avec le résultat LwM2M
            exchange.respond(response.getCode());
        } else {
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR, "Error processing request");
        }*/
    }



    public LwM2mResource read(int instanceId, int resourceId) {
        return null;
    }
}
