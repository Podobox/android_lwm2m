package com.example.lwm2m_connect;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.servers.LwM2mServer;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.link.Link;
import org.eclipse.leshan.core.response.ReadResponse;


public class Gateway extends BaseInstanceEnabler {

    private final String deviceId;
    private final String prefix;
    private Link[] iotDeviceObjects;


    public Gateway() {
        this.deviceId = "Centrale_1";
        this.prefix = "sensor_desk";
        this.iotDeviceObjects = new Link[] {
                new Link("/3/0"),   // Objet Device
                new Link("/3/1"),   // Objet Device
                new Link("/3303/0") // Objet Temperature Sensor
        };
    }
    public Gateway(String deviceId,String prefix, Link[] iotDevice) {
        this.deviceId = deviceId;
        this.prefix = prefix;
        this.iotDeviceObjects = iotDevice;
    }

    /*public void registerVirtualObject(String prefix, BaseInstanceEnabler object) {
        this.prefixToObject.put(prefix, object);
    }*/

    @Override
    public ReadResponse read(LwM2mServer server, int resourceId) {
        switch (resourceId) {
            case 0: // Device ID
                return ReadResponse.success(resourceId, getDeviceId());
            case 1: // Prefix
                return ReadResponse.success(resourceId, getPrefix());
            case 3: // IoT Device Objects
                return ReadResponse.success(getLink(resourceId));
            default:
                return ReadResponse.notFound();
        }
    }

    protected String getDeviceId() {
        return this.deviceId;
    }

    protected String getPrefix() {
        return this.prefix;
    }

    protected LwM2mSingleResource getLink(int resourceId) {
        return LwM2mSingleResource.newCoreLinkResource(resourceId, iotDeviceObjects);
    }

}
