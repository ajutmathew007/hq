/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004, 2005, 2006], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */

package org.hyperic.hq.product;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.util.config.ConfigResponse;

/**
 * Generic detector for use by pure-xml plugins.
 */
public class DaemonDetector
    extends ServerDetector
    implements AutoServerDetector,
               FileServerDetector {

    private static final Log log =
        LogFactory.getLog(DaemonDetector.class.getName());

    protected String getProcessQuery() {
        return getTypeProperty("PROC_QUERY");
    }

    protected ServerResource newServerResource(String exe) {
        ServerResource server = createServerResource(exe);
        //try the defaults
        setProductConfig(server, new ConfigResponse());
        setMeasurementConfig(server, new ConfigResponse());
        return server;
    }

    public List getServerResources(ConfigResponse platformConfig)
        throws PluginException {

        //we can only report one of these to prevent duplication,
        //since they will all have the same default configuration
        List servers = new ArrayList();
        List found = getFileResources(platformConfig);
        if (found.size() != 0) {
            servers.add(found.get(0));
        }
        else {
            found = getProcessResources(platformConfig);
            if (found.size() != 0) {
                servers.add(found.get(0));
            }   
        }

        return servers;
    }

    public List getServerResources(ConfigResponse platformConfig,
                                   String path)
        throws PluginException {

        List servers = new ArrayList();
        servers.add(newServerResource(path));
        return servers;
    }

    protected List discoverServices(ConfigResponse config)
        throws PluginException {

        //e.g. qmail plugin has 1 instance of each service
        String hasBuiltinServices =
            getTypeProperty("HAS_BUILTIN_SERVICES");

        if (!"true".equals(hasBuiltinServices)) {
            return super.discoverServices(config);    
        }

        List services = new ArrayList();
        TypeInfo[] types = this.data.getTypes();

        for (int i=0; i<types.length; i++) {
            TypeInfo type = types[i];
            if (type.getType() != TypeInfo.TYPE_SERVICE) {
                continue;
            }

            ServiceResource service = new ServiceResource();
            service.setType(type.getName());
            String name = getTypeNameProperty(type.getName());
            service.setServiceName(name);
            //try the defaults
            setProductConfig(service, new ConfigResponse());
            setMeasurementConfig(service, new ConfigResponse());
            services.add(service);
        }

        return services;
    }

    /**
     * Check for installed files using the file-scan config without
     * running a full file-scan. 
     */
    protected List getFileResources(ConfigResponse platformConfig)
        throws PluginException {

        List servers = new ArrayList();
        String type = getTypeInfo().getName();
        List includes = this.data.getFileScanIncludes(type);
        if (includes == null) {
            return servers;
        }

        for (int i=0; i<includes.size(); i++) {
            String file = (String)includes.get(i);
            if (new File(file).exists()) {
                servers.add(newServerResource(file));
            }
        }

        return servers;
    }

    /**
     * Process table scan
     */
    protected List getProcessResources(ConfigResponse platformConfig)
        throws PluginException {

        String query = getProcessQuery();
        if (query == null) {
            log.debug("no PROC_QUERY defined for: " +
                      getTypeInfo().getName());
            return null;
        }

        List servers = new ArrayList();
        long[] pids = getPids(query);

        log.debug("'" + query + "' matched " +
                  pids.length + " processes");

        for (int i=0; i<pids.length; i++) {
            long pid = pids[i];

            String exe = getProcExe(pid);
            if (exe == null) {
                log.debug("Unable to determine exe for " +
                          query + " pid=" + pid);
                exe = query;
            }

            servers.add(newServerResource(exe));
        }
        
        return servers;
    }
}
