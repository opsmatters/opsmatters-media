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
package com.opsmatters.media.config.admin;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import com.opsmatters.media.model.admin.AppParameter;
import com.opsmatters.media.model.admin.AppParameterType;
import com.opsmatters.media.model.admin.AppParameterName;

/**
 * Class representing the set of parameters.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class AppParameters extends LinkedHashMap<AppParameterType,Map>
{
    private static Map<AppParameterType,Map<AppParameterName,AppParameter>> types = new LinkedHashMap<AppParameterType,Map<AppParameterName,AppParameter>>();

    /**
     * Private constructor.
     */
    private AppParameters()
    {
    }

    /**
     * Loads the set of parameters.
     */
    public static void load(List<AppParameter> parameters)
    {
        types.clear();
        for(AppParameter parameter : parameters)
            add(parameter);
    }

    /**
     * Adds the given parameter.
     */
    private static void add(AppParameter parameter)
    {
        Map<AppParameterName,AppParameter> map = types.get(parameter.getType());
        if(map == null)
        {
            map = new LinkedHashMap<AppParameterName,AppParameter>();
            types.put(parameter.getType(), map);
        }

        map.put(parameter.getName(), parameter);
    }

    /**
     * Returns the list of parameters with the given type.
     */
    public static List<AppParameter> get(AppParameterType type)
    {
        List<AppParameter> ret = new ArrayList<AppParameter>();
        Map<AppParameterName,AppParameter> map = types.get(type);
        if(map != null)
            ret.addAll(map.values());
        return ret;
    }

    /**
     * Returns the parameter with the given type and name.
     */
    public static AppParameter get(AppParameterType type, AppParameterName name)
    {
        AppParameter ret = null;
        Map<AppParameterName,AppParameter> map = types.get(type);
        if(map != null)
            ret = map.get(name);
        return ret;
    }

    /**
     * Sets the parameter with the given type and name to the given value.
     */
    public static void put(AppParameterType type, AppParameterName name, String value)
    {
        AppParameter parameter = get(type, name);
        if(parameter == null)
            throw new IllegalStateException("parameter does not exist");
        parameter.setUpdatedDate(Instant.now());
        parameter.setValue(value);
    }

    /**
     * Sets the parameter with the given type and name to the given value.
     */
    public static void put(AppParameterType type, AppParameterName name, int value)
    {
        put(type, name, Integer.toString(value));
    }
}