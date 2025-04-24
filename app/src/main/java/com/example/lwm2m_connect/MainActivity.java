package com.example.lwm2m_connect;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.eclipse.leshan.client.LeshanClient;
import org.eclipse.leshan.client.LeshanClientBuilder;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.LwM2mId;
import org.eclipse.leshan.core.endpoint.Protocol;
import org.eclipse.leshan.client.object.Security;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.core.link.Link;
import org.eclipse.leshan.core.model.InvalidDDFFileException;
import org.eclipse.leshan.core.model.InvalidModelException;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.model.StaticModel;
import org.eclipse.leshan.core.request.BindingMode;
import org.eclipse.leshan.transport.javacoap.client.endpoint.JavaCoapClientEndpointsProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Map<String, BaseInstanceEnabler> gatewayObjects = new HashMap<>();
    private static final String[] LOADED_FILES = {
        "0.xml", "1.xml", "2.xml", "3.xml", "25.xml", "26.xml", "3303.xml"
    };
    private final String TAG = "LWM2M_APP";
//    private static final String URI = "coap://leshan.eclipseprojects.io:5683";
    private static final String URI = "coap://192.168.106.225:5683";
    private static final int OBJECT_ID_LWM2M_LWM2M_GATEWAY = 25;
    private static final int OBJECT_ID_TEMPERATURE_SENSOR = 3303;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(() -> {
            try {
                startLwm2mClient();
            } catch (InvalidModelException | InvalidDDFFileException | IOException e) {
                Log.e("LwM2M", "Failed to start LwM2M client due to model or file error", e);
            } catch (ConnectorException e) {
                throw new RuntimeException("ConnectorException while starting LwM2M client", e);
            }
        }).start();
    }

    private void startLwm2mClient() throws InvalidModelException, InvalidDDFFileException, IOException, ConnectorException {
        String endpoint = "RetD";
        LeshanClientBuilder builder = new LeshanClientBuilder(endpoint);
        BindingMode serverBindingMode = BindingMode.fromProtocol(Objects.requireNonNull(Protocol.fromUri(URI)));

        // Load LwM2M model files
        List<ObjectModel> models = loadResources();
        Log.d(TAG, "Object initialization completed. Number of models loaded: " + models.size());

        // Initialize LwM2M objects
        ObjectsInitializer initializer = new ObjectsInitializer(new StaticModel(models));
        initializer.setInstancesForObject(LwM2mId.SECURITY, Security.noSec(URI, 12345));
        initializer.setInstancesForObject(LwM2mId.SERVER, new Server(12345, (5 * 60),
                EnumSet.of(serverBindingMode), false, BindingMode.U));
//        initializer.setInstancesForObject(OBJECT_ID_TEMPERATURE_SENSOR, new RandomTemperatureSensor());
        initializer.setInstancesForObject(LwM2mId.DEVICE, new MyDevice(this));
        initializer.setInstancesForObject(OBJECT_ID_LWM2M_LWM2M_GATEWAY, new Gateway("Desk",
                "d01",
                new Link[] {
                    new Link("/3303/0"),
            }),
            new Gateway("Lobby",
                "d02",
                new Link[] {
                    new Link("/3303/0"),
                    new Link("/3303/1")
        }));

        builder.setObjects(initializer.createAll());

        // Use of JavaCoAP
        builder.setEndpointsProviders(List.of(new JavaCoapClientEndpointsProvider()));
        LeshanClient client = builder.build();

        // Test server connectivity
        CoapClient connection = new CoapClient(URI);
        CoapResponse response = connection.get();
        if (response == null) {
            Log.e(TAG, "No response from the server");
        }else {
            client.start();
            Log.d(TAG, "Connection successfully");
            Log.d("DEBUG", "Connection successfully");

            /*// Démarrage du serveur CoAP
            CoapServer server = new CoapServer();
            GatewayRouter router = new GatewayRouter("d02", gatewayObjects);

            server.add(router);
            server.start();


            Log.d(TAG, "Serveur CoAP démarré sur la Gateway");*/
        }
    }

    @NonNull
    private List<ObjectModel> loadResources() {
        String directory = "models_lwm2m/";
        List<ObjectModel> models = new ArrayList<>();
        AssetManager assetManager = getAssets();
        String[] files = LOADED_FILES;

        if (files != null) {
                for (String filename : files) {
                    if (filename.endsWith(".xml")) {
                        try {
                            InputStream is = assetManager.open(directory + filename);
                            models.addAll(ObjectLoader.loadDdfFile(is, filename));
                        } catch (IOException e) {
                            Log.e(TAG, "File not found or I/O error: " + filename, e);
                        } catch (Exception e) {
                            Log.e(TAG, "Parsing error for file: " + filename, e);
                        }
                    }
                }
            } else {
                Log.e(TAG, "Empty or missing directory: " + directory);
            }

        return models;
    }
}
