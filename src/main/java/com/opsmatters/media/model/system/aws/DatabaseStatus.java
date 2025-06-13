/*
 * Copyright 2025 Gerald Curley
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

package com.opsmatters.media.model.system.aws;

/**
 * Represents the status of an RDS database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum DatabaseStatus
{
    UNKNOWN,
    STARTING,
    RUNNING,
    STOPPING,
    STOPPED,
    BACKING_UP,
    UPGRADING,
    REBOOTING,
    CONFIGURING,
    MODIFYING;

    /**
     * Returns <CODE>true</CODE> if the environment has a busy status.
     * @return <CODE>true</CODE> if environment has a busy status
     */
    public boolean busy()
    {
        return this == STARTING
            || this == BACKING_UP
            || this == UPGRADING
            || this == REBOOTING
            || this == CONFIGURING
            || this == MODIFYING;
    }
}