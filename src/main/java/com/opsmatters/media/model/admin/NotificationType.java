/*
 * Copyright 2021 Gerald Curley
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

package com.opsmatters.media.model.admin;

/**
 * Represents a notification type.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum NotificationType
{
    NONE,
    MONITOR_ALERT,
    MONITOR_ERROR_COUNT,
    MONITOR_CHANGE_COUNT,
    MONITOR_ALERT_COUNT,
    LOG_ERROR,
    LOG_WARN,
    SOCIAL_ERROR_COUNT,
    SOCIAL_ERROR_POST,
    SOCIAL_WAITING,
    FEED_ALERT,
    FEED_ERROR_COUNT,
    FEED_SUBMITTED,
    TASK_ALERT,
    TASK_ERROR_COUNT,
    TASK_PENDING,
    TASK_UPDATED,
    TASK_DELETED,
    EMAIL_ERROR_COUNT,
    THREAD_STALLED,
    SETTINGS_ERROR,
    SETTINGS_UPDATE,
    ORGANISATION_UPDATE,
    IMAGE_UPDATE;
}