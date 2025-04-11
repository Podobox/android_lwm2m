package com.example.lmw2w_connect;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.eclipse.leshan.client.LeshanClient;
import org.eclipse.leshan.client.LeshanClientBuilder;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.LwM2mId;
import org.eclipse.leshan.core.endpoint.Protocol;
import org.eclipse.leshan.client.object.Security;
import org.eclipse.leshan.client.object.Server;
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
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

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
        BindingMode serverBindingMode = BindingMode.fromProtocol(Objects.requireNonNull(Protocol.fromUri("coap://leshan.eclipseprojects.io:5683")));

        // Load LwM2M model files
        List<ObjectModel> models = loadResources();
        Log.d("LwM2M", "Object initialization completed. Number of models loaded: " + models.size());

        // Initialize LwM2M objects
        ObjectsInitializer initializer = new ObjectsInitializer(new StaticModel(models));
        initializer.setInstancesForObject(LwM2mId.SECURITY, Security.noSec("coap://leshan.eclipseprojects.io:5683", 12345));
        initializer.setInstancesForObject(LwM2mId.SERVER, new Server(12345, (5 * 60),
                EnumSet.of(serverBindingMode), false, BindingMode.U));
        initializer.setInstancesForObject(LwM2mId.DEVICE, new MyDevice());

        builder.setObjects(initializer.createAll());

        // Use Californium instead of JavaCoAP
        builder.setEndpointsProviders(List.of(new JavaCoapClientEndpointsProvider()));
        LeshanClient client = builder.build();

        // Test server connectivity
        CoapClient connection = new CoapClient("coap://leshan.eclipseprojects.io:5683/");
        CoapResponse response = connection.get();
        if (response == null) {
            Log.e("LwM2M", "No response from the server!");
        }

        client.start();
    }

    private List<ObjectModel> loadResources() {
        String directory = "models_lwm2m/";
        List<ObjectModel> models = new ArrayList<>();
        AssetManager assetManager = getAssets();

        try {
            String[] files = assetManager.list(directory);
            if (files != null) {
                for (String filename : files) {
                    if (filename.endsWith(".xml")) {
                        try {
                            InputStream is = assetManager.open(directory + filename);
                            models.addAll(ObjectLoader.loadDdfFile(is, filename));
                        } catch (IOException e) {
                            Log.e("DEBUG", "File not found or I/O error: " + filename, e);
                        } catch (Exception e) {
                            Log.e("DEBUG", "Parsing error for file: " + filename, e);
                        }
                    }
                }
            } else {
                Log.e("DEBUG", "Empty or missing directory: " + directory);
            }
        } catch (IOException e) {
            Log.e("DEBUG", "Error accessing assets: ", e);
        }

        return models;
    }

}
