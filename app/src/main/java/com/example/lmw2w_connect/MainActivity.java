package com.example.lmw2w_connect;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import org.eclipse.leshan.client.LeshanClient;
import org.eclipse.leshan.client.LeshanClientBuilder;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.LwM2mId;
import org.eclipse.leshan.core.endpoint.Protocol;
import org.eclipse.leshan.client.object.Security;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.core.request.BindingMode;
//import org.eclipse.leshan.transport.californium.endpoint.CfClientEndpointsProvider;
import org.eclipse.leshan.transport.javacoap.client.endpoint.JavaCoapClientEndpointsProvider;

import java.util.EnumSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(this::startLwm2mClient).start(); // Exécuter en arrière-plan
    }

    private void startLwm2mClient() {
        String endpoint = "RetD";
        LeshanClientBuilder builder = new LeshanClientBuilder(endpoint);
        BindingMode serverBindingMode = BindingMode.fromProtocol(Protocol.fromUri("coap://leshan.eclipseprojects.io:5683"));

        // Initialisation des objets LwM2M
        ObjectsInitializer initializer = new ObjectsInitializer();
        initializer.setInstancesForObject(LwM2mId.SECURITY, Security.noSec("coap://leshan.eclipseprojects.io:5683", 12345));
        initializer.setInstancesForObject(LwM2mId.SERVER, new Server(12345, (5 * 60),
                EnumSet.of(serverBindingMode),false, BindingMode.U));

        builder.setObjects(initializer.createAll());

        // Utiliser Californium au lieu de JavaCoAP
        builder.setEndpointsProviders(List.of(new JavaCoapClientEndpointsProvider()));

        LeshanClient client = builder.build();
        client.start();
    }
}
