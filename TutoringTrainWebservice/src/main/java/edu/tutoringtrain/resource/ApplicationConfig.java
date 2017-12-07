/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import edu.tutoringtrain.annotations.AuthenticationFilter;
import edu.tutoringtrain.annotations.LanguageFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Elias
 */
@javax.ws.rs.ApplicationPath("services")
public class ApplicationConfig extends Application {

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> map = new HashMap<>();
        map.put("jersey.config.server.provider.classnames", "org.glassfish.jersey.media.multipart.MultiPartFeature");
        return map;
    }
    
    @Override
    public Set<Object> getSingletons() {
        final Set<Object> singletons = new HashSet<>();
        
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);    
        provider.setMapper(objectMapper);
        singletons.add(provider);
        
        return singletons;
    }
    
    @Override
    public Set<Class<?>> getClasses() {        
        Set<Class<?>> resources = new java.util.HashSet<>();
        
        resources.add(AuthenticationFilter.class);
        resources.add(LanguageFilter.class);

        resources.add(UserResource.class);
        resources.add(AuthenticationResource.class);
        resources.add(SubjectResource.class);
        resources.add(OfferResource.class);
        resources.add(RequestResource.class);
        return resources;
    }
}
