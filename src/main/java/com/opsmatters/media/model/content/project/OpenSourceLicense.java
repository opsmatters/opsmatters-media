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

package com.opsmatters.media.model.content.project;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents an open source license.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum OpenSourceLicense
{
    AGPL_3_0("agpl-3.0", "AGPL 3.0"),
    APACHE_2_0("apache-2.0", "Apache 2.0"),
    BSD_2_CLAUSE("bsd-2-clause", "BSD 2 Clause"),
    BSD_3_CLAUSE("bsd-3-clause", "BSD 3 Clause"),
    CC_BY_SA_3_0("cc-by-sa-3.0", "CC-BY-SA 3.0"),
    CC_BY_NC_SA_4_0("cc-by-nc-sa-4.0", "CC-BY-NC-SA 4.0"),
    CDDL_1_0("cddl-1.0", "CDDL 1.0"),
    CDDL_1_1("cddl-1.1", "CDDL 1.1"),
    GPL_2_0("gpl-2.0", "GPL 2.0"),
    GPL_3_0("gpl-3.0", "GPL 3.0"),
    GPL_3_0_PLUS("gpl-3.0-or-later", "GPL v3+"),
    ISC("isc", "ISC"),
    LGPL_2_1("lgpl-2.1", "LGPL 2.1"),
    LGPL_3_0("lgpl-3.0", "LGPL 3.0"),
    MIT("mit", "MIT"),
    MPL_2_0("mpl-2.0", "MPL 2.0"),
    PHP("php", "PHP"),
    SSPL("sspl", "SSPL");

    private String code;
    private String value;

    /**
     * Constructor that takes the code and name.
     * @param code The code for the license
     * @param value The value of the license
     */
    OpenSourceLicense(String code, String value)
    {
        this.code = code;
        this.value = value;
    }

    /**
     * Returns the value of the license.
     * @return The value of the license.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the code of the license.
     * @return The code of the license.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the value of the license.
     * @return The value of the license.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the type for the given code.
     * @param code The type code
     * @return The type for the given code
     */
    public static OpenSourceLicense fromCode(String code)
    {
        OpenSourceLicense[] types = values();
        for(OpenSourceLicense type : types)
        {
            if(type.code().equals(code))
                return type;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given code is contained in the list of types.
     * @param code The type code
     * @return <CODE>true</CODE> if the given code is contained in the list of types
     */
    public static boolean contains(String code)
    {
        return fromCode(code) != null;
    }

    /**
     * Returns a list of the license values.
     */
    public static List<String> toList(boolean blank)
    {
        List<String> ret = new ArrayList<String>();

        if(blank)
            ret.add("-");
        for(OpenSourceLicense license : OpenSourceLicense.values())
            ret.add(license.value());

        return ret;
    }
}