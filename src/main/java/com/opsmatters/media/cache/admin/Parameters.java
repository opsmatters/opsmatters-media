/*
 * Copyright 2020 Gerald Curley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opsmatters.media.cache.admin;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import com.opsmatters.media.model.admin.Parameter;
import com.opsmatters.media.model.admin.ParameterType;
import com.opsmatters.media.model.admin.ParameterName;

/**
 * Class representing the cache of application parameters.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Parameters extends LinkedHashMap<ParameterType,Map>
{
    private static Map<ParameterType,Map<ParameterName,Parameter>> types = new LinkedHashMap<ParameterType,Map<ParameterName,Parameter>>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Parameters()
    {
    }

    /**
     * Returns <CODE>true</CODE> if parameters have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of parameters.
     */
    public static void load(List<Parameter> parameters)
    {
        initialised = false;

        types.clear();
        for(Parameter parameter : parameters)
            add(parameter);

        initialised = true;
    }

    /**
     * Sets the given parameters.
     */
    public static void set(List<Parameter> parameters)
    {
        for(Parameter parameter : parameters)
            add(parameter);
    }

    /**
     * Adds the given parameter.
     */
    private static void add(Parameter parameter)
    {
        Map<ParameterName,Parameter> map = types.get(parameter.getType());
        if(map == null)
        {
            map = new LinkedHashMap<ParameterName,Parameter>();
            types.put(parameter.getType(), map);
        }

        map.put(parameter.getName(), parameter);
    }

    /**
     * Returns the parameter with the given type and name.
     */
    public static Parameter get(ParameterType type, ParameterName name)
    {
        Parameter ret = null;
        Map<ParameterName,Parameter> map = types.get(type);
        if(map != null)
            ret = map.get(name);
        return ret;
    }

    /**
     * Sets the parameter with the given type and name to the given value.
     */
    public static void put(ParameterType type, ParameterName name, String value)
    {
        Parameter parameter = get(type, name);
        if(parameter == null)
            throw new IllegalStateException("parameter does not exist");
        parameter.setUpdatedDate(Instant.now());
        parameter.setValue(value);
    }

    /**
     * Sets the parameter with the given type and name to the given value.
     */
    public static void put(ParameterType type, ParameterName name, int value)
    {
        put(type, name, Integer.toString(value));
    }

    /**
     * Sets the parameter with the given type and name to the given value.
     */
    public static void put(ParameterType type, ParameterName name, boolean value)
    {
        put(type, name, Boolean.toString(value));
    }

    /**
     * Sets the parameter with the given type and name to the given value.
     */
    public static void put(ParameterType type, ParameterName name, Instant value)
    {
        put(type, name, Long.toString(value.toEpochMilli()));
    }

    /**
     * Returns the map of parameters with the given type.
     */
    public static Map<ParameterName,Parameter> map(ParameterType type)
    {
        return types.get(type);
    }

    /**
     * Returns the list of parameters with the given type.
     */
    public static List<Parameter> list(ParameterType type)
    {
        List<Parameter> ret = new ArrayList<Parameter>();
        Map<ParameterName,Parameter> map = types.get(type);
        if(map != null)
            ret.addAll(map.values());
        return ret;
    }
}