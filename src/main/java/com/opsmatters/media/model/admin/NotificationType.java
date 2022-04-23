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
    MONITOR_SUSPENDED,
    MONITOR_ERROR_COUNT,
    MONITOR_CHANGED_COUNT,
    MONITOR_REVIEW_COUNT,
    SOCIAL_ERROR_COUNT,
    SOCIAL_ERROR_POST,
    SOCIAL_WAITING,
    FEED_ERROR_COUNT,
    FEED_SUBMITTED,
    TASK_ERROR_COUNT,
    TASK_PENDING,
    TASK_UPDATED,
    TASK_DELETED,
    EMAIL_ERROR_COUNT,
    THREAD_STALLED,
    CONFIG_ERROR,
    CONFIG_UPDATE,
    ORGANISATION_UPDATE;
}