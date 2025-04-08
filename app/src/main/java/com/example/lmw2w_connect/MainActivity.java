package com.example.lmw2w_connect;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
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
//import org.eclipse.leshan.transport.californium.endpoint.CfClientEndpointsProvider;
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
                e.printStackTrace(); // Remplacer par des log plus appropriés
            }
        }).start();

    }

    private void startLwm2mClient() throws InvalidModelException, InvalidDDFFileException, IOException {
        Log.d("LwM2M","Bienvenue sur le test LwM2M !");
        String endpoint = "RetD";
        LeshanClientBuilder builder = new LeshanClientBuilder(endpoint);
        BindingMode serverBindingMode = BindingMode.fromProtocol(Objects.requireNonNull(Protocol.fromUri("coap://leshan.eclipseprojects.io:5683")));

        // Load model
        InputStream test = ObjectLoader.class.getResourceAsStream("/models/object_1.xml");
        if (test == null) {
            Log.e("DEBUG", "Fichier non trouvé !");
        } else {
            Log.d("DEBUG", "Fichier bien chargé !");
        }
        String[] modelPaths = new String[] {"object_1.xml"};
        // Normalement initialisé avec ObjectLoader.loadDefault()
        List<ObjectModel> models = new ArrayList<>(ObjectLoader.loadDdfResources("models", modelPaths));


        // Initialisation des objets LwM2M
        ObjectsInitializer initializer = new ObjectsInitializer(new StaticModel(models));
        initializer.setInstancesForObject(LwM2mId.SECURITY, Security.noSec("coap://leshan.eclipseprojects.io:5683", 12345));
        initializer.setInstancesForObject(LwM2mId.SERVER, new Server(12345, (5 * 60),
                EnumSet.of(serverBindingMode),false, BindingMode.U));

        builder.setObjects(initializer.createAll());
        // Utiliser Californium au lieu de JavaCoAP
        builder.setEndpointsProviders(List.of(new JavaCoapClientEndpointsProvider()));
        LeshanClient client = builder.build();
        Log.d("LwM2M","Fin du test !");
        client.start();
    }
}
